package com.afl.restaurante.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
import org.springframework.web.multipart.MultipartFile;

import com.afl.restaurante.entities.Empresa;
import com.afl.restaurante.entities.Slider;
import com.afl.restaurante.services.IEmpresaService;
import com.afl.restaurante.services.ISliderService;
import com.afl.restaurante.services.files.IUploadFileService;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController

@RequestMapping("/api")

public class EmpresaController {
	private Logger log = LoggerFactory.getLogger(EmpresaController.class);

	@Value("${app.uploadsDir:uploads}")
	private String uploadsDir;
	
	@Autowired
	private IEmpresaService empresaService;
	
	@Autowired
	private ISliderService sliderService;
	
	@Autowired
	private Empresa empresaStore;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@Autowired
	private SimpMessagingTemplate template;
	
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void messageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
    }
    
	@RequestMapping(value = "/empresa", method = RequestMethod.GET)
	public Empresa getEmpresa(
			@RequestParam(value = "id"  ,  defaultValue="1"  , required = false) Long id) {
		return empresaService.findById(id);
	}
	
	@Secured({"ROLE_ADMIN"})
    @PutMapping("/empresa")
	public ResponseEntity<?> update(@Valid @RequestBody Empresa empresa, BindingResult result) {

    	Empresa empresaUpdated = null;
    	Empresa empresaActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		empresaActual = empresaService.findById(empresa.getId());
		if (empresaActual == null) {
			response.put("mensaje",	"la empresa con id=".concat(empresa.getId().toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			empresaActual.setNombre(empresa.getNombre());
			empresaActual.setCif(empresa.getCif());
			empresaActual.setDireccion(empresa.getDireccion());
			empresaActual.setLocalidad(empresa.getLocalidad());
			empresaActual.setProvincia(empresa.getProvincia());
			empresaActual.setTelefono(empresa.getTelefono());
			empresaActual.setEmail(empresa.getEmail());
			empresaActual.setUrlWeb(empresa.getUrlWeb());
			empresaActual.setDescripcionBreve(empresa.getDescripcionBreve());
			empresaActual.setPortada(empresa.getPortada());
			empresaActual.setDiasDescanso(empresa.getDiasDescanso());
			empresaActual.setHoraApertura(empresa.getHoraApertura());
			empresaActual.setHoraCierre(empresa.getHoraCierre());
			empresaActual.setHorasMinPreparacionPedido(empresa.getHorasMinPreparacionPedido());
			empresaActual.setDiasMaxEntregaPedido(empresa.getDiasMaxEntregaPedido());
						
			empresaUpdated = empresaService.save(empresaActual);
			
			setDatosEmpresaStore(empresaUpdated);
			
			template.convertAndSend("/topic/datosEmpresa", empresaUpdated);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar empresa con id=".concat(empresa.getId().toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		response.put("mensaje", "sin error al actualizar empresa con id=".concat(empresa.getId().toString()));
		response.put("empresa", empresaUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/empresa")
	public ResponseEntity<?> create(@Valid @RequestBody Empresa empresa, BindingResult result) {
		Empresa empresaNew = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			empresaNew = empresaService.save(empresa);
			setDatosEmpresaStore(empresaNew);

		} catch (DataAccessException e) {
			response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible crear la empresa");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			System.out.println(response);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "emrpresa creada");
		response.put("empresa", empresaNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	// @Secured({"ROLE_ADMIN"})
	@GetMapping("/empresa/sliders")
	public List<Slider> index() {
		List<Slider> sliders = empresaService.findAllSliders();
		log.info("sliders:");
		log.info (sliders.toString());
    	return empresaService.findAllSliders();  	
	}
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/empresa/slider")
	public ResponseEntity<?> createSlider(@Valid @RequestBody Slider slider, BindingResult result) {
		Slider sliderNew = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			slider.setImgFileName("no-photo.png");
			sliderNew = sliderService.save(slider);
		} catch (DataAccessException e) {
			response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible crear el slider");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			log.error(response.toString());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "slider creado");
		response.put("data", sliderNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN"})
    @PutMapping("/empresa/slider")
	public ResponseEntity<?> updateSlider(@Valid @RequestBody Slider slider, BindingResult result) {

    	Slider sliderUpdated = null;
    	Slider sliderActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		sliderActual = sliderService.findById(slider.getId());
		if (sliderActual == null) {
			response.put("mensaje",	"slider con id=".concat(slider.getId().toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			sliderActual.setLabel(slider.getLabel());
			sliderActual.setDescripcion(slider.getDescripcion());
			sliderUpdated = sliderService.save(sliderActual);

		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar slider con id=".concat(slider.getId().toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "actualizado slider con id=".concat(slider.getId().toString()));
		response.put("data", sliderUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
		
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping("/empresa/slider/{id}")
	public ResponseEntity<?> deleteSlider(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			Slider slider = sliderService.findById(id);
			if (slider == null) {
				response.put("mensaje", "slider id=".concat(id.toString().concat(" no se encontró en la base de datos")));
				response.put("error", "Ya no existe en la base de datos");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			String nombreFoto = slider.getImgFileName();
            uploadFileService.eliminar(uploadsDir + File.separator + "sliders", nombreFoto);	  
			sliderService.deleteById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "slider id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "slider id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
//	@Secured({"ROLE_ADMIN"})
//	@PostMapping("/empresa/uploads/img/sliders/")
//	public ResponseEntity<?> uploadFoto(@RequestParam ("archivo") MultipartFile archivo, @RequestParam ("id") Long id) {
//		
//		Slider slider;
//		Map<String, Object> response = new HashMap<>();
//		
//		log.debug ("id=" + id.toString());
//		
//		slider = sliderService.findById(id);
//		if (slider == null) {
//			response.put("mensaje",	"el slider con id=".concat(id.toString().concat(" no está en la base de datos")));
//			response.put("error", "el slidere con id=".concat(id.toString().concat(" no está en la base de datos")));
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
//		}
//		
//		if (!archivo.isEmpty()) {
// 	    	String nombreArchivo = null;
// 	    	try {
// 	    		nombreArchivo = uploadFileService.copia(archivo);
//			} catch (IOException e) {
//				e.printStackTrace();
// 				response.put("mensaje",	"slider con id=".concat(id.toString().concat(" error al subir imagen")));
//				response.put("error", "IOException");
//	 			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//			}
// 	    	
// 	    	String nombreFotoAnterior = slider.getImgFileName();
// 	    	uploadFileService.eliminar(nombreFotoAnterior);
// 	    	
// 	    	slider.setImgFileName(nombreArchivo);
// 			sliderService.save(slider);
// 			response.put("slider", slider);
// 			response.put("mensaje", "slider id=".concat(id.toString().concat(" upload OK")));
//		}
//		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
//	}
	
	@Secured({"ROLE_ADMIN"})
	@PostMapping("/empresa/uploads/img/sliders")
	public ResponseEntity<?> uploadFotoSlider(@RequestParam ("archivo") MultipartFile archivo, @RequestParam ("id") Long id) {
		
		Slider slider;
		Map<String, Object> response = new HashMap<>();
		
		log.debug ("id=" + id.toString());
		
		slider = sliderService.findById(id);
		if (slider == null) {
			response.put("mensaje",	"el slider con id=".concat(id.toString().concat(" no está en la base de datos")));
			response.put("error", "el slidere con id=".concat(id.toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		String nombreArchivo = null;
		if (!archivo.isEmpty()) {
			nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
    		Path rutaArchivo = uploadFileService.getPath(uploadsDir + File.separator + "sliders", nombreArchivo);
 	    	try {
 	    		 uploadFileService.copia(rutaArchivo, archivo, nombreArchivo);
			} catch (IOException e) {
				e.printStackTrace();
 				response.put("mensaje",	"slider con id=".concat(id.toString().concat(" error al subir imagen")));
				response.put("error", "IOException");
	 			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
 	    	
 	    	String nombreFotoAnterior = slider.getImgFileName();
 	    	uploadFileService.eliminar(uploadsDir + File.separator + "sliders", nombreFotoAnterior);
 	    	slider.setImgFileName(nombreArchivo);
 			sliderService.save(slider);
 			response.put("data", slider);
 			response.put("mensaje", "slider id=".concat(id.toString().concat(" upload OK")));
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	/*
	 * @GetMapping("/empresa/uploads/img/{nombreFoto:.+}") public
	 * ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto) {
	 * 
	 * Resource resource = null; try { // Path path =
	 * comentario Paths.get(uploadsDir+"/imagenes").resolve(nombreFoto).toAbsolutePath(); Path
	 * path = Paths.get(uploadsDir).resolve(nombreFoto).toAbsolutePath();
	 * 
	 * log.debug("path:"); log.debug(path.toString());
	 * 
	 * resource = uploadFileService.salidaFichero(path); } catch
	 * (MalformedURLException e) { e.printStackTrace(); }
	 * 
	 * HttpHeaders cabecera = new HttpHeaders();
	 * cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
	 * resource.getFilename() + "\""); return new ResponseEntity<Resource>(resource,
	 * cabecera, HttpStatus.OK); }
	 */
	
   	@GetMapping("/empresa/uploads/img/admin/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFotoAdmin(@PathVariable String nombreFoto) {
        return verFotoGenerico (Paths.get(uploadsDir + "/admin").resolve(nombreFoto).toAbsolutePath());
        
	}
	
   	@GetMapping("/empresa/uploads/img/sliders/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFotoSliders(@PathVariable String nombreFoto) {
        return verFotoGenerico (Paths.get(uploadsDir + "/sliders").resolve(nombreFoto).toAbsolutePath());
        
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
	
	
	private void setDatosEmpresaStore (Empresa empresa) {
		empresaStore.setNombre(empresa.getNombre());
		empresaStore.setCif(empresa.getCif());
		empresaStore.setDireccion(empresa.getDireccion());
		empresaStore.setLocalidad(empresa.getLocalidad());
		empresaStore.setProvincia(empresa.getProvincia());
		empresaStore.setTelefono(empresa.getTelefono());
		empresaStore.setEmail(empresa.getEmail());
		empresaStore.setUrlWeb(empresa.getUrlWeb());
		empresaStore.setDescripcionBreve(empresa.getDescripcionBreve());
		
	}
}
