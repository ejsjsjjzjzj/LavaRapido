package model.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.Agendamento;
import model.Cliente;

/**
 * Regras de negócio do cadastro de clientes.
 */
public class ClienteService {

    private final Repositorio repo;

    public ClienteService(Repositorio repo) {
        this.repo = repo;
    }

    /** Clientes em ordem alfabética. */
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>(repo.clientes());
        lista.sort(Comparator.comparing(c -> c.getNome().toLowerCase()));
        return lista;
    }

    public Cliente buscarPorId(long id) {
        for (Cliente c : repo.clientes()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public Cliente cadastrar(String nome, String cpf, String telefone, String email, String placa)
            throws ValidacaoException {
        Cliente novo = new Cliente();
        validarEPreencher(novo, nome, cpf, telefone, email, placa);
        novo.setId(repo.novoIdCliente());
        repo.clientes().add(novo);
        repo.salvar();
        return novo;
    }

    public Cliente editar(long id, String nome, String cpf, String telefone, String email, String placa)
            throws ValidacaoException {
        Cliente existente = buscarPorId(id);
        if (existente == null) {
            throw new ValidacaoException("Cliente não encontrado.");
        }
        validarEPreencher(existente, nome, cpf, telefone, email, placa);
        repo.salvar();
        return existente;
    }

    /** Não permite excluir clientes que possuem agendamentos (mantém o histórico íntegro). */
    public void excluir(long id) throws ValidacaoException {
        Cliente cliente = buscarPorId(id);
        if (cliente == null) {
            throw new ValidacaoException("Cliente não encontrado.");
        }
        for (Agendamento a : repo.agendamentos()) {
            if (a.getCliente().getId() == id) {
                throw new ValidacaoException(
                        "Este cliente possui agendamentos e não pode ser excluído.\n"
                        + "Cancele/remova os agendamentos primeiro ou mantenha o cadastro.");
            }
        }
        repo.clientes().remove(cliente);
        repo.salvar();
    }

    /** Valida todos os campos e só então altera o objeto (evita estado parcial). */
    private void validarEPreencher(Cliente destino, String nome, String cpf, String telefone,
                                   String email, String placa) throws ValidacaoException {
        nome = nome == null ? "" : nome.trim();
        if (nome.length() < 3) {
            throw new ValidacaoException("Informe o nome do cliente (mínimo de 3 caracteres).");
        }
        if (!Validador.cpfValido(cpf)) {
            throw new ValidacaoException("CPF inválido.");
        }
        String cpfFormatado = Validador.formatarCpf(cpf);
        for (Cliente outro : repo.clientes()) {
            if (outro.getId() != destino.getId() && outro.getCpf().equals(cpfFormatado)) {
                throw new ValidacaoException("Já existe um cliente cadastrado com este CPF.");
            }
        }
        if (!Validador.telefoneValido(telefone)) {
            throw new ValidacaoException("Telefone inválido. Informe o DDD e o número (10 ou 11 dígitos).");
        }
        if (!Validador.emailValido(email)) {
            throw new ValidacaoException("E-mail inválido.");
        }
        String placaNormalizada = Validador.normalizarPlaca(placa);
        if (!Validador.placaValida(placaNormalizada)) {
            throw new ValidacaoException("Placa inválida. Use o formato ABC1234 ou ABC1D23.");
        }

        destino.setNome(nome);
        destino.setCpf(cpfFormatado);
        destino.setTelefone(Validador.formatarTelefone(telefone));
        destino.setEmail(email.trim());
        destino.setPlacaVeiculo(placaNormalizada);
    }
}
