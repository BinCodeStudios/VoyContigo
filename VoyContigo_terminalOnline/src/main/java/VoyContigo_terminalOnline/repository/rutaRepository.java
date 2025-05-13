package VoyContigo_terminalOnline.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import VoyContigo_terminalOnline.entity.Flota;
import VoyContigo_terminalOnline.entity.Ruta;
import java.util.List;

public interface rutaRepository extends MongoRepository<Ruta, String>{
	List<Ruta> findByFlotas_Id(String flotaId);



}
