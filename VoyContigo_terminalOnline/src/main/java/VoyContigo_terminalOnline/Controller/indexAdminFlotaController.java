package VoyContigo_terminalOnline.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.MongoException;
import com.mongodb.client.gridfs.GridFSDownloadStream;

import VoyContigo_terminalOnline.entity.AdminFlota;
import VoyContigo_terminalOnline.entity.Bus;
import VoyContigo_terminalOnline.entity.Flota;
import VoyContigo_terminalOnline.repository.AdminFlotaRepository;
import VoyContigo_terminalOnline.repository.BusRepository;
import VoyContigo_terminalOnline.repository.flotaRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class indexAdminFlotaController {

	@Autowired
	private AdminFlotaRepository adminRepository;
	@Autowired
    private BusRepository busRepository; // Asegúrate de que tienes un repositorio para gestionar buses.
	@Autowired
	private  flotaRepository flotaRepository;
	@Autowired
	private GridFSBucket gridFSBucket;
	
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
	    model.addAttribute("adminFlota", admin);  // <-- Agrega esto

	    model.addAttribute("empresaNombre", admin.getFlota().getNombre());
        List<Bus> buses = busRepository.findByFlotaId(admin.getFlota().getId());
        model.addAttribute("buses", buses);
	    return "adminflota/dashboard"; // Vista HTML
	}
	@GetMapping("/adminflota/flota/logo/{id}")
	public void obtenerLogoFlota(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
	    Flota flota = flotaRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Flota no encontrada"));

	    if (flota.getLogoFileID() == null || flota.getLogoFileID().isEmpty()) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND, "La flota no tiene logo");
	        return;
	    }

	    ObjectId fileId = new ObjectId(flota.getLogoFileID());

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
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener el logo");
	    }


	}
	
}
