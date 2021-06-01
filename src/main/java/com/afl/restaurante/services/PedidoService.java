package com.afl.restaurante.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.controllers.MenuController;
import com.afl.restaurante.dao.IPedidoDao;
import com.afl.restaurante.dao.IPedidoLineaMenuDao;
import com.afl.restaurante.dao.IPedidoLineaSugerenciaDao;
import com.afl.restaurante.entities.EnumEstadoPedido;
import com.afl.restaurante.entities.Herramienta;
import com.afl.restaurante.entities.Pedido;
import com.afl.restaurante.entities.PedidoLineaMenu;
import com.afl.restaurante.entities.PedidoLineaSugerencia;
import com.afl.restaurante.entities.Sugerencia;

@Service
public class PedidoService implements IPedidoService {

	private Logger log = LoggerFactory.getLogger(MenuController.class);

	@Autowired
	private IPedidoDao pedidoDao;

	@Autowired
	IPedidoLineaSugerenciaDao pedidoLineaSugerenciaDao;

	@Autowired
	IPedidoLineaMenuDao pedidoLineaMenuDao;
	
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

		Set<Pedido> pedidos;
		Pedido pedido2;

		pedidos = findByUsuarioEstadoCreacion(pedido.getUsuario(), EnumEstadoPedido.CREACION);
		if (pedidos.isEmpty() || pedidos == null) {
		} else {
			pedido2 = pedidos.iterator().next();
			pedido.setId(pedido2.getId());

		}

//		Pedido pedidoNew = pedidoDao.saveAndFlush(pedido);
//		Pedido pedidoNew2 = pedidoDao.findById(pedidoNew.getId()).orElse(null);
//		if (pedidoNew2 == null) {
//			log.debug("No se ha podido leer el pedido en proceso de actualizacion, pedido es null");
//			return null;
//		}
//		// pedidoNew2.setCalculos();
//		Pedido pedidoNew3 = pedidoDao.saveAndFlush(pedidoNew2);

		Pedido pedidoNew3 = pedidoDao.saveAndFlush(pedido);

		return pedidoNew3;
	}

	@Override
	@Transactional
	public Pedido savePedido(Pedido pedido) {
		return pedidoDao.save(pedido);
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

	@Transactional
	@Override
	public Pedido confirmarPedido(Pedido pedido) {

		Pedido pedidoAct = null;
		Pedido pedidoResult = null;

		pedidoAct = pedidoDao.findById(pedido.getId()).orElse(null);

		if (pedidoAct.getEstadoPedido() == EnumEstadoPedido.CONFIRMADO) {
			return null;
		} else {
			if (pedidoAct.getEstadoPedido() == EnumEstadoPedido.CREACION) {

				if (!preciosSinCambio(pedido)) {
					pedidoResult = pedidoDao.saveAndFlush(pedido);
					return pedidoResult;
				}

				pedido.setEstadoPedido(EnumEstadoPedido.CONFIRMADO);
				pedidoResult = pedidoDao.saveAndFlush(pedido);
				return null;
			}
		}

		return null;

	}
	
	@Override
	@Transactional (readOnly = true)
	public Page<Pedido> findAll(Specification<Pedido> especification, Pageable pageable) {
		return pedidoDao.findAll (especification, pageable);
	}
	

	boolean preciosSinCambio(Pedido pedido) {

		PedidoLineaSugerencia linSugerencia;
		PedidoLineaMenu linMenu;
		int c;


		for (PedidoLineaSugerencia lineaSugerencia : pedido.getPedidoLineaSugerencias()) {
			linSugerencia = pedidoLineaSugerenciaDao.findById(lineaSugerencia.getId()).orElse(null);
			
//			if (linSugerencia.getSugerencia().getPrecio() != lineaSugerencia.getSugerencia().getPrecio()) {
//				return false;
//			}
			
			c = Double.compare (linSugerencia.getSugerencia().getPrecio(), lineaSugerencia.getSugerencia().getPrecio());
			if (c != 0){
				return false;
			}
		}
		
		for (PedidoLineaMenu lineaMenu : pedido.getPedidoLineaMenus()) {
			linMenu = pedidoLineaMenuDao.findById(lineaMenu.getId()).orElse(null);
			
//			if (linMenu.getMenu().getPrecio() != lineaMenu.getMenu().getPrecio()) {
//				return false;
//			}
//			
			c = Double.compare (linMenu.getMenu().getPrecio(), lineaMenu.getMenu().getPrecio());
			if (c != 0){
				return false;
			}
		}

		return true;
	}

}
