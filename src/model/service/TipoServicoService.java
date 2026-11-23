package model.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.Agendamento;
import model.TipoServico;

/**
 * Regras de negócio do cadastro de tipos de serviço.
 */
public class TipoServicoService {

    private final Repositorio repo;

    public TipoServicoService(Repositorio repo) {
        this.repo = repo;
    }

    /** Tipos de serviço em ordem de preço. */
    public List<TipoServico> listar() {
        List<TipoServico> lista = new ArrayList<>(repo.tiposServico());
        lista.sort(Comparator.comparingDouble(TipoServico::getPreco));
        return lista;
    }

    public TipoServico buscarPorId(long id) {
        for (TipoServico t : repo.tiposServico()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public TipoServico cadastrar(String nome, String descricao, double preco) throws ValidacaoException {
        TipoServico novo = new TipoServico();
        validarEPreencher(novo, nome, descricao, preco);
        novo.setId(repo.novoIdTipoServico());
        repo.tiposServico().add(novo);
        repo.salvar();
        return novo;
    }

    public TipoServico editar(long id, String nome, String descricao, double preco) throws ValidacaoException {
        TipoServico existente = buscarPorId(id);
        if (existente == null) {
            throw new ValidacaoException("Tipo de serviço não encontrado.");
        }
        validarEPreencher(existente, nome, descricao, preco);
        repo.salvar();
        return existente;
    }

    /** Não permite excluir tipos que já foram usados em agendamentos. */
    public void excluir(long id) throws ValidacaoException {
        TipoServico tipo = buscarPorId(id);
        if (tipo == null) {
            throw new ValidacaoException("Tipo de serviço não encontrado.");
        }
        for (Agendamento a : repo.agendamentos()) {
            if (a.getTipoServico().getId() == id) {
                throw new ValidacaoException("Este tipo de serviço possui agendamentos e não pode ser excluído.");
            }
        }
        repo.tiposServico().remove(tipo);
        repo.salvar();
    }

    /** Cadastra os serviços básicos na primeira execução. */
    public void cadastrarPadroesSeVazio() throws ValidacaoException {
        if (!repo.tiposServico().isEmpty()) {
            return;
        }
        cadastrar("Lavagem simples", "Lavagem externa com shampoo e secagem", 30.00);
        cadastrar("Lavagem completa", "Lavagem externa e interna (aspiração e painel)", 60.00);
        cadastrar("Lavagem com cera", "Lavagem completa com aplicação de cera protetora", 80.00);
        cadastrar("Lavagem detalhada", "Higienização detalhada, polimento e hidratação de bancos", 150.00);
    }

    private void validarEPreencher(TipoServico destino, String nome, String descricao, double preco)
            throws ValidacaoException {
        nome = nome == null ? "" : nome.trim();
        descricao = descricao == null ? "" : descricao.trim();
        if (nome.length() < 3) {
            throw new ValidacaoException("Informe o nome do serviço (mínimo de 3 caracteres).");
        }
        if (descricao.isEmpty()) {
            throw new ValidacaoException("Informe a descrição do serviço.");
        }
        if (preco <= 0) {
            throw new ValidacaoException("O preço deve ser maior que zero.");
        }
        for (TipoServico outro : repo.tiposServico()) {
            if (outro.getId() != destino.getId() && outro.getNome().equalsIgnoreCase(nome)) {
                throw new ValidacaoException("Já existe um serviço com este nome.");
            }
        }
        destino.setNome(nome);
        destino.setDescricao(descricao);
        destino.setPreco(preco);
    }
}
