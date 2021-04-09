package com.afl.restaurante.services;

import com.afl.restaurante.entities.PedidoLineaSugerencia;

public interface IPedidoLineaSugerenciaService {
	
	public void deleteById (Long id);

	public PedidoLineaSugerencia save (PedidoLineaSugerencia PedidoLineaSugerencia);
}
