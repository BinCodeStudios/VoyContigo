package VoyContigo_terminalOnline.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import VoyContigo_terminalOnline.entity.Cliente;
import VoyContigo_terminalOnline.entity.Ticket;
import VoyContigo_terminalOnline.entity.Trabajador;
import VoyContigo_terminalOnline.entity.Viaje;
import VoyContigo_terminalOnline.entity.Bus;
import VoyContigo_terminalOnline.entity.Flota;
import VoyContigo_terminalOnline.repository.ClienteRepository;
import VoyContigo_terminalOnline.repository.TicketRepository;
import VoyContigo_terminalOnline.repository.TrabajadorRepository;
import VoyContigo_terminalOnline.repository.ViajeRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.Chunk;

import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trabajadores")
public class TrabajadorController {

    private static final Logger logger = LoggerFactory.getLogger(TrabajadorController.class);

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @GetMapping("/login")
    public String showLoginForm(jakarta.servlet.http.HttpSession session, Model model) {
        if (session.getAttribute("trabajadorId") != null) {
            String tipo = (String) session.getAttribute("trabajadorTipo");
            if ("chofer".equalsIgnoreCase(tipo)) {
                return "redirect:/trabajadores/chofer/dashboard";
            } else if ("taquillero".equalsIgnoreCase(tipo)) {
                return "redirect:/trabajadores/taquillero/dashboard";
            }
        }
        return "trabajadores/login-trabajador";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("correo") String correo, 
                              @RequestParam("contrasena") String contrasena, 
                              HttpSession session, Model model) {
        Trabajador trabajador = trabajadorRepository.findByCorreo(correo);
        if (trabajador != null && trabajador.getContrasena().equals(contrasena)) {
            session.setAttribute("trabajadorId", trabajador.getId());
            session.setAttribute("trabajadorTipo", trabajador.getTipo());
            session.setAttribute("trabajadorNombre", trabajador.getNombreCompleto());
            if ("chofer".equalsIgnoreCase(trabajador.getTipo())) {
                return "redirect:/trabajadores/chofer/dashboard";
            } else if ("taquillero".equalsIgnoreCase(trabajador.getTipo())) {
                return "redirect:/trabajadores/taquillero/dashboard";
            } else {
                model.addAttribute("error", "Tipo de trabajador no válido");
                return "trabajadores/login-trabajador";
            }
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "trabajadores/login-trabajador";
        }
    }


    @GetMapping("/taquillero/dashboard")
    public String showTaquilleroDashboard(@RequestParam(value = "clienteId", required = false) String clienteId, 
                                         HttpSession session, Model model) {
        if (session.getAttribute("trabajadorId") == null || 
            !"taquillero".equalsIgnoreCase((String) session.getAttribute("trabajadorTipo"))) {
            return "redirect:/trabajadores/login";
        }
        List<Ticket> tickets;
        if (clienteId != null && !clienteId.isEmpty()) {
            tickets = ticketRepository.findByClienteId(clienteId);
        } else {
            tickets = ticketRepository.findAll();
        }
        model.addAttribute("nombre", session.getAttribute("trabajadorNombre"));
        model.addAttribute("tickets", tickets != null ? tickets : Collections.emptyList());
        model.addAttribute("clienteId", clienteId);
        return "trabajadores/taquillero-dashboard";
    }

    @GetMapping("/taquillero/clientes")
    public String showClientesList(@RequestParam(value = "clienteId", required = false) String clienteId, 
                                  HttpSession session, Model model) {
        if (session.getAttribute("trabajadorId") == null || 
            !"taquillero".equalsIgnoreCase((String) session.getAttribute("trabajadorTipo"))) {
            return "redirect:/trabajadores/login";
        }
        List<Cliente> clientes;
        if (clienteId != null && !clienteId.isEmpty()) {
            clientes = clienteRepository.findById(clienteId)
                .map(List::of)
                .orElse(List.of());
        } else {
            clientes = clienteRepository.findAll();
        }
        model.addAttribute("nombre", session.getAttribute("trabajadorNombre"));
        model.addAttribute("clientes", clientes != null ? clientes : Collections.emptyList());
        model.addAttribute("clienteId", clienteId);
        return "trabajadores/clientes-lista";
    }

