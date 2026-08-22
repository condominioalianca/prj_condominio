package com.condominio.novaalianca.banking.models.entities;

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
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_extrato_pagamento")
public class PagamentoDetalhe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EXTRATO_PAGAMENTO")
    private Long id;

    @Column(name = "VALOR_TOTAL")
    private String valorTotal;

    @Column(name = "DETALHE_DESCRICAO")
    private String detalheDescricao;

    @Column(name = "CONTA_BANCARIA")
    private String contaBancaria;

    @Column(name = "AGENCIA")
    private String agencia;

    @Column(name = "ADICIONADO")
    private String adicionado;

    @Column(name = "DATA_VENCIMENTO")
    private String dataVencimento;

    @Column(name = "CODIGO_AFILIADO")
    private String codigoAfiliado;

    @Column(name = "EMPRESA_EMISSORA")
    private String empresaEmissora;

    @Column(name = "VALOR_ORIGINAL")
    private String valorOriginal;

    @Column(name = "DESCONTO")
    private String desconto;

    @Column(name = "CPF_CNPJ")
    private String cpfCnpj;

    @Column(name = "VALOR_PRINCIPAL")
    private String valorPrincipal;

    @Column(name = "PERIODO_APURACAO")
    private String periodoApuracao;

    @Column(name = "VALOR_AUMENTADO")
    private String valorAumentado;

    @Column(name = "COD_BARRAS")
    private String codBarras;

    @Column(name = "VALOR_PARCIAL")
    private String valorParcial;

    @Column(name = "HORA")
    private String hora;

    @Column(name = "JUROS")
    private String juros;

    @Column(name = "MULTA")
    private String multa;

    @Column(name = "EMPRESA_ORIGEM")
    private String empresaOrigem;

    @Column(name = "NOME_DESTINATARIO")
    private String nomeDestinatario;

    @Column(name = "TIPO_DETALHE")
    private String tipoDetalhe;

    @Column(name = "NOME_ORIGEM")
    private String nomeOrigem;

    @Column(name = "CODIGO_RECEITA")
    private String codigoReceita;

    @Column(name = "LINHA_DIGITAVEL")
    private String linhaDigitavel;

    @Column(name = "AUTENTICACAO")
    private String autenticacao;
}
