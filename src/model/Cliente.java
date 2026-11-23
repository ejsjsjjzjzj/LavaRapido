package model;

import java.io.Serializable;

/**
 * Entidade que representa um cliente do lava rápido.
 */
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String placaVeiculo;

    public Cliente() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPlacaVeiculo() { return placaVeiculo; }
    public void setPlacaVeiculo(String placaVeiculo) { this.placaVeiculo = placaVeiculo; }

    /** Usado pelos JComboBox da interface. */
    @Override
    public String toString() {
        return nome + " (" + placaVeiculo + ")";
    }
}
