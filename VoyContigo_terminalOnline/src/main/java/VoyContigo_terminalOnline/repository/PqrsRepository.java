package VoyContigo_terminalOnline.repository;

import VoyContigo_terminalOnline.entity.Pqrs;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PqrsRepository extends MongoRepository<Pqrs, String> {
    List<Pqrs> findByCliente_Id(String clienteId);
    List<Pqrs> findByEstado(String estado);
    List<Pqrs> findByTipo(String tipo);
    List<Pqrs> findByCliente_IdAndEstado(String clienteId, String estado);
}