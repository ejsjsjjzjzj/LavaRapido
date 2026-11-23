package controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import model.Agendamento;
import model.StatusAgendamento;
import model.service.AgendamentoService;
import model.service.ClienteService;
import model.service.TipoServicoService;
import model.service.ValidacaoException;
import view.AgendaView;
import view.UI;

/**
 * Controller da agenda: converte a entrada da View, aciona o
 * AgendamentoService (que valida conflitos) e atualiza a tela.
 */
public class AgendaController {

    private final AgendaView view;
    private final AgendamentoService agendamentoService;
    private final ClienteService clienteService;
    private final TipoServicoService tipoServicoService;

    public AgendaController(AgendaView view, AgendamentoService agendamentoService,
                            ClienteService clienteService, TipoServicoService tipoServicoService) {
        this.view = view;
        this.agendamentoService = agendamentoService;
        this.clienteService = clienteService;
        this.tipoServicoService = tipoServicoService;

        view.addAgendarListener(e -> agendar());
        view.addSalvarAlteracaoListener(e -> salvarAlteracao());
        view.addConcluirListener(e -> concluir());
        view.addCancelarListener(e -> cancelar());
        view.addLimparListener(e -> view.limpar());
        view.addAtualizarListener(e -> atualizarTabela());
        view.addHojeListener(e -> {
            view.setDataFiltro(LocalDate.now().format(UI.DATA));
            atualizarTabela();
        });
        view.addSelecaoListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            long id = view.getIdSelecionado();
            Agendamento a = id >= 0 ? agendamentoService.buscarPorId(id) : null;
            if (a != null) {
                view.preencher(a);
            }
        });

        view.setDataFiltro(LocalDate.now().format(UI.DATA));
        aoAlterarDados();
    }

    /** Chamado quando clientes ou tipos de serviço mudam: recarrega combos e tabela. */
    public void aoAlterarDados() {
        view.setClientes(clienteService.listar());
        view.setTipos(tipoServicoService.listar());
        atualizarTabela();
    }

    /** Recarrega a tabela conforme a data e a visão (diária/semanal) escolhidas. */
    public void atualizarTabela() {
        LocalDate base;
        try {
            base = LocalDate.parse(view.getDataFiltro(), UI.DATA);
        } catch (DateTimeParseException ex) {
            view.mostrarErro("Data do filtro inválida. Use o formato dd/mm/aaaa.");
            return;
        }

        LocalDate inicio = base;
        LocalDate fim = base;
        if (view.isVisaoSemanal()) {
            inicio = base.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            fim = inicio.plusDays(6);
        }

        List<Agendamento> lista = agendamentoService.listarPorPeriodo(inicio, fim);
        double total = lista.stream()
                .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
                .mapToDouble(a -> a.getTipoServico().getPreco())
                .sum();
        String periodo = inicio.equals(fim)
                ? inicio.format(UI.DATA)
                : inicio.format(UI.DATA) + " a " + fim.format(UI.DATA);
        String resumo = String.format("Período: %s  |  %d agendamento(s)  |  Total previsto (exceto cancelados): %s",
                periodo, lista.size(), UI.MOEDA.format(total));
        view.atualizarTabela(lista, resumo);
    }

    private void agendar() {
        try {
            LocalDateTime dataHora = lerDataHora();
            agendamentoService.agendar(view.getClienteSelecionado(), view.getTipoSelecionado(), dataHora);
            view.mostrarInfo("Serviço agendado com sucesso!");
            depoisDeAlterar(dataHora);
        } catch (ValidacaoException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    private void salvarAlteracao() {
        long id = view.getIdSelecionado();
        if (id < 0) {
            view.mostrarErro("Selecione um agendamento na tabela para editar.");
            return;
        }
        try {
            LocalDateTime dataHora = lerDataHora();
            agendamentoService.editar(id, view.getClienteSelecionado(), view.getTipoSelecionado(), dataHora);
            view.mostrarInfo("Agendamento atualizado com sucesso!");
            depoisDeAlterar(dataHora);
        } catch (ValidacaoException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    private void cancelar() {
        long id = view.getIdSelecionado();
        if (id < 0) {
            view.mostrarErro("Selecione um agendamento na tabela para cancelar.");
            return;
        }
        if (!view.confirmar("Deseja realmente cancelar este agendamento?")) {
            return;
        }
        try {
            agendamentoService.cancelar(id);
            view.limpar();
            atualizarTabela();
        } catch (ValidacaoException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    private void concluir() {
        long id = view.getIdSelecionado();
        if (id < 0) {
            view.mostrarErro("Selecione um agendamento na tabela para concluir.");
            return;
        }
        try {
            agendamentoService.concluir(id);
            view.limpar();
            atualizarTabela();
        } catch (ValidacaoException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    /** Mostra na tabela o dia do agendamento que acabou de ser salvo. */
    private void depoisDeAlterar(LocalDateTime dataHora) {
        view.setDataFiltro(dataHora.toLocalDate().format(UI.DATA));
        view.limpar();
        atualizarTabela();
    }

    private LocalDateTime lerDataHora() throws ValidacaoException {
        try {
            LocalDate data = LocalDate.parse(view.getData(), UI.DATA);
            LocalTime hora = LocalTime.parse(view.getHora(), UI.HORA);
            return LocalDateTime.of(data, hora);
        } catch (DateTimeParseException ex) {
            throw new ValidacaoException("Data ou hora inválida. Use dd/mm/aaaa e hh:mm (ex.: 25/11/2026 e 14:30).");
        }
    }
}
