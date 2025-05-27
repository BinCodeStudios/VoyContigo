package VoyContigo_terminalOnline.Controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import VoyContigo_terminalOnline.entity.Cliente;
import VoyContigo_terminalOnline.entity.Pqrs;
import VoyContigo_terminalOnline.entity.Ticket;
import VoyContigo_terminalOnline.entity.Viaje;
import VoyContigo_terminalOnline.entity.Ruta;
import VoyContigo_terminalOnline.entity.Bus;
import VoyContigo_terminalOnline.entity.Flota;
import VoyContigo_terminalOnline.repository.ClienteRepository;
import VoyContigo_terminalOnline.repository.PqrsRepository;
import VoyContigo_terminalOnline.repository.TicketRepository;
import VoyContigo_terminalOnline.repository.ViajeRepository;
import VoyContigo_terminalOnline.repository.rutaRepository;
import VoyContigo_terminalOnline.service.FileStorageService;
import VoyContigo_terminalOnline.repository.BusRepository;
import VoyContigo_terminalOnline.repository.flotaRepository;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.itextpdf.text.Chunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/clientes")
public class ClientesController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PqrsRepository pqrsRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private rutaRepository rutaRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private flotaRepository flotaRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GridFSBucket gridFSBucket;

    // Mostrar la lista de todos los clientes
    @GetMapping
    public String listarClientes(Model model, HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return "redirect:/user/clientes/loginclientes";
        }
        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);
        return "clientes/lista-clientes";
    }

    // Mostrar detalles de un cliente específico
    @GetMapping("/detalles/{id}")
    public String verDetallesCliente(@PathVariable("id") String id, Model model, HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return "redirect:/user/clientes/loginclientes";
        }
        if (!id.equals(session.getAttribute("clienteLoggedIn"))) {
            return "redirect:/user/clientes/loginclientes";
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        List<Pqrs> pqrs = pqrsRepository.findByCliente_Id(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("pqrsCount", pqrs.size());
        return "clientes/detalles-cliente";
    }

    // Mostrar perfil de un cliente
    @GetMapping("/porfile/{id}")
    public String verDetallesClienteOne(@PathVariable("id") String id, Model model, HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return "redirect:/user/clientes/loginclientes";
        }
        if (!id.equals(session.getAttribute("clienteLoggedIn"))) {
            return "redirect:/user/clientes/loginclientes";
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        List<Pqrs> pqrs = pqrsRepository.findByCliente_Id(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("pqrsCount", pqrs.size());
        return "clientes/perfil-cliente";
    }

    // Mostrar formulario para editar cliente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") String id, Model model, HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return "redirect:/user/clientes/loginclientes";
        }
        if (!id.equals(session.getAttribute("clienteLoggedIn"))) {
            return "redirect:/user/clientes/loginclientes";
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        model.addAttribute("cliente", cliente);
        return "clientes/editar-cliente";
    }

    // Manejar actualización de cliente
    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable("id") String id, @ModelAttribute("cliente") Cliente cliente,
                                    @RequestParam("fotoFile") MultipartFile fotoFile, Model model, HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return "redirect:/user/clientes/loginclientes";
        }
        if (!id.equals(session.getAttribute("clienteLoggedIn"))) {
            return "redirect:/user/clientes/loginclientes";
        }
        try {
            Cliente existingCliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));

            // Actualizar campos
            existingCliente.setNombreCompleto(cliente.getNombreCompleto());
            existingCliente.setCorreo(cliente.getCorreo());
            existingCliente.setContrasena(cliente.getContrasena());

            // Actualizar foto si se proporciona una nueva
            if (!fotoFile.isEmpty()) {
                String fotoFileId = fileStorageService.storeFile(fotoFile);
                existingCliente.setFotoFileID(fotoFileId);
            }

            clienteRepository.save(existingCliente);
            return "redirect:/user/clientes/porfile/" + id;
        } catch (MaxUploadSizeExceededException e) {
            model.addAttribute("error", "El archivo excede el tamaño máximo permitido de 10MB");
            model.addAttribute("cliente", cliente);
            return "clientes/editar-cliente";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar los datos: " + e.getMessage());
            model.addAttribute("cliente", cliente);
            return "clientes/editar-cliente";
        }
    }

    // Mostrar página de inicio de sesión
    @GetMapping("/loginclientes")
    public String showLoginPage(Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "registered", required = false) String registered) {
        model.addAttribute("error", error != null ? "Correo o contraseña incorrectos" : null);
        model.addAttribute("registered", registered != null ? "Registro exitoso. Inicia sesión." : null);
        return "clientes/loginclientes";
    }

    // Manejar inicio de sesión
    @PostMapping("/loginclientes")
    public String login(@RequestParam("correo") String correo,
                        @RequestParam("contrasena") String contrasena,
                        HttpSession session) {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente != null && contrasena.equals(cliente.getContrasena())) {
            session.setAttribute("clienteLoggedIn", cliente.getId());
            return "redirect:/user/clientes/dashboard?id=" + cliente.getId();
        }
        return "redirect:/user/clientes/loginclientes?error";
    }

    // Mostrar página de registro
    @GetMapping("/register")
    public String showRegisterPage(Model model, @RequestParam(value = "fileSizeError", required = false) String fileSizeError) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("fileSizeError", fileSizeError != null);
        return "clientes/register";
    }

    // Manejar registro
    @PostMapping("/register")
    public String registerCliente(@ModelAttribute Cliente cliente,
                                  @RequestParam("fotoFile") MultipartFile fotoFile) throws IOException {
        try {
            if (!fotoFile.isEmpty()) {
                String fotoFileId = fileStorageService.storeFile(fotoFile);
                cliente.setFotoFileID(fotoFileId);
            }
            clienteRepository.save(cliente);
            return "redirect:/user/clientes/loginclientes?registered";
        } catch (MaxUploadSizeExceededException e) {
            return "redirect:/user/clientes/register?fileSizeError";
        }
    }

    // Mostrar dashboard del cliente
    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam("id") String id, Model model, HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return "redirect:/user/clientes/loginclientes";
        }
        if (!id.equals(session.getAttribute("clienteLoggedIn"))) {
            return "redirect:/user/clientes/loginclientes";
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        List<Pqrs> pqrs = pqrsRepository.findByCliente_Id(id);
        List<Ticket> tickets = ticketRepository.findByClienteId(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("pqrs", pqrs);
        model.addAttribute("nuevoPqr", new Pqrs());
        model.addAttribute("tickets", tickets);
        return "clientes/dashboard";
    }

    // Obtener foto del cliente
    @GetMapping("/foto/{id}")
    public void obtenerFotoCliente(@PathVariable("id") String id, HttpServletResponse response, HttpSession session) throws IOException {
        if (session.getAttribute("clienteLoggedIn") == null) {
            response.sendRedirect("/user/clientes/loginclientes");
            return;
        }
        if (!id.equals(session.getAttribute("clienteLoggedIn"))) {
            response.sendRedirect("/user/clientes/loginclientes");
            return;
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (cliente.getFotoFileID() == null || cliente.getFotoFileID().isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Cliente no tiene foto");
            return;
        }

        ObjectId fileId = new ObjectId(cliente.getFotoFileID());

        try (GridFSDownloadStream stream = gridFSBucket.openDownloadStream(fileId);
             ServletOutputStream outputStream = response.getOutputStream()) {

            String contentType = "image/png";
            if (stream.getGridFSFile().getMetadata() != null) {
                String ct = stream.getGridFSFile().getMetadata().getString("contentType");
                if (ct != null && !ct.isEmpty()) {
                    contentType = ct;
                }
            }
            response.setContentType(contentType);

            IOUtils.copy(stream, outputStream);
            outputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener la foto");
        }
    }

    // Mostrar formulario para crear PQR
    @GetMapping("/pqr/crear")
    public String mostrarFormularioPqr(@RequestParam("id") String id, Model model, HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return "redirect:/user/clientes/loginclientes";
        }
        if (!id.equals(session.getAttribute("clienteLoggedIn"))) {
            return "redirect:/user/clientes/loginclientes";
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        model.addAttribute("cliente", cliente);
        model.addAttribute("pqr", new Pqrs());
        return "clientes/crear-pqr";
    }

    // Manejar creación de PQR
    @PostMapping("/pqr/crear")
    public String crearPqr(@RequestParam("id") String id, @ModelAttribute("pqr") Pqrs pqr, Model model, HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return "redirect:/user/clientes/loginclientes";
        }
        if (!id.equals(session.getAttribute("clienteLoggedIn"))) {
            return "redirect:/user/clientes/loginclientes";
        }
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        pqr.setCliente(cliente);
        pqr.setFecha(java.time.LocalDateTime.now().toString());
        pqr.setEstado("Abierto");
        pqr.setRespuesta("");
        pqrsRepository.save(pqr);
        return "redirect:/user/clientes/dashboard?id=" + id;
    }

    // Mostrar página de información del ticket
    @GetMapping("/ticket/{ticketId}")
    public String showTicketInfo(@PathVariable("ticketId") String ticketId,
                                 @RequestParam("clienteId") String clienteId,
                                 Model model, HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return "redirect:/user/clientes/loginclientes";
        }
        if (!clienteId.equals(session.getAttribute("clienteLoggedIn"))) {
            return "redirect:/user/clientes/loginclientes";
        }
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + clienteId));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + ticketId));

        if (!ticket.getClienteId().equals(clienteId)) {
            model.addAttribute("error", "El ticket no pertenece al cliente.");
            model.addAttribute("cliente", cliente);
            return "clientes/ticket-info";
        }

        Viaje viaje = viajeRepository.findById(ticket.getAsignacionId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado: " + ticket.getAsignacionId()));

        Ruta ruta = viaje.getRuta();
        if (ruta == null) {
            throw new IllegalArgumentException("Ruta no encontrada para el viaje: " + viaje.getId());
        }

        Bus bus = viaje.getBus();
        if (bus == null) {
            throw new IllegalArgumentException("Bus no encontrado para el viaje: " + viaje.getId());
        }

        Flota flota = viaje.getFlota();
        if (flota == null) {
            throw new IllegalArgumentException("Flota no encontrada para el viaje: " + viaje.getId());
        }

        model.addAttribute("cliente", cliente);
        model.addAttribute("ticket", ticket);
        model.addAttribute("viaje", viaje);
        model.addAttribute("ruta", ruta);
        model.addAttribute("bus", bus);
        model.addAttribute("flota", flota);

        return "clientes/ticket-info";
    }

    // Descargar recibo en PDF
    @GetMapping("/ticket/{ticketId}/pdf")
    public ResponseEntity<byte[]> descargarRecibo(@PathVariable("ticketId") String ticketId,
                                                  @RequestParam("clienteId") String clienteId,
                                                  HttpSession session) {
        if (session.getAttribute("clienteLoggedIn") == null) {
            return ResponseEntity.status(302).header("Location", "/user/clientes/loginclientes").build();
        }
        if (!clienteId.equals(session.getAttribute("clienteLoggedIn"))) {
            return ResponseEntity.status(302).header("Location", "/user/clientes/loginclientes").build();
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));

        if (!ticket.getClienteId().equals(clienteId)) {
            throw new IllegalArgumentException("El ticket no pertenece al cliente");
        }

        Viaje viaje = viajeRepository.findById(ticket.getAsignacionId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        Ruta ruta = viaje.getRuta();
        Bus bus = viaje.getBus();
        Flota flota = viaje.getFlota();

        try {
            byte[] pdfBytes = PdfReciboGenerator.generarRecibo(cliente, ticket, viaje, ruta, bus, flota);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "ticket_" + ticketId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    // Manejar cierre de sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/clientes/loginclientes";
    }
    // Procesar la compra de tickets
    @PostMapping("/viajes/comprar")
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

    // Clase interna para generar PDF
    public static class PdfReciboGenerator {
        public static byte[] generarRecibo(Cliente cliente, Ticket ticket, Viaje viaje, Ruta ruta, Bus bus, Flota flota) throws Exception {
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
            detalles.addCell(getCell(ruta.getOrigen().getNombre() + " - " + ruta.getDestino().getNombre(), normalFont));

            detalles.addCell(getCell("Bus:", headerFont));
            detalles.addCell(getCell(bus.getPlaca(), normalFont));

            detalles.addCell(getCell("Empresa:", headerFont));
            detalles.addCell(getCell(flota.getNombre(), normalFont));

            detalles.addCell(getCell("Fecha del Viaje:", headerFont));
            detalles.addCell(getCell(viaje.getFechaSalida().toString(), normalFont));

            detalles.addCell(getCell("Asiento:", headerFont));
            detalles.addCell(getCell(Integer.toString(ticket.getAsiento()), normalFont));

            detalles.addCell(getCell("Fecha Compra:", headerFont));
            detalles.addCell(getCell(ticket.getFechaCompra().toString(), normalFont));

            detalles.addCell(getCell("Precio:", headerFont));
            detalles.addCell(getCell("$" + ruta.getPrecio(), normalFont));

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