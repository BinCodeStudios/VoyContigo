package VoyContigo_terminalOnline.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import VoyContigo_terminalOnline.entity.Flota;

public interface flotaRepository extends MongoRepository<Flota, String> {
	boolean existsByNit(String nit);
    Optional<Flota> findByNit(String nit);
}