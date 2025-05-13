package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tickets")
public class Ticket {
    @Id
    private String id;
    private String clienteId;
    private String asignacionId;
    private int asiento;
    private String fechaCompra;

    public Ticket() {}

    public Ticket(String clienteId, String asignacionId, int asiento, String fechaCompra) {
        this.clienteId = clienteId;
        this.asignacionId = asignacionId;
        this.asiento = asiento;
        this.fechaCompra = fechaCompra;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getAsignacionId() { return asignacionId; }
    public void setAsignacionId(String asignacionId) { this.asignacionId = asignacionId; }
    public int getAsiento() { return asiento; }
    public void setAsiento(int asiento) { this.asiento = asiento; }
    public String getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(String fechaCompra) { this.fechaCompra = fechaCompra; }
}
