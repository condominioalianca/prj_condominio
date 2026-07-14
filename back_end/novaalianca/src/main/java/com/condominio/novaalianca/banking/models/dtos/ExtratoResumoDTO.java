package com.condominio.novaalianca.banking.models.dtos;

import com.condominio.novaalianca.banking.models.enums.StatusConciliacao;
import com.condominio.novaalianca.banking.models.enums.StatusGeral;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtratoResumoDTO {
    private Long id;
    private String idTransacao;
    private LocalDate dtInclusao;
    private LocalDate dtTransacao;
    private String descricao;
    private String tipoTransacao;
    private String tipoOperacao;
    private String tituloTransacao;
    private Double valorTransacao;
    private String nomeRecebedor;
    private String documenteRecebedor;
    private String nomePagador;
    private String documentePagador;
    private Long idBoleto;
    private StatusConciliacao statusConciliado;
    private StatusGeral statusGeral;
    
    // Categoria
    private Long idCategoriaGasto;
    private String descricaoCategoriaGasto;
    
    // Comprovante
    private Boolean possuiComprovante;
    private Long idComprovante;
    private String nomeArquivoComprovante;
}
