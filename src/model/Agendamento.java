package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidade que representa um serviço agendado para um cliente.
 */
public class Agendamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private Cliente cliente;
    private TipoServico tipoServico;
    private LocalDateTime dataHora;
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    public Agendamento() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public TipoServico getTipoServico() { return tipoServico; }
    public void setTipoServico(TipoServico tipoServico) { this.tipoServico = tipoServico; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }
}
