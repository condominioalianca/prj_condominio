package com.condominio.novaalianca.services;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.dto.EmailDTO;
import com.condominio.novaalianca.util.TemplateEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final NovaAliancaProperties propertiesConfig;

    private final JavaMailSender mailSender;

//    @Autowired
//    private BoletoService boletoService;
//
//    @Autowired
//    private RequestBoletoBuilder builder;

    public String sendMail(EmailDTO emailDTO) throws Exception {

        log.info("Email: {} numero unidade {}", emailDTO.getTo(), emailDTO.getNumeroUnidade() );

        DateTimeFormatter formatterReferencia = DateTimeFormatter.ofPattern("MM-yyyy");
//        byte[] decoder = Base64.getDecoder().decode(boletoService.downloadPDF(emailDTO.getNossoNumero(), builder.requestBoleto("boleto-cobranca.read")).getPdf());
//
//        emailDTO.setAnexo(decoder);
        emailDTO.setSubject("Cobrança Condomínio - " + LocalDate.now().format(formatterReferencia).toString());
        String mesReferencia = LocalDate.now().format(formatterReferencia).toString();

        // Try cath
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mail, true);

            // Configurando os detalhes necessários para envio
            messageHelper.setFrom(propertiesConfig.getMailFrom());
            messageHelper.setTo(emailDTO.getTo());
            messageHelper.setSubject(emailDTO.getSubject());

            //TEMPLATE DO EMAIL
            TemplateEmail template = new TemplateEmail();
            String htmlFileName = template.templateEmail();
            htmlFileName = htmlFileName.replace("datadoboleto", mesReferencia);
            messageHelper.setText(htmlFileName, true);

            //Anexo do Email
            final DataSource attachment = new ByteArrayDataSource(emailDTO.getAnexo(), "application/pdf");
            messageHelper.addAttachment("Boleto Cobranca - AP " + emailDTO.getNumeroUnidade() + ".pdf", attachment);

            // Enviando email
            mailSender.send(mail);
            return ("Email enviado com Sucesso, Unidade" + emailDTO.getNumeroUnidade() +"Mes"+ mesReferencia);
        }

        catch (Exception e) {
            throw new Exception( "Erro enviando Email!!!   " + e) ;
        }
    }

    public void sendPasswordResetEmail(String toEmail, String code) throws Exception {
        log.info("Enviando e-mail de redefinição de senha para {}", toEmail);
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mail, false);

            messageHelper.setFrom(propertiesConfig.getMailFrom());
            messageHelper.setTo(toEmail);
            messageHelper.setSubject("Código de Recuperação de Senha - Condomínio Nova Aliança");

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; rounded-lg: 12px;'>"
                + "<h2 style='color: #0f172a; text-align: center;'>Recuperação de Senha</h2>"
                + "<p style='color: #475569; font-size: 16px;'>Você solicitou a redefinição de sua senha para acesso ao painel do Condomínio Nova Aliança. Utilize o código de 8 dígitos abaixo para prosseguir:</p>"
                + "<div style='background-color: #f1f5f9; padding: 15px; text-align: center; border-radius: 8px; margin: 20px 0;'>"
                + "  <span style='letter-spacing: 6px; font-family: monospace; font-size: 28px; font-weight: bold; color: #10b981;'>" + code + "</span>"
                + "</div>"
                + "<p style='color: #64748b; font-size: 14px;'>Este código é válido por <strong>10 minutos</strong>. Se você não solicitou esta redefinição, por favor, ignore este e-mail.</p>"
                + "<hr style='border: 0; border-top: 1px solid #e2e8f0; margin: 20px 0;'>"
                + "<p style='color: #94a3b8; font-size: 12px; text-align: center;'>Condomínio Nova Aliança - Segurança da Informação</p>"
                + "</div>";

            messageHelper.setText(htmlBody, true);
            mailSender.send(mail);
        } catch (Exception e) {
            log.error("Erro ao enviar email de reset de senha para {}: {}", toEmail, e.getMessage());
            throw new Exception("Erro enviando Email de reset!!! " + e);
        }
    }
}


