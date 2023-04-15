package com.condominio.novaalianca.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_CONTA")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONTA")
    private Long id;

    @Column(name = "TX_COD_BANCO")
    private String codBanco;

    @Column(name = "TX_NR_AGENCIA")
    private String nrAgencia;

    @Column(name = "TX_DIG_AGENCIA")
    private String dgAgencia;

    @Column(name = "TX_NR_CONTA")
    private String nrConta;

    @Column(name = "TX_DIG_CONTA")
    private String dgConta;

    @ManyToOne
    @JoinColumn(name = "ID_EMPRESA")
    private Empresa empresa;
}
