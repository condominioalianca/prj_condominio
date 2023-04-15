package com.condominio.novaalianca.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Autenticacao extends Authenticator{
	
	private String username;  
	private String password;
	
	public Autenticacao(){}
	
	public Autenticacao(String usuario, String senha){
		this.username = usuario;
		this.password = senha;
	}
	
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}

}
