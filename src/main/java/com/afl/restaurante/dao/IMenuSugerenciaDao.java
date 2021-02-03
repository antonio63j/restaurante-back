package com.afl.restaurante.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.afl.restaurante.entities.MenuSugerencia;

public interface IMenuSugerenciaDao extends JpaRepository<MenuSugerencia, Long> {

	@Query ("select ms from MenuSugerencia ms where ms.sugerencia.id = ?1")
	List<MenuSugerencia> findMenuSugerencias(Long id);



//	@Query ("select ms from MenuSugerencia ms where ms.sugerencia_id = ?1")
//	void deleteAllSugerenciaId(Long id);

//	int deleteMenuSugerenciaByIdEquals(Long id);
	
}
