package com.afl.restaurante.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
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
import org.springframework.web.multipart.MultipartFile;

import com.afl.restaurante.entities.Empresa;
import com.afl.restaurante.entities.Herramienta;
import com.afl.restaurante.entities.Slider;
import com.afl.restaurante.entities.Tipoplato;
import com.afl.restaurante.services.ITipoplatoService;
import com.afl.restaurante.services.files.IUploadFileService;


@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class TipodatoController {
	
	private Logger log = LoggerFactory.getLogger(TipodatoController.class);
	
	@Value("${app.uploadsDir:uploads}")
	private String uploadsDir;

	@Autowired ITipoplatoService tipoplatoService;
	
	@Autowired IUploadFileService uploadFileService;
	
//	@Secured({"ROLE_ADMIN"})
	@PostMapping("/tipoplato/create")
	public ResponseEntity<?> createTipoPlato(@Valid @RequestBody Tipoplato tipoplato, BindingResult result) {
		Tipoplato tipoplatoNew = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			tipoplato.setImgFileName("no-photo");
			tipoplatoNew = tipoplatoService.save(tipoplato);
		} catch (DataAccessException e) {
			response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible crear el tipoplato");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			log.error(response.toString());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Tipoplatocreado");
		response.put("data", tipoplatoNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
//	@Secured({"ROLE_ADMIN"})
	@GetMapping("/tipoplato/list")
	public List<Tipoplato> findAll() {
		log.trace("en findAll");
		return tipoplatoService.findAllByNombreTipo();
	}
	
//	@Secured({"ROLE_ADMIN"})
    @PutMapping("/tipoplato/update")
	public ResponseEntity<?> update(@Valid @RequestBody Tipoplato tipoplato, BindingResult result) {

    	Tipoplato tipoplatoUpdated = null;
    	Tipoplato tipoplatoActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		tipoplatoActual = tipoplatoService.findById(tipoplato.getId());
		if (tipoplatoActual == null) {
			response.put("mensaje",	"la tipoplato con id=".concat(tipoplato.getId().toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			tipoplatoActual.setNombre(tipoplato.getNombre());
			tipoplatoActual.setDescripcion(tipoplato.getDescripcion());
			
			tipoplatoUpdated = tipoplatoService.save(tipoplatoActual);

		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar tipoplato con id=".concat(tipoplato.getId().toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "sin error al actualizar tipoplato con id=".concat(tipoplato.getId().toString()));
		response.put("data", tipoplatoUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
    
	// @Secured({"ROLE_ADMIN"})
	@DeleteMapping("/tipoplato/{id}")
	
//	@RequestMapping(
//			  value = "/tipoplato/{id}", 
//			  produces = "application/json", 
//			  method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteTipoplato(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			Tipoplato tipoplato = tipoplatoService.findById(id);
			if (tipoplato == null) {
				response.put("mensaje", "tipoplato id=".concat(id.toString().concat(" no se encontró en la base de datos")));
				response.put("error", "Ya no existe en la base de datos");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			String nombreFoto = tipoplato.getImgFileName();
            uploadFileService.eliminar(uploadsDir + File.separator + "tipoplato", nombreFoto);	  
			tipoplatoService.deleteById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "tipoplato id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "tipoplato id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	// @Secured({"ROLE_ADMIN"})
	@PostMapping("/tipoplato/uploads/img")
	public ResponseEntity<?> uploadFoto(@RequestParam ("archivo") MultipartFile archivo, @RequestParam ("id") Long id) {
		
		Tipoplato tipoplato;
		Map<String, Object> response = new HashMap<>();
		
		log.debug ("id=" + id.toString());
		
		tipoplato = tipoplatoService.findById(id);
		if (tipoplato == null) {
			response.put("mensaje",	"el tipoplato con id=".concat(id.toString().concat(" no está en la base de datos")));
			response.put("error", "el tipoplatoe con id=".concat(id.toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		String nombreArchivo = null;
		if (!archivo.isEmpty()) {
			nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
    		Path rutaArchivo = uploadFileService.getPath(uploadsDir + File.separator + "tipoplatos", nombreArchivo);
 	    	try {
 	    		 uploadFileService.copia(rutaArchivo, archivo, nombreArchivo);
			} catch (IOException e) {
				e.printStackTrace();
 				response.put("mensaje",	"tipoplato con id=".concat(id.toString().concat(" error al subir imagen")));
				response.put("error", "IOException");
	 			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
 	    	
 	    	String nombreFotoAnterior = tipoplato.getImgFileName();
 	    	uploadFileService.eliminar(uploadsDir + File.separator + "tipoplatos", nombreFotoAnterior);
 	    	tipoplato.setImgFileName(nombreArchivo);
 			tipoplatoService.save(tipoplato);
 			response.put("data", tipoplato);
 			response.put("mensaje", "tipoplato id=".concat(id.toString().concat(" upload OK")));
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
   	@GetMapping("/tipoplato/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto) {
        return verFotoGenerico (Paths.get(uploadsDir + "/tipoplatos").resolve(nombreFoto).toAbsolutePath());
        
	}
		
	//      Auxiliares
	
	private ResponseEntity<Resource> verFotoGenerico (Path path) {
		Resource resource = null;
		try {
			log.debug("path:");
			log.debug(path.toString());
			resource = uploadFileService.salidaFichero(path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
		return new ResponseEntity<Resource>(resource, cabecera, HttpStatus.OK);

	}
}
