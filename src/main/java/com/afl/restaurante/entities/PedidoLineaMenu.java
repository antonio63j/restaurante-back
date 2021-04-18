package com.afl.restaurante.entities;
import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@NoArgsConstructor 
@Getter 
@Setter

@Entity

//@Table(name="pedido_linea_menu", uniqueConstraints = {
//	    @UniqueConstraint(columnNames = { "menu_id", "primero_id", "segundo_id", "postre_id", "pedido_id"  })
//	})
public class PedidoLineaMenu{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int cantidad;
     
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Menu menu;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Sugerencia primero;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Sugerencia segundo;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Sugerencia postre;
    

    
    @Override
	public String toString() {
		return "PedidoLineaMenu [id=" + id + ", cantidad=" + cantidad + ", menu=" + menu + ", primero=" + primero
				+ ", segundo=" + segundo + ", postre=" + postre + "]";
	}
    
    
}
