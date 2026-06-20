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
@Table(name = "tb_extrato_compra_debito")
public class CompraDebitoDetalhe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EXTRATO_COMPRA_DEBITO")
    private Long id;

    @Column(name = "ESTABELECIMENTO")
    private String estabelecimento;

    @Column(name = "TIPO_DETALHE")
    private String tipoDetalhe;
}
