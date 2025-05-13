package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.LocalDateTime;

@Document(collection = "pqrs")
public class Pqrs {
    @Id
    private String id;
    
    @DBRef
    private Cliente cliente;
    
    private String tipo; // Queja, Reclamo, Sugerencia, Petición
    private String mensaje;
    private String fecha;
    private String respuesta;
    private String estado; // Abierto, En proceso, Cerrado

    public Pqrs() {}

    public Pqrs(Cliente cliente, String tipo, String mensaje, String estado) {
        this.cliente = cliente;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.fecha = LocalDateTime.now().toString();
        this.respuesta = "";
        this.estado = estado;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}