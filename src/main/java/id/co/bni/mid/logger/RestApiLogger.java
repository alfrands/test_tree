package id.co.bni.mid.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class RestApiLogger extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RestApiLogger.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        request.setAttribute("requestId", requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        log(request, response, executeTime);
    }

    private void log(HttpServletRequest request, HttpServletResponse response, long executeTime) {
        RestApiLog restApiLog = new RestApiLog(request.getAttribute("requestId"), request.getRemoteAddr(), request.getHeader("host"), response.getHeader("Date"),
         request.getMethod(), request.getRequestURI(), request.getHeader("user-agent"), request.getHeader("content-length"), request.getHeader("content-type"),
                response.getStatus(), String.format("%s ms", executeTime));
        logger.info(restApiLog.toString());
    }
}