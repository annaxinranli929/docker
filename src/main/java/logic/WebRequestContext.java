package logic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

public class WebRequestContext implements RequestContext {

    private Map<String, String[]> _parameters;
    private HttpServletRequest _request;

    public String getFactoryPath() {
        String servletPath = _request.getServletPath();
        String commandPath = servletPath.substring(1);
        return commandPath;
    }
    public String[] getParameter(String key) {
        return _parameters.get(key);
    }
    public Object getRequest() {
        return _request;
    }
    public void setRequest(Object req) {
        _request = (HttpServletRequest) req;
        _parameters = _request.getParameterMap();
    }
    public void startSession() {
        _request.getSession(true);
        System.out.println("セッション開始");
    }
    public Object getSessionAttribute(String key) {
        try {
            HttpSession session = _request.getSession(false);
            Object result = session.getAttribute(key);
            return result;
        } catch (IllegalStateException e) {
            throw new RuntimeException("セッション開始前にgetAttributeしたね", e);
        }
    }
    public void setSessionAttribute(String key, Object value) {
        try {
            HttpSession session = _request.getSession(false);
            session.setAttribute(key, value);
        } catch (IllegalStateException e) {
            throw new RuntimeException("セッション開始前にsetAttributeしたね", e);
        }
    }
    public void invalidateSession() {
        try {
            HttpSession session = _request.getSession(false);
            session.invalidate();
            System.out.println("セッション終了");
        } catch (IllegalStateException e) {
            throw new RuntimeException("セッション開始前にinvalidateしたね", e);
        }
    }
}