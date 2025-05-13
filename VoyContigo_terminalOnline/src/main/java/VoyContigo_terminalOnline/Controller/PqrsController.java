package VoyContigo_terminalOnline.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import VoyContigo_terminalOnline.entity.Pqrs;
import VoyContigo_terminalOnline.repository.PqrsRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/pqrs")
public class PqrsController {

    @Autowired
    private PqrsRepository pqrsRepository;

    // Display the list of all PQRs
    @GetMapping
    public String listarPqrs(Model model) {
        List<Pqrs> pqrs = pqrsRepository.findAll();
        model.addAttribute("pqrs", pqrs);
        return "pqrs/lista-pqrs";
    }

    // Display form to respond to a specific PQR
    @GetMapping("/responder/{id}")
    public String mostrarFormularioResponder(@PathVariable("id") String id, Model model) {
        Pqrs pqr = pqrsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PQR no encontrado: " + id));
        model.addAttribute("pqr", pqr);
        return "pqrs/responder-pqr";
    }

    // Handle PQR response submission
    @PostMapping("/responder/{id}")
    public String responderPqr(@PathVariable("id") String id, 
                              @RequestParam("respuesta") String respuesta,
                              @RequestParam("estado") String estado) {
        Pqrs pqr = pqrsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PQR no encontrado: " + id));
        pqr.setRespuesta(respuesta);
        pqr.setEstado(estado);
        pqrsRepository.save(pqr);
        return "redirect:/admin/pqrs";
    }
}