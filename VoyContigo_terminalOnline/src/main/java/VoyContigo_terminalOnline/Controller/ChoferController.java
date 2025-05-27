package VoyContigo_terminalOnline.Controller;

import VoyContigo_terminalOnline.entity.Trabajador;
import VoyContigo_terminalOnline.entity.Viaje;
import VoyContigo_terminalOnline.entity.Bus;
import VoyContigo_terminalOnline.entity.Cliente;
import VoyContigo_terminalOnline.entity.Flota;
import VoyContigo_terminalOnline.entity.Ticket;
import VoyContigo_terminalOnline.repository.TrabajadorRepository;
import VoyContigo_terminalOnline.repository.ViajeRepository;
import VoyContigo_terminalOnline.repository.BusRepository;
import VoyContigo_terminalOnline.repository.ClienteRepository;
import VoyContigo_terminalOnline.repository.TicketRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/trabajadores")
public class ChoferController {

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private BusRepository busRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/chofer/dashboard")
    public String showChoferDashboard(HttpSession session, Model model) {
        if (session.getAttribute("trabajadorId") == null ||
            !"chofer".equalsIgnoreCase((String) session.getAttribute("trabajadorTipo"))) {
            return "redirect:/trabajadores/login";
        }

        String trabajadorId = (String) session.getAttribute("trabajadorId");
        
        // Buscar bus asignado al chofer
        Optional<Bus> busOpt = busRepository.findByChoferId(trabajadorId);
        if (busOpt.isEmpty()) {
            model.addAttribute("mensaje", "No tiene un bus asignado.");
            return "trabajadores/chofer/chofer-dashboard";
        }

        Bus bus = busOpt.get();
        model.addAttribute("bus", bus);

        // Buscar viajes asignados al bus (opcionalmente filtrar por fecha)
        List<Viaje> viajesAsignados = viajeRepository.findByBusId(bus.getId());
        model.addAttribute("viajesAsignados", viajesAsignados);

        model.addAttribute("nombre", session.getAttribute("trabajadorNombre"));

        return "trabajadores/chofer/chofer-dashboard";
    }

    @GetMapping("/chofer/viaje/pdf")
    public ResponseEntity<byte[]> descargarDetalleViaje(HttpSession session) {
        String trabajadorId = (String) session.getAttribute("trabajadorId");

        if (trabajadorId == null) {
            return ResponseEntity.status(302).header("Location", "/trabajadores/login").build();
        }

        // Obtener el bus asignado al chofer
        Bus bus = busRepository.findByChoferId(trabajadorId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un bus asignado para este chofer."));

        // Obtener el viaje asignado al bus (asumiendo un viaje activo por día)
        Date inicioDia = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date finDia = Date.from(LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        Viaje viaje = viajeRepository.findByBus_Id(bus.getId())
        	    .stream()
        	    .findFirst()
        	    .orElseThrow(() -> new IllegalArgumentException("No hay un viaje asignado para este bus."));

        // Obtener los tickets del viaje
        List<Ticket> tickets = ticketRepository.findByAsignacionId(viaje.getId());

        // Mapear los clientes por asiento
        Map<Integer, Cliente> clientesPorAsiento = new HashMap<>();
        for (Ticket ticket : tickets) {
            if (ticket.getClienteId() != null) {
                Cliente cliente = clienteRepository.findById(ticket.getClienteId()).orElse(null);
                if (cliente != null) {
                    clientesPorAsiento.put(ticket.getAsiento(), cliente);
                }
            }
        }

        try {
            byte[] pdfBytes = PdfReciboGenerator.generarReciboViajeConPasajeros(
                    viaje, bus, bus.getFlota(), tickets, clientesPorAsiento);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "detalle_viaje_" + viaje.getId() + ".pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }
    }


    public static class PdfReciboGenerator {
        public static byte[] generarReciboViajeConPasajeros(Viaje viaje, Bus bus, Flota flota, List<Ticket> tickets, Map<Integer, Cliente> clientesPorAsiento) throws Exception {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A6, 20, 20, 20, 20);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);

            Paragraph title = new Paragraph("DETALLE DEL VIAJE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            doc.add(title);

            LineSeparator ls = new LineSeparator();
            ls.setLineColor(BaseColor.LIGHT_GRAY);
            doc.add(new Chunk(ls));

            PdfPTable infoViaje = new PdfPTable(2);
            infoViaje.setWidthPercentage(100);
            infoViaje.setSpacingBefore(10f);
            infoViaje.setSpacingAfter(10f);
            infoViaje.setWidths(new float[]{2f, 3f});

            infoViaje.addCell(getCell("Ruta:", headerFont));
            infoViaje.addCell(getCell(viaje.getRuta().getOrigen().getNombre() + " - " + viaje.getRuta().getDestino().getNombre(), normalFont));

            infoViaje.addCell(getCell("Bus:", headerFont));
            infoViaje.addCell(getCell(bus.getPlaca() + " (" + bus.getModelo() + ")", normalFont));

            infoViaje.addCell(getCell("Empresa:", headerFont));
            infoViaje.addCell(getCell(flota.getNombre(), normalFont));

            infoViaje.addCell(getCell("Fecha Salida:", headerFont));
            infoViaje.addCell(getCell(viaje.getFechaSalida().toString(), normalFont));

            doc.add(infoViaje);

            // Tabla de asientos y pasajeros
            Paragraph pAsientos = new Paragraph("Pasajeros por Asiento", headerFont);
            pAsientos.setSpacingBefore(10f);
            pAsientos.setSpacingAfter(5f);
            doc.add(pAsientos);

            PdfPTable tablaAsientos = new PdfPTable(2);
            tablaAsientos.setWidthPercentage(100);
            tablaAsientos.setWidths(new float[]{1f, 4f});

            // Iterar de 1 a capacidad para listar todos los asientos
            for (int asiento = 1; asiento <= bus.getCapacidad(); asiento++) {
                tablaAsientos.addCell(getCell("Asiento " + asiento + ":", headerFont));
                Cliente cliente = clientesPorAsiento.get(asiento);
                String nombreCliente = (cliente != null) ? cliente.getNombreCompleto() : "(vacío)";
                tablaAsientos.addCell(getCell(nombreCliente, normalFont));
            }

            doc.add(tablaAsientos);

            Paragraph pie = new Paragraph("Gracias por confiar en nuestra empresa.", smallFont);
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(15f);
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
