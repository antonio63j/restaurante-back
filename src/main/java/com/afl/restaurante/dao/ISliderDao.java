package com.afl.restaurante.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afl.restaurante.entities.Slider;

public interface ISliderDao extends JpaRepository<Slider, Long> {
	

}
