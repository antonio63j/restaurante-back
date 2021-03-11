package com.afl.restaurante.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.afl.restaurante.entities.Menu;
import com.afl.restaurante.entities.Tipoplato;

public interface IMenuDao extends JpaRepository<Menu, Long> {

    @Query ("select m from Menu m order by m.label asc")
    public Set<Menu> findAllByLabel();
  
	public Set<Menu> findByVisibleIsOrderByLabel(boolean b);
	
}
