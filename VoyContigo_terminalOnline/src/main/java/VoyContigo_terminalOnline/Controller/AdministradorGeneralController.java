package VoyContigo_terminalOnline.Controller;

import VoyContigo_terminalOnline.entity.AdministradorGeneral;
import VoyContigo_terminalOnline.entity.Cliente;
import VoyContigo_terminalOnline.repository.AdministradorGeneralRepository;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mongodb.MongoException;

@Controller
public class AdministradorGeneralController {

    @Autowired
    private AdministradorGeneralRepository adminRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session) {
        try {
            // Buscar el administrador por username
            AdministradorGeneral admin = adminRepository.findByUsername(username);
            if (admin == null) {
                model.addAttribute("error", "Usuario no encontrado");
                return "login";
            }

            // Validar la contraseña
            if (!admin.getPassword().equals(password)) {
                model.addAttribute("error", "Contraseña incorrecta");
                return "login";
            }

            // Credenciales válidas, establecer la sesión
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/admin/dashboard";
        } catch (MongoException e) {
            model.addAttribute("error", "Error de conexión con la base de datos: " + e.getMessage());
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar el login: " + e.getMessage());
            return "login";
        }
    }

 
    
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        return "admin/dashboard";
    }

    @GetMapping("/admin/create")
    public String showCreateAdminForm(Model model) {
        model.addAttribute("admin", new AdministradorGeneral());
        return "admin/create";
    }

    @PostMapping("/admin/create")
    public String createAdmin(@ModelAttribute AdministradorGeneral admin, Model model) {
        try {
            // Validar campos
            if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
                model.addAttribute("error", "El nombre de usuario es obligatorio");
                model.addAttribute("admin", admin);
                return "admin/create";
            }
            if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
                model.addAttribute("error", "La contraseña es obligatoria");
                model.addAttribute("admin", admin);
                return "admin/create";
            }
            if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
                model.addAttribute("error", "El correo electrónico es obligatorio");
                model.addAttribute("admin", admin);
                return "admin/create";
            }

            // Verificar si el usuario ya existe
            if (adminRepository.existsByUsername(admin.getUsername())) {
                model.addAttribute("error", "El nombre de usuario ya existe");
                model.addAttribute("admin", admin);
                return "admin/create";
            }

            // Guardar el administrador
            adminRepository.save(admin);
            return "redirect:/login";
        } catch (MongoException e) {
            model.addAttribute("error", "Error de conexión con la base de datos: " + e.getMessage());
            model.addAttribute("admin", admin);
            return "admin/create";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el administrador: " + e.getMessage());
            model.addAttribute("admin", admin);
            return "admin/create";
        }
    }
}