    @GetMapping("/taquillero/clientes/tickets/{id}")
    public String showClienteTickets(@PathVariable("id") String clienteId, HttpSession session, Model model) {
        if (session.getAttribute("trabajadorId") == null || 
            !"taquillero".equalsIgnoreCase((String) session.getAttribute("trabajadorTipo"))) {
            return "redirect:/trabajadores/login";
        }
        List<Ticket> tickets = ticketRepository.findByClienteId(clienteId);
        List<Cliente> clientes = clienteRepository.findById(clienteId)
            .map(List::of)
            .orElse(List.of());
        String mensaje = tickets.isEmpty() ? "El cliente no tiene tickets asociados." : null;
        model.addAttribute("nombre", session.getAttribute("trabajadorNombre"));
        model.addAttribute("clientes", clientes);
        model.addAttribute("mensaje", mensaje);
        return "trabajadores/clientes-lista";
    }

    @GetMapping("/taquillero/agregar-ticket")
    public String showAgregarTicketForm(@RequestParam(value = "clienteId", required = false) String clienteId,
                                       HttpSession session, Model model) {
        if (session.getAttribute("trabajadorId") == null || 
            !"taquillero".equalsIgnoreCase((String) session.getAttribute("trabajadorTipo"))) {
            return "redirect:/trabajadores/login";
        }
        List<Cliente> clientes = clienteRepository.findAll();
        logger.debug("Clientes cargados: {}", clientes != null ? clientes.size() : 0);
        model.addAttribute("nombre", session.getAttribute("trabajadorNombre"));
        model.addAttribute("clientes", clientes != null ? clientes : Collections.emptyList());
        if (clienteId != null && !clienteId.isEmpty()) {
            List<Viaje> viajes = viajeRepository.findAll();
            logger.debug("Viajes cargados para clienteId {}: {}", clienteId, viajes != null ? viajes.size() : 0);
            model.addAttribute("clienteId", clienteId);
            model.addAttribute("viajes", viajes != null ? viajes : Collections.emptyList());
        }
        return "trabajadores/agregar-ticket";
    }

