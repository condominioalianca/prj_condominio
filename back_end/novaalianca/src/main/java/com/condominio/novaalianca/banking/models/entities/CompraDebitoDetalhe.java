package com.condominio.novaalianca.banking.models.entities;

import lombok.*;
import javax.persistence.*;

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
