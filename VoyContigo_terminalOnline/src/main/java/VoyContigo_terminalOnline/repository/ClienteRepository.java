package VoyContigo_terminalOnline.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import VoyContigo_terminalOnline.entity.Cliente;

public interface ClienteRepository extends MongoRepository<Cliente, String> {
    Cliente findByCorreo(String correo);
}