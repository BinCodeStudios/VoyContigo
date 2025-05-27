package VoyContigo_terminalOnline.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import VoyContigo_terminalOnline.entity.Cliente;
import VoyContigo_terminalOnline.entity.Ticket;
import VoyContigo_terminalOnline.entity.Viaje;
import VoyContigo_terminalOnline.repository.ClienteRepository;
import VoyContigo_terminalOnline.repository.ViajeRepository;
import VoyContigo_terminalOnline.repository.TicketRepository;
import VoyContigo_terminalOnline.repository.rutaRepository;
import VoyContigo_terminalOnline.repository.flotaRepository;
import VoyContigo_terminalOnline.repository.OrigenDestinoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/clientes/viajes")
public class ViajesController {

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private rutaRepository rutaRepository;

    @Autowired
    private flotaRepository flotaRepository;

    @Autowired
    private OrigenDestinoRepository origenDestinoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Mostrar página de búsqueda de viajes
    @GetMapping("/buscar")
    public String buscarViajes(Model model, 
                              @RequestParam(value = "origen", required = false) String origen,
                              @RequestParam(value = "destino", required = false) String destino,
                              @RequestParam(value = "flota", required = false) String flota,
                              @RequestParam(value = "fecha", required = false) String fecha,
                              @RequestParam(value = "clienteId", required = false) String clienteId) {
        List<Viaje> viajes = viajeRepository.findAll();

        // Aplicar filtros
        if (origen != null && !origen.isEmpty()) {
            viajes = viajes.stream()
                    .filter(v -> v.getOrigen().getNombre().equalsIgnoreCase(origen))
                    .collect(Collectors.toList());
        }
        if (destino != null && !destino.isEmpty()) {
            viajes = viajes.stream()
                    .filter(v -> v.getDestino().getNombre().equalsIgnoreCase(destino))
                    .collect(Collectors.toList());
        }
        if (flota != null && !flota.isEmpty()) {
            viajes = viajes.stream()
                    .filter(v -> v.getFlota().getNombre().equalsIgnoreCase(flota))
                    .collect(Collectors.toList());
        }
        if (fecha != null && !fecha.isEmpty()) {
            LocalDateTime fechaFiltro = LocalDateTime.parse(fecha);
            viajes = viajes.stream()
                    .filter(v -> v.getFechaSalida().toLocalDate().equals(fechaFiltro.toLocalDate()))
                    .collect(Collectors.toList());
        }

        // Obtener información del cliente
        if (clienteId != null && !clienteId.trim().isEmpty()) {
            Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
            if (cliente != null) {
                model.addAttribute("usuarioNombre", cliente.getNombreCompleto());
                model.addAttribute("usuarioCorreo", cliente.getCorreo());
                model.addAttribute("clienteId", clienteId);
            } else {
                model.addAttribute("error", "Cliente no encontrado con ID: " + clienteId);
                model.addAttribute("usuarioNombre", "Invitado");
                model.addAttribute("usuarioCorreo", "");
                model.addAttribute("clienteId", "");
            }
        } else {
            model.addAttribute("usuarioNombre", "Invitado");
            model.addAttribute("usuarioCorreo", "");
            model.addAttribute("clienteId", "");
        }

        model.addAttribute("viajes", viajes);
        model.addAttribute("origenes", origenDestinoRepository.findAll());
        model.addAttribute("destinos", origenDestinoRepository.findAll());
        model.addAttribute("flotas", flotaRepository.findAll());
        return "clientes/buscar-viajes";
    }

    // Mostrar detalles de un viaje
    @GetMapping("/detalles/{id}")
    public String mostrarDetallesViaje(@PathVariable("id") String id, 
                                      @RequestParam(value = "clienteId", required = false) String clienteId, 
                                      Model model) {
        Viaje viaje = viajeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado: " + id));

        if (viaje.getRuta() == null) {
            throw new IllegalArgumentException("Ruta no encontrada para el viaje: " + id);
        }

        Integer capacidadBus = viaje.getBus() != null ? viaje.getBus().getCapacidad() : 0;
        List<Integer> asientosOcupados = ticketRepository.findByAsignacionId(id)
                .stream()
                .map(Ticket::getAsiento)
                .collect(Collectors.toList());

        // Obtener información del cliente
        if (clienteId != null && !clienteId.trim().isEmpty()) {
            Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
            if (cliente != null) {
                model.addAttribute("usuarioNombre", cliente.getNombreCompleto());
                model.addAttribute("usuarioCorreo", cliente.getCorreo());
                model.addAttribute("clienteId", clienteId);
            } else {
                model.addAttribute("error", "Cliente no encontrado con ID: " + clienteId);
                model.addAttribute("usuarioNombre", "Invitado");
                model.addAttribute("usuarioCorreo", "");
                model.addAttribute("clienteId", "");
            }
        } else {
            model.addAttribute("usuarioNombre", "Invitado");
            model.addAttribute("usuarioCorreo", "");
            model.addAttribute("clienteId", "");
        }

        model.addAttribute("viaje", viaje);
        model.addAttribute("capacidadBus", capacidadBus);
        model.addAttribute("asientosOcupados", asientosOcupados);

        return "clientes/detalles-viaje";
    }

    // Procesar la compra de tickets
    @PostMapping("/comprar")
    public String comprarViaje(@RequestParam("viajeId") String viajeId,
                               @RequestParam("clienteId") String clienteId,
                               @RequestParam("asientos") List<Integer> asientos,
                               Model model) {
        // Validar clienteId
        if (clienteId == null || clienteId.trim().isEmpty()) {
            model.addAttribute("error", "Debes iniciar sesión para comprar un ticket.");
            return "redirect:/admin/clientes/loginclientes";
        }

        // Verificar si el cliente existe
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente == null) {
            model.addAttribute("error", "Cliente no encontrado con ID: " + clienteId);
            Viaje viaje = viajeRepository.findById(viajeId)
                    .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado: " + viajeId));
            Integer capacidadBus = viaje.getBus() != null ? viaje.getBus().getCapacidad() : 0;
            List<Integer> asientosOcupados = ticketRepository.findByAsignacionId(viajeId)
                    .stream()
                    .map(Ticket::getAsiento)
                    .collect(Collectors.toList());
            model.addAttribute("viaje", viaje);
            model.addAttribute("capacidadBus", capacidadBus);
            model.addAttribute("asientosOcupados", asientosOcupados);
            model.addAttribute("usuarioNombre", "Invitado");
            model.addAttribute("usuarioCorreo", "");
            model.addAttribute("clienteId", "");
            return "clientes/detalles-viaje";
        }

        // Verificar el viaje
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado: " + viajeId));

        // Crear un ticket por cada asiento seleccionado
        for (Integer asiento : asientos) {
            Ticket ticket = new Ticket();
            ticket.setClienteId(clienteId);
            ticket.setAsignacionId(viajeId);
            ticket.setAsiento(asiento);
            ticket.setFechaCompra(LocalDateTime.now().toString());
            ticketRepository.save(ticket);
        }

        return "redirect:/user/clientes/dashboard?id=" + clienteId;
    }
}