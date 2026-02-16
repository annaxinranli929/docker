package command.price;

import java.util.List;

import logic.ResponseContext;
import command.AbstractCommand;
import dao.AbstractDao;
import dao.ConnectionManager;
import bean.price.PriceBean;

public class ShowPriceCommand extends AbstractCommand<PriceBean> {
    public ResponseContext execute(ResponseContext resc) {
        AbstractDao<PriceBean> dao = getDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        List<PriceBean> prices = dao.selectAll();

        resc.setResult(prices);
        resc.setTarget("price");
        return resc;
    }
}