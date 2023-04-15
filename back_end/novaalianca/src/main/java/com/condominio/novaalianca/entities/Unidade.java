package com.condominio.novaalianca.entities;



import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_UNIDADE")
public class Unidade {
	@Id
	@Column(name = "ID_UNIDADE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idUnidade;
	
	@Column(name="TX_NUMERO_UNIDADE")
	private String	numeroUnidade;
	
	@Column(name="TX_ANDAR")
	private String	andarUnidade;

	@Column(name="TX_BLOCO")
	private String	blocoUnidade;


}
