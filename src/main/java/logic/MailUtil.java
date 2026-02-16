package logic;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;
import jakarta.mail.util.ByteArrayDataSource;
import java.util.Properties;

public class MailUtil {

    private static final String FROM_EMAIL = "hch902@gmail.com";
    private static final String APP_PASSWORD = "beotmhnnkgnoowcg";

    /* QRコード添付予約メール送信     */
    public static void sendBookingMailWithQr(
            String toEmail,
            String subject,
            String body,
            byte[] qrPngBytes
    ) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        System.out.println("[MAIL] send start to=" + toEmail);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM_EMAIL, "NEOcinema"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject, "UTF-8");

            //  メール本文
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body, "UTF-8");

            // QRコード添付部分
            MimeBodyPart qrPart = new MimeBodyPart();
            DataSource ds = new ByteArrayDataSource(qrPngBytes, "image/png");
            qrPart.setDataHandler(new DataHandler(ds));
            qrPart.setFileName("ticket_qr.png");

            // メルチパート構成
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(qrPart);

            msg.setContent(multipart);

            Transport.send(msg);

            System.out.println("[MAIL] send success");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
