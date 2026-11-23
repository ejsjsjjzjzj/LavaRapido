package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import model.Agendamento;
import model.Cliente;
import model.TipoServico;

/**
 * Tela da agenda de serviços: formulário de agendamento, filtro diário/semanal
 * e tabela com os serviços agendados.
 */
public class AgendaView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private final JComboBox<TipoServico> cmbTipo = new JComboBox<>();
    private final JTextField txtData = new JTextField(10);
    private final JTextField txtHora = new JTextField(6);

    private final JButton btnAgendar = new JButton("Agendar");
    private final JButton btnSalvarAlteracao = new JButton("Salvar alteração");
    private final JButton btnConcluir = new JButton("Marcar como concluído");
    private final JButton btnCancelar = new JButton("Cancelar agendamento");
    private final JButton btnLimpar = new JButton("Limpar");

    private final JTextField txtDataFiltro = new JTextField(10);
    private final JComboBox<String> cmbVisao = new JComboBox<>(new String[] { "Diária", "Semanal" });
    private final JButton btnAtualizar = new JButton("Atualizar");
    private final JButton btnHoje = new JButton("Hoje");
    private final JLabel lblResumo = new JLabel(" ");

    private final DefaultTableModel modelo =
            UI.modelo("ID", "Data", "Hora", "Cliente", "Placa", "Serviço", "Valor", "Status");
    private final JTable tabela = UI.criarTabela(modelo);

    public AgendaView() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulário de agendamento
        JPanel form = UI.painelFormulario("Agendamento (selecione uma linha para editar ou cancelar)");
        UI.addLinha(form, 0, "Cliente:", cmbCliente);
        UI.addLinha(form, 1, "Tipo de serviço:", cmbTipo);
        JPanel dataHora = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dataHora.add(new JLabel("Data (dd/mm/aaaa): "));
        dataHora.add(txtData);
        dataHora.add(new JLabel("   Hora (hh:mm): "));
        dataHora.add(txtHora);
        UI.addLinha(form, 2, "Quando:", dataHora);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnAgendar);
        botoes.add(btnSalvarAlteracao);
        botoes.add(btnConcluir);
        botoes.add(btnCancelar);
        botoes.add(btnLimpar);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        // Filtro e tabela
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.add(new JLabel("Ver agenda:"));
        filtro.add(cmbVisao);
        filtro.add(new JLabel("a partir de (dd/mm/aaaa):"));
        filtro.add(txtDataFiltro);
        filtro.add(btnAtualizar);
        filtro.add(btnHoje);

        JPanel centro = new JPanel(new BorderLayout(4, 4));
        centro.add(filtro, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabela), BorderLayout.CENTER);
        centro.add(lblResumo, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(1).setMaxWidth(90);
        tabela.getColumnModel().getColumn(2).setMaxWidth(60);
    }

    // ---- leitura dos campos ----
    public Cliente getClienteSelecionado() { return (Cliente) cmbCliente.getSelectedItem(); }
    public TipoServico getTipoSelecionado() { return (TipoServico) cmbTipo.getSelectedItem(); }
    public String getData() { return txtData.getText().trim(); }
    public String getHora() { return txtHora.getText().trim(); }
    public String getDataFiltro() { return txtDataFiltro.getText().trim(); }
    public boolean isVisaoSemanal() { return cmbVisao.getSelectedIndex() == 1; }

    // ---- atualização da tela ----
    public void setDataFiltro(String data) { txtDataFiltro.setText(data); }

    public void setClientes(List<Cliente> clientes) {
        cmbCliente.removeAllItems();
        for (Cliente c : clientes) {
            cmbCliente.addItem(c);
        }
    }

    public void setTipos(List<TipoServico> tipos) {
        cmbTipo.removeAllItems();
        for (TipoServico t : tipos) {
            cmbTipo.addItem(t);
        }
    }

    /** Preenche o formulário com os dados do agendamento selecionado. */
    public void preencher(Agendamento a) {
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i).getId() == a.getCliente().getId()) {
                cmbCliente.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < cmbTipo.getItemCount(); i++) {
            if (cmbTipo.getItemAt(i).getId() == a.getTipoServico().getId()) {
                cmbTipo.setSelectedIndex(i);
            }
        }
        LocalDateTime dh = a.getDataHora();
        txtData.setText(dh.format(UI.DATA));
        txtHora.setText(dh.format(UI.HORA));
    }

    public void limpar() {
        txtData.setText("");
        txtHora.setText("");
        tabela.clearSelection();
    }

    public void atualizarTabela(List<Agendamento> agendamentos, String resumo) {
        modelo.setRowCount(0);
        for (Agendamento a : agendamentos) {
            modelo.addRow(new Object[] {
                    a.getId(),
                    a.getDataHora().format(UI.DATA),
                    a.getDataHora().format(UI.HORA),
                    a.getCliente().getNome(),
                    a.getCliente().getPlacaVeiculo(),
                    a.getTipoServico().getNome(),
                    UI.MOEDA.format(a.getTipoServico().getPreco()),
                    a.getStatus().getRotulo() });
        }
        lblResumo.setText(resumo);
    }

    public long getIdSelecionado() {
        return UI.idSelecionado(tabela, modelo);
    }

    // ---- ligação com o Controller ----
    public void addAgendarListener(ActionListener l) { btnAgendar.addActionListener(l); }
    public void addSalvarAlteracaoListener(ActionListener l) { btnSalvarAlteracao.addActionListener(l); }
    public void addConcluirListener(ActionListener l) { btnConcluir.addActionListener(l); }
    public void addCancelarListener(ActionListener l) { btnCancelar.addActionListener(l); }
    public void addLimparListener(ActionListener l) { btnLimpar.addActionListener(l); }
    public void addAtualizarListener(ActionListener l) { btnAtualizar.addActionListener(l); }
    public void addHojeListener(ActionListener l) { btnHoje.addActionListener(l); }
    public void addSelecaoListener(ListSelectionListener l) { tabela.getSelectionModel().addListSelectionListener(l); }

    // ---- mensagens ----
    public void mostrarErro(String mensagem) { UI.erro(this, mensagem); }
    public void mostrarInfo(String mensagem) { UI.info(this, mensagem); }
    public boolean confirmar(String mensagem) { return UI.confirmar(this, mensagem); }
}
