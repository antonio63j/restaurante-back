package com.afl.restaurante.services;

import com.afl.restaurante.entities.Slider;

public interface ISliderService {

	public Slider findById(Long id);
	
	public Slider save (Slider slider);
	
	void deleteById (Long id);
}
