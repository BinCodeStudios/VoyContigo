package VoyContigo_terminalOnline.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import VoyContigo_terminalOnline.entity.Bus;

public interface BusRepository extends MongoRepository<Bus, String> {
    // Aquí puedes agregar consultas personalizadas si es necesario
	List<Bus> findByFlotaId(String flotaId);

}
