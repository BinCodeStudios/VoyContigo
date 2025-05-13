package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "trabajadores")
public class Trabajador {
    @Id
    private String id;

    private String nombre;
    private String apellido;
    private String correo;
    private String arl;
    private String tipoDocumento;
    private String numeroDocumento;
    private String contratoFileId;   // ID del archivo en GridFS (contrato)
    private String licenciaFileId;   // ID del archivo en GridFS (solo choferes)
    private String contrasena;
    private String tipo; // chofer, taquillero, etc.

    @DBRef
    private Flota flota;

    public Trabajador() {}

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getArl() {
        return arl;
    }

    public void setArl(String arl) {
        this.arl = arl;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getContratoFileId() {
        return contratoFileId;
    }

    public void setContratoFileId(String contratoFileId) {
        this.contratoFileId = contratoFileId;
    }

    public String getLicenciaFileId() {
        return licenciaFileId;
    }

    public void setLicenciaFileId(String licenciaFileId) {
        this.licenciaFileId = licenciaFileId;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Flota getFlota() {
        return flota;
    }

    public void setFlota(Flota flota) {
        this.flota = flota;
    }

    // Método opcional para mostrar nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
