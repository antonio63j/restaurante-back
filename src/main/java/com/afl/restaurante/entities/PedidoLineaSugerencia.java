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

@NoArgsConstructor 
@Getter 
@Setter

@Entity

@Table(name="pedido_linea_sugerencia", uniqueConstraints = {
	    @UniqueConstraint(columnNames = { "sugerencia_id", "pedido_id"  })
	})
public class PedidoLineaSugerencia{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private int cantidad;
    

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Sugerencia sugerencia;


//    @ManyToOne(optional = false, fetch = FetchType.EAGER)
//    private Sugerencia sugerencia;

//    
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "pedido_id")
//    private Pedido pedido;

	@Override
	public String toString() {
		return "PedidoLineaSugerencia [id=" + id + ", cantidad=" + cantidad + ", sugerencia=" + sugerencia + "]";
	}
    
    
}
