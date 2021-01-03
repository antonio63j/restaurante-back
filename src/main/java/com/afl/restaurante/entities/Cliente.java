package com.afl.restaurante.entities;

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name="clientes", uniqueConstraints = {
	    @UniqueConstraint(columnNames = { "empresa", "cliente", "inicio" })
	})
public class Cliente {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column (name = "empresa")
	private String empresa;
	
	@Column (name = "cliente")
	private String cliente;
	
	@Temporal(TemporalType.DATE)
	private Date inicio;
	
	@Temporal(TemporalType.DATE)
	private Date fin;
	
	@Column (name ="sector")
	private String sector;
	
	@Column (name ="empresafoto")
	private String empresafoto;
	
	@Column (name ="clientefoto")
	private String clientefoto;
	
	@Column (name = "actividad",columnDefinition="TEXT")
	private String actividad;
	
	@Column (name = "experiencia", columnDefinition="TEXT")
	private String experiencia;
	
	@JsonIgnoreProperties(value={"cliente", "hibernateLazyInitializer", "handler"}, allowSetters=true)
	@OneToMany(fetch=FetchType.LAZY, mappedBy="cliente", cascade=CascadeType.ALL)
	private List<Proyecto> proyectos = new ArrayList<>();
	
}
