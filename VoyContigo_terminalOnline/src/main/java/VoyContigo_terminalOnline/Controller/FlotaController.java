package VoyContigo_terminalOnline.Controller;

import VoyContigo_terminalOnline.entity.Flota;
import VoyContigo_terminalOnline.repository.flotaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.mongodb.MongoException;

@Controller
public class FlotaController {

    @Autowired
    private flotaRepository flotaRepository;

    // List all Flota entries
    @GetMapping("/admin/flota")
    public String listFlota(Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        model.addAttribute("flotas", flotaRepository.findAll());
        return "admin/flota-list";
    }

    // Show form to create a new Flota
    @GetMapping("/admin/flota/create")
    public String showCreateFlotaForm(Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        model.addAttribute("flota", new Flota());
        return "admin/flota-form";
    }

    // Process creation of a new Flota
    @PostMapping("/admin/flota/create")
    public String createFlota(@ModelAttribute Flota flota, Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        try {
            // Validate fields
            if (flota.getNombre() == null || flota.getNombre().trim().isEmpty()) {
                model.addAttribute("error", "El nombre es obligatorio");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }
            if (flota.getNit() == null || flota.getNit().trim().isEmpty()) {
                model.addAttribute("error", "El NIT es obligatorio");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }
            if (flota.getDireccion() == null || flota.getDireccion().trim().isEmpty()) {
                model.addAttribute("error", "La dirección es obligatoria");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }
            if (flota.getTelefono() == null || flota.getTelefono().trim().isEmpty()) {
                model.addAttribute("error", "El teléfono es obligatorio");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }

            // Check for duplicate NIT
            if (flotaRepository.existsByNit(flota.getNit())) {
                model.addAttribute("error", "El NIT ya está registrado");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }

            // Save Flota
            flotaRepository.save(flota);
            return "redirect:/admin/flota";
        } catch (MongoException e) {
            model.addAttribute("error", "Error de conexión con la base de datos: " + e.getMessage());
            model.addAttribute("flota", flota);
            return "admin/flota-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear la flota: " + e.getMessage());
            model.addAttribute("flota", flota);
            return "admin/flota-form";
        }
    }

    // Show form to edit an existing Flota
    @GetMapping("/admin/flota/edit/{id}")
    public String showEditFlotaForm(@PathVariable("id") String id, Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        try {
            Flota flota = flotaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Flota no encontrada"));
            model.addAttribute("flota", flota);
            return "admin/flota-form";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("flotas", flotaRepository.findAll());
            return "admin/flota-list";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la flota: " + e.getMessage());
            model.addAttribute("flotas", flotaRepository.findAll());
            return "admin/flota-list";
        }
    }

    // Process update of an existing Flota
    @PostMapping("/admin/flota/edit/{id}")
    public String updateFlota(@PathVariable("id") String id, @ModelAttribute Flota flota, Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        try {
            // Validate ID consistency
            if (!id.equals(flota.getId())) {
                model.addAttribute("error", "El ID de la flota no coincide");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }

            // Validate fields
            if (flota.getNombre() == null || flota.getNombre().trim().isEmpty()) {
                model.addAttribute("error", "El nombre es obligatorio");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }
            if (flota.getNit() == null || flota.getNit().trim().isEmpty()) {
                model.addAttribute("error", "El NIT es obligatorio");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }
            if (flota.getDireccion() == null || flota.getDireccion().trim().isEmpty()) {
                model.addAttribute("error", "La dirección es obligatoria");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }
            if (flota.getTelefono() == null || flota.getTelefono().trim().isEmpty()) {
                model.addAttribute("error", "El teléfono es obligatorio");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }

            // Check for duplicate NIT (excluding current Flota)
            Flota existingFlota = flotaRepository.findByNit(flota.getNit()).orElse(null);
            if (existingFlota != null && !existingFlota.getId().equals(id)) {
                model.addAttribute("error", "El NIT ya está registrado");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }

            // Update Flota
            flotaRepository.save(flota);
            return "redirect:/admin/flota";
        } catch (MongoException e) {
            model.addAttribute("error", "Error de conexión con la base de datos: " + e.getMessage());
            model.addAttribute("flota", flota);
            return "admin/flota-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar la flota: " + e.getMessage());
            model.addAttribute("flota", flota);
            return "admin/flota-form";
        }
    }

    // Delete a Flota
    @PostMapping("/admin/flota/delete/{id}")
    public String deleteFlota(@PathVariable("id") String id, Model model, HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/login";
        }
        try {
            if (!flotaRepository.existsById(id)) {
                model.addAttribute("error", "Flota no encontrada");
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/flota-list";
            }
            flotaRepository.deleteById(id);
            return "redirect:/admin/flota";
        } catch (MongoException e) {
            model.addAttribute("error", "Error de conexión con la base de datos: " + e.getMessage());
            model.addAttribute("flotas", flotaRepository.findAll());
            return "admin/flota-list";
        } catch (Exception e) {
            model.addAttribute("error", "Error al eliminar la flota: " + e.getMessage());
            model.addAttribute("flotas", flotaRepository.findAll());
            return "admin/flota-list";
        }
    }
}