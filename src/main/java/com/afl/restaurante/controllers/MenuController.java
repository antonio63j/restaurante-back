package com.afl.restaurante.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import org.springframework.web.multipart.MultipartFile;

import com.afl.restaurante.entities.EnumComponenteMenu;
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
	public void messageNotReadableException(HttpMessageNotReadableException exception, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
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
			menu.setImgFileName("no-photo.png");
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

//	@Secured({"ROLE_ADMIN"})
	@PostMapping("/menusugerencia/create")
	public ResponseEntity<?> createMenuSugerencia(@RequestBody Menu menuAct,
			@RequestParam(value = "sugerenciaId", required = true) Long sugerenciaId,
//			@RequestParam(value = "primerPlato", required = true) boolean primerPlato) {
	    	@RequestParam(value = "componenteMenu", required = true) EnumComponenteMenu componenteMenu,
	    	Authentication authentication, Principal principal) {

		Map<String, Object> response = new HashMap<>();
		Menu menuFinal;
		try {


	        System.out.println(authentication.getName());
	        System.out.println("-----------------");
	        System.out.println(principal.getName());
			
			
			Sugerencia sugerencia = sugerenciaService.findById(sugerenciaId);
			MenuSugerencia menuSugerencia = new MenuSugerencia(menuAct, sugerencia, componenteMenu);
			menuService.saveMenuSugerencia(menuSugerencia);
			
			menuFinal = menuService.findById(menuAct.getId());
			response.put("mensaje", "creada nueva sugerencia al menu");
			response.put("data", menuFinal);

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

		} catch (DataAccessException e) {

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
	
	@GetMapping("/menu/list-visible")
	public Set<Menu> findAllMenusVisibles() {
		return menuService.findAllByLabelVisible(new Boolean(true));
	}
	

//		@Secured({"ROLE_ADMIN"})
	@PutMapping("/menu/update")
	public ResponseEntity<?> update(@Valid @RequestBody Menu menu, BindingResult result) {

		Menu menuUpdated = null;
		Menu menuActual = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		menuActual = menuService.findById(menu.getId());
		if (menuActual == null) {
			response.put("mensaje",
					"menu con id=".concat(menu.getId().toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			menuActual.setLabel(menu.getLabel());
			menuActual.setDescripcion(menu.getDescripcion());
			menuActual.setPrecio(menu.getPrecio());
            menuActual.setVisible(menu.isVisible());

			menuUpdated = menuService.save(menuActual);

		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar menu con id=".concat(menu.getId().toString()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "sin error al actualizar menu con id=".concat(menu.getId().toString()));
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
			response.put("mensaje",
					"menu sugerencia id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "menu sugerencia id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	// @Secured({"ROLE_ADMIN"})
	@DeleteMapping("/menu/{id}")
	public ResponseEntity<?> deleteMenu(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			menuService.deleteById(id);
		} catch (DataAccessException e) {
			response.put("mensaje",
					"menu id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "menu id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/menu/{id}")
	public ResponseEntity<?> getMenuById (@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
	    Menu menu;
		try {
			menu = menuService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje",
					"menu id=".concat(id.toString().concat(" error acceso a la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "acceso satisfactorio a menu con id=".concat(menu.getId().toString()));
		response.put("data", menu);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	// @Secured({"ROLE_ADMIN"})
	@PostMapping("/menu/uploads/img")
	public ResponseEntity<?> uploadFoto(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {

		Menu menu;
		Map<String, Object> response = new HashMap<>();

		log.debug("id=" + id.toString());

		menu = menuService.findById(id);
		if (menu == null) {
			response.put("mensaje", "menu con id=".concat(id.toString().concat(" no está en la base de datos")));
			response.put("error", "menu con id=".concat(id.toString().concat(" no está en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		String nombreArchivo = null;
		if (!archivo.isEmpty()) {
			nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo = uploadFileService.getPath(uploadsDir + File.separator + "menus", nombreArchivo);
			try {
				uploadFileService.copia(rutaArchivo, archivo, nombreArchivo);
			} catch (IOException e) {
				e.printStackTrace();
				response.put("mensaje", "menu con id=".concat(id.toString().concat(" error al subir imagen")));
				response.put("error", "IOException");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			String nombreFotoAnterior = menu.getImgFileName();
			uploadFileService.eliminar(uploadsDir + File.separator + "menus", nombreFotoAnterior);
			menu.setImgFileName(nombreArchivo);
			menuService.save(menu);
			response.put("data", menu);
			response.put("mensaje", "menu id=".concat(id.toString().concat(" upload img OK")));
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("/menu/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto) {
		return verFotoGenerico(Paths.get(uploadsDir + "/menus").resolve(nombreFoto).toAbsolutePath());

	}

	private ResponseEntity<Resource> verFotoGenerico(Path path) {
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
