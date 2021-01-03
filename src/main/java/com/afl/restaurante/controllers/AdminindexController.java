package com.afl.restaurante.controllers;

import java.io.IOException;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afl.restaurante.entities.Adminindex;
import com.afl.restaurante.entities.Proyecto;
import com.afl.restaurante.services.IAdminindexService;
import com.afl.restaurante.services.IProyectoService;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping("/api")

public class AdminindexController {

	  @Autowired
	  IAdminindexService adminindexService;
	  
	  @ExceptionHandler(HttpMessageNotReadableException.class)
	  public void messageNotReadableException(
	          HttpMessageNotReadableException exception,
	          HttpServletRequest request,
	          HttpServletResponse response)
	          throws IOException {
	        response.sendError(HttpStatus.NOT_IMPLEMENTED.value(), exception.getMessage());
	        
     }
	    //@Secured({"ROLE_ADMIN"})
		@GetMapping("/adminindex")
		public List<Adminindex> findAll () {
			return adminindexService.findAll();
		} 


}