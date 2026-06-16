package com.condominio.novaalianca.dto.inter.cobranca;

import com.condominio.novaalianca.dto.inter.cobranca.enums.CodigoDesconto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Desconto {
    private CodigoDesconto codigoDesconto;
    private BigDecimal taxa;
    private BigDecimal valor;
    private String data;
}
