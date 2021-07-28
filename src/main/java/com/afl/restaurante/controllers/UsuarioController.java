package com.afl.restaurante.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.ModelMap;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.afl.restaurante.emails.EmailService;
import com.afl.restaurante.entities.Curso;
import com.afl.restaurante.entities.Empresa;
import com.afl.restaurante.entities.Proyecto;
import com.afl.restaurante.entities.Usuario;
import com.afl.restaurante.services.ICursoService;
import com.afl.restaurante.services.IUsuarioService;

import org.springframework.web.bind.annotation.ExceptionHandler;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class UsuarioController {
	
	private Logger log = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;
	
    @Autowired
    private EmailService emailService;
    
	@Value("${app.timeActivacionMins:48}")
	private int timerActivacion; 
	
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void messageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
    }
    
    
	@PostMapping("/usuario/registro")
	public ResponseEntity<?> create(@Valid @RequestBody Usuario usuario, 
			                        BindingResult result, HttpServletRequest request,
			                        final Locale locale) throws MessagingException, IOException {
		Usuario usuarioNew = null;

		Map<String, Object> response = new HashMap<>();



		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(fielderror -> "El campo '" + fielderror.getField() + "' " + fielderror.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			
			if (usuarioService.existsByUsername(usuario.getUsername())) {
				   response.put("mensaje", "Este email ya ha sido utilizado");
				   response.put("error", usuario.getUsername() + " ya ha sido utilizado");
				   log.debug("Error en registro de usuario " + usuario.getUsername() + "," + (String) response.get("error")); 
				   return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
			}
			
			usuario.setFechaRegistro(LocalDateTime.now());
			usuario.setFinalizadaActivacion(false);
			usuario.setCodActivacion(usuario.getUsername() + UUID.randomUUID().toString());
			usuario.setEnabled(true);

			String urlConfirmacion = request.getRequestURL().toString() + "/confirmacion";
            enviarEmailActivacion(usuario, urlConfirmacion, locale);
            
			usuarioNew = usuarioService.save(usuario);
			
		} 
		  catch (DataAccessException e) {
			response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible registrar usuario");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			log.debug("Error en registro de usuario " + usuario.getEmail() + "," + (String) response.get("error")); 
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		  catch (MessagingException e) {
			response.put("mensaje", "error en el envio email de activación");
			response.put("error", e.getMessage().concat(e.getCause().toString()));
			log.debug("Error en envio email " + usuario.getEmail() + "," + (String) response.get("error")); 
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		
		response.put("mensaje", "usuario registrado");
		response.put("usuario", usuarioNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/usuarios", method = RequestMethod.GET)
	// @Secured({"ROLE_ADMIN"})
    public List<Usuario> findAll() {
    	 return usuarioService.findAll();
    }
	
	@RequestMapping(value = "/usuario", method = RequestMethod.GET)
	public Usuario getUsuario(
			@RequestParam(value = "cuenta"  , required = true) String cuenta) {
		return usuarioService.findByUsername(cuenta);
	}
	
	@PutMapping("/usuario/resetpwd")
	public ResponseEntity<?> sendCodigoResetPwd(
			@RequestBody Usuario usuario, 
			BindingResult result,
			final Locale locale) throws MessagingException, IOException {

		Usuario usuarioUpdated = null;
		Usuario usuarioActual = null;
		Map<String, Object> response = new HashMap<>();
		
//		if (result.hasErrors()) {
//			List<String> errors = result.getFieldErrors().stream()
//				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
//				.collect(Collectors.toList());
//			response.put("errors", errors);
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
//		}

		usuarioActual = usuarioService.findByUsername(usuario.getUsername());
		if (usuarioActual == null) {
			response.put("mensaje", "Imposible reset password, La cuenta no existe");
			response.put("error", "la cuenta no existe");
			log.debug("Error, " + (String) response.get("error")); 	
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			
     		//usuarioActual.setNombre(usuario.getNombre());
			//usuarioActual.setApellidos(usuario.getApellidos());
			//usuarioActual.setTelefono(usuario.getTelefono());
			//usuarioActual.setEmail(usuario.getEmail());
			//usuarioActual.setFechaRegistro(usuario.getFechaRegistro());
			//usuarioActual.setCodActivacion(usuario.getCodActivacion());
			//usuarioActual.setFinalizadaActivacion(usuario.isFinalizadaActivacion());

			//LocalDate currentDate = LocalDate.now();
		    //LocalTime currentTime = LocalTime.now();
		    //usuarioActual.setFechaResetPwd(LocalDateTime.of(currentDate, currentTime));
			usuarioActual.setFechaResetPwd(LocalDateTime.now());
	        			
			// usuarioActual.setCodResetPwd(usuario.getCodResetPwd());
			Random rand = new Random();
			int num = rand.nextInt(900000) + 100000; 
			usuarioActual.setCodResetPwd(String.valueOf(num));

			//usuarioActual.setEnabled(usuario.getEnabled());
			//usuarioActual.setAceptaEmails(usuario.getAceptaEmails());
						
			usuarioUpdated = usuarioService.save(usuarioActual);
			
            enviarEmailResetPwd(usuarioUpdated, locale);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar datos de usuario=".concat(usuario.getUsername()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "actualizado el usuario =".concat(usuario.getUsername()));
		response.put("usuario", usuarioUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping("/usuario/update")
	public ResponseEntity<?> usuarioUpdate(
			@RequestBody Usuario usuario, 
			BindingResult result,
			final Locale locale) throws MessagingException, IOException {

		Usuario usuarioUpdated = null;
		Usuario usuarioActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		log.debug(usuario.toString());
		
		usuarioActual = usuarioService.findById(usuario.getId());
		if (usuarioActual == null) {
			response.put("mensaje", "Imposible reset password, La cuenta no existe");
			response.put("error", "la cuenta no existe");
			log.debug("Error, " + (String) response.get("error")); 	
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			
     		usuarioActual.setNombre(usuario.getNombre());
			usuarioActual.setApellidos(usuario.getApellidos());
			usuarioActual.setTelefono(usuario.getTelefono());
			usuarioActual.setEmail(usuario.getEmail());
			usuarioActual.setDirecciones(usuario.getDirecciones());
			usuarioActual.setAceptaEmails(usuario.getAceptaEmails());
			
			usuarioUpdated = usuarioService.save(usuarioActual);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "error al actualizar datos de usuario=".concat(usuario.getUsername()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "actualizado el usuario =".concat(usuario.getUsername()));
		response.put("usuario", usuarioUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping("/usuario/changepwd")
	public ResponseEntity<?> changePwd(
			@Valid @RequestBody Usuario usuario, 
			BindingResult result,
			final Locale locale, Temporal to) throws MessagingException, IOException {

		Usuario usuarioUpdated = null;
		Usuario usuarioActual = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
				.map(fielderror -> "El campo '"+ fielderror.getField() + "' " + fielderror.getDefaultMessage())
				.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		usuarioActual = usuarioService.findByUsername(usuario.getUsername());
		if (usuarioActual == null) {
			response.put("mensaje", "Imposible reset password, La cuenta no existe");
			response.put("error", "la cuenta no existe");
			log.debug("Error, " + (String) response.get("error")); 	
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		};
		
		
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(usuarioActual.getFechaResetPwd() /*from*/, now  /*to */);
		if (duration.toMinutes() > timerActivacion) {
			response.put("mensaje", "No ha sido posible finalizar el reset de password");
			response.put("error", "solicitud de reset de password fuera de plazo, usuario :" + usuario.getUsername());
			log.info("Error, " + (String) response.get("error"));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		if (!usuario.getCodResetPwd().equals(usuarioActual.getCodResetPwd())) {
			response.put("mensaje", "No es posible establcer password debido a que el código para reset es invalido");
			response.put("error", "código de activación invalido");
			log.debug("Error, " + (String) response.get("error")); 	
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		};
		
		try {
			
     		//usuarioActual.setNombre(usuario.getNombre());
			//usuarioActual.setApellidos(usuario.getApellidos());
			//usuarioActual.setTelefono(usuario.getTelefono());
			//usuarioActual.setEmail(usuario.getEmail());
			//usuarioActual.setFechaRegistro(usuario.getFechaRegistro());
			//usuarioActual.setCodActivacion(usuario.getCodActivacion());
			//usuarioActual.setFinalizadaActivacion(usuario.isFinalizadaActivacion());
			// usuarioActual.setFechaResetPwd(usuario.getFechaResetPwd());
     		// usuarioActual.setCodResetPwd(usuario.getCodResetPwd());
			//usuarioActual.setEnabled(usuario.getEnabled());
			//usuarioActual.setAceptaEmails(usuario.getAceptaEmails());
			
			
            usuarioActual.setPassword(usuario.getPassword());			
			usuarioUpdated = usuarioService.save(usuarioActual);
			
            enviarEmailResetPwd(usuarioUpdated, locale);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "error en reset de password =".concat(usuario.getUsername()));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "realizado con éxito el reset de password, usuario =".concat(usuario.getUsername()));
		response.put("usuario", usuarioUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}


	public void enviarEmailActivacion(Usuario user, String url, Locale locale) throws MessagingException, IOException {
		
		String recipientEmail = user.getUsername();
		String recipientName = user.getNombre();
        String urlActivacion = url + "?token=" + user.getCodActivacion();
        
        String body = this.emailService.getMailActivacionCuentaTemplate();

        this.emailService.sendMailActivacionCuenta(
            recipientName, recipientEmail, body, urlActivacion, locale);

	}
	
	public void enviarEmailResetPwd(Usuario user, Locale locale) throws MessagingException, IOException {
		
		String recipientEmail = user.getUsername();
		String recipientName = user.getNombre();
        
        String body = this.emailService.getMailResetPwdTemplate();

        this.emailService.sendMailResetPwd(
            recipientName, recipientEmail, body, user.getCodResetPwd(), String.valueOf(timerActivacion), locale);

	}
	
}
