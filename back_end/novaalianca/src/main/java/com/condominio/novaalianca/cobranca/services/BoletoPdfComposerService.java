package com.condominio.novaalianca.cobranca.services;

import com.condominio.novaalianca.banking.models.entities.Conciliacao;
import com.condominio.novaalianca.banking.models.enums.StatusConciliacao;
import com.condominio.novaalianca.banking.repositories.ConciliacaoRepository;
import com.condominio.novaalianca.banking.services.RelatorioConciliacaoService;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import com.condominio.novaalianca.entities.CobrancaExtra;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.enums.ParametrosSistema;
import com.condominio.novaalianca.repositories.ParametrosSistemaRepository;
import com.condominio.novaalianca.services.CobrancaExtraService;
import com.condominio.novaalianca.services.exceptions.ConciliacaoPendenteException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoletoPdfComposerService {

    private static final Logger log = LoggerFactory.getLogger(BoletoPdfComposerService.class);

    private final ConciliacaoRepository conciliacaoRepository;
    private final RelatorioConciliacaoService relatorioConciliacaoService;
    private final com.condominio.novaalianca.banking.services.ConciliacaoService conciliacaoService;
    private final ParametrosSistemaRepository parametrosSistemaRepository;
    private final CobrancaExtraService cobrancaExtraService;

    /**
     * Compõe o PDF final contendo:
     * - Página 1: Boleto oficial do Banco Inter
     * - Página 2: Descritivo detalhado das taxas da unidade
     * - Páginas 3+: Relatório de conciliação do mês anterior (somente se estiver BATIDO)
     */
    public byte[] comporPdfCompleto(Usuario usuario, BoletoNovaAlianca boletoLocal, byte[] pdfBoletoInterBytes, LocalDate dataReferencia) {
        // 1. Validar status da conciliação do mês anterior
        LocalDate dataMesAnterior = dataReferencia.minusMonths(1).withDayOfMonth(1);
        Optional<Conciliacao> conciliacaoOpt = conciliacaoRepository.findByDataReferencia(dataMesAnterior);

        // Se não encontrar exatamente no dia 1, busca qualquer conciliação no mês anterior
        if (conciliacaoOpt.isEmpty()) {
            LocalDate dtInicio = dataMesAnterior;
            LocalDate dtFim = dataMesAnterior.withDayOfMonth(dataMesAnterior.lengthOfMonth());
            List<Conciliacao> conciliacoes = conciliacaoRepository.findWithFilters(dtInicio, dtFim, null);
            if (!conciliacoes.isEmpty()) {
                conciliacaoOpt = Optional.of(conciliacoes.get(0));
            }
        }

        if (conciliacaoOpt.isEmpty() || conciliacaoOpt.get().getStatus() != StatusConciliacao.BATIDO) {
            String msgError = String.format("Envio de e-mail bloqueado para a unidade %s (%s): A conciliação do mês anterior (%s) não está com status BATIDO!",
                    usuario.getUnidade() != null ? usuario.getUnidade().getNumeroUnidade() : "N/A",
                    usuario.getNomeUsuario(),
                    dataMesAnterior.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            log.warn(msgError);
            throw new ConciliacaoPendenteException(msgError);
        }

        Conciliacao conciliacaoBatida = conciliacaoOpt.get();

        // 2. Gerar PDF da Folha de Descritivo de Taxas (Página 2)
        byte[] descritivoBytes = gerarDescritivoTaxasPdf(usuario, dataReferencia);

        // 3. Obter PDF Dinâmico e Completo da Conciliação com todas as transações (Páginas 3+)
        byte[] pdfConciliacaoBytes = null;
        try {
            pdfConciliacaoBytes = conciliacaoService.exportarPdfDinamico(conciliacaoBatida.getId());
        } catch (Exception e) {
            log.warn("Erro ao gerar PDF dinâmico da conciliação ID {}, utilizando fallback salvo: {}", conciliacaoBatida.getId(), e.getMessage());
            pdfConciliacaoBytes = conciliacaoBatida.getArquivoPdf();
        }

        // 4. Juntar os 3 PDFs em um único arquivo
        List<byte[]> listaPdfs = new ArrayList<>();
        if (pdfBoletoInterBytes != null && pdfBoletoInterBytes.length > 0) {
            listaPdfs.add(pdfBoletoInterBytes);
        }
        if (descritivoBytes != null && descritivoBytes.length > 0) {
            listaPdfs.add(descritivoBytes);
        }
        if (pdfConciliacaoBytes != null && pdfConciliacaoBytes.length > 0) {
            listaPdfs.add(pdfConciliacaoBytes);
        }

        return juntarPdfs(listaPdfs);
    }

    /**
     * Gera o PDF com a folha de descritivo detalhado das taxas (OpenPDF).
     */
    public byte[] gerarDescritivoTaxasPdf(Usuario usuario, LocalDate dataReferencia) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Locale ptBr = new Locale("pt", "BR");
            DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
            DateTimeFormatter formatterMonthYear = DateTimeFormatter.ofPattern("MM/yyyy");

            Double valorCondominio = Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_CONDOMINIO.toString() + "_" + dataReferencia.format(formatterYear)));
            Double valorTaxaMinAgua = Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_TAXA_MIN_AGUA.toString()));

            double valorTaxaAguaAcrescimoSetentaPorCento = valorTaxaMinAgua * 0.7;
            boolean maisDeUmMorador = usuario.getUnidade() != null && usuario.getUnidade().getQtMorador() != null && usuario.getUnidade().getQtMorador() > 1;

            double valorTotal = valorCondominio + valorTaxaMinAgua;
            if (maisDeUmMorador) {
                valorTotal += valorTaxaAguaAcrescimoSetentaPorCento;
            }

            int mes = dataReferencia.getMonthValue();
            int ano = dataReferencia.getYear();
            List<CobrancaExtra> cobrancasExtras = cobrancaExtraService.getCobrancasExtrasParaUnidadeEMes(usuario.getUnidade(), mes, ano);

            // Cabeçalho
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph title = new Paragraph("CONDOMÍNIO NOVA ALIANÇA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(16, 185, 129));
            Paragraph subTitle = new Paragraph("DEMONSTRATIVO DE TAXAS E COBRANÇAS - MÊS " + dataReferencia.format(formatterMonthYear), subTitleFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            subTitle.setSpacingAfter(15);
            document.add(subTitle);

            // Info Morador
            Font bodyBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font bodyNormal = FontFactory.getFont(FontFactory.HELVETICA, 11);

            Paragraph info = new Paragraph();
            info.add(new Phrase("Unidade: ", bodyBold));
            info.add(new Phrase((usuario.getUnidade() != null ? usuario.getUnidade().getNumeroUnidade() : "N/A") + "    ", bodyNormal));
            info.add(new Phrase("Morador: ", bodyBold));
            info.add(new Phrase(usuario.getNomeUsuario() + "\n", bodyNormal));
            info.add(new Phrase("Qtd. Moradores: ", bodyBold));
            info.add(new Phrase((usuario.getUnidade() != null ? String.valueOf(usuario.getUnidade().getQtMorador()) : "1") + "\n\n", bodyNormal));
            document.add(info);

            // Tabela de Itens
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{70, 30});

            PdfPCell cellHeader1 = new PdfPCell(new Phrase("Item / Descrição da Taxa", bodyBold));
            cellHeader1.setBackgroundColor(new Color(241, 245, 249));
            cellHeader1.setPadding(8);
            table.addCell(cellHeader1);

            PdfPCell cellHeader2 = new PdfPCell(new Phrase("Valor (R$)", bodyBold));
            cellHeader2.setBackgroundColor(new Color(241, 245, 249));
            cellHeader2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellHeader2.setPadding(8);
            table.addCell(cellHeader2);

            // Taxa Condomínio
            table.addCell(createCell("Taxa de Condomínio Ordinária", bodyNormal));
            table.addCell(createCellRight(NumberFormat.getCurrencyInstance(ptBr).format(valorCondominio), bodyNormal));

            // Taxa Água
            table.addCell(createCell("Taxa Mínima de Água", bodyNormal));
            table.addCell(createCellRight(NumberFormat.getCurrencyInstance(ptBr).format(valorTaxaMinAgua), bodyNormal));

            if (maisDeUmMorador) {
                table.addCell(createCell("Acréscimo Água 70% (Unidade > 1 Morador)", bodyNormal));
                table.addCell(createCellRight(NumberFormat.getCurrencyInstance(ptBr).format(valorTaxaAguaAcrescimoSetentaPorCento), bodyNormal));
            }

            // Cobranças Extras e Descontos
            if (cobrancasExtras != null && !cobrancasExtras.isEmpty()) {
                for (CobrancaExtra cob : cobrancasExtras) {
                    if (cob.getValorCobranca() != null && cob.getValorCobranca() > 0) {
                        boolean isDesconto = "DESCONTO".equalsIgnoreCase(cob.getTipoOperacao());
                        String desc = isDesconto ? "[Desconto] " + cob.getDescricao() : cob.getDescricao();
                        double v = cob.getValorCobranca();
                        if (isDesconto) {
                            valorTotal -= v;
                            table.addCell(createCell(desc, bodyNormal));
                            table.addCell(createCellRight("- " + NumberFormat.getCurrencyInstance(ptBr).format(v), bodyNormal));
                        } else {
                            valorTotal += v;
                            table.addCell(createCell(desc, bodyNormal));
                            table.addCell(createCellRight(NumberFormat.getCurrencyInstance(ptBr).format(v), bodyNormal));
                        }
                    }
                }
            }

            // Linha Total
            PdfPCell totalLabelCell = new PdfPCell(new Phrase("VALOR TOTAL DA COBRANÇA", bodyBold));
            totalLabelCell.setBackgroundColor(new Color(226, 232, 240));
            totalLabelCell.setPadding(8);
            table.addCell(totalLabelCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(NumberFormat.getCurrencyInstance(ptBr).format(valorTotal), bodyBold));
            totalValueCell.setBackgroundColor(new Color(226, 232, 240));
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setPadding(8);
            table.addCell(totalValueCell);

            document.add(table);

            Paragraph footer = new Paragraph("\n\n* Este demonstrativo é parte integrante do boleto de cobrança emitido via Banco Inter.", FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC, Color.GRAY));
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Erro ao gerar PDF do descritivo de taxas", e);
            throw new RuntimeException("Erro ao gerar PDF do descritivo de taxas", e);
        }
    }

    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell createCellRight(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(6);
        return cell;
    }

    /**
     * Junta uma lista de arquivos PDF (byte[]) em um único PDF de saída usando OpenPDF (PdfCopy).
     */
    public byte[] juntarPdfs(List<byte[]> pdfs) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, out);
            document.open();

            for (byte[] pdfByte : pdfs) {
                if (pdfByte != null && pdfByte.length > 0) {
                    PdfReader reader = new PdfReader(pdfByte);
                    int n = reader.getNumberOfPages();
                    for (int page = 1; page <= n; page++) {
                        copy.addPage(copy.getImportedPage(reader, page));
                    }
                    reader.close();
                }
            }
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Erro ao realizar merge dos PDFs", e);
            throw new RuntimeException("Erro ao mesclar arquivos PDF", e);
        }
    }
}
