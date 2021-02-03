package com.afl.restaurante.controllers;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afl.restaurante.entities.Cliente;
import com.afl.restaurante.entities.Menu;
import com.afl.restaurante.entities.MenuSugerencia;
import com.afl.restaurante.entities.Sugerencia;
import com.afl.restaurante.entities.Tipoplato;
import com.afl.restaurante.services.IMenuService;
import com.afl.restaurante.services.ISugerenciaService;
import com.afl.restaurante.services.ITipoplatoService;
import com.afl.restaurante.services.files.IUploadFileService;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class MenuController {
	
	private Logger log = LoggerFactory.getLogger(MenuController.class);

	
	  @Autowired
	  IMenuService menuService;
	  
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
		@PostMapping("/menu/create")
		public ResponseEntity<?> createMenu(@Valid @RequestBody Menu menu, BindingResult result) {
			Menu menuNew = null;
			Map<String, Object> response = new HashMap<>();

			if (result.hasErrors()) {
				List<String> errors = result.getFieldErrors().stream()
						.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
						.collect(Collectors.toList());
				response.put("errors", errors);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			}

			try {
				menu.setImgFileName("no-photo");
				menuNew = menuService.save(menu);
			} catch (DataAccessException e) {
				response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible persistir el objeto");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				log.error(response.toString());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			response.put("mensaje", "creado menu");
			response.put("data", menuNew);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		}
		
		@PostMapping("/menusugerencia/create")
		public ResponseEntity<?> createMenuSugerencia(
				@RequestParam(value = "menu", required = true) Long menuId,
				@RequestParam(value = "sugerencia", required = true) Long sugerenciaId,
				@RequestParam(value = "primerPlato", required = true) boolean primerPlato) {
			
			Map<String, Object> response = new HashMap<>();
			try {
				Menu menu = menuService.findById(menuId);
				Sugerencia sugerencia = sugerenciaService.findById(sugerenciaId);
				MenuSugerencia menuSugerencia = new MenuSugerencia (menu, sugerencia, primerPlato);
				menuService.saveMenuSugerencia(menuSugerencia);
				response.put("mensaje", "creada nueva sugerencia al menu");
				response.put("data", menuSugerencia);
				
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
				
			}	
			catch (DataAccessException e) {
				
				response.put("mensaje", "error no ha sido posible persistir el objeto");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				log.error(response.toString());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}		
			

		}
	
	
		
		@GetMapping("/menu/list")
		public Set<Menu> findAllMenus() {
			return menuService.findAllByLabel();
		}
		
//		@Secured({"ROLE_ADMIN"})
	    @PutMapping("/menu/update")
		public ResponseEntity<?> update(@Valid @RequestBody Menu menu, BindingResult result) {

	    	Menu menuUpdated = null;
	    	Menu menuActual = null;
			Map<String, Object> response = new HashMap<>();
			
			if (result.hasErrors()) {
				List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
				response.put("errors", errors);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			}
			
			menuActual = menuService.findById(menu.getId());
			if (menuActual == null) {
				response.put("mensaje",	"la tipoplato con id=".concat(menu.getId().toString().concat(" no está en la base de datos")));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			try {
				menuActual.setLabel(menu.getLabel());
				menuActual.setDescripcion(menu.getDescripcion());
				menuActual.setPrecio(menu.getPrecio());
				
				menuUpdated = menuService.save(menuActual);

			} catch (DataAccessException e) {
				response.put("mensaje", "error al actualizar tipoplato con id=".concat(menu.getId().toString()));
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			response.put("mensaje", "sin error al actualizar tipoplato con id=".concat(menu.getId().toString()));
			response.put("data", menuUpdated);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		}
	    
		// @Secured({"ROLE_ADMIN"})
		@DeleteMapping("/menusugerencia/{id}")
	    public ResponseEntity<?> deleteMenuSugerencia(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			menuService.deleteMenuSugerenciaById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "menu sugerencia id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		response.put("mensaje", "menu sugerencia id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	   }
		
		
}
