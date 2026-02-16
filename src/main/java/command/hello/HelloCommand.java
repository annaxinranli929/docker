package command.hello;

import logic.ResponseContext;
import command.AbstractCommand;
import bean.hello.HelloBean;
import dao.AbstractDao;

public class HelloCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        AbstractDao dao = getDao();
        HelloBean hello = (HelloBean) dao.selectAll().get(0);
        String mess = hello.getMess();
        resc.setResult(mess);
        resc.setTarget("hello");
        return resc;
    }
}