package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import model.Cliente;

/**
 * Tela de cadastro e listagem de clientes. Não contém regra de negócio:
 * apenas exibe dados e expõe os componentes para o Controller.
 */
public class ClienteView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextField txtNome = new JTextField(28);
    private final JTextField txtCpf = new JTextField(14);
    private final JTextField txtTelefone = new JTextField(14);
    private final JTextField txtEmail = new JTextField(28);
    private final JTextField txtPlaca = new JTextField(10);

    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnNovo = new JButton("Novo / Limpar");
    private final JButton btnExcluir = new JButton("Excluir");

    private final DefaultTableModel modelo = UI.modelo("ID", "Nome", "CPF", "Telefone", "E-mail", "Placa");
    private final JTable tabela = UI.criarTabela(modelo);

    public ClienteView() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = UI.painelFormulario("Dados do cliente (selecione uma linha para editar)");
        UI.addLinha(form, 0, "Nome:", txtNome);
        UI.addLinha(form, 1, "CPF:", txtCpf);
        UI.addLinha(form, 2, "Telefone:", txtTelefone);
        UI.addLinha(form, 3, "E-mail:", txtEmail);
        UI.addLinha(form, 4, "Placa do veículo:", txtPlaca);

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

    // ---- leitura dos campos ----
    public String getNome() { return txtNome.getText(); }
    public String getCpf() { return txtCpf.getText(); }
    public String getTelefone() { return txtTelefone.getText(); }
    public String getEmail() { return txtEmail.getText(); }
    public String getPlaca() { return txtPlaca.getText(); }

    // ---- atualização da tela ----
    public void preencher(Cliente c) {
        txtNome.setText(c.getNome());
        txtCpf.setText(c.getCpf());
        txtTelefone.setText(c.getTelefone());
        txtEmail.setText(c.getEmail());
        txtPlaca.setText(c.getPlacaVeiculo());
    }

    public void limpar() {
        txtNome.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtPlaca.setText("");
        tabela.clearSelection();
        txtNome.requestFocusInWindow();
    }

    public void atualizarTabela(List<Cliente> clientes) {
        modelo.setRowCount(0);
        for (Cliente c : clientes) {
            modelo.addRow(new Object[] { c.getId(), c.getNome(), c.getCpf(), c.getTelefone(),
                    c.getEmail(), c.getPlacaVeiculo() });
        }
    }

    /** Id do cliente selecionado na tabela, ou -1. */
    public long getIdSelecionado() {
        return UI.idSelecionado(tabela, modelo);
    }

    // ---- ligação com o Controller ----
    public void addSalvarListener(ActionListener l) { btnSalvar.addActionListener(l); }
    public void addNovoListener(ActionListener l) { btnNovo.addActionListener(l); }
    public void addExcluirListener(ActionListener l) { btnExcluir.addActionListener(l); }
    public void addSelecaoListener(ListSelectionListener l) { tabela.getSelectionModel().addListSelectionListener(l); }

    // ---- mensagens ----
    public void mostrarErro(String mensagem) { UI.erro(this, mensagem); }
    public void mostrarInfo(String mensagem) { UI.info(this, mensagem); }
    public boolean confirmar(String mensagem) { return UI.confirmar(this, mensagem); }
}
