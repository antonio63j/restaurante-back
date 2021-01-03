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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afl.restaurante.entities.Cliente;
import com.afl.restaurante.entities.Curso;
import com.afl.restaurante.entities.Herramienta;
import com.afl.restaurante.entities.Proyecto;
import com.afl.restaurante.services.IProyectoService;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class ProyectoController {
	
	private Logger log = LoggerFactory.getLogger(ProyectoController.class);

	
  @Autowired
  IProyectoService proyectoService;
  
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public void messageNotReadableException(
          HttpMessageNotReadableException exception,
          HttpServletRequest request,
          HttpServletResponse response)
          throws IOException {
        response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
  }

	@GetMapping("/proyectos/page")
	public Page<Proyecto> index(
	        @RequestParam(value = "page"   ,  defaultValue="0"       ,  required = false) Integer page,
	        @RequestParam(value = "size"   ,  defaultValue="12"      ,  required = false) Integer size,
	        @RequestParam(value = "order"  ,  defaultValue="inicio"  ,  required = false) String order) {
		Pageable sortedByInicioDescNombre = 
				  PageRequest.of(page, size, Sort.by(order).descending().and(Sort.by("nombre")));
		return proyectoService.findAll(sortedByInicioDescNombre);
	}
	
	@GetMapping("/proyectos/cliente")
	public Page<Proyecto> index(
			@RequestParam(value = "cliente",  defaultValue="0"       ,  required = true) Integer clienteId,
	        @RequestParam(value = "page"   ,  defaultValue="0"       ,  required = false) Integer page,
	        @RequestParam(value = "size"   ,  defaultValue="12"      ,  required = false) Integer size,
	        @RequestParam(value = "order"  ,  defaultValue="inicio"  ,  required = false) String order) {
		
    	Proyecto qry = new Proyecto();
        Cliente cliente = new Cliente();
        cliente.setId(clienteId.longValue());
        qry.setCliente(cliente);
        return  proyectoService.findAll(Example.of(qry), PageRequest.of(page, size, Sort.by(order).descending()));
	}
		
	
	@GetMapping("/proyectos/filtro")
	public List<Proyecto> search (
			@RequestParam(value = "strBusca",  required = true) String cadena
			) {
		return proyectoService.search(cadena);
	}
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@PostMapping("/proyectos")
	public ResponseEntity<?> create(@Valid @RequestBody Proyecto proyecto, BindingResult result) {
		Proyecto proyectoNew = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			proyectoNew = proyectoService.save(proyecto);
		} catch (DataAccessException e) {
			response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible crear el proyecto");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			System.out.println(response);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "proyecto creado");
		response.put("proyecto", proyectoNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@PutMapping("/proyecto/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Proyecto proyecto, BindingResult result, @PathVariable Long id) {

		Proyecto proyectoUpdated = null;
		Proyecto proyectoActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		proyectoActual = proyectoService.findById(id);
		if (proyectoActual == null) {
			response.put("mensaje",	"el proyecto con id=".concat(id.toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			proyectoActual.setNombre(proyecto.getNombre());
			proyectoActual.setInicio(proyecto.getInicio());
			proyectoActual.setFin(proyecto.getFin());
			proyectoActual.setDescripcion(proyecto.getDescripcion());
			proyectoActual.setHerramientas(proyecto.getHerramientas());
			proyectoActual.setCliente(proyecto.getCliente());
			proyectoUpdated = proyectoService.save(proyectoActual);
			 
		} catch (DataAccessException e) {
			response.put("mensaje", "DataAccessException al actualizar el proyecto con id=".concat(id.toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			response.put("mensaje", "Exception al actualizar el proyecto con id=".concat(id.toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMessage()));
     		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		response.put("mensaje", "sin error al actualizar el proyecto con id=".concat(id.toString()));
		response.put("proyecto", proyectoUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping("/proyecto/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			proyectoService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "proyecto id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "proyecto id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
