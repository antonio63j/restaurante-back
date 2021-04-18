package com.afl.restaurante.services;

import java.util.List;
import java.util.Set;

import com.afl.restaurante.entities.EnumEstadoPedido;
import com.afl.restaurante.entities.Pedido;

public interface IPedidoService {

	public Pedido findById(Long id);

	public Pedido save(Pedido pedido);

	public void deleteById(Long id);
	
	public Set<Pedido> findByUsuarioEstadoCreacion(String usuario, EnumEstadoPedido estado);
	
}
