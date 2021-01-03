package com.afl.restaurante.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afl.restaurante.entities.Curso;
import com.afl.restaurante.entities.Herramienta;
import com.afl.restaurante.entities.Proyecto;
import com.afl.restaurante.services.IHerramientaService;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class HerramientaController {

	private Logger log = LoggerFactory.getLogger(HerramientaController.class);

	@Autowired
	IHerramientaService herramientaService;
	
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void messageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
    }
	
	@GetMapping("/herramientas")
	public List<Herramienta> findAll() {
		return herramientaService.findAllByNombre();
	}
	
    @RequestMapping(value = "/herramientas/page/tipo", method = RequestMethod.GET)
    public Page<Herramienta> index(
        @RequestParam(value = "page"  ,  defaultValue="0"  , required = false) Integer page,
        @RequestParam(value = "size"  ,  defaultValue="12" , required = false) Integer size,
        @RequestParam(value = "tipo"  ,  required = false) String tipo) {
    	 	
    	Herramienta qry = new Herramienta();
        if (tipo != null) 
        	if (tipo.equals("TODOS"))
        		tipo = null; 
        qry.setTipo(tipo);
        return  herramientaService.findAll(Example.of(qry), PageRequest.of(page, size, Sort.by("nombre").ascending()));
    }
    

    
    @RequestMapping(value = "/herramientas/filtro", method = RequestMethod.GET)
    public List<Herramienta> search(@RequestParam(value = "strBusca", required = true) String term) {  
        return  herramientaService.search(term);
    }
    
	@Secured({"ROLE_ADMIN","ROLE_USER"}) 
	@PostMapping("/herramientas")
	public ResponseEntity<?> create(@Valid @RequestBody Herramienta herramienta, BindingResult result) {
		Herramienta herramientaNew = null;
		Map<String, Object> response = new HashMap<>();

		/*
		 * sin stream if (result.hasErrors()) { List<String> errors = new ArrayList<>();
		 * for (FieldError err: result.getFieldErrors()) { errors.add("El campo '"+
		 * err.getField() + "' " + err.getDefaultMessage()); } }
		 */

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			herramientaNew = herramientaService.save(herramienta);
		} catch (DataAccessException e) {
			response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible crear la herramienta");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			System.out.println(response);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "herramienta creada");
		response.put("herramienta", herramientaNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
    
	@Secured({"ROLE_ADMIN","ROLE_USER"})
    @PutMapping("/herramienta/update")
	public ResponseEntity<?> update(@Valid @RequestBody Herramienta herramienta, BindingResult result) {

    	Herramienta herramientaUpdated = null;
    	Herramienta herramientaActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		herramientaActual = herramientaService.findById(herramienta.getId());
		if (herramientaActual == null) {
			response.put("mensaje",	"la herramienta con id=".concat(herramienta.getId().toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			
			herramientaActual.setNombre(herramienta.getNombre());
			herramientaActual.setTipo(herramienta.getTipo());
			herramientaActual.setNivel(herramienta.getNivel());
			herramientaActual.setComentario(herramienta.getComentario());

			herramientaUpdated = herramientaService.save(herramientaActual);
			 
		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar herramienta con id=".concat(herramienta.getId().toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "sin error al actualizar herramiena con id=".concat(herramienta.getId().toString()));
		response.put("herramienta", herramientaUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
    
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping("/herramienta/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			herramientaService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "herramienta con id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "herramienta id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
