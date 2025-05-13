package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clientes")
public class Cliente {
    @Id
    private String id;
    private String nombreCompleto;
    private String correo;
    private String contrasena;
    private String foto;

    public Cliente() {}

    public Cliente(String nombreCompleto, String correo, String contrasena, String foto) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contrasena = contrasena;
        this.foto = foto;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}