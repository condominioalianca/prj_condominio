package com.condominio.novaalianca.cobranca.models.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class PixDTO {

    private String txid;
    private String pixCopiaECola;
}
