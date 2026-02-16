package bean.mail;

import bean.Bean;

public class MailInformationBean implements Bean {
    private String userName = "movieticketsystem2525";
    private String displayedName = "NEOcinema - 映画予約システム";
    private String password = "ioha hjls urbv phvm";
    private String sendUserName;
    private String mailSubject;
    private String mailHtmlPath;
    private String token;

    public String getUserName() {
        return userName;
    }
    public String getDisplayedName() {
        return displayedName;
    }
    public String getPassword() {
        return password;
    }
    public String getSendUserName() {
        return sendUserName;
    } 
    public String getMailSubject() {
        return mailSubject;
    }
    public String getMailHtmlPath() {
        return mailHtmlPath;
    }
    public String getToken() {
        return token;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }
    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }
    public void setMailHtmlPath(String mailHtmlPath) {
        this.mailHtmlPath = mailHtmlPath;
    }
    public void setToken(String token) {
        this.token = token;
    }
}