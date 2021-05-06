package com.afl.restaurante.request;


import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor 
@Getter 
@Setter

public class PedidoConfirmacion {

	private Long idCarrito;
	private LocalDateTime fhRecogidaSolicitada;
	// private String horaConfirmacion;
	private String nota;
    
}
