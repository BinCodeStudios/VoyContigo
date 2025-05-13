package VoyContigo_terminalOnline.Controller;

import VoyContigo_terminalOnline.entity.AdminFlota;
import VoyContigo_terminalOnline.entity.Flota;
import VoyContigo_terminalOnline.repository.AdminFlotaRepository;
import VoyContigo_terminalOnline.repository.flotaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.mongodb.MongoException;

@Controller
public class AdminFlotaController {

    @Autowired
    private AdminFlotaRepository adminFlotaRepository;

    @Autowired
    private flotaRepository flotaRepository;

    // List all AdminFlota entries
    @GetMapping("/admin/admin-flota")
    public String listAdminFlota(Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        model.addAttribute("adminFlotas", adminFlotaRepository.findAll());
        return "admin/admin-flota-list";
    }

    // Show form to create a new AdminFlota
    @GetMapping("/admin/admin-flota/create")
    public String showCreateAdminFlotaForm(Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        model.addAttribute("adminFlota", new AdminFlota());
        model.addAttribute("flotas", flotaRepository.findAll());
        return "admin/admin-flota-form";
    }

    // Process creation of a new AdminFlota
    @PostMapping("/admin/admin-flota/create")
    public String createAdminFlota(@ModelAttribute AdminFlota adminFlota, @RequestParam("flotaId") String flotaId, Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        try {
            // Validate fields
            if (adminFlota.getNombreCompleto() == null || adminFlota.getNombreCompleto().trim().isEmpty()) {
                model.addAttribute("error", "El nombre completo es obligatorio");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }
            if (adminFlota.getCorreo() == null || adminFlota.getCorreo().trim().isEmpty()) {
                model.addAttribute("error", "El correo es obligatorio");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }
            if (adminFlota.getContrasena() == null || adminFlota.getContrasena().trim().isEmpty()) {
                model.addAttribute("error", "La contraseña es obligatoria");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }
            if (flotaId == null || flotaId.trim().isEmpty()) {
                model.addAttribute("error", "Debe seleccionar una flota");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }

            // Check for duplicate correo
            if (adminFlotaRepository.existsByCorreo(adminFlota.getCorreo())) {
                model.addAttribute("error", "El correo ya está registrado");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }

            // Set Flota
            Flota flota = flotaRepository.findById(flotaId)
                    .orElseThrow(() -> new IllegalArgumentException("Flota no encontrada"));
            adminFlota.setFlota(flota);

            // Save AdminFlota
            adminFlotaRepository.save(adminFlota);
            return "redirect:/admin/admin-flota";
        } catch (MongoException e) {
            model.addAttribute("error", "Error de conexión con la base de datos: " + e.getMessage());
            model.addAttribute("adminFlota", adminFlota);
            model.addAttribute("flotas", flotaRepository.findAll());
            return "admin/admin-flota-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el administrador de flota: " + e.getMessage());
            model.addAttribute("adminFlota", adminFlota);
            model.addAttribute("flotas", flotaRepository.findAll());
            return "admin/admin-flota-form";
        }
    }

    // Show form to edit an existing AdminFlota
    @GetMapping("/admin/admin-flota/edit/{id}")
    public String showEditAdminFlotaForm(@PathVariable("id") String id, Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        try {
            AdminFlota adminFlota = adminFlotaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Administrador de flota no encontrado"));
            model.addAttribute("adminFlota", adminFlota);
            model.addAttribute("flotas", flotaRepository.findAll());
            return "admin/admin-flota-form";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("adminFlotas", adminFlotaRepository.findAll());
            return "admin/admin-flota-list";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el administrador de flota: " + e.getMessage());
            model.addAttribute("adminFlotas", adminFlotaRepository.findAll());
            return "admin/admin-flota-list";
        }
    }

    // Process update of an existing AdminFlota
    @PostMapping("/admin/admin-flota/edit/{id}")
    public String updateAdminFlota(@PathVariable("id") String id, @ModelAttribute AdminFlota adminFlota, @RequestParam("flotaId") String flotaId, Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        try {
            // Validate ID consistency
            if (!id.equals(adminFlota.getId())) {
                model.addAttribute("error", "El ID del administrador no coincide");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }

            // Validate fields
            if (adminFlota.getNombreCompleto() == null || adminFlota.getNombreCompleto().trim().isEmpty()) {
                model.addAttribute("error", "El nombre completo es obligatorio");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }
            if (adminFlota.getCorreo() == null || adminFlota.getCorreo().trim().isEmpty()) {
                model.addAttribute("error", "El correo es obligatorio");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }
            if (adminFlota.getContrasena() == null || adminFlota.getContrasena().trim().isEmpty()) {
                model.addAttribute("error", "La contraseña es obligatoria");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }
            if (flotaId == null || flotaId.trim().isEmpty()) {
                model.addAttribute("error", "Debe seleccionar una flota");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }

            // Check for duplicate correo (excluding current AdminFlota)
            AdminFlota existingAdmin = adminFlotaRepository.findByCorreo(adminFlota.getCorreo());
            if (existingAdmin != null && !existingAdmin.getId().equals(id)) {
                model.addAttribute("error", "El correo ya está registrado");
                model.addAttribute("adminFlota", adminFlota);
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/admin-flota-form";
            }

            // Set Flota
            Flota flota = flotaRepository.findById(flotaId)
                    .orElseThrow(() -> new IllegalArgumentException("Flota no encontrada"));
            adminFlota.setFlota(flota);

            // Update AdminFlota
            adminFlotaRepository.save(adminFlota);
            return "redirect:/admin/admin-flota";
        } catch (MongoException e) {
            model.addAttribute("error", "Error de conexión con la base de datos: " + e.getMessage());
            model.addAttribute("adminFlota", adminFlota);
            model.addAttribute("flotas", flotaRepository.findAll());
            return "admin/admin-flota-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el administrador de flota: " + e.getMessage());
            model.addAttribute("adminFlota", adminFlota);
            model.addAttribute("flotas", flotaRepository.findAll());
            return "admin/admin-flota-form";
        }
    }

    // Delete an AdminFlota
    @PostMapping("/admin/admin-flota/delete/{id}")
    public String deleteAdminFlota(@PathVariable("id") String id, Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        try {
            if (!adminFlotaRepository.existsById(id)) {
                model.addAttribute("error", "Administrador de flota no encontrado");
                model.addAttribute("adminFlotas", adminFlotaRepository.findAll());
                return "admin/admin-flota-list";
            }
            adminFlotaRepository.deleteById(id);
            return "redirect:/admin/admin-flota";
        } catch (MongoException e) {
            model.addAttribute("error", "Error de conexión con la base de datos: " + e.getMessage());
            model.addAttribute("adminFlotas", adminFlotaRepository.findAll());
            return "admin/admin-flota-list";
        } catch (Exception e) {
            model.addAttribute("error", "Error al eliminar el administrador de flota: " + e.getMessage());
            model.addAttribute("adminFlotas", adminFlotaRepository.findAll());
            return "admin/admin-flota-list";
        }
    }
}