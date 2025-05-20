package VoyContigo_terminalOnline.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import VoyContigo_terminalOnline.entity.Flota;

public interface flotaRepository extends MongoRepository<Flota, String> {
    // Buscar flota por nombre
    Flota findByNombre(String nombre);

    // Buscar flota por NIT
    Flota findByNit(String nit);

    // Verificar si existe una flota con el NIT dado
    boolean existsByNit(String nit);
}