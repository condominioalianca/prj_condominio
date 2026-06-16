package com.condominio.novaalianca.entities;

import lombok.*;

import javax.persistence.*;
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

    @Column(name = "DESCRICAO")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "ID_UNIDADE")
    private Unidade unidade;
}
