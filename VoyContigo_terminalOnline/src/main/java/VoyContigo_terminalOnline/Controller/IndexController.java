package VoyContigo_terminalOnline.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	  // Example: Map to a different endpoint if needed
    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }
    @GetMapping("/enlaces")
    public String showEnlaces() {
    	return"enlaces-interes";
    }
    @GetMapping("/contacto")
    public String showContacto(){
    	return"contacto";
    }
    @GetMapping("/mapa-site")
    public String showMap() {
    	return "mapa-sitio";
    }
    @GetMapping("/politica")
    public String showPolitica() {
    	return"politica-privacidad";
    }
}
