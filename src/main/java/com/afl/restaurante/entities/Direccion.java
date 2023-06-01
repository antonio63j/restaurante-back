package com.afl.restaurante.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data

@Table(name = "direccion")
public class Direccion {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Size(min = 3, max = 60,message="debe tener de 3 a 60 posiciones")
	private String calle;
	
    @Column (name = "numero", length = 10)
	private String numero;
    
    private String planta;
    
    @Column (name = "puerta", length = 10)
    private String puerta;
    
    @NotNull
	@Column (name = "codigo_postal", length = 5)
	private Integer codigoPostal;
	
    @Size(min = 3, max = 40,message="debe tener de 3 a 40 posiciones")
	private String municipio;
	
    @Size(min = 3, max = 40,message="debe tener de 3 a 40 posiciones")
	private String provincia;
	
		
}
