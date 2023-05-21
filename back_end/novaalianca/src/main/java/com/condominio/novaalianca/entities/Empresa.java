package com.condominio.novaalianca.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EMPRESA")
    private Long id;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empresa")
    private List<Conta> contaEmpresa;

}
