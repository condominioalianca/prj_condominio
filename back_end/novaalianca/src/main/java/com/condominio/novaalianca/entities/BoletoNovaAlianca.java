package com.condominio.novaalianca.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_boleto")
public class BoletoNovaAlianca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_BOLETO")
    private Long id;

    @Column(name = "TX_NOSSONUMERO")
    private String nossoNumero;

    @Column(name = "TX_SEU_NUMERO")
    private String seuNumero;

    @Column(name = "TX_CANCELAMENTO")
    private String txCancelamento;

    @Column(name = "TX_SITUACAO")
    private String txSituacao;

    @Column(name = "DH_SITUACAO")
    private LocalDateTime dhSituacao;

    @Column(name = "DT_VENCIMENTO", columnDefinition = "TIMESTAMP")
    private LocalDate dtVencimento;

    @Column(name = "VL_BOLETO")
    private Double valor;

    @Column(name = "DT_EMISSAO",  columnDefinition = "TIMESTAMP"  )
    private LocalDate dtEmissao;

    @Column(name = "DT_LIMITE_PAGAMENTO")
    private LocalDate dtLimitePagamento;

    @Column(name = "TX_ESPECIE")
    private String txEspecie;

    @Column(name = "TX_COD_BARRAS")
    private String txCodBarras;

    @Column(name = "TX_LINHA_DIGITAVEL")
    private String txLinhaDigitavel;

    @Column(name = "TX_ORIGEM")
    private String txOrigem;

    @ManyToOne
    @JoinColumn(name = "ID_EMPRESA")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @Column(name = "VL_PAGAMENTO")
    private Double valorPagamento;

    @Column(name = "TX_MOTIVO_BAIXA")
    private String motivoBaixa;

    @Column(name = "DT_BAIXA",columnDefinition = "TIMESTAMP")
    private LocalDate dtBaixa;

    @Column(name = "DT_PAGAMENTO", columnDefinition = "TIMESTAMP")
    private LocalDate dtPagamento;

    @Column(name = "DT_ENVIO", columnDefinition = "TIMESTAMP")
    private LocalDate dtEnvio;

    @ManyToOne
    @JoinColumn(name = "ID_UNIDADE")
    private Unidade idUnidade;

    @Column(name = "ARQUIVO_PDF")
    private byte[] arquivopdf;

    @Column(name = "FL_ATIVO")
    private Boolean ativo;

    @Column(name = "FL_EMAIL_ENVIADO")
    private Boolean emailEnviado;

    @Column(name = "COD_SOLICITACAO")
    private String codSolicitacao;

}
