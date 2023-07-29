package com.condominio.novaalianca.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UsuarioDTO {
    @JsonProperty("id")
    private Long idUsuario;
    @NotBlank(message = "Nome requerido")
    private String nomeUsuario;
    @JsonProperty("email")
    private String txEmail;

    private String nrTelefoneDdd;

    private String nrTelefone;

    private String nrCelularDdd;

    private String nrCelular;

    @JsonProperty("cpf")
    private String nrDocumentoCpf;

    private String nrDocumentoCnpj;
    @JsonProperty("tipoPessoa")
    private String txTipoPessoa;

    private boolean enviaBoleto;

    private boolean enviaSms;

    @Setter(AccessLevel.NONE)
    private Set<PerfilDTO> listPerfis = new HashSet<>();


    private boolean ativo;

    @JsonProperty("endereco")
    private EnderecoDTO enderecoDTO;

    @JsonProperty("unidade")
    private UnidadeDTO unidadeDTO;

}
