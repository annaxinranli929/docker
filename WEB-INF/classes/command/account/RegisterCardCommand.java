package command.account;

import java.util.List;

import bean.account.AccountBean;
import bean.card.CardBean;
import command.AbstractCommand;
import dao.ConnectionManager;
import dao.card.CardDao;
import logic.RequestContext;
import logic.ResponseContext;

public class RegisterCardCommand extends AbstractCommand<CardBean> {

    @Override
    public ResponseContext execute(ResponseContext resc) {

        RequestContext reqc = getRequestContext();

        String cardNumber = reqc.getParameter("creditCardNumber")[0];
        int expMonth = Integer.parseInt(reqc.getParameter("expireMonth")[0]);
        int expYear  = Integer.parseInt(reqc.getParameter("expireYear")[0]);
        int userId   = Integer.parseInt(reqc.getParameter("userId")[0]);

        String brand = detectBrand(cardNumber);
        String last4 = cardNumber.substring(cardNumber.length() - 4);

        CardBean card = new CardBean();
        card.setUserId(userId);
        card.setBrand(brand);
        card.setLast4(last4);
        card.setExpMonth(expMonth);
        card.setExpYear(expYear);
        card.setDefault(true);

        CardDao cardDao = (CardDao) getDao();
        cardDao.setConnection(ConnectionManager.getInstance().getConnection());

        ConnectionManager.getInstance().beginTransaction();

        boolean result;

        List<CardBean> list = cardDao.findByUserId(userId);

        if (list == null || list.isEmpty()) {
            result = cardDao.insert(card);
        } else {
            result = cardDao.updateByUserId(card);
        }

        if (result) {
            ConnectionManager.getInstance().commit();

            // sessionのaccount更新
            AccountBean acc =
                (AccountBean) reqc.getSessionAttribute("account");
            acc.setCreditCardNumber(last4);
            reqc.setSessionAttribute("account", acc);

            reqc.setSessionAttribute("message", "クレジットカードを登録しました。");
        } else {
            ConnectionManager.getInstance().rollback();
            reqc.setSessionAttribute("message", "カード登録に失敗しました。");
        }

        resc.setTarget("update_page");
        return resc;
    }

    private String detectBrand(String num) {
        if (num.startsWith("4")) return "VISA";
        if (num.startsWith("5")) return "MasterCard";
        if (num.startsWith("3")) return "AMEX";
        return "UNKNOWN";
    }
}
