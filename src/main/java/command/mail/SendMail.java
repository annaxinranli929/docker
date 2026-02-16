package command.mail;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

import java.io.InputStream;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import bean.mail.MailInformationBean;

public class SendMail {

    public boolean send(MailInformationBean info) {

        // SMTPサーバー設定
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        // Gmailのメールアカウント情報
        String userName = info.getUserName();
        String displayedName = info.getDisplayedName();
        String password = info.getPassword();

        // 送信先の情報
        String sendUserName = info.getSendUserName();

        // セッション作成
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        try {
            // メール作成
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sendUserName, displayedName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendUserName));
            message.setSubject(info.getMailSubject());

            // htmlを読み込む
            // クラスパス（src/main/resources直下）から読み込む
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("mail.html")) {
                if (is == null) {
                    throw new IOException("テンプレートが見つかりません。パスを確認してください。");
                }
                // InputStreamをStringに変換
                String htmlContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                String path = "http://localhost:8080/NEOcinema/changePass?token=" + info.getToken();
                htmlContent = htmlContent.replace("{{resetURL}}", path);

                message.setHeader("Content-Type", "text/html; charset=UTF-8");
                message.setContent(htmlContent, "text/html; charset=utf-8");
            }

            // 送信
            Transport.send(message);

            System.out.println("メール送信成功！");

            return true;

        // } catch (MessagingException e) {
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
