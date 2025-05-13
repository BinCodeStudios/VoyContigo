package VoyContigo_terminalOnline.repository;


import VoyContigo_terminalOnline.Controller.AdministradorGeneralController;
import VoyContigo_terminalOnline.entity.AdministradorGeneral;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdministradorGeneralRepository extends MongoRepository<AdministradorGeneral, String> {
	 boolean existsByUsername(String username);
	    AdministradorGeneral findByUsername(String username);
}