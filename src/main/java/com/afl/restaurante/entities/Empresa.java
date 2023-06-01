package com.afl.restaurante.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Data

@Table(name = "empresa")
public class Empresa implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, length = 30)
	@NotNull
	@NotEmpty
	@Size(min = 2, max=30, message="debe tener de 4 a 30 posiciones")
	private String nombre;
	
	@Column (name ="cif", length = 9)
	private String cif;

	
	@Column (name ="direccion", length = 60)
	private String direccion;
	
	@Column (name ="localidad", length = 40)
	private String localidad;
	
	@Column (name ="provincia")
	@Size(min = 4, max=30, message="debe tener de 4 a 30 posiciones")
	private String provincia;
	
	@Column (name ="telefono", length = 11)
	private String telefono;
	
	@Column (name = "urlweb", length = 60)
	private String urlWeb;
	
	@Column (name = "email", length = 60)
	private String email;
	
	@Column(columnDefinition="TEXT")
	private String descripcionBreve;
	
	@Column(columnDefinition="TEXT")
	private String portada;

	@ElementCollection
    private List<String> diasDescanso = new ArrayList<String>();
	
	@Column(name="hora_apertura", length = 5)
	private String horaApertura;
	
	@Column(name="hora_cierre", length = 5)
	private String horaCierre;
	
	private int horasMinPreparacionPedido;
	
	private int diasMaxEntregaPedido;
	
//	@JsonIgnoreProperties(value={"nombre", "hibernateLazyInitializer", "handler"}, allowSetters=true)
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="nombre", cascade=CascadeType.ALL)
//	private List<Slider> sliders = new ArrayList<>();
	
	
}
