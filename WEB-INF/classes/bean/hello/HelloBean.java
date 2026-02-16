package bean.hello;

import bean.Bean;

public class HelloBean implements Bean {
    private String mess;
    public void setMess(String mess) {
        this.mess = mess;
    }
    public String getMess() {
        return mess;
    }
}