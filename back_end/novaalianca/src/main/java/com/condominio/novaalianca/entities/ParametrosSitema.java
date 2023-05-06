package com.condominio.novaalianca.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

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
