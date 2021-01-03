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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afl.restaurante.entities.Curso;
import com.afl.restaurante.entities.Empresa;
import com.afl.restaurante.services.IEmpresaService;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController

@RequestMapping("/api")

public class EmpresaController {
	private Logger log = LoggerFactory.getLogger(CursoController.class);

	@Autowired
	private IEmpresaService empresaService;
	
	@Autowired
	private Empresa empresaStore;
	
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
			empresaActual.setDireccion(empresa.getDireccion());
			empresaActual.setProvincia(empresa.getProvincia());
			empresaActual.setTelefono(empresa.getTelefono());
			empresaActual.setEmail(empresa.getEmail());
			empresaActual.setUrlWeb(empresa.getUrlWeb());
			empresaActual.setDescripcionBreve(empresa.getDescripcionBreve());
			empresaActual.setHorario(empresa.getHorario());
			
			empresaUpdated = empresaService.save(empresaActual);
			setDatosEmpresa(empresaUpdated);
			
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
			setDatosEmpresa(empresaNew);

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

	//      Auxiliares
	
	private void setDatosEmpresa (Empresa empresa) {
		empresaStore.setNombre(empresa.getNombre());
		empresaStore.setDireccion(empresa.getDireccion());
		empresaStore.setProvincia(empresa.getProvincia());
		empresaStore.setTelefono(empresa.getTelefono());
		empresaStore.setEmail(empresa.getEmail());
		empresaStore.setUrlWeb(empresa.getUrlWeb());
		empresaStore.setDescripcionBreve(empresa.getDescripcionBreve());
		empresaStore.setHorario(empresa.getHorario());
		
	}
}
