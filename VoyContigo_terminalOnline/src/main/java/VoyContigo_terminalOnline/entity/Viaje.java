package VoyContigo_terminalOnline.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "viajes")
public class Viaje {

    @Id
    private String id;

    @DBRef
    private Flota flota;

    @DBRef
    private Bus bus;

    @DBRef
    private Ruta ruta;

    @DBRef
    private OrigenDestino origen;

    @DBRef
    private OrigenDestino destino;

    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegadaEstimada;

    private String estado; // En lugar de un enum, usamos String para gestionarlo desde el frontend

    private String observaciones;

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Flota getFlota() {
        return flota;
    }

    public void setFlota(Flota flota) {
        this.flota = flota;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public OrigenDestino getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenDestino origen) {
        this.origen = origen;
    }

    public OrigenDestino getDestino() {
        return destino;
    }

    public void setDestino(OrigenDestino destino) {
        this.destino = destino;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public LocalDateTime getFechaLlegadaEstimada() {
        return fechaLlegadaEstimada;
    }

    public void setFechaLlegadaEstimada(LocalDateTime fechaLlegadaEstimada) {
        this.fechaLlegadaEstimada = fechaLlegadaEstimada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
