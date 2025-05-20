package VoyContigo_terminalOnline.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import VoyContigo_terminalOnline.entity.Ruta;

import java.util.List;

public interface rutaRepository extends MongoRepository<Ruta, String> {
    // Buscar rutas por ID de flota
    List<Ruta> findByFlotas_Id(String flotaId);

    // Buscar rutas por origen y destino
    List<Ruta> findByOrigenNombreAndDestinoNombre(String origenNombre, String destinoNombre);

    // Buscar rutas por origen
    List<Ruta> findByOrigenNombre(String origenNombre);

    // Buscar rutas por destino
    List<Ruta> findByDestinoNombre(String destinoNombre);
}