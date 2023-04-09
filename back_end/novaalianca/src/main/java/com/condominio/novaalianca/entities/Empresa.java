package com.condominio.novaalianca.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_ENDERECO")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EMPRESA")
    private int id;

    @Column(name = "TX_NM_EMPRESA")
    private String nomeEmpresa;

    @Column(name = "NR_DOCUMENTO")
    private String nrDocumento;

    @Column(name = "NR_CELULAR")
    private String nrCelular;

    @Column(name = "NR_TELEFONE")
    private String nrTelefone;

    @Column(name = "TX_ENDERECO")
    private String txEndereco;

    @Column(name = "TX_ENDERECO_COMPLEMENTO")
    private String txEnderecoComplemento;

    @Column(name = "TX_ENDERECO_NUMERO")
    private String txEnderecoNumero;

    @Column(name = "TX_CEP")
    private String txCep;

    @Column(name = "TX_BAIRRO")
    private String txBairro;

    @Column(name = "TX_EMAIL")
    private String txEmail;


    public Empresa (){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getNrDocumento() {
        return nrDocumento;
    }

    public void setNrDocumento(String nrDocumento) {
        this.nrDocumento = nrDocumento;
    }

    public String getNrCelular() {
        return nrCelular;
    }

    public void setNrCelular(String nrCelular) {
        this.nrCelular = nrCelular;
    }

    public String getNrTelefone() {
        return nrTelefone;
    }

    public void setNrTelefone(String nrTelefone) {
        this.nrTelefone = nrTelefone;
    }

    public String getTxEndereco() {
        return txEndereco;
    }

    public void setTxEndereco(String txEndereco) {
        this.txEndereco = txEndereco;
    }

    public String getTxEnderecoComplemento() {
        return txEnderecoComplemento;
    }

    public void setTxEnderecoComplemento(String txEnderecoComplemento) {
        this.txEnderecoComplemento = txEnderecoComplemento;
    }

    public String getTxEnderecoNumero() {
        return txEnderecoNumero;
    }

    public void setTxEnderecoNumero(String txEnderecoNumero) {
        this.txEnderecoNumero = txEnderecoNumero;
    }

    public String getTxCep() {
        return txCep;
    }

    public void setTxCep(String txCep) {
        this.txCep = txCep;
    }

    public String getTxBairro() {
        return txBairro;
    }

    public void setTxBairro(String txBairro) {
        this.txBairro = txBairro;
    }

    public String getTxEmail() {
        return txEmail;
    }

    public void setTxEmail(String txEmail) {
        this.txEmail = txEmail;
    }
}
