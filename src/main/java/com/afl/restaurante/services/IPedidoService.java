package com.afl.restaurante.services;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.afl.restaurante.entities.EnumEstadoPedido;
import com.afl.restaurante.entities.Pedido;
import com.afl.restaurante.entities.Sugerencia;

public interface IPedidoService {

	public Pedido findById(Long id);

	public Pedido save(Pedido pedido);

	public void deleteById(Long id);
	
	public Set<Pedido> findByUsuarioEstadoCreacion(String usuario, EnumEstadoPedido estado);

	public Pedido confirmarPedido(Pedido pedido);
	
	public Page<Pedido> findAll (Specification<Pedido> especification, Pageable pageable);

	
}
