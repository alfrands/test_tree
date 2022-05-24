package id.co.bni.mid.controller;

import id.co.bni.mid.service.CheckEdclogService;
import id.co.bni.mid.validator.TerminalIdValidator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@ConditionalOnExpression("${apiendpoint.edclog:false}")
public class EdclogController {
    private static Logger logger = LoggerFactory.getLogger(EdclogController.class);

    @Autowired
    CheckEdclogService checkEdclogService;

    @PostMapping(value = "/api/edclog", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity edclogA(@Validated @RequestBody TerminalIdValidator body, HttpServletRequest request) throws Exception {
        String outcoming = checkEdclogService.edclogA(body, request.getRemoteAddr());
        logger.info(String.format("request|%s|response|%s", body.getTerminalId(), new JSONObject(outcoming).get("message")));
        return ResponseEntity.ok(outcoming);
    }
}
