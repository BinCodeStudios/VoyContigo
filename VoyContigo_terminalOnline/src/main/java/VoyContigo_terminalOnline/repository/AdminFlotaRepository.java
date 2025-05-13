package VoyContigo_terminalOnline.repository;

import VoyContigo_terminalOnline.entity.AdminFlota;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


public interface AdminFlotaRepository extends MongoRepository<AdminFlota, String> {
    boolean existsByCorreo(String correo);
    AdminFlota findByCorreo(String correo);
}