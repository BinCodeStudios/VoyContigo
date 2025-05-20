package VoyContigo_terminalOnline.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import VoyContigo_terminalOnline.entity.Trabajador;
import VoyContigo_terminalOnline.repository.TrabajadorRepository;

@Service
public class TrabajadorService {
	 @Autowired
	    private TrabajadorRepository trabajadorRepository;

	    public List<Trabajador> obtenerChoferes() {
	        return trabajadorRepository.findByTipo("chofer");
	    }
}
