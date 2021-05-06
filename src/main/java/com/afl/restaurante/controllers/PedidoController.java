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

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
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

import com.afl.restaurante.dao.IPedidoDao;
import com.afl.restaurante.entities.EnumEstadoPedido;
import com.afl.restaurante.entities.Pedido;
import com.afl.restaurante.services.IPedidoLineaMenuService;
import com.afl.restaurante.services.IPedidoLineaSugerenciaService;
import com.afl.restaurante.services.IPedidoService;
import com.afl.restaurante.request.PedidoConfirmacion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class PedidoController {

	private Logger log = LoggerFactory.getLogger(MenuController.class);

	@Autowired
	IPedidoService pedidoService;

	@Autowired
	IPedidoLineaSugerenciaService pedidoLineaSugerenciaService;

	@Autowired
	IPedidoLineaMenuService pedidoLineaMenuService;

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public void messageNotReadableException(HttpMessageNotReadableException exception, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
	}

	@PostMapping("/pedido/save")
	public ResponseEntity<?> savePedido(@Valid @RequestBody Pedido pedido, BindingResult result) {
		Pedido pedidoNew = null;

		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {

			log.debug("pedido:");
			log.debug(pedido.toString());

			pedidoNew = pedidoService.save(pedido);

			log.debug("pedidoNew:");
			log.debug(pedidoNew.toString());

		} catch (DataAccessException e) {
			response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible persistir el objeto");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			log.error(response.toString());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "creado pedido");
		response.put("data", pedidoNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@PostMapping("/pedido/confirmacion")
	public ResponseEntity<?> confirmacionPedido(
			@Valid @RequestBody Pedido pedido, BindingResult result) {
		
		Pedido pedidoAct = null;
		Pedido pedidoNew = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {

			log.debug("pedido para confirmacion:");
			log.debug(pedido.toString());
			
			pedidoNew = pedidoService.confirmarPedido(pedido);
			
			if (pedidoNew == null) {
				return new ResponseEntity<Map<String, Object>>(HttpStatus.CREATED);
			}	
			
		} catch (DataAccessException e) {
			response.put("mensaje", "No ha sido posible gestionar la confirmacion del pedido");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			log.error(response.toString());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Pedido confirmado");
		response.put("data", pedidoNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

//		@Secured({"ROLE_ADMIN"})
//	@PutMapping("/pedido/update")
//	public ResponseEntity<?> update(@Valid @RequestBody Pedido pedido, BindingResult result) {
//
//		Pedido pedidoUpdated = null;
//		Pedido pedidoActual = null;
//		Map<String, Object> response = new HashMap<>();
//
//		if (result.hasErrors()) {
//			List<String> errors = result.getFieldErrors().stream()
//					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
//					.collect(Collectors.toList());
//			response.put("errors", errors);
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
//		}
//
//		pedidoActual = pedidoService.findById(pedido.getId());
//		if (pedidoActual == null) {
//			response.put("mensaje",
//					"pedido con id=".concat(pedido.getId().toString().concat(" no está en la base de datos")));
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
//		}
//		try {
////			pedidoActual.setUsuario(pedido.getUsuario());
////			pedidoActual.setEstadoPedido(pedido.getEstadoPedido());
////			pedidoActual.setTotal(pedido.getTotal());
//			pedidoActual.setPedidoLineaSugerencias(pedido.getPedidoLineaSugerencias());
//			pedidoActual.setPedidoLineaMenus(pedido.getPedidoLineaMenus());
//			
//			pedidoUpdated = pedidoService.save(pedidoActual);
//			
//			log.debug("pedidoUpdated:");
//			log.debug(pedidoUpdated.toString());
//
//		} catch (DataAccessException e) {
//			response.put("mensaje", "error al actualizar pedido con id=".concat(pedido.getId().toString()));
//			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		response.put("mensaje", "sin error al actualizar pedido con id=".concat(pedido.getId().toString()));
//		response.put("data", pedidoUpdated);
//		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
//	}

	@DeleteMapping("/pedido/{id}")
	public ResponseEntity<?> deletePedido(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			pedidoService.deleteById(id);
		} catch (DataAccessException e) {
			response.put("mensaje",
					"pedido id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "pedido id=".concat(id.toString().concat(" eliminado de la base de datos")));
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

//	@DeleteMapping("/pedido/sugerencia/{id}")
//	public ResponseEntity<?> deletePedidoSugerencia(@PathVariable Long id) {
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//			pedidoLineaSugerenciaService.deleteById(id);
//		} catch (DataAccessException e) {
//			response.put("mensaje",
//					"sugerencia de pedido id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
//			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		response.put("mensaje", "sugerencia de pedido id=".concat(id.toString().concat(" eliminado de la base de datos")));
//		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
//	}

	@DeleteMapping("/pedido/lineaSugerencia")
	public ResponseEntity<?> deletePedidoSugerencia(@RequestParam(value = "idPedido", required = true) Long idPedido,
			@RequestParam(value = "idLineaSugerencia", required = true) Long idLineaSugerencia

	) {
		Map<String, Object> response = new HashMap<>();
		Pedido carrito = null;
		try {
			carrito = pedidoLineaSugerenciaService.deleteLineaSugerenciaById(idPedido, idLineaSugerencia);
		} catch (DataAccessException e) {
			response.put("mensaje", "sugerencia de pedido idLineaSugerencia="
					.concat(idLineaSugerencia.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (carrito == null) {
			return new ResponseEntity<Map<String, Object>>(HttpStatus.CREATED);
		}
		response.put("mensaje", "se ha eliminado la linea de sugerencia  con id=".concat(idLineaSugerencia.toString()));
		response.put("data", carrito);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}

	@DeleteMapping("/pedido/lineaMenu")
	public ResponseEntity<?> deletePedidoMenu(@RequestParam(value = "idPedido", required = true) Long idPedido,
			@RequestParam(value = "idLineaMenu", required = true) Long idLineaMenu) {

		Map<String, Object> response = new HashMap<>();
		Pedido carrito = null;
		try {
			carrito = pedidoLineaMenuService.deleteLineaMenuId(idPedido, idLineaMenu);
		} catch (DataAccessException e) {
			response.put("mensaje", "sugerencia de pedido idLineaMenu="
					.concat(idLineaMenu.toString().concat(" error al eliminar en la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (carrito == null) {
			return new ResponseEntity<Map<String, Object>>(HttpStatus.CREATED);
		}
		response.put("mensaje", "se ha eliminado la linea menu con id=".concat(idLineaMenu.toString()));
		response.put("data", carrito);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}

//	@DeleteMapping("/pedido/menu/{id}")
//	public ResponseEntity<?> deletePedidoMenu(@PathVariable Long id) {
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//			pedidoLineaMenuService.deleteById(id);
//		} catch (DataAccessException e) {
//			response.put("mensaje",
//					"menu de pedido id=".concat(id.toString().concat(" error al eliminar en la base de datos")));
//			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		response.put("mensaje", "menu de pedido id=".concat(id.toString().concat(" eliminado de la base de datos")));
//		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
//	}

//	@GetMapping("/pedido/byid/{id}")
//	public ResponseEntity<?> getPedidoById (@PathVariable Long id) {
//		Map<String, Object> response = new HashMap<>();
//	    Pedido pedido;
//		try {
//			pedido = pedidoService.findById(id);
//			
//		} catch (DataAccessException e) {
//			response.put("mensaje",
//					"pedido id=".concat(id.toString().concat(" error acceso a la base de datos")));
//			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
//			
//			log.debug("error en findById=" + e.toString());
//			
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		
//		if (pedido == null) {
//			return new ResponseEntity<Map<String, Object>>(HttpStatus.CREATED);
//		}
//		
//		response.put("mensaje", "acceso satisfactorio a pedido con id=".concat(pedido.getId().toString()));
//		response.put("data", pedido);
//		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
//	}
//	

	@GetMapping("/pedido/usuario")
	public ResponseEntity<?> getPedidoByUsuario(@RequestParam(value = "usuario", required = true) String usr) {
		Map<String, Object> response = new HashMap<>();
		Set<Pedido> pedidos;
		Pedido pedido;

		try {
			pedidos = pedidoService.findByUsuarioEstadoCreacion(usr, EnumEstadoPedido.CREACION);
			if (pedidos.isEmpty() || pedidos == null) {
				pedido = null;
			} else {
				pedido = pedidos.iterator().next();

			}

			// log.trace ("pedido obtenido:" + pedido.toString());

		} catch (DataAccessException e) {
			response.put("mensaje", "pedido id=".concat(usr.toString().concat(" error acceso a la base de datos")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (pedido == null) {
			return new ResponseEntity<Map<String, Object>>(HttpStatus.CREATED);
		}

		response.put("mensaje", "acceso satisfactorio a pedido con id=".concat(pedido.getId().toString()));
		response.put("data", pedido);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// @Valid @RequestBody PedidoConfirmacion pedidoConfirmacion, BindingResult
	// result) {

//	@GetMapping("/pedido/byid")
//	public ResponseEntity<?> getPedidoByIdNew (
//			@Valid @RequestBody Pedido pedido, BindingResult result) {
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//			pedido = pedidoService.findById(pedido.getId());
//			
//		} catch (DataAccessException e) {
//			response.put("mensaje",
//					"pedido id=".concat(pedido.getId().toString().concat(" error acceso a la base de datos")));
//			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
//			
//			
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		
//		if (pedido == null) {
//			return new ResponseEntity<Map<String, Object>>(HttpStatus.CREATED);
//		}
//		
//		response.put("mensaje", "acceso satisfactorio a pedido con id=".concat(pedido.getId().toString()));
//		response.put("data", pedido);
//		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
//	}

}