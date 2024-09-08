package com.condominio.novaalianca.banking.models.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_extrato")
public class Extrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EXTRATO")
    private Long id;

    @Column(name = "ID_TRANSACAO")
    private String idTransacao;

    @Column(name = "DT_INCLUSAO")
    private LocalDate dtInclusao;

    @Column(name = "TP_TRANACAO")
    private String tipoTransacao;

    @Column(name = "TP_OPERACAO")
    private String tipoOperacao;

    @Column(name = "TITULO_TRANSACAO")
    private String tituloTransacao;

    @Column(name = "VL_TRANSACAO")
    private Double valorTransacao;

    @Column(name = "NOME_RECEBEDOR")
    private String nomeRecebedor;

    @Column(name = "DOCUMENTO_RECEBEDOR")
    private String documenteRecebedor;

    @Column(name = "NOME_PAGADOR")
    private String nomePagador;

    @Column(name = "DOCUMENTO_PAGADOR")
    private String documentePagador;
}
