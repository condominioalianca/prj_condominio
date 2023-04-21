package com.condominio.novaalianca.dto.parametros;

import com.condominio.novaalianca.dto.UsuarioDTO;
import lombok.*;


import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ParametrosDTO {

    private Long idParametro;

    private String descParametro;

    private String valorParametro;

    private LocalDate dtCriacao;

    private LocalDate dtAlteracao;

    private UsuarioDTO usuarioCriacao;

    private UsuarioDTO usuarioAlteracao;

    private Boolean ativo;
}
