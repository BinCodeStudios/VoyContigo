package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "consultas")
public class Consulta {
    @Id
    private String id;
    private String choferId;
    private String empresaId;
    private String mensaje;
    private String fecha;
    private String respuesta;
    private String respondidoPor;

    public Consulta() {}

    public Consulta(String choferId, String empresaId, String mensaje, String fecha, String respuesta, String respondidoPor) {
        this.choferId = choferId;
        this.empresaId = empresaId;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.respuesta = respuesta;
        this.respondidoPor = respondidoPor;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getChoferId() { return choferId; }
    public void setChoferId(String choferId) { this.choferId = choferId; }
    public String getEmpresaId() { return empresaId; }
    public void setEmpresaId(String empresaId) { this.empresaId = empresaId; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public String getRespondidoPor() { return respondidoPor; }
    public void setRespondidoPor(String respondidoPor) { this.respondidoPor = respondidoPor; }
}
