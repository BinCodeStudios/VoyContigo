package VoyContigo_terminalOnline.Controller;

import java.io.IOException;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;

import VoyContigo_terminalOnline.entity.Cliente;
import VoyContigo_terminalOnline.entity.Pqrs;
import VoyContigo_terminalOnline.repository.BusRepository;
import VoyContigo_terminalOnline.repository.ClienteRepository;
import VoyContigo_terminalOnline.repository.PqrsRepository;
import VoyContigo_terminalOnline.repository.TicketRepository;
import VoyContigo_terminalOnline.repository.ViajeRepository;
import VoyContigo_terminalOnline.repository.flotaRepository;
import VoyContigo_terminalOnline.repository.rutaRepository;
import VoyContigo_terminalOnline.service.FileStorageService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/clientes")
public class AdminClienteController {
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
    @GetMapping("/lista-clientes")
    public String listarClientes(Model model, HttpSession session) {

        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);
        return "/admin/lista-clientes";
    }
    @GetMapping("/detalles/{id}")
    public String verDetallesCliente(@PathVariable("id") String id, Model model, HttpSession session) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        List<Pqrs> pqrs = pqrsRepository.findByCliente_Id(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("pqrsCount", pqrs.size());
        return "/admin/detalles-cliente";
    }
    
    @GetMapping("/foto/{id}")
    public void obtenerFotoCliente(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
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



    
    
    
    
}
