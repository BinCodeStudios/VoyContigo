package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "origen_destino")
public class OrigenDestino {
    @Id
    private String id;
    private String nombre;

    public OrigenDestino() {}

    public OrigenDestino(String nombre) {
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}