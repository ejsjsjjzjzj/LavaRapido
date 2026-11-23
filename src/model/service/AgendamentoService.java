package model.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import model.Agendamento;
import model.Cliente;
import model.StatusAgendamento;
import model.TipoServico;

/**
 * Regras de negócio da agenda de serviços, incluindo a validação de conflitos.
 *
 * Premissas: o lava rápido possui uma única baia, cada atendimento ocupa um
 * intervalo fixo de {@link #DURACAO_MINUTOS} minutos e o expediente vai das
 * {@link #ABERTURA} às {@link #FECHAMENTO}.
 */
public class AgendamentoService {

    public static final int DURACAO_MINUTOS = 60;
    public static final LocalTime ABERTURA = LocalTime.of(8, 0);
    public static final LocalTime FECHAMENTO = LocalTime.of(18, 0);

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final Repositorio repo;

    public AgendamentoService(Repositorio repo) {
        this.repo = repo;
    }

    /** Agendamentos cuja data está entre inicio e fim (inclusive), em ordem cronológica. */
    public List<Agendamento> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return repo.agendamentos().stream()
                .filter(a -> {
                    LocalDate dia = a.getDataHora().toLocalDate();
                    return !dia.isBefore(inicio) && !dia.isAfter(fim);
                })
                .sorted(Comparator.comparing(Agendamento::getDataHora))
                .collect(Collectors.toList());
    }

    public Agendamento buscarPorId(long id) {
        for (Agendamento a : repo.agendamentos()) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    public Agendamento agendar(Cliente cliente, TipoServico tipo, LocalDateTime dataHora)
            throws ValidacaoException {
        validar(cliente, tipo, dataHora, null);
        Agendamento novo = new Agendamento();
        novo.setId(repo.novoIdAgendamento());
        novo.setCliente(cliente);
        novo.setTipoServico(tipo);
        novo.setDataHora(dataHora);
        novo.setStatus(StatusAgendamento.AGENDADO);
        repo.agendamentos().add(novo);
        repo.salvar();
        return novo;
    }

    public Agendamento editar(long id, Cliente cliente, TipoServico tipo, LocalDateTime dataHora)
            throws ValidacaoException {
        Agendamento existente = buscarPorId(id);
        if (existente == null) {
            throw new ValidacaoException("Agendamento não encontrado.");
        }
        if (existente.getStatus() != StatusAgendamento.AGENDADO) {
            throw new ValidacaoException("Somente agendamentos com status \"Agendado\" podem ser editados.");
        }
        validar(cliente, tipo, dataHora, existente);
        existente.setCliente(cliente);
        existente.setTipoServico(tipo);
        existente.setDataHora(dataHora);
        repo.salvar();
        return existente;
    }

    public void cancelar(long id) throws ValidacaoException {
        alterarStatus(id, StatusAgendamento.CANCELADO, "cancelados");
    }

    public void concluir(long id) throws ValidacaoException {
        alterarStatus(id, StatusAgendamento.CONCLUIDO, "concluídos");
    }

    private void alterarStatus(long id, StatusAgendamento novoStatus, String descricao) throws ValidacaoException {
        Agendamento a = buscarPorId(id);
        if (a == null) {
            throw new ValidacaoException("Agendamento não encontrado.");
        }
        if (a.getStatus() != StatusAgendamento.AGENDADO) {
            throw new ValidacaoException("Somente agendamentos com status \"Agendado\" podem ser " + descricao + ".");
        }
        a.setStatus(novoStatus);
        repo.salvar();
    }

    /**
     * Valida os dados e garante que não haja conflito na agenda.
     *
     * @param existente agendamento que está sendo editado (ignorado na checagem
     *                  de conflito), ou null quando for um novo agendamento
     */
    private void validar(Cliente cliente, TipoServico tipo, LocalDateTime dataHora, Agendamento existente)
            throws ValidacaoException {
        if (cliente == null) {
            throw new ValidacaoException("Selecione um cliente.");
        }
        if (tipo == null) {
            throw new ValidacaoException("Selecione um tipo de serviço.");
        }
        if (dataHora == null) {
            throw new ValidacaoException("Informe a data e a hora.");
        }

        boolean mudouHorario = existente == null || !existente.getDataHora().equals(dataHora);
        if (mudouHorario && dataHora.isBefore(LocalDateTime.now())) {
            throw new ValidacaoException("Não é possível agendar em uma data/hora que já passou.");
        }

        LocalTime hora = dataHora.toLocalTime();
        if (hora.isBefore(ABERTURA) || hora.isAfter(FECHAMENTO.minusMinutes(DURACAO_MINUTOS))) {
            throw new ValidacaoException(String.format(
                    "Horário fora do expediente. Atendemos das %s às %s (último horário: %s).",
                    ABERTURA, FECHAMENTO, FECHAMENTO.minusMinutes(DURACAO_MINUTOS)));
        }

        for (Agendamento outro : repo.agendamentos()) {
            if (outro.getStatus() != StatusAgendamento.AGENDADO) {
                continue;
            }
            if (existente != null && outro.getId() == existente.getId()) {
                continue;
            }
            long diferenca = Duration.between(outro.getDataHora(), dataHora).abs().toMinutes();
            if (diferenca < DURACAO_MINUTOS) {
                throw new ValidacaoException(
                        "Conflito na agenda: já existe um serviço agendado em "
                        + outro.getDataHora().format(FORMATO) + " (" + outro.getCliente().getNome() + ").\n"
                        + "Cada atendimento ocupa " + DURACAO_MINUTOS + " minutos.");
            }
        }
    }
}
