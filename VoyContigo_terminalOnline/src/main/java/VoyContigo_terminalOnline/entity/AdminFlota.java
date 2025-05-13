package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "admin_flota")
public class AdminFlota {
    @Id
    private String id;
    private String nombreCompleto;
    private String correo;
    private String contrasena;
    
    // Relación con Flota (puedes usar @DBRef o simplemente el ID como String)
    @DBRef
    private Flota flota;  // Referencia directa al documento Flota
    
    // O si prefieres solo almacenar el ID:
    // private String flotaId;

    public AdminFlota() {}

    // Constructor con referencia a Flota
    public AdminFlota(String nombreCompleto, String correo, String contrasena, Flota flota) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contrasena = contrasena;
        this.flota = flota;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Flota getFlota() { return flota; }
    public void setFlota(Flota flota) {
        this.flota = flota;
    }
	public Object getPassword() {
		// TODO Auto-generated method stub
		return null;
	}
}