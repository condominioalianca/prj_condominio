package com.condominio.novaalianca.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_cobranca_extra")
public class CobrancaExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COBRANCA_EXTRA")
    private Long idCobrancaExtra;

    @Column(name = "VL_COBRANCA")
    private Double valorCobranca;

    @Column(name = "DT_INCLUSAO",columnDefinition = "TIMESTAMP")
    private LocalDate dtInclusao;

    @Column(name = "MES_REFERENCIA")
    private Long mesReferencia;

    @Column(name = "ANO_REFERENCIA")
    private Long anoReferencia;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Column(name = "TIPO_OPERACAO", length = 20)
    private String tipoOperacao; // 'ACRESCIMO' ou 'DESCONTO'

    @Column(name = "RECORRENTE")
    private Boolean recorrente; // true ou false

    @Column(name = "TIPO_ABRANGENCIA", length = 20)
    private String tipoAbrangencia; // 'GERAL' ou 'UNIDADE'

    @ManyToOne
    @JoinColumn(name = "ID_UNIDADE", nullable = true)
    private Unidade unidade;
}
