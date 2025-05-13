package VoyContigo_terminalOnline.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import VoyContigo_terminalOnline.entity.OrigenDestino;
import VoyContigo_terminalOnline.repository.OrigenDestinoRepository;

@Controller
@RequestMapping("/admin/origen-destino")
public class OrigenDestinoController {

    @Autowired
    private OrigenDestinoRepository origenDestinoRepository;

    // Listar todos los orígenes y destinos
    @GetMapping
    public String listOrigenDestinos(Model model) {
        model.addAttribute("origenDestinos", origenDestinoRepository.findAll());
        return "origen-destino/list-origendestino";
    }

    // Mostrar formulario para crear un nuevo origen/destino
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("origenDestino", new OrigenDestino());
        return "origen-destino/form-origendestino";
    }

    // Guardar un nuevo origen/destino o editar uno existente
    @PostMapping("/save")
    public String saveOrigenDestino(@ModelAttribute OrigenDestino origenDestino) {
        if (origenDestino.getId() != null && origenDestino.getId().isEmpty()) {
            origenDestino.setId(null); // Asegura que las nuevas entradas no tengan un ID vacío
        }
        origenDestinoRepository.save(origenDestino);
        return "redirect:/admin/origen-destino";
    }

    // Mostrar formulario para editar un origen/destino existente
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model) {
        OrigenDestino origenDestino = origenDestinoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de origenDestino inválido: " + id));
        model.addAttribute("origenDestino", origenDestino);
        return "origen-destino/form-origendestino";
    }

    // Eliminar un origen/destino
    @GetMapping("/delete/{id}")
    public String deleteOrigenDestino(@PathVariable("id") String id) {
        origenDestinoRepository.deleteById(id);
        return "redirect:/admin/origen-destino";
    }
}