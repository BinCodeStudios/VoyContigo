package VoyContigo_terminalOnline.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import VoyContigo_terminalOnline.entity.Ticket;

import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    // Buscar tickets por ID de cliente
    List<Ticket> findByClienteId(String clienteId);

    // Buscar tickets por ID de asignación (viaje)
    List<Ticket> findByAsignacionId(String asignacionId);
}