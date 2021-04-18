package com.afl.restaurante.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.IPedidoDao;
import com.afl.restaurante.dao.IPedidoLineaMenuDao;
import com.afl.restaurante.entities.Pedido;
import com.afl.restaurante.entities.PedidoLineaMenu;

@Service

public class PedidoLineaMenuService implements IPedidoLineaMenuService {

	@Autowired IPedidoLineaMenuDao pedidoLineaMenuDao;
	
	@Autowired IPedidoDao pedidoDao;

	
	@Override
	@Transactional
	public void deleteById(Long id) {
		pedidoLineaMenuDao.deleteById (id);

	}

	@Override
	@Transactional
	public PedidoLineaMenu save(PedidoLineaMenu pedidoLineaMenu) {
		return pedidoLineaMenuDao.save (pedidoLineaMenu);
	}

	@Override
	@Transactional
	public Pedido deleteLineaMenuId(Long idPedido, Long idLineaMenu) {
		pedidoLineaMenuDao.deleteById(idLineaMenu);
		
		pedidoLineaMenuDao.flush();
		
		Pedido carrito = pedidoDao.findById(idPedido).orElse(null);
		
		if (carrito == null | (carrito.getPedidoLineaSugerencias().size() == 0 & carrito.getPedidoLineaMenus().size() == 0)) {
			pedidoDao.deleteById(idPedido);
			return null;
		} else {
			carrito.setCalculos();
			return pedidoDao.saveAndFlush(carrito);
		}
	}
	
	

}
