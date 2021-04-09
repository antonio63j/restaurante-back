package com.afl.restaurante.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afl.restaurante.dao.IPedidoLineaMenuDao;
import com.afl.restaurante.entities.PedidoLineaMenu;

@Service

public class PedidoLineaMenuService implements IPedidoLineaMenuService {

	@Autowired IPedidoLineaMenuDao pedidoLineaMenuDao;
	
	@Override
	public void deleteById(Long id) {
		pedidoLineaMenuDao.deleteById (id);

	}

	@Override
	public PedidoLineaMenu save(PedidoLineaMenu pedidoLineaMenu) {
		return pedidoLineaMenuDao.save (pedidoLineaMenu);
	}

}
