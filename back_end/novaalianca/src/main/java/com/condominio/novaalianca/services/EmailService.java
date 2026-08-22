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


}


