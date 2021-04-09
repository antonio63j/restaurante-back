package com.afl.restaurante.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afl.restaurante.entities.PedidoLineaSugerencia;

public interface IPedidoLineaSugerenciaDao extends JpaRepository<PedidoLineaSugerencia, Long> {

}
