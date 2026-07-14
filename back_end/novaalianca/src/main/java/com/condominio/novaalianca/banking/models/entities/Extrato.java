package com.condominio.novaalianca.banking.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.condominio.novaalianca.banking.models.enums.StatusConciliacao;
import com.condominio.novaalianca.banking.models.enums.StatusGeral;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.Lob;
import javax.persistence.Table;
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

    @Column(name = "DT_INCLUSAO", columnDefinition = "TIMESTAMP")
    private LocalDate dtInclusao;

    @Column(name = "DT_TRANSACAO", columnDefinition = "TIMESTAMP")
    private LocalDate dtTransacao;

    @Column(name = "DESCRICAO")
    private String descricao;

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

    @Column(name = "ID_BOLETO")
    private Long idBoleto;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ID_EXTRATO_PIX")
    private PixDetalhe pixDetalhe;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ID_EXTRATO_PAGAMENTO")
    private PagamentoDetalhe pagamentoDetalhe;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ID_EXTRATO_COMPRA_DEBITO")
    private CompraDebitoDetalhe compraDebitoDetalhe;

    @ManyToOne
    @JoinColumn(name = "CONCILIACAO_ID")
    @JsonIgnore
    private Conciliacao conciliacao;

    @ManyToOne
    @JoinColumn(name = "ID_CATEGORIA_GASTO")
    private CategoriaGasto categoriaGasto;

    @ManyToOne
    @JoinColumn(name = "ID_COMPROVANTE")
    private Comprovante comprovante;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS_CONCILIADO")
    private StatusConciliacao statusConciliado;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS_GERAL")
    private StatusGeral statusGeral;
}
