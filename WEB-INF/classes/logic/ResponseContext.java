package logic;

public interface ResponseContext {
    public Object getResult();
    public String getTarget();
    public void setResult(Object result);
    public void setTarget(String transferInfo);
    public void setTargetURI(String uri);
    public void setResponse(Object response);
    public Object getResponse();
    public void setRedirect(boolean redirect);
    public boolean isRedirect();
}