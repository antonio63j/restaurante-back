package com.afl.restaurante.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name="menu_sugerencia", uniqueConstraints = {
	    @UniqueConstraint(columnNames = { "menu_id", "sugerencia_id"})
	})
public class MenuSugerencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    @NotNull
    @JsonIgnore
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sugerencia_id")
    @NotNull
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Sugerencia sugerencia;

    private boolean primerPlato;

    public MenuSugerencia(Menu menu, Sugerencia sugerencia, boolean primerPlato) {
    	this.menu = menu;
        this.sugerencia = sugerencia;
        this.primerPlato = primerPlato;
    }

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		result = prime * result + ((menu == null) ? 0 : menu.hashCode());
//		result = prime * result + (primerPlato ? 1231 : 1237);
//		result = prime * result + ((sugerencia == null) ? 0 : sugerencia.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		MenuSugerencia other = (MenuSugerencia) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		if (menu == null) {
//			if (other.menu != null)
//				return false;
//		} else if (!menu.equals(other.menu))
//			return false;
//		if (primerPlato != other.primerPlato)
//			return false;
//		if (sugerencia == null) {
//			if (other.sugerencia != null)
//				return false;
//		} else if (!sugerencia.equals(other.sugerencia))
//			return false;
//		return true;
//	}

    
}
