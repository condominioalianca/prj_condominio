package com.condominio.novaalianca.entities;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_ENDERECO")
public class EnderecoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ENDERECO")
    private int idEndereco;

    @Column(name = "TX_ENDERECO")
    private String txEndereco;

    @Column(name = "TX_ENDERECO_NUMERO")
    private String txEnderecoNumero;

    @Column(name = "TX_ENDERECO_COMPLEMENTO")
    private String txEnderecoComplemento;

    @Column(name = "TX_BAIRRO")
    private String txBairro;

    @Column(name = "TX_CIDADE")
    private String txCidade;

    @Column(name = "TX_UF")
    private String txUf;

    @ManyToOne
    @JoinColumn(name="ID_USUARIO")
    private Usuario usuario;

    @Column(name = "NR_DOCUMENTO_CNPJ")
    private String nrDocumentoCnpj;

    @Column(name = "TX_TIPO_PESSOA")
    private String txTipoPessoa;

    public EnderecoUsuario () {

    }

    public int getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(int idEndereco) {
        this.idEndereco = idEndereco;
    }

    public String getTxEndereco() {
        return txEndereco;
    }

    public void setTxEndereco(String txEndereco) {
        this.txEndereco = txEndereco;
    }

    public String getTxEnderecoNumero() {
        return txEnderecoNumero;
    }

    public void setTxEnderecoNumero(String txEnderecoNumero) {
        this.txEnderecoNumero = txEnderecoNumero;
    }

    public String getTxEnderecoComplemento() {
        return txEnderecoComplemento;
    }

    public void setTxEnderecoComplemento(String txEnderecoComplemento) {
        this.txEnderecoComplemento = txEnderecoComplemento;
    }

    public String getTxBairro() {
        return txBairro;
    }

    public void setTxBairro(String txBairro) {
        this.txBairro = txBairro;
    }

    public String getTxCidade() {
        return txCidade;
    }

    public void setTxCidade(String txCidade) {
        this.txCidade = txCidade;
    }

    public String getTxUf() {
        return txUf;
    }

    public void setTxUf(String txUf) {
        this.txUf = txUf;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

}
