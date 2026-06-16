package com.condominio.novaalianca.dto.inter.cobranca;

import com.condominio.novaalianca.dto.inter.cobranca.enums.CodigoMora;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mora {
    private CodigoMora codigo;
    private String data;
    private BigDecimal taxa;
    private BigDecimal valor;
}
