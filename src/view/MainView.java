package view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Janela principal: reúne as telas de agenda, clientes e tipos de serviço em abas.
 */
public class MainView extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainView(AgendaView agenda, ClienteView clientes, TipoServicoView tipos) {
        super("Lava Rápido - Controle de Clientes e Agenda de Serviços");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Agenda de Serviços", agenda);
        abas.addTab("Clientes", clientes);
        abas.addTab("Tipos de Serviço", tipos);
        add(abas);

        setSize(1000, 660);
        setLocationRelativeTo(null);
    }
}
