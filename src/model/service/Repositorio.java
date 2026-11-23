package model.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import model.Agendamento;
import model.Cliente;
import model.TipoServico;

/**
 * Persistência simples: mantém todas as listas em memória e as grava em um
 * único arquivo por serialização Java. Como tudo é gravado junto, as
 * referências entre Agendamento, Cliente e TipoServico são preservadas.
 */
public class Repositorio implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<TipoServico> tiposServico = new ArrayList<>();
    private final List<Agendamento> agendamentos = new ArrayList<>();
    private long proximoIdCliente = 1;
    private long proximoIdTipoServico = 1;
    private long proximoIdAgendamento = 1;

    /** Arquivo de destino (não é gravado dentro do próprio arquivo). */
    private transient Path arquivo;

    /** Carrega os dados do arquivo; se não existir (ou estiver corrompido), começa vazio. */
    public static Repositorio carregar(Path arquivo) {
        Repositorio repo = null;
        if (Files.exists(arquivo)) {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(arquivo))) {
                repo = (Repositorio) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Não foi possível ler " + arquivo + ": " + e.getMessage());
            }
        }
        if (repo == null) {
            repo = new Repositorio();
        }
        repo.arquivo = arquivo;
        return repo;
    }

    /** Grava o estado atual no arquivo. */
    void salvar() throws ValidacaoException {
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(arquivo))) {
                out.writeObject(this);
            }
        } catch (IOException e) {
            throw new ValidacaoException("Falha ao gravar os dados em arquivo: " + e.getMessage());
        }
    }

    List<Cliente> clientes() { return clientes; }
    List<TipoServico> tiposServico() { return tiposServico; }
    List<Agendamento> agendamentos() { return agendamentos; }

    long novoIdCliente() { return proximoIdCliente++; }
    long novoIdTipoServico() { return proximoIdTipoServico++; }
    long novoIdAgendamento() { return proximoIdAgendamento++; }
}
