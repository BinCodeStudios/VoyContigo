package VoyContigo_terminalOnline.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.MongoException;

import VoyContigo_terminalOnline.entity.AdminFlota;
import VoyContigo_terminalOnline.repository.AdminFlotaRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class indexAdminFlotaController {

	@Autowired
	private AdminFlotaRepository adminRepository;
	
	@GetMapping("/login-adminflota")
	public String showLoginPage() {
	    return "login-adminflota";
	}

	
	@PostMapping("login-admin")
	public String processLogin(@RequestParam("correo") String correo,
	                           @RequestParam("contrasena") String contrasena,
	                           Model model,
	                           HttpSession session) {
	    try {
	        AdminFlota admin = adminRepository.findByCorreo(correo);
	        if (admin == null) {
	            model.addAttribute("error", "Correo no encontrado");
	            return "login-adminflota";
	        }

	        if (!admin.getContrasena().equals(contrasena)) {
	            model.addAttribute("error", "Contraseña incorrecta");
	            return "login-adminflota";
	        }

	        session.setAttribute("adminLoggedIn", true);
	        session.setAttribute("adminFlota", admin); 
	        return "redirect:/adminflota/dashboard";
	    } catch (MongoException e) {
	        model.addAttribute("error", "Error de conexión con la base de datos: " + e.getMessage());
	        return "login-adminflota";
	    } catch (Exception e) {
	        model.addAttribute("error", "Error al procesar el login: " + e.getMessage());
	        return "login-adminflota";
	    }
	}


	@GetMapping("/adminflota/dashboard")
	public String showDashboard(HttpSession session, Model model) {
	    AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
	    if (admin == null) return "redirect:/login-adminflota";

	    model.addAttribute("empresaNombre", admin.getFlota().getNombre());
	    return "adminflota/dashboard"; // Vista HTML
	}


}
