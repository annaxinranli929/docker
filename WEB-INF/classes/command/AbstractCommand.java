package command;

import logic.RequestContext;
import logic.ResponseContext;
import dao.AbstractDao;
import bean.Bean;

public abstract class AbstractCommand<T extends Bean> {
    private RequestContext reqc;
    private AbstractDao<T> dao;
    public void init(RequestContext reqc) {
        this.reqc = reqc;
    }
    public void setDao(AbstractDao<T> dao) {
        this.dao = dao;
    }
    public AbstractDao<T> getDao() {
        return dao;
    }
    public RequestContext getRequestContext() {
        return reqc;
    }
    public abstract ResponseContext execute(ResponseContext resc);
}