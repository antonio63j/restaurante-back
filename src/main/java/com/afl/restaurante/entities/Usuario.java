package com.afl.restaurante.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, length = 60)
	@NotNull
	@NotEmpty
	private String username;

	@Column(length = 60)
	@NotNull
	@NotEmpty
	private String password;
	
	private String nombre;
	private String apellidos;
	private String telefono;
	
	@Column(unique = false)
	private String email;
	
	// @Temporal(TemporalType.DATE)
	@Column(columnDefinition = "TIMESTAMP")
	private LocalDateTime fechaRegistro;
	
	@Column(unique = true, length = 80)
	private String codActivacion;
	
	private boolean finalizadaActivacion;
	
	@Column(columnDefinition = "TIMESTAMP")
	private LocalDateTime fechaResetPwd;
	
	@Column(length = 6)
	private String codResetPwd;
	
//	@Temporal(TemporalType.DATE)
//	private Date fechaSolCambioPassw;
//	
//	@Column(unique = true, length = 80)
//	private String codCambioPassw;
//	
//	private Boolean esperandoCambioPassw;
	private Boolean enabled;
	private Boolean aceptaEmails;

	@ManyToMany
	@JoinTable(
			  name = "usuarios_roles", 
			  joinColumns = @JoinColumn(name = "usuario_id"), 
			  inverseJoinColumns = @JoinColumn(name = "role_id"),
			  uniqueConstraints = @UniqueConstraint(columnNames={"usuario_id", "role_id"})
			  )
	private List<Role> roles;


	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getCodActivacion() {
		return codActivacion;
	}

	public void setCodActivacion(String codActivacion) {
		this.codActivacion = codActivacion;
	}

	public Boolean getAceptaEmails() {
		return aceptaEmails;
	}

	public void setAceptaEmails(Boolean aceptaEmails) {
		this.aceptaEmails = aceptaEmails;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public boolean isFinalizadaActivacion() {
		return finalizadaActivacion;
	}

	public void setFinalizadaActivacion(boolean finalizadaActivacion) {
		this.finalizadaActivacion = finalizadaActivacion;
	}

	public String getCodResetPwd() {
		return codResetPwd;
	}

	public void setCodResetPwd(String codResetPwd) {
		this.codResetPwd = codResetPwd;
	}
	
	public LocalDateTime getFechaResetPwd() {
		return fechaResetPwd;
	}

	public void setFechaResetPwd(LocalDateTime fechaResetPwd) {
		this.fechaResetPwd = fechaResetPwd;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", username=" + username + ", password=" + password + ", nombre=" + nombre
				+ ", apellidos=" + apellidos + ", telefono=" + telefono + ", email=" + email + ", fechaRegistro="
				+ fechaRegistro + ", codActivacion=" + codActivacion + ", finalizadaActivacion=" + finalizadaActivacion
				+ ", fechaRestPwd=" + fechaResetPwd + ", codResetPwd=" + codResetPwd + ", enabled=" + enabled
				+ ", aceptaEmails=" + aceptaEmails + ", roles=" + roles + "]";
	}



	private static final long serialVersionUID = 1L;
}
