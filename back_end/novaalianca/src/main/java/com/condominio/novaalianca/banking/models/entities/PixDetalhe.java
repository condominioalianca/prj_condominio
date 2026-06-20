package com.condominio.novaalianca.banking.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_extrato_pix")
public class PixDetalhe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EXTRATO_PIX")
    private Long id;

    @Column(name = "TX_ID")
    private String txId;

    @Column(name = "NOME_PAGADOR")
    private String nomePagador;

    @Column(name = "DESCRICAO_PIX")
    private String descricaoPix;

    @Column(name = "CPF_CNPJ_PAGADOR")
    private String cpfCnpjPagador;

    @Column(name = "CONTA_BANCARIA_RECEBEDOR")
    private String contaBancariaRecebedor;

    @Column(name = "NOME_EMPRESA_PAGADOR")
    private String nomeEmpresaPagador;

    @Column(name = "TIPO_DETALHE")
    private String tipoDetalhe;

    @Column(name = "END_TO_END_ID")
    private String endToEndId;

    @Column(name = "CHAVE_PIX_RECEBEDOR")
    private String chavePixRecebedor;

    @Column(name = "NOME_EMPRESA_RECEBEDOR")
    private String nomeEmpresaRecebedor;

    @Column(name = "NOME_RECEBEDOR")
    private String nomeRecebedor;

    @Column(name = "AGENCIA_RECEBEDOR")
    private String agenciaRecebedor;

    @Column(name = "CPF_CNPJ_RECEBEDOR")
    private String cpfCnpjRecebedor;

    @Column(name = "ORIGEM_MOVIMENTACAO")
    private String origemMovimentacao;

    @Column(name = "CODIGO_SOLICITACAO")
    private String codigoSolicitacao;
}
