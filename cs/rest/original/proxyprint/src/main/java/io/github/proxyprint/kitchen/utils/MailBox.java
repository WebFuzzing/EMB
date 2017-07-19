package io.github.proxyprint.kitchen.utils;

/**
 * Created by daniel on 17-04-2016.
 */

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created by daniel on 2016.04.17
 */
public class MailBox {
    private static String PROXYPRINT_EMAIL = "proxyprint.pt@gmail.com";
    private static String PROXYPRINT_PASSWORD = "cenasmaradas";
    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    private Properties props;

    public MailBox() {
        // Set properties
        props = System.getProperties();
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.store.protocol", "pop3");
        props.put("mail.transport.protocol", "smtp");
    }

    public boolean sedMailAcceptedRequest(RegisterRequest rr) {

        final String username = PROXYPRINT_EMAIL;
        final String password = PROXYPRINT_PASSWORD;
        try {
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(PROXYPRINT_EMAIL));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(rr.getManagerEmail(), false));
            msg.setSubject("ProxyPrint");

            msg.setText("Caro(a), " + rr.getManagerName() + ", a sua reprografia ("+rr.getpShopName()+") foi aceite na ProxyPrint. Já pode fazer log in na plataforma com as suas credenciais" +
                    "\nde acesso e entrar na área de administração da sua reprografia.\n\nCom os melhores cumprimentos,\n\nA equipa ProxyPrint");

            msg.setSentDate(new Date());
            Transport.send(msg);
            return true;
        } catch (MessagingException e) {
            System.out.println("Erro no envio de email: " + e);
            return false;
        }
    }

    public boolean sedMailRejectedRegisterRequest(RegisterRequest rr, String motive) {

        final String username = PROXYPRINT_EMAIL;
        final String password = PROXYPRINT_PASSWORD;
        try {
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(PROXYPRINT_EMAIL));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(rr.getManagerEmail(), false));
            msg.setSubject("ProxyPrint");
            msg.setText("Caro(a), " + rr.getManagerName() + ", infelizmente o pedido de inscrição da sua reprografia ("+rr.getpShopName()+"), foi rejeitado.\n" +
                    "Motivo: " + motive + "\n\n" +
                    "Com os melhores cumprimentos,\n" +
                    "\n" +
                    "A equipa ProxyPrint");
            msg.setSentDate(new Date());
            Transport.send(msg);
            return true;
        } catch (MessagingException e) {
            System.out.println("Erro no envio de email: " + e);
            return false;
        }
    }

    public void sendEmailFinishedPrintRequest(Consumer consumer, long printRequestID, String pshopName) {
        final String username = PROXYPRINT_EMAIL;
        final String password = PROXYPRINT_PASSWORD;
        try {
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(PROXYPRINT_EMAIL));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(consumer.getEmail(), false));
            msg.setSubject("ProxyPrint");
            msg.setText("Olá " + consumer.getName() + ", o seu pedido nº #"+(printRequestID)+", está pronto!\n\n" +
                    "Assim que quiser pode deslocar-se à reprografia "+pshopName+" para levantar o pedido.\n\n" +
                    "Com os melhores cumprimentos,\n" +
                    "\n" +
                    "A equipa ProxyPrint");
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (MessagingException e) {
            System.out.println("Erro no envio de email: " + e);
        }
    }

    public boolean sendEmailCancelledPrintRequest(PrintRequest preq, Consumer c, String motive) {

        final String username = PROXYPRINT_EMAIL;
        final String password = PROXYPRINT_PASSWORD;
        try {
            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(PROXYPRINT_EMAIL));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(c.getEmail(), false));
            msg.setSubject("ProxyPrint");
            msg.setText("Caro(a), " + c.getName() + ", infelizmente o seu pedido de impressão #"+ preq.getId() +", foi cancelado pela reprografia "+preq.getPrintshop().getName()+".\n" +
                    "Motivo: " + motive + "\n\n" +
                    "Com os melhores cumprimentos,\n" +
                    "\n" +
                    "A equipa ProxyPrint");
            msg.setSentDate(new Date());
            Transport.send(msg);
            return true;
        } catch (MessagingException e) {
            System.out.println("Erro no envio de email: " + e);
            return false;
        }
    }
}
