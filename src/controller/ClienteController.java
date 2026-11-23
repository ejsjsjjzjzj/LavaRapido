package controller;

import model.Cliente;
import model.service.ClienteService;
import model.service.ValidacaoException;
import view.ClienteView;

/**
 * Controller do módulo de clientes: trata os eventos da ClienteView,
 * chama o ClienteService (Model) e atualiza a View com o resultado.
 */
public class ClienteController {

    private final ClienteView view;
    private final ClienteService service;
    /** Avisa os outros módulos (ex.: agenda) que os dados mudaram. */
    private final Runnable aoAlterarDados;

    public ClienteController(ClienteView view, ClienteService service, Runnable aoAlterarDados) {
        this.view = view;
        this.service = service;
        this.aoAlterarDados = aoAlterarDados;

        view.addSalvarListener(e -> salvar());
        view.addNovoListener(e -> view.limpar());
        view.addExcluirListener(e -> excluir());
        view.addSelecaoListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            long id = view.getIdSelecionado();
            Cliente c = id >= 0 ? service.buscarPorId(id) : null;
            if (c != null) {
                view.preencher(c);
            }
        });

        atualizarTabela();
    }

    public void atualizarTabela() {
        view.atualizarTabela(service.listar());
    }

    /** Cadastra um novo cliente, ou edita o selecionado na tabela. */
    private void salvar() {
        try {
            long id = view.getIdSelecionado();
            if (id < 0) {
                service.cadastrar(view.getNome(), view.getCpf(), view.getTelefone(),
                        view.getEmail(), view.getPlaca());
                view.mostrarInfo("Cliente cadastrado com sucesso!");
            } else {
                service.editar(id, view.getNome(), view.getCpf(), view.getTelefone(),
                        view.getEmail(), view.getPlaca());
                view.mostrarInfo("Cliente atualizado com sucesso!");
            }
            atualizarTabela();
            view.limpar();
            aoAlterarDados.run();
        } catch (ValidacaoException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }

    private void excluir() {
        long id = view.getIdSelecionado();
        if (id < 0) {
            view.mostrarErro("Selecione um cliente na tabela para excluir.");
            return;
        }
        if (!view.confirmar("Deseja realmente excluir este cliente?")) {
            return;
        }
        try {
            service.excluir(id);
            atualizarTabela();
            view.limpar();
            aoAlterarDados.run();
        } catch (ValidacaoException ex) {
            view.mostrarErro(ex.getMessage());
        }
    }
}
