package com.afl.restaurante.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.afl.restaurante.entities.Menu;
import com.afl.restaurante.entities.Sugerencia;
import com.afl.restaurante.entities.Tipoplato;
import com.afl.restaurante.entities.specification.SearchCriteria;
import com.afl.restaurante.entities.specification.SearchOperation;
import com.afl.restaurante.entities.specification.SugerenciaSpecification;
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
	public void messageNotReadableException(HttpMessageNotReadableException exception, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
	}

	@GetMapping("/sugerencia/list")
	public Set<Sugerencia> findAllSegerencias() {
		return sugerenciaService.findAllByLabel();
	}

	@RequestMapping(value = "/sugerencia/page", method = RequestMethod.GET)
	public Page<Sugerencia> listPage(
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue = "12", required = false) Integer size,
			@RequestParam(value = "order", defaultValue = "label", required = false) String order,
			@RequestParam(value = "direction", defaultValue = "desc", required = false) String direction,
           	@RequestParam(value = "label", required = false) String label,
           	@RequestParam(value = "tipo", required = false) String tipo,
           	@RequestParam(value = "precioMin", required = false) String precioMin,
           	@RequestParam(value = "precioMax", required = false) String precioMax,
           	@RequestParam(value = "descripcion", required = false) String descripcion,
           	@RequestParam(value = "visible", required = false) String visible
           	)
	
	{

		Pageable pageable;
		
		
		if (direction.equals("desc")) {
			pageable = PageRequest.of(page, size, Sort.by(order).descending());
		} else {
			pageable = PageRequest.of(page, size, Sort.by(order).ascending());
		}
		
        SugerenciaSpecification espec = new SugerenciaSpecification();

        if (label != null) {
          espec.add(new SearchCriteria("label", label, SearchOperation.MATCH));
        }
        
        if (tipo != null) {
            espec.add(new SearchCriteria("tipo", tipo, SearchOperation.MATCH));
        }
        
        if (precioMin != null) {
            espec.add(new SearchCriteria("precio", Double.parseDouble(precioMin), SearchOperation.GREATER_THAN_EQUAL));
        }
        
        if (precioMax != null) {
            espec.add(new SearchCriteria("precio", Double.parseDouble(precioMax), SearchOperation.LESS_THAN_EQUAL));
        }
        
        if (descripcion != null) {
            espec.add(new SearchCriteria("descripcion", descripcion, SearchOperation.MATCH));
        }
        
        if (visible != null) {
            espec.add(new SearchCriteria("visible", visible, SearchOperation.MATCH));
        }
        
        return sugerenciaService.findAll (espec, pageable);
		
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
			sugerencia.setImgFileName("no-photo.png");
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
				response.put("mensaje",
						"sugerencia id=".concat(id.toString().concat(" no se encontró en la base de datos")));
				response.put("error", "Ya no existe en la base de datos");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			String nombreFoto = sugerencia.getImgFileName();
			uploadFileService.eliminar(uploadsDir + File.separator + "sugerencias", nombreFoto);
			sugerenciaService.deleteById(id);
		} catch (DataAccessException e) {
			response.put("mensaje",
					"sugerencia id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "sugerencia id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

 	@Secured({"ROLE_ADMIN"})
	@PutMapping("/sugerencia/update")
	public ResponseEntity<?> updateSugerencia(@Valid @RequestBody Sugerencia sugerencia, BindingResult result) {

		Sugerencia sugerenciaUpdated = null;
		Sugerencia sugerenciaActual = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		sugerenciaActual = sugerenciaService.findById(sugerencia.getId());
		if (sugerenciaActual == null) {
			response.put("mensaje", "la sugerencia con id="
					.concat(sugerencia.getId().toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			sugerenciaActual.setLabel(sugerencia.getLabel());
			sugerenciaActual.setDescripcion(sugerencia.getDescripcion());
			sugerenciaActual.setTipo(sugerencia.getTipo());
			sugerenciaActual.setPrecio(sugerencia.getPrecio());
            sugerenciaActual.setVisible(sugerencia.getVisible());

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
	
	// @Secured({"ROLE_ADMIN"})
	// @PostMapping("/sugerencia/uploads/img")
	@RequestMapping(path = "/sugerencia/uploads/img", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> uploadFoto(
			@RequestParam ("id") Long id, 
			@RequestPart ("archivo") MultipartFile archivo)
	    {
		
		Sugerencia sugerencia;
		Map<String, Object> response = new HashMap<>();
		
		log.debug ("id=" + id.toString());
		
		sugerencia =sugerenciaService.findById(id);
		if (sugerencia == null) {
			response.put("mensaje",	"sugerencia con id=".concat(id.toString().concat(" no está en la base de datos")));
			response.put("error", "sugerenciae con id=".concat(id.toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		String nombreArchivo = null;
		if (!archivo.isEmpty()) {
			nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
    		Path rutaArchivo = uploadFileService.getPath(uploadsDir + File.separator + "sugerencias", nombreArchivo);
 	    	try {
 	    		 uploadFileService.copia(rutaArchivo, archivo, nombreArchivo);
			} catch (IOException e) {
				e.printStackTrace();
 				response.put("mensaje",	"sugerencia con id=".concat(id.toString().concat(" error al subir imagen")));
				response.put("error", "IOException");
	 			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
 	    	
 	    	String nombreFotoAnterior =sugerencia.getImgFileName();
 	    	uploadFileService.eliminar(uploadsDir + File.separator + "sugerencias", nombreFotoAnterior);
 	    	sugerencia.setImgFileName(nombreArchivo);
 			sugerenciaService.save(sugerencia);
 			response.put("data",sugerencia);
 			response.put("mensaje", "sugerencia id=".concat(id.toString().concat(" upload img OK")));
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}	
	

   	@GetMapping("/sugerencia/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto) {
        return verFotoGenerico (Paths.get(uploadsDir + "/sugerencias").resolve(nombreFoto).toAbsolutePath());
        
	}

   	
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
