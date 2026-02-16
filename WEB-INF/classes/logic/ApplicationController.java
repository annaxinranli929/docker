package logic;

public interface ApplicationController {
    public RequestContext getRequestContext(Object request);
    public ResponseContext handleRequest(RequestContext reqc);
    public void handleResponse(RequestContext reqc, ResponseContext resc);
}