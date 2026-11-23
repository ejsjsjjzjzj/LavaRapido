package app;

import java.nio.file.Paths;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import controller.AgendaController;
import controller.ClienteController;
import controller.TipoServicoController;
import model.service.AgendamentoService;
import model.service.ClienteService;
import model.service.Repositorio;
import model.service.TipoServicoService;
import model.service.ValidacaoException;
import view.AgendaView;
import view.ClienteView;
import view.MainView;
import view.TipoServicoView;

/**
 * Ponto de entrada: monta Model, Views e Controllers e abre a janela principal.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // mantém o visual padrão do Swing
            }

            // Model: persistência em arquivo (pasta "dados") e serviços de negócio
            Repositorio repositorio = Repositorio.carregar(Paths.get("dados", "lavarapido.dat"));
            ClienteService clienteService = new ClienteService(repositorio);
            TipoServicoService tipoServicoService = new TipoServicoService(repositorio);
            AgendamentoService agendamentoService = new AgendamentoService(repositorio);
            try {
                tipoServicoService.cadastrarPadroesSeVazio();
            } catch (ValidacaoException e) {
                System.err.println("Não foi possível cadastrar os serviços padrão: " + e.getMessage());
            }

            // Views
            AgendaView agendaView = new AgendaView();
            ClienteView clienteView = new ClienteView();
            TipoServicoView tipoServicoView = new TipoServicoView();

            // Controllers (ligam cada View ao seu Model)
            AgendaController agendaController =
                    new AgendaController(agendaView, agendamentoService, clienteService, tipoServicoService);
            new ClienteController(clienteView, clienteService, agendaController::aoAlterarDados);
            new TipoServicoController(tipoServicoView, tipoServicoService, agendaController::aoAlterarDados);

            new MainView(agendaView, clienteView, tipoServicoView).setVisible(true);
        });
    }
}
