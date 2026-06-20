package com.condominio.novaalianca.banking.services;

import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.banking.models.entities.PixDetalhe;
import com.condominio.novaalianca.banking.models.entities.PagamentoDetalhe;
import com.condominio.novaalianca.banking.models.entities.CompraDebitoDetalhe;
import com.condominio.novaalianca.banking.repositories.ExtratoRepository;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.dto.inter.banking.BoletoCobrancaExtratoDetalheDTO;
import com.condominio.novaalianca.dto.inter.banking.CompraDebitoExtratoDetalheDTO;
import com.condominio.novaalianca.dto.inter.banking.ExtratoEnriquecidoResponseDTO;
import com.condominio.novaalianca.dto.inter.banking.ExtratoEnriquecidoTransacaoDTO;
import com.condominio.novaalianca.dto.inter.banking.ExtratoResponseDTO;
import com.condominio.novaalianca.dto.inter.banking.PagamentoExtratoDetalheDTO;
import com.condominio.novaalianca.dto.inter.banking.PixExtratoDetalheDTO;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import com.condominio.novaalianca.cobranca.repositories.BoletoRepository;
import com.condominio.novaalianca.enums.inter.TipoOperacaoEnum;
import com.condominio.novaalianca.enums.inter.TipoTransacaoEnum;
import com.condominio.novaalianca.services.inter.InterService;
import com.condominio.novaalianca.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class ExtratoService {

    @Autowired
    private InterService interService;

    @Autowired
    private ExtratoRepository extratoRepository;

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private NovaAliancaProperties properties;

    @Autowired
    private DateUtils dateUtils;

    /**
     * Recupera o extrato enriquecido do Banco Inter, realiza o parse/mapeamento dos campos,
     * associa com as tabelas de detalhes e boletos locais, e salva na base de dados (evitando duplicados).
     */
    @Transactional
    public ExtratoEnriquecidoResponseDTO getExtratoEnriquecido(String dataInicio, String dataFim, String ambiente) {
        ExtratoEnriquecidoResponseDTO extratoEnriquecidoResponseDTO = new ExtratoEnriquecidoResponseDTO();
        AtomicReference<Integer> persistidasCount = new AtomicReference<>(0);

        ExtratoEnriquecidoResponseDTO responseDTO = interService.buscarExtratoEnriquecido(
                dataInicio,
                dataFim,
                0,
                1000,
                null,
                null,
                properties.getNumeroContaCorrente(),
                ambiente);

        if (responseDTO == null || responseDTO.getTransacoes() == null) {
            log.warn("Nenhuma transação retornada pelo Banco Inter para o período {} a {}", dataInicio, dataFim);
            return extratoEnriquecidoResponseDTO;
        }

        for (ExtratoEnriquecidoTransacaoDTO transacaoDTO : responseDTO.getTransacoes()) {
            if (transacaoDTO.getIdTransacao() == null) {
                continue;
            }

            // Evitar duplicações
            Optional<Extrato> extratoExistente = extratoRepository.getbyIdTransacao(transacaoDTO.getIdTransacao());
            if (extratoExistente.isPresent()) {
                log.debug("Transação ID {} já existe na base. Ignorando persistência.", transacaoDTO.getIdTransacao());
                continue;
            }

            try {
                Extrato extrato = converterParaEntidade(transacaoDTO);
                extratoRepository.save(extrato);
                persistidasCount.set(persistidasCount.get() + 1);
            } catch (Exception e) {
                log.error("Erro ao persistir transação ID {}: {}", transacaoDTO.getIdTransacao(), e.getMessage(), e);
            }
        }

        log.info("Processamento de Extrato Enriquecido concluído. Transações persistidas: {}", persistidasCount.get());
        return responseDTO;
    }

    private Extrato converterParaEntidade(ExtratoEnriquecidoTransacaoDTO dto) {
        Extrato extrato = new Extrato();
        extrato.setIdTransacao(dto.getIdTransacao());
        extrato.setDtInclusao(parseDate(dto.getDataInclusao()));
        extrato.setDtTransacao(parseDate(dto.getDataTransacao()));
        extrato.setDescricao(dto.getDescricao());
        extrato.setTipoTransacao(dto.getTipoTransacao() != null ? dto.getTipoTransacao().name() : null);
        extrato.setTipoOperacao(dto.getTipoOperacao() != null ? dto.getTipoOperacao().getValue() : null);
        extrato.setTituloTransacao(dto.getTitulo());

        // Parse do valor de String para Double
        if (dto.getValor() != null) {
            try {
                extrato.setValorTransacao(Double.valueOf(dto.getValor()));
            } catch (NumberFormatException e) {
                log.warn("Formato de valor inválido para transação ID {}: {}", dto.getIdTransacao(), dto.getValor());
            }
        }

        // Mapeamento condicional do campo detalhes de acordo com o Tipo de Transação
        Object detalhes = dto.getDetalhes();
        if (detalhes != null) {
            if (dto.getTipoTransacao() == TipoTransacaoEnum.PIX && detalhes instanceof PixExtratoDetalheDTO) {
                PixExtratoDetalheDTO pixDto = (PixExtratoDetalheDTO) detalhes;
                extrato.setNomeRecebedor(pixDto.getNomeRecebedor() != null ? pixDto.getNomeRecebedor() : pixDto.getNomeEmpresaRecebedor());
                extrato.setDocumenteRecebedor(pixDto.getCpfCnpjRecebedor());
                extrato.setNomePagador(pixDto.getNomePagador() != null ? pixDto.getNomePagador() : pixDto.getNomeEmpresaPagador());
                extrato.setDocumentePagador(pixDto.getCpfCnpjPagador());

                PixDetalhe pixDetalhe = PixDetalhe.builder()
                        .txId(pixDto.getTxId())
                        .nomePagador(pixDto.getNomePagador())
                        .descricaoPix(pixDto.getDescricaoPix())
                        .cpfCnpjPagador(pixDto.getCpfCnpjPagador())
                        .contaBancariaRecebedor(pixDto.getContaBancariaRecebedor())
                        .nomeEmpresaPagador(pixDto.getNomeEmpresaPagador())
                        .tipoDetalhe(pixDto.getTipoDetalhe())
                        .endToEndId(pixDto.getEndToEndId())
                        .chavePixRecebedor(pixDto.getChavePixRecebedor())
                        .nomeEmpresaRecebedor(pixDto.getNomeEmpresaRecebedor())
                        .nomeRecebedor(pixDto.getNomeRecebedor())
                        .agenciaRecebedor(pixDto.getAgenciaRecebedor())
                        .cpfCnpjRecebedor(pixDto.getCpfCnpjRecebedor())
                        .origemMovimentacao(pixDto.getOrigemMovimentacao())
                        .codigoSolicitacao(pixDto.getCodigoSolicitacao())
                        .build();
                extrato.setPixDetalhe(pixDetalhe);

            } else if (dto.getTipoTransacao() == TipoTransacaoEnum.BOLETO_COBRANCA && detalhes instanceof BoletoCobrancaExtratoDetalheDTO) {
                BoletoCobrancaExtratoDetalheDTO boletoDto = (BoletoCobrancaExtratoDetalheDTO) detalhes;
                extrato.setNomeRecebedor("Condominio Nova Aliança");
                extrato.setDocumenteRecebedor("07890271000109");
                extrato.setNomePagador(boletoDto.getNome());
                extrato.setDocumentePagador(boletoDto.getCpfCnpj());

                // Lookup do boleto local pelo nossoNumero para criar o vínculo (ID_BOLETO)
                if (boletoDto.getNossoNumero() != null) {
                    try {
                        BoletoNovaAlianca localBoleto = boletoRepository.findByNossoNumero(boletoDto.getNossoNumero());
                        if (localBoleto != null) {
                            extrato.setIdBoleto(localBoleto.getId());
                        }
                    } catch (Exception e) {
                        log.warn("Erro ao buscar boleto por nossoNumero [{}]: {}", boletoDto.getNossoNumero(), e.getMessage());
                    }
                }

            } else if (dto.getTipoTransacao() == TipoTransacaoEnum.PAGAMENTO && detalhes instanceof PagamentoExtratoDetalheDTO) {
                PagamentoExtratoDetalheDTO pagDto = (PagamentoExtratoDetalheDTO) detalhes;
                extrato.setNomeRecebedor(pagDto.getNomeDestinatario() != null ? pagDto.getNomeDestinatario() : pagDto.getEmpresaEmissora());
                extrato.setDocumenteRecebedor(pagDto.getCpfCnpj());
                extrato.setNomePagador(pagDto.getNomeOrigem() != null ? pagDto.getNomeOrigem() : pagDto.getEmpresaOrigem());
                extrato.setDocumentePagador("07890271000109");

                PagamentoDetalhe pagDetalhe = PagamentoDetalhe.builder()
                        .valorTotal(pagDto.getValorTotal())
                        .detalheDescricao(pagDto.getDetalheDescricao())
                        .contaBancaria(pagDto.getContaBancaria())
                        .agencia(pagDto.getAgencia())
                        .adicionado(pagDto.getAdicionado())
                        .dataVencimento(pagDto.getDataVencimento())
                        .codigoAfiliado(pagDto.getCodigoAfiliado())
                        .empresaEmissora(pagDto.getEmpresaEmissora())
                        .valorOriginal(pagDto.getValorOriginal())
                        .desconto(pagDto.getDesconto())
                        .cpfCnpj(pagDto.getCpfCnpj())
                        .valorPrincipal(pagDto.getValorPrincipal())
                        .periodoApuracao(pagDto.getPeriodoApuracao())
                        .valorAumentado(pagDto.getValorAumentado())
                        .codBarras(pagDto.getCodBarras())
                        .valorParcial(pagDto.getValorParcial())
                        .hora(pagDto.getHora())
                        .juros(pagDto.getJuros())
                        .multa(pagDto.getMulta())
                        .empresaOrigem(pagDto.getEmpresaOrigem())
                        .nomeDestinatario(pagDto.getNomeDestinatario())
                        .tipoDetalhe(pagDto.getTipoDetalhe())
                        .nomeOrigem(pagDto.getNomeOrigem())
                        .codigoReceita(pagDto.getCodigoReceita())
                        .linhaDigitavel(pagDto.getLinhaDigitavel())
                        .autenticacao(pagDto.getAutenticacao())
                        .build();
                extrato.setPagamentoDetalhe(pagDetalhe);

            } else if (dto.getTipoTransacao() == TipoTransacaoEnum.COMPRA_DEBITO && detalhes instanceof CompraDebitoExtratoDetalheDTO) {
                CompraDebitoExtratoDetalheDTO compraDto = (CompraDebitoExtratoDetalheDTO) detalhes;
                extrato.setNomeRecebedor(compraDto.getEstabelecimento());
                extrato.setNomePagador("Condominio Nova Aliança");

                CompraDebitoDetalhe compraDetalhe = CompraDebitoDetalhe.builder()
                        .estabelecimento(compraDto.getEstabelecimento())
                        .tipoDetalhe(compraDto.getTipoDetalhe())
                        .build();
                extrato.setCompraDebitoDetalhe(compraDetalhe);
            }
        }

        // Fallbacks se nomeRecebedor ou nomePagador não foram definidos de acordo com o fluxo
        if (extrato.getNomeRecebedor() == null || extrato.getNomeRecebedor().trim().isEmpty()) {
            if (dto.getTipoOperacao() == TipoOperacaoEnum.CREDITO) {
                extrato.setNomeRecebedor("Condominio Nova Aliança");
                extrato.setDocumenteRecebedor("07890271000109");
            }
        }
        if (extrato.getNomePagador() == null || extrato.getNomePagador().trim().isEmpty()) {
            if (dto.getTipoOperacao() == TipoOperacaoEnum.DEBITO) {
                extrato.setNomePagador("Condominio Nova Aliança");
                extrato.setDocumentePagador("07890271000109");
            }
        }

        return extrato;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        if (dateStr.contains(" ")) {
            dateStr = dateStr.split(" ")[0];
        } else if (dateStr.contains("T")) {
            dateStr = dateStr.split("T")[0];
        }
        try {
            return dateUtils.StringYYYYMMDDToLocalDate(dateStr.trim());
        } catch (Exception e) {
            log.warn("Erro ao realizar parse da data [{}]: {}", dateStr, e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Extrato> findAll() {
        return extratoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Extrato findById(Long id) {
        return extratoRepository.findById(id)
                .orElseThrow(() -> new com.condominio.novaalianca.services.exceptions.ResourceNotFoundException("Extrato nao encontrado para o ID: " + id));
    }

    @Transactional
    public Extrato save(Extrato entity) {
        return extratoRepository.save(entity);
    }

    @Transactional
    public Extrato update(Extrato entity) {
        if (!extratoRepository.existsById(entity.getId())) {
            throw new com.condominio.novaalianca.services.exceptions.ResourceNotFoundException("Extrato nao encontrado para o ID: " + entity.getId());
        }
        return extratoRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!extratoRepository.existsById(id)) {
            throw new com.condominio.novaalianca.services.exceptions.ResourceNotFoundException("Extrato nao encontrado para o ID: " + id);
        }
        extratoRepository.deleteById(id);
    }
}
