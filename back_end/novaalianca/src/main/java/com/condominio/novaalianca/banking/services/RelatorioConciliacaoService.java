package com.condominio.novaalianca.banking.services;

import com.condominio.novaalianca.banking.models.entities.Conciliacao;
import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.banking.models.entities.Saldo;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RelatorioConciliacaoService {

    public byte[] gerarPdfConciliacao(Conciliacao conciliacao, Saldo ultimoSaldo) {
        try {
            Map<String, Double> despesasPorDia = new TreeMap<>();
            Map<String, Double> receitasPorDia = new TreeMap<>();
            
            List<ExtratoReportDTO> extratosDTO = new ArrayList<>();
            List<Extrato> extratos = conciliacao.getExtratos();
            
            if (extratos != null) {
                extratos.sort((e1, e2) -> {
                    if (e1.getDtTransacao() == null) return 1;
                    if (e2.getDtTransacao() == null) return -1;
                    return e1.getDtTransacao().compareTo(e2.getDtTransacao());
                });

                for (Extrato e : extratos) {
                    // Prepara dados do grafico
                    if (e.getValorTransacao() != null && e.getDtTransacao() != null) {
                        String dataStr = e.getDtTransacao().format(DateTimeFormatter.ofPattern("dd/MM"));
                        if ("C".equalsIgnoreCase(e.getTipoOperacao()) || "CREDITO".equalsIgnoreCase(e.getTipoTransacao())) {
                            receitasPorDia.put(dataStr, receitasPorDia.getOrDefault(dataStr, 0.0) + e.getValorTransacao());
                            despesasPorDia.putIfAbsent(dataStr, 0.0);
                        } else {
                            despesasPorDia.put(dataStr, despesasPorDia.getOrDefault(dataStr, 0.0) + Math.abs(e.getValorTransacao()));
                            receitasPorDia.putIfAbsent(dataStr, 0.0);
                        }
                    }
                    
                    // Prepara DTO para a Tabela Jasper
                    ExtratoReportDTO dto = new ExtratoReportDTO();
                    dto.setData(e.getDtTransacao() != null ? e.getDtTransacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
                    dto.setTitulo(e.getTituloTransacao() != null ? e.getTituloTransacao() : "");
                    
                    boolean isCredito = "C".equalsIgnoreCase(e.getTipoOperacao()) || "CREDITO".equalsIgnoreCase(e.getTipoTransacao());
                    dto.setTipoBadge(isCredito ? "C" : "D");
                    
                    dto.setValor(e.getValorTransacao() != null ? "R$ " + String.format("%.2f", Math.abs(e.getValorTransacao())) : "R$ 0,00");
                    dto.setDescricao(e.getDescricao() != null ? e.getDescricao() : "");
                    
                    String nomeCategoria = "-";
                    if (e.getCategoriaGasto() != null && e.getCategoriaGasto().getDescricao() != null) {
                        nomeCategoria = e.getCategoriaGasto().getDescricao();
                    }
                    dto.setCategoria(nomeCategoria);
                    
                    extratosDTO.add(dto);
                }
            }

            // Gerar Gráfico de Barras FLAT com JFreeChart
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (String data : receitasPorDia.keySet()) {
                dataset.addValue(receitasPorDia.get(data), "Receitas (Créditos)", data);
                dataset.addValue(despesasPorDia.get(data), "Despesas (Débitos)", data);
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Fluxo de Caixa (Débitos vs Créditos) - Últimos 30 Dias",
                    "Período",
                    "Valor (R$)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false
            );
            
            chart.setBackgroundPaint(Color.WHITE);
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.setOutlineVisible(false);
            
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setBarPainter(new StandardBarPainter()); // Flat style
            renderer.setSeriesPaint(0, new Color(46, 204, 113));
            renderer.setSeriesPaint(1, new Color(231, 76, 60));
            renderer.setDrawBarOutline(false);
            renderer.setItemMargin(0.1);

            BufferedImage chartImage = chart.createBufferedImage(800, 250);

            // Carregar Template Jasper
            InputStream jasperStream = getClass().getResourceAsStream("/relatorios/conciliacao.jrxml");
            if (jasperStream == null) {
                throw new RuntimeException("Arquivo conciliacao.jrxml não encontrado em src/main/resources/relatorios");
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

            // Parâmetros
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("MES_REFERENCIA", conciliacao.getDataReferencia() != null ? conciliacao.getDataReferencia().format(DateTimeFormatter.ofPattern("MM/yyyy")) : "");
            parameters.put("DATA_GERACAO", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            if (ultimoSaldo != null && ultimoSaldo.getDisponivel() != null) {
                parameters.put("SALDO_FINAL", "R$ " + String.format("%.2f", ultimoSaldo.getDisponivel()));
            } else {
                parameters.put("SALDO_FINAL", "Não disponível");
            }
            
            parameters.put("CHART_IMAGE", chartImage);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(extratosDTO);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            return JasperExportManager.exportReportToPdf(jasperPrint);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF da Conciliação com JasperReports", e);
        }
    }

    public static class ExtratoReportDTO {
        private String data;
        private String titulo;
        private String tipoBadge;
        private String valor;
        private String descricao;
        private String categoria;

        public String getData() { return data; }
        public void setData(String data) { this.data = data; }

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getTipoBadge() { return tipoBadge; }
        public void setTipoBadge(String tipoBadge) { this.tipoBadge = tipoBadge; }

        public String getValor() { return valor; }
        public void setValor(String valor) { this.valor = valor; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
    }
}
