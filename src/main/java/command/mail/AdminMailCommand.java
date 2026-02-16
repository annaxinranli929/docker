package command.mail;

import java.util.List;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import logic.ResponseContext;
import logic.RequestContext;
import command.AbstractCommand;
import bean.mail.MailInformationBean;
import bean.account.AccountBean;
import dao.AbstractDao;
import dao.ConnectionManager;

public class AdminMailCommand extends AbstractCommand<AccountBean> {
    public ResponseContext execute(ResponseContext resc) {

        System.out.println("MailCommand到達");

        SendMail sendMail = new SendMail();
        MailInformationBean mailInfo = new MailInformationBean();
        RequestContext reqc = getRequestContext();

        String token = generateToken();
        System.out.println("token = " + token);

        mailInfo.setSendUserName(reqc.getParameter("mailAddress")[0]);
        mailInfo.setMailSubject("パスワードリセットのお願い");
        mailInfo.setMailHtmlPath("/WEB-INF/mail.html");
        mailInfo.setToken(token);

        boolean result = sendMail.send(mailInfo);

        if (!result) {
            resc.setResult(result);
            resc.setRedirect(true);
            resc.setTargetURI("showUsers");
            return resc;
        }

        AccountBean user = new AccountBean();

        user.setEmail(reqc.getParameter("mailAddress")[0]);
        user.setPassword(token);

        AbstractDao<AccountBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        ConnectionManager.getInstance().beginTransaction();
        dao.update(user);
        ConnectionManager.getInstance().commit();

        resc.setResult(result);

        resc.setRedirect(true);
        resc.setTargetURI("showUsers");
        return resc;
    }

    private String generateToken() {
        String charCandidate = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        
        return IntStream.range(0, 20)
                .mapToObj(i -> String.valueOf(charCandidate.charAt(random.nextInt(charCandidate.length()))))
                .collect(Collectors.joining());
    }
}