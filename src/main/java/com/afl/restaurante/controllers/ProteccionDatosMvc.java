package com.afl.restaurante.controllers;

import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.afl.restaurante.entities.Empresa;
import com.afl.restaurante.entities.Usuario;
import com.afl.restaurante.services.IUsuarioService;

@Controller
@RequestMapping("/api")
public class ProteccionDatosMvc {
	
	private Logger log = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private Empresa empresaStore;
	
	  @GetMapping("/politica-cookies")
	    public ModelAndView console(final HttpServletRequest request, final Model model) {

	        Locale locale = request.getLocale();

     
	        String urlWeb = empresaStore.getUrlWeb();

	        model.addAttribute("urlWeb", urlWeb);
//	        model.addAttribute("empresa", empresaStore.getNombre());
	        
	        model.addAttribute("empresa", empresaStore);


	        return new ModelAndView("politica-cookies");
	    }


}
