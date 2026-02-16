package command.account;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import bean.account.AccountBean;
import bean.card.CardBean;
import command.AbstractCommand;
import dao.ConnectionManager;
import dao.card.CardDao;
import logic.RequestContext;
import logic.ResponseContext;

public class SendUpdateAccountCommand extends AbstractCommand{
    public ResponseContext execute(ResponseContext resc){

        RequestContext reqc = getRequestContext();
        AccountBean account = (AccountBean) reqc.getSessionAttribute("account");

        CardDao cardDao = new CardDao();
        cardDao.setConnection(ConnectionManager.getInstance().getConnection());
        List<CardBean> cards = cardDao.findByUserId(account.getUserId());

    
        HttpServletRequest request = (HttpServletRequest) reqc.getRequest();
        request.setAttribute("cards", cards);

        resc.setTarget("update_page");
        return resc;
    }
}
