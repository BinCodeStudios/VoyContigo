package VoyContigo_terminalOnline.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import VoyContigo_terminalOnline.entity.Trabajador;

public interface TrabajadorRepository extends MongoRepository<Trabajador, String>{
    List<Trabajador> findByFlotaId(String flotaId);
    List<Trabajador> findByTipo(String tipo);
}
