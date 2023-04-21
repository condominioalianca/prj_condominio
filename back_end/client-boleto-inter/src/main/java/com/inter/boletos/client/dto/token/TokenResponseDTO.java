package com.inter.boletos.client.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TokenResponseDTO {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String scope;



}
