package com.condominio.novaalianca.builder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;


import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import com.condominio.novaalianca.dto.boleto.ContentDTO;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.enums.*;
import com.condominio.novaalianca.repositories.ParametrosSistemaRepository;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.condominio.novaalianca.util.Feriados;
import com.condominio.novaalianca.dto.boleto.BoletoEmissaoDTO;
import inter.cobranca.model.Boleto;
import inter.cobranca.model.BoletoDetalhado;
import inter.cobranca.model.Desconto;
import inter.cobranca.model.Mensagem;
import inter.cobranca.model.Mora;
import inter.cobranca.model.Multa;
import inter.cobranca.model.Pessoa;
import inter.cobranca.model.enums.CodigoDesconto;
import inter.cobranca.model.enums.CodigoMora;
import inter.cobranca.model.enums.CodigoMulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BoletoBuilder {

	@Autowired
	Feriados feriados;

	@Autowired
	private UnidadeBuilder unidadeBuilder;

	@Autowired
	private UsuarioBuilder usuarioBuilder;
	
	@Autowired
	ParametrosSistemaRepository parametrosSistemaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private NovaAliancaProperties properties;

	Locale BRASILLOCALE = new Locale("pt","BR");

//	public BoletoEmissaoDTO carregaDadosEmissao(Usuario usuario) throws ParseException {
//
//
//		BoletoEmissaoDTO boleto = new BoletoEmissaoDTO();
//		DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
//		String valorCOndominio1 =(parametrosSistemaRepository.findValorParametro("VALOR_CONDOMINIO_" + LocalDate.now().format(formatterYear)));
//		Float valorCondominio = Float.parseFloat(valorCOndominio1);
//		Float valorMulta = Float.parseFloat(parametrosSistemaRepository.findValorParametro("VALOR_MULTA"));
//		Float valorMora = Float.parseFloat(parametrosSistemaRepository.findValorParametro("VALOR_MORA"));
//		int diaVencimento = Integer.parseInt(parametrosSistemaRepository.findValorParametro("DIA_DE_VENCIMENTO_BOLETO"));
//		DateTimeFormatter formatterSeuNumer = DateTimeFormatter.ofPattern("MMyyyy");
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//
//
//		boleto.setSeuNumero(LocalDate.now().format(formatterSeuNumer) + usuario.getUnidade().getNumeroUnidade());
//		boleto.setValorNominal(valorCondominio);
//		boleto.setDataVencimento(this.verificaFeriado(diaVencimento).toString());
//		boleto.setNumDiasAgenda(30);
//
//
//
//
//		/* Preenchendo Dados Pagador */
//		boleto.getPagador().setCpfCnpj(Objects.isNull(usuario.getNrDocumentoCpf() )? usuario.getNrDocumentoCnpj() : usuario.getNrDocumentoCpf());
//		boleto.getPagador().setNome(usuario.getNomeUsuario());
//		boleto.getPagador().setEmail(usuario.getTxEmail());
//		boleto.getPagador().setTelefone(Objects.isNull(usuario.getNrCelular() ) ? "" : usuario.getNrCelular());
//		boleto.getPagador().setEndereco(usuario.getEndereco().getTxEndereco());
//		boleto.getPagador().setNumero(usuario.getEndereco().getTxEnderecoNumero());
//		boleto.getPagador().setComplemento(usuario.getEndereco().getTxEnderecoComplemento());
//		boleto.getPagador().setBairro(usuario.getEndereco().getTxBairro());
//		boleto.getPagador().setCidade(usuario.getEndereco().getTxCidade());
//		boleto.getPagador().setUf(usuario.getEndereco().getTxUf());
//		boleto.getPagador().setCep(usuario.getEndereco().getTxCep());
//		boleto.getPagador().setDdd(usuario.getNrCelularDdd() == null ? "" : usuario.getNrCelularDdd());
//		boleto.getPagador().setTipoPessoa(TipoPessoa.FISICA.toString());
//
//
//
//
//		/* Preenchendo Mensagens */
//		// boleto.getMensagem().setLinha1("JUROS(MORA) - TAXA MENSAL - 1 DIA apos DO
//		// VENCIMENTO - PERCENTUAL 2%");
//		// boleto.getMensagem().setLinha2("MULTA - VALOR FIXO - 1 DIA após DO VENCIMENTO
//		// - VALOR 5,40");
//		boleto.getMensagem().setLinha1("TAXA CONDOMINAL REFERENTE AO MÊS " + LocalDate.now().format(formatterSeuNumer));
//
//		/* Preenchendo Desconto 1 */
//		boleto.getDesconto1().setCodigoDesconto(TipoDesconto.NAOTEMDESCONTO.toString());
//		boleto.getDesconto1().setTaxa(0.0);
//		boleto.getDesconto1().setValor(0.0);
//		boleto.getDesconto1().setData("");
//
//		/* Preenchendo Desconto 2 */
//		boleto.getDesconto2().setCodigoDesconto(TipoDesconto.NAOTEMDESCONTO.toString());
//		boleto.getDesconto2().setTaxa(0.0);
//		boleto.getDesconto2().setValor(0.0);
//		boleto.getDesconto2().setData("");
//
//		/* Preenchendo Desconto 3 */
//		boleto.getDesconto3().setCodigoDesconto(TipoDesconto.NAOTEMDESCONTO.toString());
//		boleto.getDesconto3().setTaxa(0.0);
//		boleto.getDesconto3().setValor(0.0);
//		boleto.getDesconto3().setData("");
//
//
//
//		/* Preenchendo Multa */
//		boleto.getMulta().setCodigoMulta(TipoMulta.VALORFIXO.toString());
//		boleto.getMulta().setData(this.verificaFeriado(diaVencimento).plusDays(1).format(formatter));
//		boleto.getMulta().setValor(valorMulta);
//		boleto.getMulta().setTaxa(0F);
//
//		/* Preenchendo Mora */
//		boleto.getMora().setCodigoMora(TipoMora.TAXAMENSAL.toString());
//		boleto.getMora().setData(this.verificaFeriado(diaVencimento).plusDays(1).format(formatter));
//		boleto.getMora().setTaxa(valorMora);
//		boleto.getMora().setValor(0F);
//
//		boleto.getBeneficiarioDTO().setNome("Condominio Nova Alianca");
//		boleto.getBeneficiarioDTO().setCpfCnpj(properties.getCnpjCpfBenificiario());
//		boleto.getBeneficiarioDTO().setTipoPessoa(TipoPessoa.JURIDICA.toString());
//		boleto.getBeneficiarioDTO().setCep("09894205");
//		boleto.getBeneficiarioDTO().setEndereco("RUA ARNALDO MARGONARI");
//		boleto.getBeneficiarioDTO().setBairro("JORDANOPOLIS");
//		boleto.getBeneficiarioDTO().setCidade("SAO BERNARDO DO CAMPO");
//		boleto.getBeneficiarioDTO().setUf("SP");
//
//
//
//		return boleto;
//	}

	public BoletoDTO entityToDTO (BoletoNovaAlianca boletoNovaAlianca){
		return BoletoDTO.builder()
				.dtLimitePagamento(boletoNovaAlianca.getDtLimitePagamento())
				.dtEmissao(boletoNovaAlianca.getDtEmissao())
				.txSituacao(boletoNovaAlianca.getTxSituacao())
				.dtVencimento(boletoNovaAlianca.getDtVencimento())
				.valor(boletoNovaAlianca.getValor())
				.valorPagamento(boletoNovaAlianca.getValorPagamento())
				.id(boletoNovaAlianca.getId())
				.seuNumero(boletoNovaAlianca.getSeuNumero())
				.ativo(boletoNovaAlianca.getAtivo())
				.dhSituacao(boletoNovaAlianca.getDhSituacao())
				.dtBaixa(boletoNovaAlianca.getDtBaixa())
				.dtEnvio(boletoNovaAlianca.getDtEnvio())
				.dtPagamento(boletoNovaAlianca.getDtPagamento())
				.mesReferencia(boletoNovaAlianca.getDtEmissao().getMonth().getDisplayName(TextStyle.FULL, BRASILLOCALE).toUpperCase())
				.anoReferencia(boletoNovaAlianca.getDtEmissao().getYear())
				.motivoBaixa(boletoNovaAlianca.getMotivoBaixa())
				.nossoNumero(boletoNovaAlianca.getNossoNumero())
				.txCancelamento(boletoNovaAlianca.getTxCancelamento())
				.txCodBarras(boletoNovaAlianca.getTxCodBarras())
				.txEspecie(boletoNovaAlianca.getTxEspecie())
				.unidade(unidadeBuilder.entityToDto(boletoNovaAlianca.getIdUnidade()))
				.usuario(usuarioBuilder.entityToDto(boletoNovaAlianca.getUsuario()))
				.build();
	}
	



	public Boleto boletoInter (Usuario usuario) throws ParseException {
		Locale ptBr = new Locale("pt", "BR");
		DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
		Double valorCondominio = (Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_CONDOMINIO.toString() +"_"+ LocalDate.now().format(formatterYear))));
		Double valorTaxaMinAgua = (Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_TAXA_MIN_AGUA.toString())));
		Double TAXA_ACRESCIMO_AGUA = (Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.TAXA_ACRESCIMO_AGUA.toString())));
		Double valorMulta = (Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_MULTA.toString())));
		Double valorMora = (Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_MORA.toString())));
		int diaVencimento = Integer.parseInt(parametrosSistemaRepository.findValorParametro(ParametrosSistema.DIA_DE_VENCIMENTO_BOLETO.toString()));
		double valorTaxaAguaAcrescimoSetentaPorCento = valorTaxaMinAgua * 0.7;
		double valorCondominio1Morador = valorCondominio+valorTaxaMinAgua;
		double valoraguaMaisMorador = valorTaxaMinAgua+valorTaxaAguaAcrescimoSetentaPorCento;
		double valorCondominioMaisMorador = valorCondominio+valoraguaMaisMorador;
		DateTimeFormatter formatterSeuNumer = DateTimeFormatter.ofPattern("MMyyyy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		/* Preenchendo Mensagens */
		// boleto.getMensagem().setLinha1("JUROS(MORA) - TAXA MENSAL - 1 DIA apos DO
		// VENCIMENTO - PERCENTUAL 2%");
		// boleto.getMensagem().setLinha2("MULTA - VALOR FIXO - 1 DIA após DO VENCIMENTO
		// - VALOR 5,40");
		Mensagem mensagem = new Mensagem();
		mensagem.setLinha1("TAXA CONDOMINAL REFERENTE AO MÊS " + LocalDate.now().format(formatterSeuNumer));
		mensagem.setLinha2("TAXA CONDOMINNIO = " + NumberFormat.getCurrencyInstance(ptBr).format(valorCondominio));
		mensagem.setLinha3("TAXA MIN AGUA = "+ NumberFormat.getCurrencyInstance(ptBr).format(valorTaxaMinAgua));




		Boleto boletoInter = new inter.cobranca.model.Boleto();
		boletoInter.setSeuNumero(LocalDate.now().format(formatterSeuNumer) + usuario.getUnidade().getNumeroUnidade());
		boletoInter.setDataVencimento(this.verificaFeriado(diaVencimento).toString());
		boletoInter.setNumDiasAgenda(30);

		if(usuario.getUnidade().getQtMorador()>1){
			mensagem.setLinha4("ACRESCIMO 70% DA TAXA MIN (UNIDADE COM MAIS DE 1 MORADOR) = "+ NumberFormat.getCurrencyInstance(ptBr).format(valorTaxaAguaAcrescimoSetentaPorCento));
			mensagem.setLinha5("VALOR TOTAL DA COBRANÇA = "+ NumberFormat.getCurrencyInstance(ptBr).format(valorCondominioMaisMorador));
			boletoInter.setValorNominal(BigDecimal.valueOf(valorCondominioMaisMorador).setScale(2,BigDecimal.ROUND_HALF_EVEN));

		}else{
			mensagem.setLinha4("VALOR TOTAL DA COBRANÇA = "+ NumberFormat.getCurrencyInstance(ptBr).format(valorCondominio1Morador));
			boletoInter.setValorNominal(BigDecimal.valueOf(valorCondominio1Morador).setScale(2, BigDecimal.ROUND_HALF_EVEN));


		}

		boletoInter.setMensagem(mensagem);

		Pessoa pagador = new Pessoa();
		/* Preenchendo Dados Pagador */
		pagador.setCpfCnpj(Objects.isNull(usuario.getNrDocumentoCpf() )? usuario.getNrDocumentoCnpj() : usuario.getNrDocumentoCpf());
		pagador.setNome(usuario.getNomeUsuario());
		pagador.setEmail(usuario.getTxEmail());
		pagador.setTelefone(Objects.isNull(usuario.getNrCelular() ) ? "" : usuario.getNrCelular());
		pagador.setEndereco(usuario.getEndereco().getTxEndereco());
		pagador.setNumero(usuario.getEndereco().getTxEnderecoNumero());
		pagador.setComplemento(usuario.getEndereco().getTxEnderecoComplemento());
		pagador.setBairro(usuario.getEndereco().getTxBairro());
		pagador.setCidade(usuario.getEndereco().getTxCidade());
		pagador.setUf(usuario.getEndereco().getTxUf());
		pagador.setCep(usuario.getEndereco().getTxCep());
		pagador.setDdd(usuario.getNrCelularDdd() == null ? "" : usuario.getNrCelularDdd());
		pagador.setTipoPessoa(inter.cobranca.model.enums.TipoPessoa.FISICA);

		boletoInter.setPagador(pagador);




		Desconto desconto = new Desconto();
		/* Preenchendo Desconto 1 */
		desconto.setCodigoDesconto(CodigoDesconto.NAOTEMDESCONTO);
		desconto.setTaxa(BigDecimal.ZERO);
		desconto.setValor(BigDecimal.ZERO);
		desconto.setData("");

		/* Preenchendo Desconto 2 */
		boletoInter.setDesconto1(desconto);
		boletoInter.setDesconto2(desconto);
		boletoInter.setDesconto3(desconto);


		Multa multa = new Multa();
		boletoInter.setMulta(multa);

		/* Preenchendo Multa */
		boletoInter.getMulta().setCodigoMulta(CodigoMulta.VALORFIXO);
		boletoInter.getMulta().setData(this.verificaFeriado(diaVencimento).plusDays(1).format(formatter));
		boletoInter.getMulta().setValor(BigDecimal.valueOf(valorMulta));
		boletoInter.getMulta().setTaxa(BigDecimal.ZERO);
		Mora mora = new Mora();
		boletoInter.setMora(mora);

		/* Preenchendo Mora */
		boletoInter.getMora().setCodigoMora(CodigoMora.TAXAMENSAL);
		boletoInter.getMora().setData(this.verificaFeriado(diaVencimento).plusDays(1).format(formatter));
		boletoInter.getMora().setTaxa(BigDecimal.valueOf(valorMora));
		boletoInter.getMora().setValor(BigDecimal.ZERO);

		Pessoa beneficiarioFinal = new Pessoa();
		boletoInter.setBeneficiarioFinal(beneficiarioFinal);
		boletoInter.getBeneficiarioFinal().setNome("Condominio Nova Alianca");
		boletoInter.getBeneficiarioFinal().setCpfCnpj(properties.getCnpjCpfBenificiario());
		boletoInter.getBeneficiarioFinal().setTipoPessoa(inter.cobranca.model.enums.TipoPessoa.JURIDICA);
		boletoInter.getBeneficiarioFinal().setCep("09894205");
		boletoInter.getBeneficiarioFinal().setEndereco("RUA ARNALDO MARGONARI");
		boletoInter.getBeneficiarioFinal().setBairro("JORDANOPOLIS");
		boletoInter.getBeneficiarioFinal().setCidade("SAO BERNARDO DO CAMPO");
		boletoInter.getBeneficiarioFinal().setUf("SP");

		return boletoInter;
	}

	public LocalDate verificaFeriado(Integer diaVencimento) throws ParseException {

		LocalDate dataVencimento = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
				diaVencimento);
		while (feriados.verificaFeriado(dataVencimento) || dataVencimento.getDayOfWeek().getValue() > 5) {
			dataVencimento = dataVencimento.plusDays(1);
		}
		return dataVencimento;

	}



	public BoletoNovaAlianca entityInterToEntityNovaAlianca(BoletoDetalhado boleto) {
		DateTimeFormatter formatterLocalDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Usuario usuario = usuarioRepository.findByTxEmail(boleto.getPagador().getEmail());


		return BoletoNovaAlianca.builder()
				.nossoNumero(boleto.getNossoNumero())
				.seuNumero(boleto.getSeuNumero())
				.txCancelamento(boleto.getMotivoCancelamento())
				.txSituacao(boleto.getSituacao())
				.dhSituacao(LocalDateTime.parse(boleto.getDataHoraSituacao(), formatterLocalDateTime))
				.dtVencimento(LocalDate.parse(boleto.getDataVencimento(),formatterLocalDate))
				.valor(boleto.getValorNominal().doubleValue())
				.dtEmissao(LocalDate.parse(boleto.getDataEmissao(),formatterLocalDate))
				.dtLimitePagamento(LocalDate.parse(boleto.getDataLimite(),formatterLocalDate))
				.txEspecie(boleto.getCodigoEspecie())
				.txCodBarras(boleto.getCodigoBarras())
				.txLinhaDigitavel(boleto.getLinhaDigitavel())
				.txOrigem(boleto.getOrigem())
				//.empresa()
				.usuario(usuario)
				//.valorPagamento(boleto.get)
				//.motivoBaixa()
				//.dtBaixa()
				//.dtPagamento()
				.dtEnvio(LocalDate.now())
				.idUnidade(usuario.getUnidade())
				//.arquivopdf()
				.ativo(Objects.isNull(boleto.getMotivoCancelamento()) ? Boolean.TRUE : Boolean.FALSE)
				.build();
	}

	public BoletoNovaAlianca updateBoletoCarga(BoletoNovaAlianca boleto, ContentDTO dto){
		DateTimeFormatter formatterDataHora1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		boleto.setDhSituacao(LocalDateTime.parse(dto.getDataHoraSituacao(),formatterDataHora1));
		boleto.setDtPagamento(LocalDate.parse(dto.getDataHoraSituacao(), formatterDataHora1));
		boleto.setTxEspecie(dto.getCodigoEspecie());
		boleto.setTxOrigem(dto.getOrigem());
		boleto.setTxSituacao(dto.getSituacao());
		boleto.setValor(dto.getValorNominal().doubleValue());
		boleto.setValorPagamento(Objects.isNull(dto.getValorTotalRecebimento())? 0 : dto.getValorTotalRecebimento().doubleValue());
		boleto.setAtivo(!dto.getSituacao().equals("CANCELADO") ? Boolean.TRUE : Boolean.FALSE);
		return boleto;
	}

	public BoletoNovaAlianca newBoletoCarga (ContentDTO dto){
		DateTimeFormatter formatterDataHora1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		DateTimeFormatter formatterData1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		Usuario usuario = usuarioBuilder.byCPF(dto.getPagador().getCpfCnpj());
		BoletoNovaAlianca boletoNovaAlianca = new BoletoNovaAlianca();
		boletoNovaAlianca.setDhSituacao(LocalDateTime.parse(dto.getDataHoraSituacao(),formatterDataHora1));
		boletoNovaAlianca.setDtBaixa(null);
		boletoNovaAlianca.setDtEmissao(LocalDate.parse(dto.getDataEmissao().format(formatterData1),formatterData1));
		boletoNovaAlianca.setDtEnvio(null);
		boletoNovaAlianca.setDtLimitePagamento(dto.getDataLimite());
		boletoNovaAlianca.setDtPagamento(LocalDate.parse(dto.getDataHoraSituacao(), formatterDataHora1));
		boletoNovaAlianca.setDtVencimento(LocalDate.parse(dto.getDataVencimento().format(formatterData1),formatterData1));
		boletoNovaAlianca.setMotivoBaixa(null);
		boletoNovaAlianca.setNossoNumero(dto.getNossoNumero());
		boletoNovaAlianca.setSeuNumero(dto.getSeuNumero());
		boletoNovaAlianca.setTxCancelamento(null);
		boletoNovaAlianca.setTxCodBarras(dto.getCodigoBarras());
		boletoNovaAlianca.setTxEspecie(dto.getCodigoEspecie());
		boletoNovaAlianca.setTxLinhaDigitavel(dto.getLinhaDigitavel());
		boletoNovaAlianca.setTxOrigem(dto.getOrigem());
		boletoNovaAlianca.setTxSituacao(dto.getSituacao());
		boletoNovaAlianca.setValor(dto.getValorNominal().doubleValue());
		boletoNovaAlianca.setValorPagamento(Objects.isNull(dto.getValorTotalRecebimento())? 0 : dto.getValorTotalRecebimento().doubleValue());
		boletoNovaAlianca.setEmpresa(null);
		boletoNovaAlianca.setIdUnidade(usuario.getUnidade());
		boletoNovaAlianca.setUsuario(usuario);
		boletoNovaAlianca.setAtivo(!dto.getSituacao().equals("CANCELADO") ? Boolean.TRUE : Boolean.FALSE);
		boletoNovaAlianca.setEmailEnviado(Boolean.FALSE);

		//boletoRepository.save(boletoNovaAlianca);
		return boletoNovaAlianca;
	}

}
