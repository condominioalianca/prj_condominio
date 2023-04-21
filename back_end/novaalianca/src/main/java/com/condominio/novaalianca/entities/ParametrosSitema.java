package com.condominio.novaalianca.entities;

import java.time.LocalDate;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_PARAMETROS_SISTEMA")
public class ParametrosSitema {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PARAMETRO")
	private Long idParametro;
	
	@Column(name = "TX_DESC_PARAMETRO")
	private String descParametro;
	
	@Column(name = "VL_PARAMETRO")
	private String valorParametro;
	
	@Column(name = "DT_CRIACAO")
	private LocalDate dtCriacao;
	
	@Column(name = "DT_ALTERACAO")
	private LocalDate dtAlteracao;
	
	@ManyToOne
	@JoinColumn(name = "ID_USUARIO_CRIACAO")
	private Usuario usuarioCriacao;
	
	@ManyToOne
	@JoinColumn(name = "ID_USUARIO_ALTERACAO")
	private Usuario usuarioAlteracao;
	
	@Column(name = "FL_ATIVO")
	private Boolean ativo;

}
