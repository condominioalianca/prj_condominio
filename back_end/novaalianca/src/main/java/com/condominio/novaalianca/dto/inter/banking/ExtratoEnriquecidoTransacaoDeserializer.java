package com.condominio.novaalianca.dto.inter.banking;

import com.condominio.novaalianca.enums.inter.TipoOperacaoEnum;
import com.condominio.novaalianca.enums.inter.TipoTransacaoEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Deserializer customizado para tratar o parse dinâmico e resiliente do campo 'detalhes'
 * com base no valor de 'tipoTransacao'.
 */
@Slf4j
public class ExtratoEnriquecidoTransacaoDeserializer extends JsonDeserializer<ExtratoEnriquecidoTransacaoDTO> {

    @Override
    public ExtratoEnriquecidoTransacaoDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);

        ExtratoEnriquecidoTransacaoDTO dto = new ExtratoEnriquecidoTransacaoDTO();

        dto.setIdTransacao(getText(node, "idTransacao"));
        dto.setDataInclusao(getText(node, "dataInclusao"));
        dto.setDataTransacao(getText(node, "dataTransacao"));

        // Desserialização resiliente do TipoTransacaoEnum
        String tipoTransStr = getText(node, "tipoTransacao");
        if (tipoTransStr != null) {
            try {
                dto.setTipoTransacao(TipoTransacaoEnum.fromValue(tipoTransStr));
            } catch (Exception e) {
                log.warn("Tipo de transação desconhecido ou inválido retornado pela API Inter: [{}]. Detalhes: {}", tipoTransStr, e.getMessage());
                dto.setTipoTransacao(null);
            }
        }

        // Desserialização resiliente do TipoOperacaoEnum
        String tipoOperStr = getText(node, "tipoOperacao");
        if (tipoOperStr != null) {
            try {
                dto.setTipoOperacao(TipoOperacaoEnum.fromValue(tipoOperStr));
            } catch (Exception e) {
                log.warn("Tipo de operação desconhecido ou inválido retornado pela API Inter: [{}]. Detalhes: {}", tipoOperStr, e.getMessage());
                dto.setTipoOperacao(null);
            }
        }

        dto.setValor(getText(node, "valor"));
        dto.setTitulo(getText(node, "titulo"));
        dto.setDescricao(getText(node, "descricao"));
        dto.setNumeroDocumento(getText(node, "numeroDocumento"));

        // Desserialização condicional e dinâmica do campo detalhes
        if (node.has("detalhes") && !node.get("detalhes").isNull()) {
            JsonNode detalhesNode = node.get("detalhes");
            TipoTransacaoEnum tipo = dto.getTipoTransacao();
            if (tipo != null) {
                try {
                    switch (tipo) {
                        case PIX:
                            dto.setDetalhes(mapper.treeToValue(detalhesNode, PixExtratoDetalheDTO.class));
                            break;
                        case BOLETO_COBRANCA:
                            dto.setDetalhes(mapper.treeToValue(detalhesNode, BoletoCobrancaExtratoDetalheDTO.class));
                            break;
                        case PAGAMENTO:
                            dto.setDetalhes(mapper.treeToValue(detalhesNode, PagamentoExtratoDetalheDTO.class));
                            break;
                        case COMPRA_DEBITO:
                            dto.setDetalhes(mapper.treeToValue(detalhesNode, CompraDebitoExtratoDetalheDTO.class));
                            break;
                        default:
                            dto.setDetalhes(detalhesNode); // Retorna como JsonNode para outros tipos
                            break;
                    }
                } catch (Exception e) {
                    log.error("Erro ao realizar o parse dinâmico do campo 'detalhes' para o tipo [{}]: {}", tipo, e.getMessage(), e);
                    dto.setDetalhes(detalhesNode); // Fallback seguro
                }
            } else {
                dto.setDetalhes(detalhesNode); // Fallback seguro
            }
        }

        return dto;
    }

    private String getText(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return null;
    }
}
