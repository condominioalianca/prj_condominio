package com.condominio.novaalianca.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_unidade")
public class Unidade {
    @Id
    @Column(name = "ID_UNIDADE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUnidade;

    @Column(name = "TX_NUMERO_UNIDADE")
    private String numeroUnidade;

    @Column(name = "TX_ANDAR")
    private String andarUnidade;

    @Column(name = "TX_BLOCO")
    private String blocoUnidade;

    @Column(name = "QT_MORADOR")
    private Long qtMorador;


}