    @GetMapping("/taquillero/agregar-ticket/viajes")
    public String showAgregarTicketViajes(@RequestParam("clienteId") String clienteId,
                                         HttpSession session, Model model) {
        if (session.getAttribute("trabajadorId") == null || 
            !"taquillero".equalsIgnoreCase((String) session.getAttribute("trabajadorTipo"))) {
            return "redirect:/trabajadores/login";
        }
        logger.debug("Cargando viajes para clienteId: {}", clienteId);
        model.addAttribute("nombre", session.getAttribute("trabajadorNombre"));
        model.addAttribute("clienteId", clienteId);
        
        try {
            Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            List<Viaje> viajes = viajeRepository.findAll();
            logger.debug("Viajes encontrados: {}", viajes != null ? viajes.size() : 0);
            model.addAttribute("cliente", cliente);
            model.addAttribute("viajes", viajes != null ? viajes : Collections.emptyList());
        } catch (IllegalArgumentException e) {
            logger.error("Error al cargar cliente: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        
        return "trabajadores/agregar-ticket";
    }

    @GetMapping("/taquillero/agregar-ticket/detalles")
    public String showViajeDetalles(@RequestParam("clienteId") String clienteId,
                                   @RequestParam("viajeId") String viajeId,
                                   HttpSession session, Model model) {
        if (session.getAttribute("trabajadorId") == null || 
            !"taquillero".equalsIgnoreCase((String) session.getAttribute("trabajadorTipo"))) {
            return "redirect:/trabajadores/login";
        }
        logger.debug("Cargando detalles para clienteId: {}, viajeId: {}", clienteId, viajeId);
        try {
            Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
            List<Ticket> tickets = ticketRepository.findByAsignacionId(viajeId);
            List<Integer> asientosOcupados = tickets != null ? tickets.stream()
                .map(Ticket::getAsiento)
                .collect(Collectors.toList()) : Collections.emptyList();
            logger.debug("Asientos ocupados: {}", asientosOcupados.size());
            model.addAttribute("cliente", cliente);
            model.addAttribute("clienteId", clienteId);
            model.addAttribute("viaje", viaje);
            model.addAttribute("asientosOcupados", asientosOcupados);
            model.addAttribute("capacidadBus", viaje.getBus() != null && viaje.getBus().getCapacidad() > 0 ? viaje.getBus().getCapacidad() : 1);
            model.addAttribute("nombre", session.getAttribute("trabajadorNombre"));
        } catch (IllegalArgumentException e) {
            logger.error("Error al cargar detalles: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "trabajadores/agregar-ticket";
        }
        return "trabajadores/detalles-viaje-taquillero";
    }

    @PostMapping("/taquillero/agregar-ticket")
    public String crearTicket(@RequestParam("clienteId") String clienteId,
                             @RequestParam("viajeId") String viajeId,
                             @RequestParam("asientos") List<Integer> asientos,
                             HttpSession session, Model model) {
        if (session.getAttribute("trabajadorId") == null || 
            !"taquillero".equalsIgnoreCase((String) session.getAttribute("trabajadorTipo"))) {
            return "redirect:/trabajadores/login";
        }
        logger.debug("Creando ticket para clienteId: {}, viajeId: {}, asientos: {}", clienteId, viajeId, asientos);
        try {
            Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
            
            // Verificar asientos ocupados
            List<Ticket> existingTickets = ticketRepository.findByAsignacionId(viajeId);
            List<Integer> occupiedSeats = existingTickets != null ? existingTickets.stream()
                .map(Ticket::getAsiento)
                .collect(Collectors.toList()) : Collections.emptyList();
            for (Integer asiento : asientos) {
                if (occupiedSeats.contains(asiento)) {
                    logger.warn("Asiento ocupado detectado: {}", asiento);
                    model.addAttribute("error", "El asiento " + asiento + " ya está ocupado.");
                    return showViajeDetalles(clienteId, viajeId, session, model);
                }
            }

            // Verificar capacidad del bus
            int capacidad = viaje.getBus() != null && viaje.getBus().getCapacidad() > 0 ? viaje.getBus().getCapacidad() : 1;
            for (Integer asiento : asientos) {
                if (asiento < 1 || asiento > capacidad) {
                    logger.warn("Asiento inválido: {}", asiento);
                    model.addAttribute("error", "El asiento " + asiento + " es inválido.");
                    return showViajeDetalles(clienteId, viajeId, session, model);
                }
            }

            // Crear un ticket por cada asiento
            for (Integer asiento : asientos) {
                Ticket ticket = new Ticket(clienteId, viajeId, asiento, LocalDateTime.now().toString());
                ticketRepository.save(ticket);
                logger.info("Ticket creado: clienteId={}, viajeId={}, asiento={}", clienteId, viajeId, asiento);
            }
            return "redirect:/trabajadores/taquillero/dashboard";
        } catch (IllegalArgumentException e) {
            logger.error("Error al crear ticket: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return showViajeDetalles(clienteId, viajeId, session, model);
        }
    }

    @GetMapping("/taquillero/ticket/{ticketId}/pdf")
    public ResponseEntity<byte[]> descargarRecibo(@PathVariable("ticketId") String ticketId,
                                                 @RequestParam("clienteId") String clienteId,
                                                 HttpSession session) {
        if (session.getAttribute("trabajadorId") == null || 
            !"taquillero".equalsIgnoreCase((String) session.getAttribute("trabajadorTipo"))) {
            return ResponseEntity.status(302).header("Location", "/trabajadores/login").build();
        }
        logger.debug("Generando PDF para ticketId: {}, clienteId: {}", ticketId, clienteId);
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));
        if (!ticket.getClienteId().equals(clienteId)) {
            throw new IllegalArgumentException("El ticket no pertenece al cliente");
        }
        Viaje viaje = viajeRepository.findById(ticket.getAsignacionId())
            .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
        Bus bus = viaje.getBus();
        Flota flota = viaje.getFlota();
        try {
            byte[] pdfBytes = PdfReciboGenerator.generarRecibo(cliente, ticket, viaje, bus, flota);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "ticket_" + ticketId + ".pdf");
            logger.info("PDF generado para ticketId: {}", ticketId);
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
        } catch (Exception e) {
            logger.error("Error al generar PDF: {}", e.getMessage());
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/trabajadores/login";
    }

    public static class PdfReciboGenerator {
        public static byte[] generarRecibo(Cliente cliente, Ticket ticket, Viaje viaje, Bus bus, Flota flota) throws Exception {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A6, 20, 20, 20, 20);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);

            Paragraph title = new Paragraph("RECIBO DE COMPRA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            doc.add(title);

            LineSeparator ls = new LineSeparator();
            ls.setLineColor(BaseColor.LIGHT_GRAY);
            doc.add(new Chunk(ls));

            PdfPTable infoCliente = new PdfPTable(2);
            infoCliente.setWidthPercentage(100);
            infoCliente.setSpacingBefore(10f);
            infoCliente.setSpacingAfter(10f);
            infoCliente.setWidths(new float[]{1.5f, 3f});

            infoCliente.addCell(getCell("Cliente:", headerFont));
            infoCliente.addCell(getCell(cliente.getNombreCompleto(), normalFont));
            infoCliente.addCell(getCell("Correo:", headerFont));
            infoCliente.addCell(getCell(cliente.getCorreo(), normalFont));
            infoCliente.addCell(getCell("Ticket ID:", headerFont));
            infoCliente.addCell(getCell(ticket.getId(), normalFont));

            doc.add(infoCliente);

            PdfPTable detalles = new PdfPTable(2);
            detalles.setWidthPercentage(100);
            detalles.setWidths(new float[]{2f, 3f});

            detalles.addCell(getCell("Ruta:", headerFont));
            detalles.addCell(getCell((viaje.getOrigen() != null ? viaje.getOrigen().getNombre() : "N/A") + " - " + 
                                    (viaje.getDestino() != null ? viaje.getDestino().getNombre() : "N/A"), normalFont));

            detalles.addCell(getCell("Bus:", headerFont));
            detalles.addCell(getCell(bus != null ? bus.getPlaca() : "N/A", normalFont));

            detalles.addCell(getCell("Empresa:", headerFont));
            detalles.addCell(getCell(flota != null ? flota.getNombre() : "N/A", normalFont));

            detalles.addCell(getCell("Fecha del Viaje:", headerFont));
            detalles.addCell(getCell(viaje.getFechaSalida() != null ? viaje.getFechaSalida().toString() : "N/A", normalFont));

            detalles.addCell(getCell("Asiento:", headerFont));
            detalles.addCell(getCell(Integer.toString(ticket.getAsiento()), normalFont));

            detalles.addCell(getCell("Fecha Compra:", headerFont));
            detalles.addCell(getCell(ticket.getFechaCompra() != null ? ticket.getFechaCompra().toString() : "N/A", normalFont));

            detalles.addCell(getCell("Precio:", headerFont));

            doc.add(detalles);

            Paragraph gracias = new Paragraph("¡Gracias por su compra!", normalFont);
            gracias.setAlignment(Element.ALIGN_CENTER);
            gracias.setSpacingBefore(15f);
            doc.add(gracias);

            Paragraph pie = new Paragraph("Contacto: info@tuempresa.com", smallFont);
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(5f);
            doc.add(pie);

            doc.close();
            return baos.toByteArray();
        }

        private static PdfPCell getCell(String text, Font font) {
            PdfPCell cell = new PdfPCell(new Phrase(text, font));
            cell.setBorder(Rectangle.NO_BORDER);
            return cell;
        }
    }
}