package com.condominio.novaalianca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCadastroDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nomeUsuario;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String txEmail;

    @NotBlank(message = "DDD é obrigatório")
    @Pattern(regexp = "\\d{2}", message = "DDD deve conter 2 dígitos")
    private String nrCelularDdd;

    @NotBlank(message = "Celular é obrigatório")
    private String nrCelular;

    @NotBlank(message = "CPF é obrigatório")
    private String nrDocumentoCpf;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
