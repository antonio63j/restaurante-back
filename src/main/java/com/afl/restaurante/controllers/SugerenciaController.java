package com.afl.restaurante.controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
import org.springframework.web.bind.annotation.RestController;

import com.afl.restaurante.entities.Menu;
import com.afl.restaurante.entities.Sugerencia;
import com.afl.restaurante.entities.Tipoplato;
import com.afl.restaurante.services.ISugerenciaService;
import com.afl.restaurante.services.files.IUploadFileService;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class SugerenciaController {
	
	private Logger log = LoggerFactory.getLogger(SugerenciaController.class);

	
	  @Autowired
	  ISugerenciaService sugerenciaService;
	  
	  @Autowired 
	  IUploadFileService uploadFileService;
	  
	  @Value("${app.uploadsDir:uploads}")
	  private String uploadsDir;
	  
	  @ExceptionHandler(HttpMessageNotReadableException.class)
	  public void messageNotReadableException(
	          HttpMessageNotReadableException exception,
	          HttpServletRequest request,
	          HttpServletResponse response)
	          throws IOException {
	        response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
	  }
	  
//		@Secured({"ROLE_ADMIN"})
		@PostMapping("/sugerencia/create")
		public ResponseEntity<?> createSugerencia(@Valid @RequestBody Sugerencia sugerencia, BindingResult result) {
			Sugerencia sugerenciaNew = null;
			Map<String, Object> response = new HashMap<>();

			if (result.hasErrors()) {
				List<String> errors = result.getFieldErrors().stream()
						.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
						.collect(Collectors.toList());
				response.put("errors", errors);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			}

			try {
				sugerencia.setImgFileName("no-photo");
				sugerenciaNew = sugerenciaService.save(sugerencia);
			} catch (DataAccessException e) {
				response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible persistir el objeto");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				log.error(response.toString());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			response.put("mensaje", "creada sugerencia");
			response.put("data", sugerenciaNew);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		}
		

	
	// @Secured({"ROLE_ADMIN"})
	@DeleteMapping("/sugerencia/{id}")
    public ResponseEntity<?> deleteSugerencia(@PathVariable Long id) {
	Map<String, Object> response = new HashMap<>();

	try {
		Sugerencia sugerencia = sugerenciaService.findById(id);
		if (sugerencia == null) {
			response.put("mensaje", "sugerencia id=".concat(id.toString().concat(" no se encontró en la base de datos")));
			response.put("error", "Ya no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		String nombreFoto = sugerencia.getImgFileName();
        uploadFileService.eliminar(uploadsDir + File.separator + "sugerencias", nombreFoto);	  
		sugerenciaService.deleteById(id);
	} catch (DataAccessException e) {
		response.put("mensaje", "sugerencia id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
		response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	  }
	response.put("mensaje", "sugerencia id=".concat(id.toString().concat(" eliminado de la base de datos")));
	return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
   }
	
//	@Secured({"ROLE_ADMIN"})
    @PutMapping("/sugerencia/update")
	public ResponseEntity<?> updateSugerencia(@Valid @RequestBody Sugerencia sugerencia, BindingResult result) {

    	Sugerencia sugerenciaUpdated = null;
    	Sugerencia sugerenciaActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		sugerenciaActual = sugerenciaService.findById(sugerencia.getId());
		if (sugerenciaActual == null) {
			response.put("mensaje",	"la sugerencia con id=".concat(sugerencia.getId().toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			sugerenciaActual.setLabel(sugerencia.getLabel());
			sugerenciaActual.setDescripcion(sugerencia.getDescripcion());
			sugerenciaActual.setPrecio(sugerencia.getPrecio());
			
			sugerenciaUpdated = sugerenciaService.save(sugerenciaActual);

		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar sugerencia con id=".concat(sugerencia.getId().toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "actualizada sugerencia con id=".concat(sugerencia.getId().toString()));
		response.put("data", sugerenciaUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
		
	@GetMapping("/sugerencia/list")
	public Set<Sugerencia> findAllSegerencias() {
		return sugerenciaService.findAllByLabel();
	}
	


}
