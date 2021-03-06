package com.afl.restaurante.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Data
@Table(name = "empresa")
public class Empresa implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, length = 60)
	@NotNull
	@NotEmpty
	private String nombre;
	
	@Column (name ="direccion", length = 60)
	private String direccion;
	
	@Column (name ="provincia")
	@Size(min = 4, max=30, message="debe tener de 4 a 30 posiciones")
	private String provincia;
	
	@Column (name ="telefono", length = 11)
	private String telefono;
	
	@Column (name = "urlweb", length = 60)
	private String urlWeb;
	
	@Column (name = "email", length = 60)
	private String email;
	
	@Column (name = "horario")
	private String horario;
	
	@Column(columnDefinition="TEXT")
	private String descripcionBreve;
	
	@Column(columnDefinition="TEXT")
	private String portada;
	
//	@JsonIgnoreProperties(value={"nombre", "hibernateLazyInitializer", "handler"}, allowSetters=true)
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="nombre", cascade=CascadeType.ALL)
//	private List<Slider> sliders = new ArrayList<>();
	
	
}
