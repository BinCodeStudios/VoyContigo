package VoyContigo_terminalOnline.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;

import VoyContigo_terminalOnline.entity.AdminFlota;
import VoyContigo_terminalOnline.entity.Trabajador;
import VoyContigo_terminalOnline.repository.AdminFlotaRepository;
import VoyContigo_terminalOnline.repository.TrabajadorRepository;
import jakarta.servlet.http.HttpSession;


import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import org.bson.types.ObjectId;

@Controller
public class GestorDeEmpleadosController {

    @Autowired
    private AdminFlotaRepository adminRepository;

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @GetMapping("/adminflota/gestor-empleados")
    public String showEmpleadosPage(Model model, HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null || admin.getFlota() == null) {
            return "redirect:/login-adminflota";
        }

        List<Trabajador> trabajadores = trabajadorRepository.findByFlotaId(admin.getFlota().getId());
        model.addAttribute("trabajadores", trabajadores);
        model.addAttribute("empresaNombre", admin.getFlota().getNombre());

        return "adminflota/gestor-empleados";
    }

    @PostMapping("/adminflota/gestor-empleados/crear")
    public String crearTrabajador(
    	    @RequestParam("nombre") String nombre,
    	    @RequestParam("apellido") String apellido,
    	    @RequestParam("correo") String correo,
    	    @RequestParam("contrasena") String contrasena,
    	    @RequestParam("arl") String arl,
    	    @RequestParam("tipoDocumento") String tipoDocumento,
    	    @RequestParam("numeroDocumento") String numeroDocumento,
    	    @RequestParam("tipo") String tipo,
    	    @RequestParam("contrato") MultipartFile contratoFile,
    	    @RequestParam(value = "licencia", required = false) MultipartFile licenciaFile,
    	    HttpSession session
    	) throws IOException {

        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null || admin.getFlota() == null) {
            return "redirect:/login-adminflota";
        }

        Trabajador trabajador = new Trabajador();
        trabajador.setNombre(nombre);
        trabajador.setApellido(apellido);
        trabajador.setCorreo(correo);
        trabajador.setContrasena(contrasena);
        trabajador.setArl(arl);
        trabajador.setTipoDocumento(tipoDocumento);
        trabajador.setNumeroDocumento(numeroDocumento);
        trabajador.setTipo(tipo);
        trabajador.setFlota(admin.getFlota());

        // Subir contrato
        if (!contratoFile.isEmpty()) {
            ObjectId contratoId = gridFsTemplate.store(contratoFile.getInputStream(), contratoFile.getOriginalFilename(), contratoFile.getContentType());
            trabajador.setContratoFileId(contratoId.toHexString());
        }

        // Subir licencia solo si es chofer
        if ("chofer".equalsIgnoreCase(tipo) && licenciaFile != null && !licenciaFile.isEmpty()) {
            ObjectId licenciaId = gridFsTemplate.store(licenciaFile.getInputStream(), licenciaFile.getOriginalFilename(), licenciaFile.getContentType());
            trabajador.setLicenciaFileId(licenciaId.toHexString());
        } else {
            trabajador.setLicenciaFileId("No aplica");
        }

        trabajadorRepository.save(trabajador);
        return "redirect:/adminflota/gestor-empleados";
    }

    @PostMapping("/adminflota/gestor-empleados/actualizar")
    public String actualizarTrabajador(@RequestParam("id") String id,
                                       @RequestParam String nombre,
                                       @RequestParam String apellido,
                                       @RequestParam String correo,
                                       @RequestParam String contrasena,
                                       @RequestParam String arl,
                                       @RequestParam String tipoDocumento,
                                       @RequestParam String numeroDocumento,
                                       @RequestParam String tipo,
                                       @RequestParam("contrato") MultipartFile contratoFile,
                                       @RequestParam(value = "licencia", required = false) MultipartFile licenciaFile,
                                       HttpSession session) throws IOException {

        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null || admin.getFlota() == null) {
            return "redirect:/login-adminflota";
        }

        Trabajador trabajador = trabajadorRepository.findById(id).orElse(null);
        if (trabajador != null) {
            trabajador.setNombre(nombre);
            trabajador.setApellido(apellido);
            trabajador.setCorreo(correo);
            trabajador.setContrasena(contrasena);
            trabajador.setArl(arl);
            trabajador.setTipoDocumento(tipoDocumento);
            trabajador.setNumeroDocumento(numeroDocumento);
            trabajador.setTipo(tipo);
            trabajador.setFlota(admin.getFlota());

            // Actualizar contrato si se sube uno nuevo
            if (!contratoFile.isEmpty()) {
                ObjectId contratoId = gridFsTemplate.store(contratoFile.getInputStream(), contratoFile.getOriginalFilename(), contratoFile.getContentType());
                trabajador.setContratoFileId(contratoId.toHexString());
            }

            // Actualizar licencia si es chofer
            if ("chofer".equalsIgnoreCase(tipo)) {
                if (licenciaFile != null && !licenciaFile.isEmpty()) {
                    ObjectId licenciaId = gridFsTemplate.store(licenciaFile.getInputStream(), licenciaFile.getOriginalFilename(), licenciaFile.getContentType());
                    trabajador.setLicenciaFileId(licenciaId.toHexString());
                }
            } else {
                trabajador.setLicenciaFileId("No aplica");
            }

            trabajadorRepository.save(trabajador);
        }

        return "redirect:/adminflota/gestor-empleados";
    }

    @GetMapping("/adminflota/gestor-empleados/eliminar/{id}")
    public String eliminarTrabajador(@PathVariable("id") String id) {
        Trabajador trabajador = trabajadorRepository.findById(id).orElse(null);
        if (trabajador != null) {
            trabajadorRepository.delete(trabajador);
        }
        return "redirect:/adminflota/gestor-empleados";
    }

    @GetMapping("/adminflota/gestor-empleados/lista")
    public String listarTrabajadores(Model model, HttpSession session) {
        AdminFlota admin = (AdminFlota) session.getAttribute("adminFlota");
        if (admin == null || admin.getFlota() == null) {
            return "redirect:/login-adminflota";
        }

        model.addAttribute("empresaNombre", admin.getFlota().getNombre());
        model.addAttribute("trabajadores", trabajadorRepository.findByFlotaId(admin.getFlota().getId()));

        return "adminflota/gestor-empleados";
    }
    @GetMapping("/adminflota/gestor-empleados/ver-documento/{tipo}/{id}")
    public ResponseEntity<?> verDocumento(@PathVariable("tipo") String tipo, @PathVariable("id") String id) throws IOException {
        Optional<Trabajador> optional = trabajadorRepository.findById(id);
        if (!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Trabajador trabajador = optional.get();
        String fileIdHex = "contrato".equals(tipo) ? trabajador.getContratoFileId() : trabajador.getLicenciaFileId();

        // Si no tiene archivo o es "No aplica"
        if (fileIdHex == null || fileIdHex.equals("No aplica")) {
            return ResponseEntity.badRequest().body("Documento no disponible");
        }

        ObjectId fileId = new ObjectId(fileIdHex);
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        GridFsResource resource = gridFsTemplate.getResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(resource.getInputStream().readAllBytes());
    }



}
