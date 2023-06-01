package com.afl.restaurante.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afl.restaurante.dao.IAdminindexDao;
import com.afl.restaurante.entities.Adminindex;
import com.afl.restaurante.entities.Empresa;

@Service
public class AdminindexService implements IAdminindexService {
	
	@Autowired 
	IAdminindexDao adminindexDao;

	@Override
	public List<Adminindex> findAll() {
		// TODO Auto-generated method stub
		return adminindexDao.findAll();
	}




}
