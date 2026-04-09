package com.condominio.novaalianca.dto.boleto;

import com.condominio.novaalianca.cobranca.models.dto.DescontoDTO;
import com.condominio.novaalianca.cobranca.models.dto.MoraDTO;
import com.condominio.novaalianca.cobranca.models.dto.MultaDTO;
import com.condominio.novaalianca.cobranca.models.dto.PagadorDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class BoletoTESTEOLDDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String seuNumero;

	private Float valorNominal;

	private String dataVencimento;

	@JsonProperty("numDiasAgenda")
	private Integer numDiasAgenda;

	private PagadorDTO pagador;
	private MensagemDTO mensagem;
	private DescontoDTO desconto1;
	private DescontoDTO desconto2;
	private DescontoDTO desconto3;

	private MultaDTO multa;
	private MoraDTO mora;
	@JsonProperty("beneficiarioFinal")
	private BeneficiarioDTO beneficiarioDTO;


	
	public BoletoTESTEOLDDTO() {
		pagador = new PagadorDTO();
		mensagem = new MensagemDTO();
		desconto1 = new DescontoDTO();
		desconto2 = new DescontoDTO();
		desconto3 = new DescontoDTO();
		multa = new MultaDTO();
		mora = new MoraDTO();
		beneficiarioDTO = new BeneficiarioDTO();
		
	}
	
	
	
}
