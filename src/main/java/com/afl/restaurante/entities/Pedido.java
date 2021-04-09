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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@NoArgsConstructor 
@Getter 
@Setter

@Entity
public class Pedido implements Serializable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
//    @NotNull
//    @ManyToOne
//    @JoinColumn(name = "usuario_id", nullable = false)
//    private Usuario usuario;
    
    @NotNull
    //@ManyToOne
    //@JoinColumn(name = "userName", nullable = false)
    private String usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private EnumEstadoPedido estadoPedido;
    
    @NotNull
    private Double total;
    
    @NotNull
    private Double numArticulos;
    
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "pedido_id")
    private Set<PedidoLineaSugerencia> pedidoLineaSugerencias;

	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "pedido_id")
    private Set<PedidoLineaMenu> pedidoLineaMenus;
	
//    @JsonGetter("usuario")
//    public Long getTheName() {
//        return this.usuario.getId();
//    }
//    
    
	@Override
	public String toString() {
		return "Pedido [id=" + id + ", usuario=" + usuario + ", estadoPedido=" + estadoPedido + ", total=" + total
				+ ", pedidoLineaSugerencias=" + pedidoLineaSugerencias.toString() + ", pedidoLineaMenus=" 
				+ pedidoLineaMenus.toString() + "]";
	}
	
	public void setCalculos() {
		Double total = 0.0;
		Double numArticulos = 0.0;
		
		for( PedidoLineaSugerencia pedidoSugerencia : pedidoLineaSugerencias) {
			total = total + pedidoSugerencia.getSugerencia().getPrecio() * pedidoSugerencia.getCantidad();
			numArticulos = numArticulos + pedidoSugerencia.getCantidad();
		}
		
		for( PedidoLineaMenu pedidoMenu : pedidoLineaMenus) {
			total = total + pedidoMenu.getMenu().getPrecio() * pedidoMenu.getCantidad();
			numArticulos = numArticulos + pedidoMenu.getCantidad();

		}
		
		this.setTotal(total);
		this.setNumArticulos(numArticulos);

	}    

}
