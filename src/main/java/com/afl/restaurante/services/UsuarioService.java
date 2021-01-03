package com.afl.restaurante.services;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.IUsuarioDao;
import com.afl.restaurante.entities.Usuario;

@Service
public class UsuarioService implements UserDetailsService, IUsuarioService {

	private Logger logger = LoggerFactory.getLogger(UsuarioService.class);
	
	@Autowired
	private IUsuarioDao usuarioDao;
	
	@Override
	@Transactional (readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioDao.findByUsername (username);
		if (usuario == null) {
			logger.info ("error no existe el usuario '" + username + "' en el sistema");
			throw new UsernameNotFoundException("error no existe el usuario '" + username + "' en el sistema");
		}
		
		List<GrantedAuthority> authorities = usuario.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getNombre()))
				.peek(authority -> logger.info("Role:" + authority.getAuthority()))
				.collect(Collectors.toList());
		
		// Lo mismo que:
		/*
		List<GrantedAuthority> authorities = usuario.getRoles()
				.stream()
				.map(role -> {
   			    	            return new SimpleGrantedAuthority(role.getNombre());
   			    	         }
				    )
				.peek(authority -> logger.info("Role:" + authority.getAuthority()))
				.collect(Collectors.toList());
		*/
		
		// String encodedPassword = passwordEncode.encode(usuario.getPassword());
		BCryptPasswordEncoder passwordEncode = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncode.encode(usuario.getPassword());
		boolean credentialsnNonExpired = true;
		boolean nonAcountNotExpered = true;
		boolean nonBlocked = usuario.isFinalizadaActivacion();
		
		return new User(usuario.getUsername(), encodedPassword, usuario.getEnabled(), nonAcountNotExpered, credentialsnNonExpired, nonBlocked, authorities);

	}

	@Override
	@Transactional (readOnly = true)
	public Usuario findByUsername(String username) {
		return usuarioDao.findByUsername(username);
	}
	
	@Override
	@Transactional
	public Usuario save(Usuario usuario) {
		return usuarioDao.save(usuario);
	}
	
	@Override
	@Transactional (readOnly = true)
	public Usuario findByCodActivacion (String codigo) {
		return usuarioDao.findByCodActivacion(codigo);
	}
	
	@Override
	@Transactional
	public boolean activarUsuario(String token) {
		boolean activado = false;
		Usuario usuario = usuarioDao.findByCodActivacion(token);
		logger.info("activando usuario");
		logger.debug(usuario.getFechaRegistro().toString());
		return activado;
	}

	@Override
	@Transactional (readOnly = true)
	public List<Usuario> findAll() {
		return (List<Usuario>) usuarioDao.findAll();
	}
	
	@Override
	@Transactional (readOnly = true)
	public boolean existsByUsername(String username) {
		return usuarioDao.existsByUsername(username);
	}
	  

}
