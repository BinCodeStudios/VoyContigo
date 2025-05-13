package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "asignaciones")
public class Asignacion {
    @Id
    private String id;
    private String busId;
    private String rutaId;
    private String choferId;
    private String taquilleroId;
    private String fechaSalida;
    private String horaSalida;

    public Asignacion() {}

    public Asignacion(String busId, String rutaId, String choferId, String taquilleroId, String fechaSalida, String horaSalida) {
        this.busId = busId;
        this.rutaId = rutaId;
        this.choferId = choferId;
        this.taquilleroId = taquilleroId;
        this.fechaSalida = fechaSalida;
        this.horaSalida = horaSalida;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    public String getRutaId() { return rutaId; }
    public void setRutaId(String rutaId) { this.rutaId = rutaId; }
    public String getChoferId() { return choferId; }
    public void setChoferId(String choferId) { this.choferId = choferId; }
    public String getTaquilleroId() { return taquilleroId; }
    public void setTaquilleroId(String taquilleroId) { this.taquilleroId = taquilleroId; }
    public String getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }
    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }
}
