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
}
