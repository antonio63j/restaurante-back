package com.afl.restaurante.services;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afl.restaurante.dao.IMenuDao;
import com.afl.restaurante.dao.IMenuSugerenciaDao;
import com.afl.restaurante.entities.Menu;
import com.afl.restaurante.entities.MenuSugerencia;

@Service
public class MenuService implements IMenuService {
	
	@Autowired
	private IMenuDao menuDao;
	
	@Autowired
	private IMenuSugerenciaDao menuSugerenciaDao;

	@Override
	@Transactional (readOnly = true)
	public Set<Menu> findAllByLabel() {
		return menuDao.findAllByLabel();
	}

	
	
	@Override
	public Menu findById(Long id) {
		// TODO Auto-generated method stub
		return menuDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Menu save(Menu menu) {
		return menuDao.save(menu);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		menuDao.deleteById(id);

	}
	
	@Override
	@Transactional
	public void deleteMenuSugerenciaById(Long id) {
		
//		List<MenuSugerencia> menuSugerenciaDao.removeBySugerencia(id);
		//int n = menuSugerenciaDao.deleteMenuSugerenciaByIdEquals(id);
		menuSugerenciaDao.deleteById(id);
		// sugerenciaDao.deleteById(id);
	}

	@Override
	@Transactional
	public MenuSugerencia saveMenuSugerencia(MenuSugerencia menuSugerencia) {
		
		return menuSugerenciaDao.save(menuSugerencia);
	}



	@Override
	public Set<Menu> findAllByLabelVisible(boolean visible) {
		return menuDao.findByVisibleIsOrderByLabel(visible);
	}

}
