package id.co.bni.mid.model;

public class ResponseDictionary {

    private String respCode;
    private String respMessage;
    private String appMessage;

    public ResponseDictionary(String respCode, String respMessage, String appMessage) {
        this.respCode = respCode;
        this.respMessage = respMessage;
        this.appMessage = appMessage;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    public String getAppMessage() {
        return appMessage;
    }

    public void setAppMessage(String appMessage) {
        this.appMessage = appMessage;
    }
}
