package com.condominio.novaalianca.banking.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Access;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExtratoDTO {

    @JsonProperty("id")
    private Long idExtrato;
    private String idTransacao;
    private LocalDate dtInclusao;
    private String tipoTransacao;
    private String tipoOperacao;
    private String tituloTransacao;
    private Double valorTransacao;
    private String nomeRecebedor;
    private String documenteRecebedor;
    private String nomePagador;
    private String documentePagador;

}
