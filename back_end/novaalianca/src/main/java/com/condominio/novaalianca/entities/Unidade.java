package com.condominio.novaalianca.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private Integer idUnidade;

    @Column(name = "TX_NUMERO_UNIDADE")
    private String numeroUnidade;

    @Column(name = "TX_ANDAR")
    private String andarUnidade;

    @Column(name = "TX_BLOCO")
    private String blocoUnidade;


}
