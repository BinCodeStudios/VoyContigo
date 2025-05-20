package VoyContigo_terminalOnline.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import VoyContigo_terminalOnline.entity.AdminFlota;
import VoyContigo_terminalOnline.entity.Flota;
import VoyContigo_terminalOnline.entity.Ruta;
import VoyContigo_terminalOnline.repository.AdminFlotaRepository;
import VoyContigo_terminalOnline.repository.rutaRepository;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/adminflota/gestor-rutas")
public class GestorDeRutasController {

    @Autowired
    private rutaRepository rutaRepository;

    @Autowired
    private AdminFlotaRepository adminFlotaRepository;

    @GetMapping
    public String mostrarRutasDisponibles(Model model, HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null || admin.getFlota() == null) {
            return "redirect:/login-adminflota";
        }

        Flota flotaActual = admin.getFlota();

        // Rutas ya seleccionadas por esta flota
        List<Ruta> rutasSeleccionadas = rutaRepository.findByFlotas_Id(flotaActual.getId());

        // Rutas disponibles = todas - seleccionadas
        List<Ruta> todasRutas = rutaRepository.findAll();
        List<Ruta> rutasDisponibles = todasRutas.stream()
            .filter(r -> !rutasSeleccionadas.contains(r))
            .toList();

        model.addAttribute("rutasDisponibles", rutasDisponibles);
        model.addAttribute("rutasSeleccionadas", rutasSeleccionadas);
        model.addAttribute("empresaNombre", flotaActual.getNombre());

        return "adminflota/gestor-rutas";
    }


    // Guardar selección de rutas
    @PostMapping("/guardar")
    public String guardarRutasSeleccionadas(@RequestParam(value = "rutasSeleccionadas", required = false) List<String> idsRutas, HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null || admin.getFlota() == null) {
            return "redirect:/login-adminflota";
        }

        Flota flotaActual = admin.getFlota();

        // Obtener todas las rutas que actualmente tienen asociada esta flota
        List<Ruta> rutasActuales = rutaRepository.findByFlotas_Id(flotaActual.getId());

        // Eliminar la flota actual de todas esas rutas
        for (Ruta ruta : rutasActuales) {
            ruta.getFlotas().removeIf(f -> f.getId().equals(flotaActual.getId()));
            rutaRepository.save(ruta);
        }

        // Si no se seleccionó ninguna ruta, salir
        if (idsRutas == null || idsRutas.isEmpty()) {
            return "redirect:/adminflota/gestor-rutas";
        }

        // Asociar la flota actual a las nuevas rutas seleccionadas
        for (String idRuta : idsRutas) {
            Optional<Ruta> optionalRuta = rutaRepository.findById(idRuta);
            if (optionalRuta.isPresent()) {
                Ruta ruta = optionalRuta.get();
                if (ruta.getFlotas().stream().noneMatch(f -> f.getId().equals(flotaActual.getId()))) {
                    ruta.getFlotas().add(flotaActual);
                    rutaRepository.save(ruta);
                }
            }
        }

        return "redirect:/adminflota/gestor-rutas";
    }
}
