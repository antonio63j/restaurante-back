package com.afl.restaurante.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity

public class Sugerencia implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@NotNull
	@NotEmpty
	@Size(min = 4, max=100, message="debe tener de 4 a 100 posiciones") 
    @Column(name = "label", unique= true)
	private String label;

	private String tipo;
	
	private String imgFileName;
	
	private String descripcion;
	
	@NotNull
	private double precio;
	
//    @JsonIgnoreProperties(value={"sugerencia", "hibernateLazyInitializer", "handler"}, allowSetters=true)
//    @OneToMany(mappedBy = "sugerencia", cascade = CascadeType.ALL)
//    private Set<MenuSugerencia> menuSugerencias = new HashSet<>();


}
