package VoyContigo_terminalOnline.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import VoyContigo_terminalOnline.entity.AdminFlota;
import VoyContigo_terminalOnline.entity.Viaje;
import VoyContigo_terminalOnline.entity.Ruta;
import VoyContigo_terminalOnline.entity.Bus;
import VoyContigo_terminalOnline.repository.AdminFlotaRepository;
import VoyContigo_terminalOnline.repository.ViajeRepository;
import VoyContigo_terminalOnline.repository.rutaRepository;
import VoyContigo_terminalOnline.repository.BusRepository;

@Controller
public class GestorDeViajesController {

    @Autowired
    private AdminFlotaRepository adminRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private rutaRepository rutaRepository;

    @Autowired
    private BusRepository busRepository;  // <-- Repositorio Bus

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private LocalDateTime convertirStringALocalDateTime(String fechaStr) {
        return LocalDateTime.parse(fechaStr, formatter);
    }

    @GetMapping("/adminflota/gestor-viajes")
    public String showViajesForm(Model model, HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null || admin.getFlota() == null) {
            return "redirect:/login-adminflota";
        }

        List<Ruta> rutas = rutaRepository.findByFlotas_Id(admin.getFlota().getId());
        List<Viaje> viajes = viajeRepository.findByFlotaId(admin.getFlota().getId());
        List<Bus> buses = busRepository.findByFlotaId(admin.getFlota().getId());  // Obtiene buses de la flota

        model.addAttribute("viaje", new Viaje());
        model.addAttribute("empresaNombre", admin.getFlota().getNombre());
        model.addAttribute("rutas", rutas);
        model.addAttribute("buses", buses);  // Pasa la lista de buses al modelo
        model.addAttribute("viajes", viajes);

        return "adminflota/gestor-viajes";
    }

    @PostMapping("/adminflota/gestor-viajes/crear")
    public String crearViaje(@RequestParam("rutaId") String rutaId,
                             @RequestParam("busId") String busId,  // Recibe busId
                             @RequestParam("fecha") String fecha,
                             HttpSession session) {

        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null) return "redirect:/login-adminflota";

        Ruta ruta = rutaRepository.findById(rutaId).orElse(null);
        Bus bus = busRepository.findById(busId).orElse(null);  // Busca bus

        if (ruta == null || bus == null) return "redirect:/adminflota/gestor-viajes";

        Viaje nuevoViaje = new Viaje();
        nuevoViaje.setFlota(admin.getFlota());
        nuevoViaje.setRuta(ruta);
        nuevoViaje.setBus(bus);  // Asocia el bus
        nuevoViaje.setOrigen(ruta.getOrigen());
        nuevoViaje.setDestino(ruta.getDestino());
        nuevoViaje.setFechaSalida(convertirStringALocalDateTime(fecha));

        viajeRepository.save(nuevoViaje);

        return "redirect:/adminflota/gestor-viajes";
    }

    @GetMapping("/adminflota/gestor-viajes/eliminar/{id}")
    public String eliminarViaje(@PathVariable("id") String id) {
        viajeRepository.deleteById(id);
        return "redirect:/adminflota/gestor-viajes";
    }

    @GetMapping("/adminflota/gestor-viajes/editar/{id}")
    public String editarViaje(@PathVariable("id") String id, Model model, HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null) return "redirect:/login-adminflota";

        Viaje viaje = viajeRepository.findById(id).orElse(null);
        if (viaje == null) return "redirect:/adminflota/gestor-viajes";

        List<Ruta> rutas = rutaRepository.findByFlotas_Id(admin.getFlota().getId());
        List<Bus> buses = busRepository.findByFlotaId(admin.getFlota().getId());  // También pasa buses
        List<Viaje> viajes = viajeRepository.findByFlotaId(admin.getFlota().getId());

        model.addAttribute("viaje", viaje);
        model.addAttribute("empresaNombre", admin.getFlota().getNombre());
        model.addAttribute("rutas", rutas);
        model.addAttribute("buses", buses);  // Pasa buses para el select
        model.addAttribute("viajes", viajes);
        model.addAttribute("modoEdicion", true);

        return "adminflota/gestor-viajes";
    }

    @PostMapping("/adminflota/gestor-viajes/actualizar")
    public String actualizarViaje(@RequestParam("id") String id,
                                  @RequestParam("rutaId") String rutaId,
                                  @RequestParam("busId") String busId,  // Recibe busId
                                  @RequestParam("fecha") String fecha,
                                  HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null) return "redirect:/login-adminflota";

        Viaje viaje = viajeRepository.findById(id).orElse(null);
        Ruta ruta = rutaRepository.findById(rutaId).orElse(null);
        Bus bus = busRepository.findById(busId).orElse(null);

        if (viaje != null && ruta != null && bus != null) {
            viaje.setFlota(admin.getFlota());
            viaje.setRuta(ruta);
            viaje.setBus(bus);  // Actualiza bus
            viaje.setOrigen(ruta.getOrigen());
            viaje.setDestino(ruta.getDestino());
            viaje.setFechaSalida(convertirStringALocalDateTime(fecha));
            viajeRepository.save(viaje);
        }

        return "redirect:/adminflota/gestor-viajes";
    }
}
