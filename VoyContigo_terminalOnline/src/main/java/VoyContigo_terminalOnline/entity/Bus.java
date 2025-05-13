package VoyContigo_terminalOnline.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "buses")
public class Bus {
    @Id
    private String id;
    private String placa;
    private String modelo;
    private int capacidad;
    @DBRef
    private Flota flota;

    public Bus() {}

    public Bus(String placa, String modelo, int capacidad, String empresaId) {
        this.placa = placa;
        this.modelo = modelo;
        this.capacidad = capacidad;
        this.flota = flota;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public Flota getFlota() {
        return flota;
    }

    public void setFlota(Flota flota) {
        this.flota = flota;
    }
}