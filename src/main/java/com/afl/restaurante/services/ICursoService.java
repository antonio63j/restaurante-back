package com.afl.restaurante.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.afl.restaurante.entities.Curso;

public interface ICursoService {
	
   public List<Curso> findAll();
   
   public Page<Curso> findAll(Pageable pageable);
   
   public List<Curso> search(String term);
   
   public List<Curso> findAll(Sort sort);
   
   Curso save (Curso curso);

   void delete (Long id);
   
   public Curso findById (Long id);
   
}
