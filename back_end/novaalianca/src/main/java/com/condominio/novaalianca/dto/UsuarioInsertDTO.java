package com.condominio.novaalianca.dto;

import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.services.validation.UserInsertValid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@UserInsertValid
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioInsertDTO extends UsuarioDTO {
	
	private String password;


}
