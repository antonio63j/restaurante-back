package com.afl.restaurante.services;

import java.util.Set;

import com.afl.restaurante.entities.Pedido;
import com.afl.restaurante.entities.PedidoLineaSugerencia;

public interface IPedidoLineaSugerenciaService {
	
	public void deleteById (Long id);

	public PedidoLineaSugerencia save (PedidoLineaSugerencia PedidoLineaSugerencia);
	
	public Pedido deleteLineaSugerenciaById(Long idPedido, Long idLineaSugerencia);
	
}