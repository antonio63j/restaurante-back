package com.afl.restaurante.controllers;

import java.io.IOException;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.afl.restaurante.emails.EmailService;
import com.afl.restaurante.entities.Empresa;
import com.afl.restaurante.entities.Usuario;
import com.afl.restaurante.services.IUsuarioService;

@Controller
@RequestMapping("/api")

public class UsuarioControlerMvc {

	private Logger log = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private Empresa empresaStore;
	
	@Value("${app.timeActivacionMins:48}")
	private int timerActivacion; 
	
//    @Autowired
//    private MessageSource messages;
	
	@GetMapping("/usuario/registro/confirmacion")
	//public ResponseEntity<?> activacionCuenta(
	// public String activacionCuenta(
	public ModelAndView activacionCuenta(
			@RequestParam(value = "token", defaultValue="ninguno",  required = true) String token,
			final ModelMap model
			
            ) throws MessagingException, IOException {
		
		Usuario usuario = null;
		Usuario usuarioNew = null;
		Map<String, Object> response = new HashMap<>();

		try {
			
			// usuarioService.activarUsuario(token);
			usuario = usuarioService.findByCodActivacion(token);
			if (usuario == null) {
				log.info("Error en solicitud de activación, no encontrado el código de activación " + token );
				return null;
			}
			
     		LocalDateTime to = LocalDateTime.now();
			
//			Duration duration = Duration.between(usuario.getFechaRegistro() /*from*/,  to /*to */);
//			if (duration.toHours() > timerActivacion) {
//				log.info("Solicitud de activación fuera de plazo, el usuario " + usuario.getUsername() + " queda sin activar");
//				return null;
//			}
			
			Duration duration = Duration.between(usuario.getFechaRegistro() /*from*/,  to /*to */);
			if (duration.toMinutes() > timerActivacion) {
				log.info("Solicitud de activación fuera de plazo, el usuario " + usuario.getUsername() + " queda sin activar");
				return null;
			}
		    
 			usuario.setFinalizadaActivacion(true);
			usuarioNew = usuarioService.save(usuario);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "error no ha sido posible finalizar la activación del usuario");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			log.error( response.values().toString());
			return null;
		}
		response.put("mensaje", "usuario activadoo");
		response.put("status", "ok");
		log.debug(response.values().toString());
		
        model.addAttribute("messageKey", usuario.getUsername());
        return new ModelAndView("redirect:/api/cuentaactivada", model);
		// return modelAndView;
		// return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
    @GetMapping("/cuentaactivada")
    public ModelAndView console(final HttpServletRequest request, final Model model, @RequestParam("messageKey") final Optional<String> messageKey) {

        Locale locale = request.getLocale();

        log.info("parametro de entrada messageKey = "+ messageKey.toString());
        
//        String scheme = request.getScheme();
//        String serverName = request.getServerName();
//        int serverPort = request.getServerPort();
//        String urlWeb = scheme + "://" + serverName +":"+ serverPort;
        
        String urlWeb = empresaStore.getUrlWeb();

        model.addAttribute("name", "Antonio");
        model.addAttribute("fechaActivacion", new Date());
        model.addAttribute("urlWeb", urlWeb);
        model.addAttribute("empresa", empresaStore.getNombre());
        
        if (!messageKey.isPresent()){
        	model.addAttribute("resultado",  "Entre en la web y verifique la activación de su cuenta");
        } else {
        	String username = messageKey.get();
        	Usuario usuario = usuarioService.findByUsername(username);
			if (usuario != null) {
				if (!usuario.isFinalizadaActivacion()) {
					model.addAttribute("resultado",  "Activación no se realizó en plazo");
				} else {
					model.addAttribute("resultado",  "Tu cuenta ha sido activada, ya puedes entrar en la web con tu usuario y password");
				}
			} else {
					model.addAttribute("resultado",  "Entre en la web y verifique la activación de su cuenta");
			  }
          };
          
 //        messageKey.ifPresent( key -> {
//                    String message = messages.getMessage(key, null, locale);
//                    model.addAttribute("message", message);
//                }
//        );

        return new ModelAndView("cuentaactivada");
    }
	
}
