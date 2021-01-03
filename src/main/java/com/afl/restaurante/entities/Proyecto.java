package com.afl.restaurante.entities;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Data
@Table (name="proyecto")

public class Proyecto {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	
	@NotNull
	@NotEmpty
	@Size(min = 4, max=79, message="debe tener de 4 a 79 posiciones")
	@Column(unique = true)
	private String nombre;
	
	@NotNull
	@Temporal(TemporalType.DATE)
	private Date inicio;
	
	@NotNull
	@Temporal(TemporalType.DATE)
	private Date fin;
	
	@Column(columnDefinition="TEXT")
    private String descripcion;	

	@ManyToMany
	@JoinTable(
			  name = "proyecto_herramienta", 
			  joinColumns = @JoinColumn(name = "proyecto_id"), 
			  inverseJoinColumns = @JoinColumn(name = "herramienta_id"),
			  uniqueConstraints = @UniqueConstraint(columnNames={"proyecto_id", "herramienta_id"})
			  )

	private List<Herramienta> herramientas;
	

	@JsonIgnoreProperties(value={"proyectos", "hibernateLazyInitializer", "handler"}, allowSetters=true)
	@ManyToOne (fetch=FetchType.LAZY)
    private Cliente cliente;

}
