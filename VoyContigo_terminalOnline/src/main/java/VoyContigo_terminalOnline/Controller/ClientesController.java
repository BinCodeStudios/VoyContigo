package VoyContigo_terminalOnline.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import VoyContigo_terminalOnline.entity.Cliente;
import VoyContigo_terminalOnline.entity.Pqrs;
import VoyContigo_terminalOnline.repository.ClienteRepository;
import VoyContigo_terminalOnline.repository.PqrsRepository;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin/clientes")
public class ClientesController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PqrsRepository pqrsRepository;

    // Mostrar la lista de todos los clientes
    @GetMapping
    public String listarClientes(Model model) {
        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);
        return "clientes/lista-clientes";
    }

    // Mostrar detalles de un cliente específico
    @GetMapping("/detalles/{id}")
    public String verDetallesCliente(@PathVariable("id") String id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        List<Pqrs> pqrs = pqrsRepository.findByCliente_Id(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("pqrsCount", pqrs.size());
        return "clientes/detalles-cliente";
    }

    // Mostrar página de inicio de sesión
    @GetMapping("/loginclientes")
    public String showLoginPage(Model model, @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "registered", required = false) String registered) {
        model.addAttribute("error", error != null);
        model.addAttribute("registered", registered != null);
        return "clientes/loginclientes";
    }

    // Manejar inicio de sesión
    @PostMapping("/loginclientes")
    public String login(@RequestParam("correo") String correo,
                        @RequestParam("contrasena") String contrasena) {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente != null && contrasena.equals(cliente.getContrasena())) {
            return "redirect:/admin/clientes/dashboard?id=" + cliente.getId();
        }
        return "redirect:/admin/clientes/loginclientes?error";
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
                String uploadDir = "src/main/resources/static/images/clientes/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = System.currentTimeMillis() + "_" + fotoFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, fotoFile.getBytes());
                cliente.setFoto("/images/clientes/" + fileName);
            }
            clienteRepository.save(cliente);
            return "redirect:/admin/clientes/loginclientes?registered";
        } catch (MaxUploadSizeExceededException e) {
            return "redirect:/admin/clientes/register?fileSizeError";
        }
    }

    // Mostrar dashboard del cliente
    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam("id") String id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        List<Pqrs> pqrs = pqrsRepository.findByCliente_Id(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("pqrs", pqrs);
        model.addAttribute("nuevoPqr", new Pqrs());
        return "clientes/dashboard";
    }

    // Mostrar formulario para crear PQR
    @GetMapping("/pqr/crear")
    public String mostrarFormularioPqr(@RequestParam("id") String id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        model.addAttribute("cliente", cliente);
        model.addAttribute("pqr", new Pqrs());
        return "clientes/crear-pqr";
    }

    // Manejar creación de PQR
    @PostMapping("/pqr/crear")
    public String crearPqr(@RequestParam("id") String id, @ModelAttribute("pqr") Pqrs pqr, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        pqr.setCliente(cliente);
        pqr.setFecha(java.time.LocalDateTime.now().toString());
        pqr.setEstado("Abierto");
        pqr.setRespuesta("");
        pqrsRepository.save(pqr);
        return "redirect:/admin/clientes/dashboard?id=" + id;
    }
}