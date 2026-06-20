package com.condominio.novaalianca.dto.inter.cobranca;

import com.condominio.novaalianca.dto.inter.cobranca.enums.CodigoMulta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Multa {
    private CodigoMulta codigo;
    private String data;
    private BigDecimal valor;
    private BigDecimal taxa;
}
