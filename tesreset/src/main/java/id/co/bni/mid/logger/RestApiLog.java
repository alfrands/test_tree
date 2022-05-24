package id.co.bni.mid.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RestApiLog {

    private static Logger logger = LoggerFactory.getLogger(RestApiLog.class);

    private Object requestId;
    private String remoteAddr;
    private String host;
    private String date;
    private String method;
    private String url;
    private String userAgent;
    private String contentLength;
    private String contentType;
    private Object statusCode;
    private String responseTime;

    public RestApiLog(Object requestId, String remoteAddr, String host, String date, String method, String url, String userAgent, String contentLength, String contentType, Object statusCode, String responseTime) {
        this.requestId = requestId;
        this.remoteAddr = remoteAddr;
        this.host = host;
        this.date = parseDate(date);
        this.method = method;
        this.url = url;
        this.userAgent = userAgent;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.statusCode = statusCode;
        this.responseTime = responseTime;
    }

    public Object getRequestId() {
        return requestId;
    }

    public void setRequestId(Object requestId) {
        this.requestId = requestId;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getContentLength() {
        return contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Object getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Object statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    @Override
    public String toString() {
        return String.format("requestId|%s|remoteAddr|%s|host|%s|date|%s|method|%s|url|%s|" +
                "userAgent|%s|contentLength|%s|contentType|%s|statusCode|%s|responseTime|%s|",
                requestId, remoteAddr, host, date, method, url, userAgent, contentLength, contentType, statusCode, responseTime);
    }

    private static String parseDate(String input) {
        SimpleDateFormat parser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        Date date = null;
        try {
            date = parser.parse(input);
        } catch (ParseException er) {
            logger.warn(er.getMessage());
            logger.error(er.getMessage(), er);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatter.format(date);
    }
}
