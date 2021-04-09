package com.afl.restaurante.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afl.restaurante.dao.IPedidoLineaSugerenciaDao;
import com.afl.restaurante.entities.PedidoLineaSugerencia;

@Service

public class PedidoLineaSugerenciaService implements IPedidoLineaSugerenciaService {
	
	@Autowired IPedidoLineaSugerenciaDao pedidoSugerenciaDao;

	@Override
	public void deleteById(Long id) {
		pedidoSugerenciaDao.deleteById(id);
	}

	@Override
	public PedidoLineaSugerencia save(PedidoLineaSugerencia pedidoLineaSugerencia) {
		return pedidoSugerenciaDao.save(pedidoLineaSugerencia);
	}


}
