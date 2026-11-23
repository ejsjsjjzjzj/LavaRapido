package model;

import java.io.Serializable;
import java.util.Locale;

/**
 * Entidade que representa um tipo de serviço oferecido
 * (lavagem simples, completa, com cera, detalhada etc.).
 */
public class TipoServico implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String nome;
    private String descricao;
    private double preco;

    public TipoServico() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    /** Usado pelos JComboBox da interface. */
    @Override
    public String toString() {
        return nome + " - R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", preco);
    }
}
