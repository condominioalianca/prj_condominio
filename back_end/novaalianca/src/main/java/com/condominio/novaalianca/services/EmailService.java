package com.condominio.novaalianca.services;

import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.dto.EmailDTO;
import com.condominio.novaalianca.util.Autenticacao;
import com.condominio.novaalianca.util.TemplateEmail;
import com.inter.boletos.client.dto.boleto.BoletoPDFDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private NovaAliancaProperties propertiesConfig;

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public void sendMail(EmailDTO emailDTO) throws Exception {
        try {
            Properties props = new Properties();
            /** Parâmetros de conexão com servidor Gmail */

            props.put("mail.smtp.host", propertiesConfig.getHost());
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.socketFactory.port", propertiesConfig.getPort());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            Autenticacao auth = new Autenticacao(propertiesConfig.getUsernameMail(), propertiesConfig.getSenhaMail());
            Session sessao = Session.getInstance(props, auth);
            MimeMessage m = new MimeMessage(sessao);

            m.setFrom(new InternetAddress(emailDTO.getFrom()));
            Address[] to = new InternetAddress[]{new InternetAddress(emailDTO.getTo())};
            m.setRecipients(Message.RecipientType.TO, to);
            m.setSubject(emailDTO.getSubject());

            if (emailDTO.getAnexo() != null) {
                Multipart corpo = new MimeMultipart();
                InternetHeaders headers = new InternetHeaders();
                headers.addHeader("Content-Type", "application/pdf");
                MimeBodyPart partPhoto = new MimeBodyPart();
                partPhoto.setDataHandler(new DataHandler(new ByteArrayDataSource(emailDTO.getAnexo(), "aplication/pdf")));
                partPhoto.setFileName("Boleto Cobranca - AP " + emailDTO.getNumeroUnidade() + ".pdf");


                TemplateEmail template = new TemplateEmail();
                String htmlFileName = template.templateEmail();
                DateTimeFormatter formatterReferencia = DateTimeFormatter.ofPattern("MM-yyyy");
                htmlFileName = htmlFileName.replace("datadoboleto", LocalDate.now().format(formatterReferencia).toString());
                MimeBodyPart part = new MimeBodyPart();

                part.setText(htmlFileName, "iso-8859-1", "html");

                corpo.addBodyPart(part);
                corpo.addBodyPart(partPhoto);


                m.setContent(corpo);
            } else {
                Multipart corpo = new MimeMultipart();

                BoletoPDFDto boletoPDFDto = new BoletoPDFDto();
                InternetHeaders headers = new InternetHeaders();
                headers.addHeader("Content-Type", "application/pdf");
                MimeBodyPart partPhoto = new MimeBodyPart();
                partPhoto.setDataHandler(new DataHandler(boletoPDFDto, "aplication/pdf"));
                partPhoto.setFileName("Boleto Cobranca - AP " + emailDTO.getNumeroUnidade() + ".pdf");


                TemplateEmail template = new TemplateEmail();
                String htmlFileName = template.templateEmail();
                DateTimeFormatter formatterReferencia = DateTimeFormatter.ofPattern("MM-yyyy");
                htmlFileName = htmlFileName.replace("datadoboleto", LocalDate.now().format(formatterReferencia).toString());
                MimeBodyPart part = new MimeBodyPart();

                part.setText(htmlFileName, "iso-8859-1", "html");

                corpo.addBodyPart(part);
                corpo.addBodyPart(partPhoto);


                m.setContent(corpo);
            }


            Transport.send(m);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }


//    public String sendMail(MailDetail mailDetail) {
//        // Try block to check for exceptions handling
//        try {
//
//            // Creating a simple mail message object
//            SimpleMailMessage emailMessage = new SimpleMailMessage();
//
//            // Setting up necessary details of mail
//            emailMessage.setFrom(sender);
//            emailMessage.setTo(mailDetail.getRecipient());
//            emailMessage.setSubject(mailDetail.getSubject());
//            emailMessage.setText(mailDetail.getMsgBody());
//
//            // Sending the email
//            mailSender.send(emailMessage);
//            return "Email has been sent successfully...";
//        }
//
//        // Catch block to handle the exceptions
//        catch (Exception e) {
//            return "Error while Sending email!!!";
//        }
//    }


    public String sendMailWithAttachment(EmailDTO mailDetail) {
        // Creating a Mime Message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachment to be send
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(mailDetail.getTo());
            mimeMessageHelper.setSubject(mailDetail.getSubject());
            mimeMessageHelper.setText(mailDetail.getBody());

            // Adding the file attachment
            FileSystemResource file = new FileSystemResource(new File(mailDetail.getContent()));
            InputStream targetStream = new ByteArrayInputStream(mailDetail.getAnexo());
            ByteArrayResource resource = new ByteArrayResource(mailDetail.getAnexo());

            //mimeMessageHelper.addAttachment("Boleto Cobranca - AP " + mailDetail.getNumeroUnidade() + ".pdf", new InputStreamResource(targetStream));
            //mimeMessageHelper.addAttachment("Boleto Cobranca - AP " + mailDetail.getNumeroUnidade() + ".pdf",new InputStreamResource(targetStream),"aplication/pdf");
            //final ByteArrayOutputStream document = createInMemoryDocument("body");
            //final InputStream inputStream = new ByteArrayInputStream(document.toByteArray());
            //final DataSource attachment = new ByteArrayDataSource(inputStream, "application/octet-stream");
            mimeMessageHelper.addAttachment("Boleto Cobranca - AP " + mailDetail.getNumeroUnidade() + ".pdf", resource);

            log.info("Passou");
            // Sending the email with attachment
            mailSender.send(mimeMessage);
            return "Email has been sent successfully...";
        }

        // Catch block to handle the MessagingException
        catch (MessagingException e) {

            // Display message when exception is occurred
            return "Error while sending email!!!";
        }
    }
}



