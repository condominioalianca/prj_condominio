package com.condominio.novaalianca.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "TB_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private int idUsuario;

    @Column(name = "NM_USUARIO")
    private String nomeUsuario;

    @Column(name = "TX_EMAIL")
    private String txEmail;

    @Column(name = "NR_TELEFONE_DDD")
    private String nrTelefoneDdd;

    @Column(name = "NR_TELEFONE")
    private String nrTelefone;

    @Column(name = "NR_CELULAR_DDD")
    private String nrCelularDdd;

    @Column(name = "NR_CELULAR")
    private String nrCelular;

    @Column(name = "NR_DOCUMENTO_CPF")
    private String nrDocumentoCpf;

    @Column(name = "NR_DOCUMENTO_CNPJ")
    private String nrDocumentoCnpj;

    @Column(name = "TX_TIPO_PESSOA")
    private String txTipoPessoa;

    @Column(name = "ENVIA_BOLETO")
    private boolean enviaBoleto;

    @Column(name = "ENVIA_SMS")
    private boolean enviaSms;

    @Column(name = "ATIVO")
    private boolean ativo;

    @OneToMany
    @JoinColumn(name="ID_ENDERECO")
    private List<EnderecoUsuario> enderecoUsuarios;


    public Usuario (){

    }
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getTxEmail() {
        return txEmail;
    }

    public void setTxEmail(String txEmail) {
        this.txEmail = txEmail;
    }

    public String getNrTelefoneDdd() {
        return nrTelefoneDdd;
    }

    public void setNrTelefoneDdd(String nrTelefoneDdd) {
        this.nrTelefoneDdd = nrTelefoneDdd;
    }

    public String getNrTelefone() {
        return nrTelefone;
    }

    public void setNrTelefone(String nrTelefone) {
        this.nrTelefone = nrTelefone;
    }

    public String getNrCelularDdd() {
        return nrCelularDdd;
    }

    public void setNrCelularDdd(String nrCelularDdd) {
        this.nrCelularDdd = nrCelularDdd;
    }

    public String getNrCelular() {
        return nrCelular;
    }

    public void setNrCelular(String nrCelular) {
        this.nrCelular = nrCelular;
    }

    public String getNrDocumentoCpf() {
        return nrDocumentoCpf;
    }

    public void setNrDocumentoCpf(String nrDocumentoCpf) {
        this.nrDocumentoCpf = nrDocumentoCpf;
    }

    public String getNrDocumentoCnpj() {
        return nrDocumentoCnpj;
    }

    public void setNrDocumentoCnpj(String nrDocumentoCnpj) {
        this.nrDocumentoCnpj = nrDocumentoCnpj;
    }

    public String getTxTipoPessoa() {
        return txTipoPessoa;
    }

    public void setTxTipoPessoa(String txTipoPessoa) {
        this.txTipoPessoa = txTipoPessoa;
    }

    public boolean isEnviaBoleto() {
        return enviaBoleto;
    }

    public void setEnviaBoleto(boolean enviaBoleto) {
        this.enviaBoleto = enviaBoleto;
    }

    public boolean isEnviaSms() {
        return enviaSms;
    }

    public void setEnviaSms(boolean enviaSms) {
        this.enviaSms = enviaSms;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
