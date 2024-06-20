package com.condominio.novaalianca.job.dto;

import com.condominio.novaalianca.services.validation.UserInsertValid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@UserInsertValid
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UsuarioChangPasswordtDTO extends UsuarioDTO {
	
	private String password;


}
