package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Flota")
public class Flota {
    @Id
    private String id;
    private String nombre;
    private String nit;
    private String direccion;
    private String telefono;
    private String logoFileID;


    public Flota() {}

    public Flota(String nombre, String nit, String direccion, String telefono, String logoFileID) {
        this.nombre = nombre;
        this.nit = nit;
        this.direccion = direccion;
        this.telefono = telefono;
        this.logoFileID = logoFileID;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

	public String getLogoFileID() {
		return logoFileID;
	}

	public void setLogoFileID(String logoFileID) {
		this.logoFileID = logoFileID;
	}
}
