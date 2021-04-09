package com.afl.restaurante.services;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.IPedidoDao;
import com.afl.restaurante.entities.EnumEstadoPedido;
import com.afl.restaurante.entities.Herramienta;
import com.afl.restaurante.entities.Pedido;

@Service
public class PedidoService implements IPedidoService {
	
	@Autowired 
    private IPedidoDao pedidoDao;
	
	@Autowired
	EntityManager em;
	
	@Override
	@Transactional
	public Pedido findById(Long id) {
		return pedidoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Pedido save(Pedido pedido) {
		
//		Pedido pedidoNew = pedidoDao.saveAndFlush(pedido);
//		em.refresh(pedidoNew); Para relleno de todos los atributos de pedido
//		pedidoNew.setTotal(pedidoNew.getTotal());
//      pedidoNew = pedidoDao.saveAndFlush(pedidoNew);
        
		pedido.setCalculos();
		Pedido pedidoNew = pedidoDao.saveAndFlush(pedido);
       	return pedidoNew;
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
        pedidoDao.deleteById(id);
	}

	@Override
	public Set<Pedido> findByUsuarioEstadoCreacion(String usuario, EnumEstadoPedido estado) {
		// TODO Auto-generated method stub
		return pedidoDao.findByUsuarioContainigIgnoreCaseAndEstadoPedidoContainingIgnoreCase_2(usuario, estado);
	}
	
}
