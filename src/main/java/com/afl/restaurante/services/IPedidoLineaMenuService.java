package com.afl.restaurante.services;

import com.afl.restaurante.entities.PedidoLineaMenu;

public interface IPedidoLineaMenuService {

	public void deleteById (Long id);
	
	public PedidoLineaMenu save (PedidoLineaMenu pedidoLineaMenu);
}
