package com.afl.restaurante.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.IPedidoDao;
import com.afl.restaurante.dao.IPedidoLineaSugerenciaDao;
import com.afl.restaurante.entities.Pedido;
import com.afl.restaurante.entities.PedidoLineaSugerencia;

@Service

public class PedidoLineaSugerenciaService implements IPedidoLineaSugerenciaService {
	
	@Autowired IPedidoLineaSugerenciaDao pedidoLineaSugerenciaDao;
	
	@Autowired IPedidoDao pedidoDao;


	@Override
	@Transactional
	public void deleteById(Long id) {
		pedidoLineaSugerenciaDao.deleteById(id);
	}

	@Override
	@Transactional
	public PedidoLineaSugerencia save(PedidoLineaSugerencia pedidoLineaSugerencia) {
		return pedidoLineaSugerenciaDao.save(pedidoLineaSugerencia);
	}

	@Override
	@Transactional
	public Pedido deleteLineaSugerenciaById(Long idPedido, Long idLineaSugerencia) {
		pedidoLineaSugerenciaDao.deleteById(idLineaSugerencia);
		
		pedidoLineaSugerenciaDao.flush();
		
		Pedido carrito = pedidoDao.findById(idPedido).orElse(null);
		
		if (carrito == null | (carrito.getPedidoLineaSugerencias().size() == 0 & carrito.getPedidoLineaMenus().size() == 0)) {
			pedidoDao.deleteById(idPedido);
			return null;
		} else {
			// carrito.setCalculos();
			return pedidoDao.saveAndFlush(carrito);
		}
	}


}
