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
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.afl.restaurante.services.ICursoService;

import org.springframework.web.bind.annotation.ExceptionHandler;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class CursoController {

	private Logger log = LoggerFactory.getLogger(CursoController.class);

	@Autowired
	private ICursoService cursoService;
	
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void messageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
    }
 

	@RequestMapping(value = "/cursos/page", method = RequestMethod.GET)
	public Page<Curso> index(@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "12", required = false) Integer size,
			@RequestParam(value = "order", defaultValue = "inicio", required = false) String order,
			@RequestParam(value = "direction", defaultValue = "desc", required = false) String direction) {

		Pageable pageable;
		if (direction.equals("desc")) {
			pageable = PageRequest.of(page, size, Sort.by(order).descending());
		} else {
			pageable = PageRequest.of(page, size, Sort.by(order).ascending());
		}
		return cursoService.findAll(pageable);
	}

	// Para validar el objeto especificado en @RequesBody utilizamos el interceptor
	// @Valid

	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@PostMapping("/cursos")
	public ResponseEntity<?> create(@Valid @RequestBody Curso curso, BindingResult result) {
		Curso cursoNew = null;
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
			cursoNew = cursoService.save(curso);
		} catch (DataAccessException e) {
			response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible crear el curso");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			System.out.println(response);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "curso creado");
		response.put("curso", cursoNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@PutMapping("/curso/update")
	public ResponseEntity<?> update(@Valid @RequestBody Curso curso, BindingResult result) {

		Curso cursoUpdated = null;
		Curso cursoActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		cursoActual = cursoService.findById(curso.getId());
		if (cursoActual == null) {
			response.put("mensaje",	"el curso con id=".concat(curso.getId().toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			cursoActual.setNombre(curso.getNombre());
			cursoActual.setInicio(curso.getInicio());
			cursoActual.setFin(curso.getFin());
			cursoActual.setHoras(curso.getHoras());
			cursoUpdated = cursoService.save(cursoActual);
			 
		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar el curso =".concat(curso.getNombre()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "actualizado el curso =".concat(curso.getNombre()));
		response.put("curso", cursoUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/cursos", method = RequestMethod.GET)
	public List<Curso> findAll() {
		System.out.println("tmn page");
		return cursoService.findAll();
	}

	@Secured({"ROLE_ADMIN"})
	@DeleteMapping("/curso/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			cursoService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "curso id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "curso id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/cursos/filtro", method = RequestMethod.GET)
	public List<Curso> index(@RequestParam(value = "strBusca", required = true) String term) {
		return cursoService.search(term);
	}

}