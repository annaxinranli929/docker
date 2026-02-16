package logic;

public interface RequestContext {
    public String getFactoryPath();
    public String[] getParameter(String key);
    public Object getRequest();
    public void setRequest(Object request);
    public void startSession();
    public Object getSessionAttribute(String key);
    public void setSessionAttribute(String key, Object value);
    public void invalidateSession();

}