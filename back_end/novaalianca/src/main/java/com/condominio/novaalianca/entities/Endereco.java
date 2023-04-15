package com.condominio.novaalianca.entities;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_ENDERECO")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ENDERECO")
    private Long idEndereco;

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

    @Column(name = "TX_CEP")
    private String txCep;

}
