package com.condominio.novaalianca.dto.inter.cobranca;

import com.condominio.novaalianca.cobranca.models.dto.ResponseCobrancaDTO;
import lombok.*;

/**
 * Representa os dados detalhados/enriquecidos de um boleto retornado pela API V3 do Banco Inter.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Boletoenriquecido extends ResponseCobrancaDTO {
}
