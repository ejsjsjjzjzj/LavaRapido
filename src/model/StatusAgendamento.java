package model;

/**
 * Situações possíveis de um agendamento.
 */
public enum StatusAgendamento {
    AGENDADO("Agendado"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String rotulo;

    StatusAgendamento(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Texto amigável exibido na interface. */
    public String getRotulo() {
        return rotulo;
    }
}
