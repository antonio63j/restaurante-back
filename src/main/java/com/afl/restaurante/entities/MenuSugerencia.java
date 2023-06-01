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

    //private boolean primerPlato;
    @Enumerated(EnumType.STRING)
    @Column(length = 7)
    private EnumComponenteMenu componenteMenu;
    
    public MenuSugerencia(Menu menu, Sugerencia sugerencia, EnumComponenteMenu componenteMenu) {
    	this.menu = menu;
        this.sugerencia = sugerencia;
        // this.primerPlato = primerPlato;
        this.componenteMenu = componenteMenu;
        
    }

}
