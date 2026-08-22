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
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_saldo")
public class Saldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SALDO")
    private Long id;

    @Column(name = "BLOQUEADO_CHEQUE")
    private Double bloqueadoCheque;

    @Column(name = "DISPONIVEL")
    private Double disponivel;

    @Column(name = "BLOQUEADO_JUDICIALMENTE")
    private Double bloqueadoJudicialmente;

    @Column(name = "BLOQUEADO_ADMINISTRATIVO")
    private Double bloqueadoAdministrativo;

    @Column(name = "LIMITE")
    private Double limite;

    @Column(name = "DATA_REFERENCIA")
    private String dataReferencia;

    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
}
