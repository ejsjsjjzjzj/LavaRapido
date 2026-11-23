package view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Constantes de formatação e métodos auxiliares compartilhados pelas Views.
 */
public final class UI {

    public static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    public static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private UI() {
    }

    /** Adiciona um par "rótulo + campo" em um painel com GridBagLayout. */
    public static void addLinha(JPanel painel, int linha, String rotulo, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.gridy = linha;
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        painel.add(new JLabel(rotulo), g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campo, g);
    }

    public static JPanel painelFormulario(String titulo) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(javax.swing.BorderFactory.createTitledBorder(titulo));
        return p;
    }

    /** Modelo de tabela somente leitura. */
    public static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    public static JTable criarTabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        return tabela;
    }

    /** Id (coluna 0) da linha selecionada, ou -1 se não houver seleção. */
    public static long idSelecionado(JTable tabela, DefaultTableModel modelo) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return -1;
        }
        return (Long) modelo.getValueAt(linha, 0);
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component pai, String mensagem) {
        return JOptionPane.showConfirmDialog(pai, mensagem, "Confirmação",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
