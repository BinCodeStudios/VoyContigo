package VoyContigo_terminalOnline.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpSession;
import VoyContigo_terminalOnline.entity.AdminFlota;
import VoyContigo_terminalOnline.entity.Bus;
import VoyContigo_terminalOnline.repository.AdminFlotaRepository;
import VoyContigo_terminalOnline.repository.BusRepository;

@Controller
public class GestorDeBusesController {
	@Autowired
	private AdminFlotaRepository adminRepository;
    @Autowired
    private BusRepository busRepository; // Asegúrate de que tienes un repositorio para gestionar buses.

 // Método para mostrar el formulario de registro de un nuevo bus + lista de buses
    @GetMapping("/adminflota/gestor-buses")
    public String showRegisterBusForm(Model model, HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null || admin.getFlota() == null) {
            return "redirect:/login-adminflota";
        }

        // Agregar objeto vacío para el formulario
        model.addAttribute("bus", new Bus());

        // Agregar nombre de la empresa
        model.addAttribute("empresaNombre", admin.getFlota().getNombre());

        // Obtener lista de buses de esa flota
        List<Bus> buses = busRepository.findByFlotaId(admin.getFlota().getId());
        model.addAttribute("buses", buses);

        return "adminflota/gestor-buses";
    }



    // Método para registrar un nuevo bus
    @PostMapping("/new-bus")
    public String registerBus(@RequestParam("placa") String placa,
                              @RequestParam("modelo") String modelo,
                              @RequestParam("capacidad") int capacidad,
                              HttpSession session,
                              Model model) {
        // Recuperar el admin de la sesión
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null || admin.getFlota() == null) {
            model.addAttribute("error", "No se pudo identificar la empresa del administrador.");
            return "redirect:/login-adminflota";
        }

        // Obtener el ID de la flota
        String flotaId = admin.getFlota().getId();

        // Imprimir el ID de la flota en la consola
        System.out.println("Flota ID: " + flotaId);  // Imprime el ID de la flota en la consola

        // Crear y guardar el bus con la flota asociada
        Bus bus = new Bus();
        bus.setPlaca(placa);
        bus.setModelo(modelo);
        bus.setCapacidad(capacidad);
        bus.setFlota(admin.getFlota()); // Asignamos directamente el objeto Flota

        busRepository.save(bus);
        return "redirect:/adminflota/gestor-buses";
    }




    @GetMapping("/adminflota/gestor-buses/lista")
    public String listBuses(Model model) {
        // Obtener la lista de todos los buses registrados
        List<Bus> buses = busRepository.findAll();
        System.out.println("Lista de buses: " + buses); // Para verificar en la consola

        // Agregar la lista de buses al modelo para que esté disponible en la vista
        model.addAttribute("buses", buses);

        // Retornar la vista que contiene la tabla con los buses
        return "adminflota/gestor-buses"; // O la vista correspondiente
    }


    // Método para eliminar un bus
    @GetMapping("/adminflota/gestor-buses/eliminar/{id}")
    public String deleteBus(@PathVariable("id") String id, Model model) {
        Bus bus = busRepository.findById(id).orElse(null);
        if (bus != null) {
            busRepository.delete(bus); // Elimina el bus de la base de datos
            model.addAttribute("message", "Bus eliminado con éxito.");
        } else {
            model.addAttribute("message", "Bus no encontrado");
        }
        return "redirect:/adminflota/gestor-buses/lista";
    }
    
    @GetMapping("/adminflota/gestor-buses/editar/{id}")
    public String editBus(@PathVariable("id") String id, Model model, HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null) return "redirect:/login-adminflota";

        Bus bus = busRepository.findById(id).orElse(null);
        if (bus == null) {
            model.addAttribute("error", "Bus no encontrado");
            return "redirect:/adminflota/gestor-buses";
        }

        model.addAttribute("bus", bus); // <-- para que se rellene en el modal
        model.addAttribute("empresaNombre", admin.getFlota().getNombre());
        model.addAttribute("modoEdicion", true); // <-- para saber si es edición

        // También puedes cargar la lista de buses
        model.addAttribute("buses", busRepository.findByFlotaId(admin.getFlota().getId()));

        return "adminflota/gestor-buses";
    }
    
    @PostMapping("/adminflota/gestor-buses/actualizar")
    public String updateBus(@RequestParam("id") String id,
                            @RequestParam("placa") String placa,
                            @RequestParam("modelo") String modelo,
                            @RequestParam("capacidad") int capacidad,
                            HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null) return "redirect:/login-adminflota";

        Bus bus = busRepository.findById(id).orElse(null);
        if (bus != null) {
            bus.setPlaca(placa);
            bus.setModelo(modelo);
            bus.setCapacidad(capacidad);
            bus.setFlota(admin.getFlota()); // por si quieres asegurar que sigue con la flota actual
            busRepository.save(bus);
        }

        return "redirect:/adminflota/gestor-buses";
    }


}
