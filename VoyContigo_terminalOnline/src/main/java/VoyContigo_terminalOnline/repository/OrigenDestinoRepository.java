package VoyContigo_terminalOnline.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import VoyContigo_terminalOnline.entity.OrigenDestino;

public interface OrigenDestinoRepository extends MongoRepository<OrigenDestino, String> {
}