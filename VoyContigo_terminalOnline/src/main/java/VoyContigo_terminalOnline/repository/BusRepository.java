package VoyContigo_terminalOnline.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import VoyContigo_terminalOnline.entity.Bus;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends MongoRepository<Bus, String> {
    // Buscar buses por ID de flota
    List<Bus> findByFlotaId(String flotaId);

    // Buscar buses por placa
    Bus findByPlaca(String placa);
    

    Optional<Bus> findByChoferId(String choferId);

}