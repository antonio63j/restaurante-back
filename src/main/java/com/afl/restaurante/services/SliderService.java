package com.afl.restaurante.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afl.restaurante.dao.ISliderDao;
import com.afl.restaurante.entities.Slider;

@Service
public class SliderService implements ISliderService {

	@Autowired
	public ISliderDao sliderDao; 
	
	@Override
	@Transactional (readOnly = true)
	public Slider findById(Long id) {
		return sliderDao.findById (id).orElse(null);
	}
	
	@Override
	@Transactional 
	public Slider save (Slider slider) {
		return sliderDao.save (slider);
	}	
	
	@Override
	@Transactional
	public void deleteById (Long id) {
		sliderDao.deleteById(id);
	}

}