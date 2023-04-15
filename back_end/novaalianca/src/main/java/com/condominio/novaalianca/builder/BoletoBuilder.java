package com.condominio.novaalianca.builder;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.condominio.novaalianca.TipoDesconto;
import com.condominio.novaalianca.TipoMora;
import com.condominio.novaalianca.TipoMulta;
import com.condominio.novaalianca.TipoPessoa;
import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.ParametrosSistemaRepository;
import com.condominio.novaalianca.util.Feriados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class BoletoBuilder {
	
	@Autowired
	Feriados feriados;
	
	@Autowired
	ParametrosSistemaRepository parametrosSistemaRepository;

	public BoletoDTO carregaDadosEmissao(Usuario usuario) throws ParseException {
		BoletoDTO boleto = new BoletoDTO();
		DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
		String valorCOndominio1 =(parametrosSistemaRepository.findValorParametro("VALOR_CONDOMINIO_" + LocalDate.now().format(formatterYear)));
		Float valorCondominio = Float.parseFloat(valorCOndominio1);
		Float valorMulta = Float.parseFloat(parametrosSistemaRepository.findValorParametro("VALOR_MULTA"));
		Float valorMora = Float.parseFloat(parametrosSistemaRepository.findValorParametro("VALOR_MORA"));
		int diaVencimento = Integer
				.parseInt(parametrosSistemaRepository.findValorParametro("DIA_DE_VENCIMENTO_BOLETO"));

		/* Preenchendo Dados Pagador */
		boleto.getPagador().setCpfCnpj(Objects.isNull(usuario.getNrDocumentoCpf() )? usuario.getNrDocumentoCnpj() : usuario.getNrDocumentoCpf());
		boleto.getPagador().setNome(usuario.getNomeUsuario());
		boleto.getPagador().setEmail(usuario.getTxEmail());
		boleto.getPagador().setTelefone(Objects.isNull(usuario.getNrCelular() ) ? "" : usuario.getNrCelular());
		boleto.getPagador().setEndereco(usuario.getEndereco().getTxEndereco());
		boleto.getPagador().setNumero(usuario.getEndereco().getTxEnderecoNumero());
		boleto.getPagador().setComplemento(usuario.getEndereco().getTxEnderecoComplemento());
		boleto.getPagador().setBairro(usuario.getEndereco().getTxBairro());
		boleto.getPagador().setCidade(usuario.getEndereco().getTxCidade());
		boleto.getPagador().setUf(usuario.getEndereco().getTxUf());
		boleto.getPagador().setCep(usuario.getEndereco().getTxCep());




		boleto.getPagador().setDdd(usuario.getNrCelularDdd() == null ? "" : usuario.getNrCelularDdd());
		boleto.getPagador().setTipoPessoa(TipoPessoa.FISICA.toString());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		boleto.setDataEmissao(LocalDate.now().format(formatter));

		DateTimeFormatter formatterSeuNumer = DateTimeFormatter.ofPattern("MMyyyy");
		//boleto.setSeuNumero(LocalDate.now().format(formatterSeuNumer) + usuario.getUnidade().getNumeroUnidade());
		boleto.setDataLimite("TRINTA");

		boleto.setDataVencimento(this.verificaFeriado(diaVencimento).toString());

		/* Preenchendo Mensagens */
		// boleto.getMensagem().setLinha1("JUROS(MORA) - TAXA MENSAL - 1 DIA apos DO
		// VENCIMENTO - PERCENTUAL 2%");
		// boleto.getMensagem().setLinha2("MULTA - VALOR FIXO - 1 DIA após DO VENCIMENTO
		// - VALOR 5,40");
		boleto.getMensagem().setLinha1("TAXA CONDOMINAL REFERENTE AO MÊS " + LocalDate.now().format(formatterSeuNumer));

		/* Preenchendo Desconto 1 */
		boleto.getDesconto1().setCodigoDesconto(TipoDesconto.NAOTEMDESCONTO.toString());
		boleto.getDesconto1().setTaxa(0.0);
		boleto.getDesconto1().setValor(0.0);
		boleto.getDesconto1().setData("");

		/* Preenchendo Desconto 2 */
		boleto.getDesconto2().setCodigoDesconto(TipoDesconto.NAOTEMDESCONTO.toString());
		boleto.getDesconto2().setTaxa(0.0);
		boleto.getDesconto2().setValor(0.0);
		boleto.getDesconto2().setData("");

		/* Preenchendo Desconto 3 */
		boleto.getDesconto3().setCodigoDesconto(TipoDesconto.NAOTEMDESCONTO.toString());
		boleto.getDesconto3().setTaxa(0.0);
		boleto.getDesconto3().setValor(0.0);
		boleto.getDesconto3().setData("");

		boleto.setValorNominal(valorCondominio);
		boleto.setValorAbatimento(0F);

		/* Preenchendo Multa */
		boleto.getMulta().setCodigoMulta(TipoMulta.VALORFIXO.toString());
		boleto.getMulta().setData(this.verificaFeriado(diaVencimento).plusDays(1).format(formatter));
		boleto.getMulta().setValor(valorMulta);
		boleto.getMulta().setTaxa(0F);

		/* Preenchendo Mora */
		boleto.getMora().setCodigoMora(TipoMora.TAXAMENSAL.toString());
		boleto.getMora().setData(this.verificaFeriado(diaVencimento).plusDays(1).format(formatter));
		boleto.getMora().setTaxa(valorMora);
		boleto.getMora().setValor(0F);

		boleto.setCnpjCPFBeneficiario("07890271000109");
		boleto.setNumDiasAgenda("TRINTA");

		return boleto;
	}
	
	
	

	public LocalDate verificaFeriado(Integer diaVencimento) throws ParseException {

		LocalDate dataVencimento = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
				diaVencimento);
		while (feriados.verificaFeriado(dataVencimento) || dataVencimento.getDayOfWeek().getValue() > 5) {
			dataVencimento = dataVencimento.plusDays(1);
		}
		return dataVencimento;

	}

}
