package com.afl.restaurante.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.afl.restaurante.entities.Cliente;
import com.afl.restaurante.entities.Curso;
import com.afl.restaurante.entities.Proyecto;
import com.afl.restaurante.services.IClienteService;
import com.afl.restaurante.services.files.IUploadFileService;


@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class ClienteController {

	public final static String DIRECTORIO_UPLOAD = "www/aflcv-back/uploads";

	@Value("${app.uploadsDir:uploads}")
	private String uploadsDir;
	
	private Logger log = LoggerFactory.getLogger(ClienteController.class);

	// Pasamos @Autowired en el constructor
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void messageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
    }
	
	@Autowired
	public ClienteController (IClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@GetMapping("/clientes/page")
	public Page<Cliente> index(
			@RequestParam(value = "page", defaultValue="0", required = false) Integer page,
			@RequestParam(value = "size", defaultValue= "12", required = false) Integer size,
			@RequestParam(value = "order", defaultValue = "inicio", required = false) String order,
			@RequestParam(value = "direction", defaultValue = "desc", required = false) String direction) {
    	Pageable pageable;
    	  	
    	if (direction.equals("desc")) {
    	  pageable = PageRequest.of(page, size, Sort.by(order).descending());
    	} else {
    		 pageable = PageRequest.of(page, size, Sort.by(order).ascending());
    	 }
    	return clienteService.findAll(pageable);  	
	}
	
    @RequestMapping(value = "/clientes/filtro", method = RequestMethod.GET)
    public List<Cliente> index(@RequestParam(value = "strBusca", required = true) String term) {  
       return clienteService.search(term);
    }
    
    
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@PutMapping("/cliente/update")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result) {
        
		Cliente clienteUpdated = null;
		Cliente clienteActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		clienteActual = clienteService.findById(cliente.getId());
		if (clienteActual == null) {
			response.put("mensaje",	"el cliente con id=".concat(cliente.getId().toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			clienteActual.setEmpresa(cliente.getEmpresa());
			clienteActual.setCliente(cliente.getCliente());
			clienteActual.setInicio(cliente.getInicio());
			clienteActual.setFin(cliente.getFin());
			clienteActual.setSector(cliente.getSector());
			clienteActual.setEmpresafoto (cliente.getEmpresafoto());
			clienteActual.setClientefoto (cliente.getClientefoto());
			clienteActual.setActividad(cliente.getActividad());
			clienteActual.setExperiencia(cliente.getExperiencia());

			//clienteActual.setProyectos(cliente.getProyectos());
			clienteUpdated = clienteService.save(clienteActual);
			 
		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar el cliente con id=".concat(cliente.getId().toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "sin error al actualizar el proyecto con id=".concat(cliente.getId().toString()));
		response.put("cliente", clienteUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@PutMapping("/cliente/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
        
		Cliente clienteUpdated = null;
		Cliente clienteActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		clienteActual = clienteService.findById(id);
		if (clienteActual == null) {
			response.put("mensaje",	"el cliente con id=".concat(id.toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			clienteActual.setEmpresa(cliente.getEmpresa());
			clienteActual.setCliente(cliente.getCliente());
			clienteActual.setInicio(cliente.getInicio());
			clienteActual.setFin(cliente.getFin());
			clienteActual.setSector(cliente.getSector());
			clienteActual.setEmpresafoto (cliente.getEmpresafoto());
			clienteActual.setClientefoto (cliente.getClientefoto());
			clienteActual.setActividad(cliente.getActividad());
			clienteActual.setExperiencia(cliente.getExperiencia());

			//clienteActual.setProyectos(cliente.getProyectos());
			clienteUpdated = clienteService.save(clienteActual);
			 
		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar el cliente con id=".concat(id.toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "sin error al actualizar el proyecto con id=".concat(id.toString()));
		response.put("cliente", clienteUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/cliente/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto) {

		Resource resource = null;
		try {
			Path path = Paths.get(uploadsDir+"/imagenes").resolve(nombreFoto).toAbsolutePath();
			resource = uploadFileService.salidaFichero(path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
		return new ResponseEntity<Resource>(resource, cabecera, HttpStatus.OK);
	}
    
    @Secured({"ROLE_ADMIN"})
	@DeleteMapping("/cliente/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "proyecto id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "proyecto id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
