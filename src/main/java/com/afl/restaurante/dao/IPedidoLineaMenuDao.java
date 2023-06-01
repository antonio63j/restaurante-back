package com.afl.restaurante.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afl.restaurante.entities.PedidoLineaMenu;

public interface IPedidoLineaMenuDao extends JpaRepository<PedidoLineaMenu, Long> {

}
