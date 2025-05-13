package VoyContigo_terminalOnline.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "rutas")
public class Ruta {
    @Id
    private String id;

    @DBRef
    private OrigenDestino origen;

    @DBRef
    private OrigenDestino destino;

    private String duracionEstimada;
    private double precio;
    @DBRef
    
    private List<Flota> flotas;

    public Ruta() {}

    public Ruta(OrigenDestino origen, OrigenDestino destino, String duracionEstimada, double precio) {
        this.origen = origen;
        this.destino = destino;
        this.duracionEstimada = duracionEstimada;
        this.precio = precio;
    }

    // Getters y setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public OrigenDestino getOrigen() { return origen; }
    public void setOrigen(OrigenDestino origen) { this.origen = origen; }

    public OrigenDestino getDestino() { return destino; }
    public void setDestino(OrigenDestino destino) { this.destino = destino; }

    public String getDuracionEstimada() { return duracionEstimada; }
    public void setDuracionEstimada(String duracionEstimada) { this.duracionEstimada = duracionEstimada; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public List<Flota> getFlotas() { return flotas; }
    public void setFlotas(List<Flota> flotas) { this.flotas = flotas; }
}
