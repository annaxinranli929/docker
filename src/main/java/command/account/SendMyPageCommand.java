package command.account;

import java.util.List;
import java.util.Date;
import jakarta.servlet.http.HttpServletRequest; // ← これを追加

import logic.RequestContext;
import logic.ResponseContext;
import command.AbstractCommand;
import bean.account.AccountBean;
import bean.account.BookingHistoryBean;
import dao.AbstractDao;
import dao.ConnectionManager;
import bean.SearchDataBean;
import dao.card.CardDao;
import bean.card.CardBean;

public class SendMyPageCommand extends AbstractCommand<BookingHistoryBean> {

    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        
        AccountBean account = (AccountBean) reqc.getSessionAttribute("account");
        
        AbstractDao<BookingHistoryBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());

        SearchDataBean searchData = new SearchDataBean();
        searchData.setUserId(String.valueOf(account.getUserId()));

        List<BookingHistoryBean> results = dao.selectSearch(searchData);

        // カード情報取得
        CardDao cardDao = new CardDao();
        cardDao.setConnection(ConnectionManager.getInstance().getConnection());
        List<CardBean> cards = cardDao.findByUserId(account.getUserId());

        // RequestContext の Object を HttpServletRequest にキャスト
        HttpServletRequest request = (HttpServletRequest) reqc.getRequest();
        request.setAttribute("now", new Date());
        request.setAttribute("cards", cards);

        // 予約リストを ResponseContext にセット
        resc.setResult(results);
        resc.setTarget("my_page");

        return resc;
    }
}
