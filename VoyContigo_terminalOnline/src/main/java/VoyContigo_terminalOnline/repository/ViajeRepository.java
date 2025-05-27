package VoyContigo_terminalOnline.repository;

import VoyContigo_terminalOnline.entity.Bus;
import VoyContigo_terminalOnline.entity.Trabajador;
import VoyContigo_terminalOnline.entity.Viaje;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ViajeRepository extends MongoRepository<Viaje, String> {
    // Buscar viajes por ID de flota
    List<Viaje> findByFlotaId(String flotaId);

    // Buscar viajes por estado
    List<Viaje> findByEstado(String estado);

    // Buscar viajes por ID de bus
    List<Viaje> findByBusId(String busId);

    // Buscar viajes por origen y destino
    List<Viaje> findByOrigenNombreAndDestinoNombre(String origen, String destino);

    // Buscar viajes por fecha de salida (rango)
    List<Viaje> findByFechaSalidaBetween(LocalDateTime start, LocalDateTime end);

    // Buscar viajes por nombre de flota
    List<Viaje> findByFlotaNombre(String flotaNombre);

    // Buscar viajes por origen
    List<Viaje> findByOrigenNombre(String origenNombre);

    // Buscar viajes por destino
    List<Viaje> findByDestinoNombre(String destinoNombre);

    // Método heredado de MongoRepository para buscar múltiples IDs
    List<Viaje> findAllById(Iterable<String> ids);
    
    List<Viaje> findByChoferAndFechaSalidaBetween(Trabajador chofer, LocalDateTime inicio, LocalDateTime fin);
    List<Viaje> findByBusIdAndFechaSalidaBetween(String busId, LocalDateTime inicio, LocalDateTime fin);
    List<Viaje> findByBus_IdAndFechaSalidaBetween(String busId, Date inicio, Date fin);
    List<Viaje> findByBus_Id(String busId);

    List<Viaje> findByChoferId(String choferId);


}