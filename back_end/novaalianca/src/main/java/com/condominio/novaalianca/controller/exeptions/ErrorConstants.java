package com.condominio.novaalianca.controller.exeptions;

import java.net.URI;

public final class ErrorConstants {

    private ErrorConstants() {}

    public static final String PROBLEM_BASE_URL = "https://condominio-alianca.com.br/problems";
    
    // URI Types
    public static final URI DEFAULT_TYPE = URI.create("about:blank");
    public static final URI RESOURCE_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/resource-not-found");
    public static final URI DATABASE_ERROR_TYPE = URI.create(PROBLEM_BASE_URL + "/database-error");
    public static final URI VALIDATION_ERROR_TYPE = URI.create(PROBLEM_BASE_URL + "/validation-error");
    public static final URI INTERNAL_SERVER_ERROR_TYPE = URI.create(PROBLEM_BASE_URL + "/internal-server-error");
    
    // Titles
    public static final String RESOURCE_NOT_FOUND_TITLE = "Recurso Não Encontrado";
    public static final String DATABASE_ERROR_TITLE = "Erro de Banco de Dados";
    public static final String VALIDATION_ERROR_TITLE = "Erro de Validação de Dados";
    public static final String INTERNAL_SERVER_ERROR_TITLE = "Erro Interno do Servidor";
}
