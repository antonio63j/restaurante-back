package com.afl.restaurante.entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
@Table (name = "herramienta")

public class Herramienta {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
	
	@Column(name="nombre", unique = true, length = 80)
	private String nombre;
	
	@Column (name = "tipo")
	private String tipo;
	
	@Column (name ="nivel")
	private String nivel;
	
	@Column (name ="comentario", length = 80)
	private String comentario;
	
    @JsonIgnore
	@ManyToMany (mappedBy = "herramientas")
	private List<Proyecto> proyectos;

}
