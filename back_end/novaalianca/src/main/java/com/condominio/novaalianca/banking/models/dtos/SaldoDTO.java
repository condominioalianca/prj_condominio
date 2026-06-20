package com.condominio.novaalianca.banking.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SaldoDTO {
    private Long id;
    private Double bloqueadoCheque;
    private Double disponivel;
    private Double bloqueadoJudicialmente;
    private Double bloqueadoAdministrativo;
    private Double limite;
    private String dataReferencia;
    private LocalDateTime createdAt;
}
