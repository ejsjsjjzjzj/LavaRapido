package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import model.TipoServico;

/**
 * Tela de cadastro e listagem dos tipos de serviço oferecidos.
 */
public class TipoServicoView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextField txtNome = new JTextField(28);
    private final JTextField txtDescricao = new JTextField(40);
    private final JTextField txtPreco = new JTextField(10);

    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnNovo = new JButton("Novo / Limpar");
    private final JButton btnExcluir = new JButton("Excluir");

    private final DefaultTableModel modelo = UI.modelo("ID", "Nome", "Descrição", "Preço");
    private final JTable tabela = UI.criarTabela(modelo);

    public TipoServicoView() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = UI.painelFormulario("Tipo de serviço (selecione uma linha para editar)");
        UI.addLinha(form, 0, "Nome:", txtNome);
        UI.addLinha(form, 1, "Descrição:", txtDescricao);
        UI.addLinha(form, 2, "Preço (R$):", txtPreco);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnSalvar);
        botoes.add(btnNovo);
        botoes.add(btnExcluir);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
    }

    public String getNome() { return txtNome.getText(); }
    public String getDescricao() { return txtDescricao.getText(); }
    public String getPreco() { return txtPreco.getText(); }

    public void preencher(TipoServico t) {
        txtNome.setText(t.getNome());
        txtDescricao.setText(t.getDescricao());
        txtPreco.setText(String.format(Locale.forLanguageTag("pt-BR"), "%.2f", t.getPreco()));
    }

    public void limpar() {
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        tabela.clearSelection();
        txtNome.requestFocusInWindow();
    }

    public void atualizarTabela(List<TipoServico> tipos) {
        modelo.setRowCount(0);
        for (TipoServico t : tipos) {
            modelo.addRow(new Object[] { t.getId(), t.getNome(), t.getDescricao(), UI.MOEDA.format(t.getPreco()) });
        }
    }

    public long getIdSelecionado() {
        return UI.idSelecionado(tabela, modelo);
    }

    public void addSalvarListener(ActionListener l) { btnSalvar.addActionListener(l); }
    public void addNovoListener(ActionListener l) { btnNovo.addActionListener(l); }
    public void addExcluirListener(ActionListener l) { btnExcluir.addActionListener(l); }
    public void addSelecaoListener(ListSelectionListener l) { tabela.getSelectionModel().addListSelectionListener(l); }

    public void mostrarErro(String mensagem) { UI.erro(this, mensagem); }
    public void mostrarInfo(String mensagem) { UI.info(this, mensagem); }
    public boolean confirmar(String mensagem) { return UI.confirmar(this, mensagem); }
}
