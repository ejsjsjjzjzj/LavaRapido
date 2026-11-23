package controller;

import model.TipoServico;
import model.service.TipoServicoService;
import model.service.ValidacaoException;
import view.TipoServicoView;

/**
 * Controller do módulo de tipos de serviço.
 */
public class TipoServicoController {

    private final TipoServicoView view;
    private final TipoServicoService service;
    private final Runnable aoAlterarDados;

    public TipoServicoController(TipoServicoView view, TipoServicoService service, Runnable aoAlterarDados) {
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
            TipoServico t = id >= 0 ? service.buscarPorId(id) : null;
            if (t != null) {
                view.preencher(t);
            }
        });

        atualizarTabela();
    }

    public void atualizarTabela() {
        view.atualizarTabela(service.listar());
    }

    private void salvar() {
        double preco;
        try {
            preco = converterPreco(view.getPreco());
        } catch (NumberFormatException ex) {
            view.mostrarErro("Preço inválido. Exemplo: 45,90");
            return;
        }
        try {
            long id = view.getIdSelecionado();
            if (id < 0) {
                service.cadastrar(view.getNome(), view.getDescricao(), preco);
                view.mostrarInfo("Tipo de serviço cadastrado com sucesso!");
            } else {
                service.editar(id, view.getNome(), view.getDescricao(), preco);
                view.mostrarInfo("Tipo de serviço atualizado com sucesso!");
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
            view.mostrarErro("Selecione um tipo de serviço na tabela para excluir.");
            return;
        }
        if (!view.confirmar("Deseja realmente excluir este tipo de serviço?")) {
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

    /** Aceita "45,90", "45.90", "R$ 1.234,50" etc. */
    private double converterPreco(String texto) {
        String t = texto.replace("R$", "").trim();
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");
        }
        return Double.parseDouble(t);
    }
}
