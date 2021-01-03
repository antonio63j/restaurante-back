package com.afl.restaurante.controllers;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
			
			LocalDate currentDate = LocalDate.now();
		    LocalTime currentTime = LocalTime.now();
			usuario.setFechaRegistro(LocalDateTime.of(currentDate, currentTime));

			usuario.setFinalizadaActivacion(false);
			
			usuario.setCodActivacion(usuario.getUsername() + UUID.randomUUID().toString());

			usuario.setEnabled(true);
//			Timestamp ts = new Timestamp(System.currentTimeMillis());
//			long milisec = ts.getTime() - usuario.getFechaRegistro().getTime();
//			System.out.println("milisec = " + milisec);
			
			usuarioNew = usuarioService.save(usuario);
			String urlConfirmacion = request.getRequestURL().toString() + "/confirmacion";
            enviarEmailActivacion(usuario, urlConfirmacion, locale);
			
		} 
		  catch (DataAccessException e) {
			response.put("mensaje", "error en el acceso a la base de datos, no ha sido posible registrar usuario");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			log.debug("Error en registro de usuario " + usuario.getEmail() + "," + (String) response.get("error")); 
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
	
	public void enviarEmailActivacion(Usuario user, String url, Locale locale) throws MessagingException, IOException {
		
		String recipientEmail = user.getUsername();
		String recipientName = user.getNombre();
        String urlActivacion = url + "?token=" + user.getCodActivacion();
        
        String body = this.emailService.getEditableMailTemplate();

        this.emailService.sendEditableMail(
            recipientName, recipientEmail, body, urlActivacion, locale);

	}

}
