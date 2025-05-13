package VoyContigo_terminalOnline.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import VoyContigo_terminalOnline.entity.Ruta;
import VoyContigo_terminalOnline.repository.rutaRepository;
import VoyContigo_terminalOnline.repository.OrigenDestinoRepository;

@Controller
@RequestMapping("/admin/rutas")
public class RutaController {

    @Autowired
    private rutaRepository rutaRepository;

    @Autowired
    private OrigenDestinoRepository origenDestinoRepository;

    // Listar todas las rutas
    @GetMapping
    public String listRutas(Model model) {
        model.addAttribute("rutas", rutaRepository.findAll());
        return "rutas/list-ruta";
    }

    // Mostrar formulario para crear una nueva ruta
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("ruta", new Ruta());
        model.addAttribute("origenDestinos", origenDestinoRepository.findAll());
        return "rutas/form-ruta";
    }

    // Guardar una nueva ruta o editar una existente
    @PostMapping("/save")
    public String saveRuta(@Valid @ModelAttribute Ruta ruta, BindingResult result, Model model) {
        // Limpiar ID si es vacío para nuevas rutas
        if (ruta.getId() != null && ruta.getId().isEmpty()) {
            ruta.setId(null);
        }

        // Validar que origen y destino no sean iguales
        if (ruta.getOrigen() != null && ruta.getDestino() != null && 
            ruta.getOrigen().getId().equals(ruta.getDestino().getId())) {
            result.rejectValue("destino", "error.ruta", "El destino no puede ser igual al origen.");
        }

        if (result.hasErrors()) {
            model.addAttribute("origenDestinos", origenDestinoRepository.findAll());
            return "rutas/form-ruta";
        }

        rutaRepository.save(ruta);
        return "redirect:/admin/rutas";
    }

    // Mostrar formulario para editar una ruta existente
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de ruta inválido: " + id));
        model.addAttribute("ruta", ruta);
        model.addAttribute("origenDestinos", origenDestinoRepository.findAll());
        return "rutas/form-ruta";
    }
}