package VoyContigo_terminalOnline.Controller;

import VoyContigo_terminalOnline.entity.Flota;
import VoyContigo_terminalOnline.repository.flotaRepository;
import VoyContigo_terminalOnline.service.FileStorageService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.mongodb.MongoException;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

@Controller
public class FlotaController {

    @Autowired
    private flotaRepository flotaRepository;

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private GridFSBucket gridFSBucket;
    
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations gridFsOperations;
    
    // List all Flota entries
    @GetMapping("/admin/flota")
    public String listFlota(Model model) {
        model.addAttribute("flotas", flotaRepository.findAll());
        return "admin/flota-list";
    }

    // Show form to create a new Flota
    @GetMapping("/admin/flota/create")
    public String showCreateFlotaForm(Model model) {
        model.addAttribute("flota", new Flota());
        return "admin/flota-form";
    }

    
    @GetMapping("/admin/flota/logo/{id}")
    public void obtenerLogoFlota(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
        Flota flota = flotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flota no encontrada"));

        if (flota.getLogoFileID() == null || flota.getLogoFileID().isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Flota no tiene logo");
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

    
    @PostMapping("/admin/flota/create")
    public String createFlota(@ModelAttribute Flota flota,
                              @RequestParam("logoFile") MultipartFile logoFile,
                              Model model) {
        try {
            // Validaciones
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

            // Verificar si el NIT ya existe
            if (flotaRepository.existsByNit(flota.getNit())) {
                model.addAttribute("error", "El NIT ya está registrado");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }

            // Guardar el logo si está presente
            if (!logoFile.isEmpty()) {
                ObjectId fileId = gridFsTemplate.store(
                        logoFile.getInputStream(),
                        logoFile.getOriginalFilename(),
                        logoFile.getContentType()
                );
                flota.setLogoFileID(fileId.toHexString());
            }

            // Guardar la flota
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
    public String showEditFlotaForm(@PathVariable("id") String id, Model model) {
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

    @PostMapping("/admin/flota/edit/{id}")
    public String updateFlota(@PathVariable("id") String id,
                              @ModelAttribute Flota flota,
                              @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                              Model model) {
        try {
            if (!id.equals(flota.getId())) {
                model.addAttribute("error", "El ID de la flota no coincide");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }

            // Validaciones (igual que antes)
            if (flota.getNombre() == null || flota.getNombre().trim().isEmpty()) {
                model.addAttribute("error", "El nombre es obligatorio");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }

            // Verifica si ya existe una flota con ese NIT, distinta a esta
            Flota existingFlota = flotaRepository.findByNit(flota.getNit());
            if (existingFlota != null && !existingFlota.getId().equals(id)) {
                model.addAttribute("error", "El NIT ya está registrado");
                model.addAttribute("flota", flota);
                return "admin/flota-form";
            }

            // Obtener la flota actual desde la BD para conservar campos anteriores
            Flota flotaActual = flotaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Flota no encontrada"));

            // Si se sube una nueva imagen, reemplazar en GridFS
            if (logoFile != null && !logoFile.isEmpty()) {
                Document metaData = new Document();
                metaData.put("contentType", logoFile.getContentType());

                ObjectId logoFileId = gridFSBucket.uploadFromStream(
                        logoFile.getOriginalFilename(),
                        logoFile.getInputStream(),
                        new GridFSUploadOptions().metadata(metaData)
                );

                flotaActual.setLogoFileID(logoFileId.toHexString());
            }

            // Actualizar el resto de campos
            flotaActual.setNombre(flota.getNombre());
            flotaActual.setNit(flota.getNit());
            flotaActual.setDireccion(flota.getDireccion());
            flotaActual.setTelefono(flota.getTelefono());

            flotaRepository.save(flotaActual);
            return "redirect:/admin/flota";

        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar la flota: " + e.getMessage());
            model.addAttribute("flota", flota);
            return "admin/flota-form";
        }
    }


    // Delete a Flota
    @PostMapping("/admin/flota/delete/{id}")
    public String deleteFlota(@PathVariable("id") String id, Model model) {
        try {
            if (!flotaRepository.existsById(id)) {
                model.addAttribute("error", "Flota no encontrada");
                model.addAttribute("flotas", flotaRepository.findAll());
                return "admin/flota-list";
            }
            flotaRepository.save(new Flota());
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