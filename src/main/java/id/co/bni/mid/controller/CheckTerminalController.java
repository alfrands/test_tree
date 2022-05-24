package id.co.bni.mid.controller;

import id.co.bni.mid.service.CheckTerminalService;
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
@ConditionalOnExpression("${apiendpoint.checkterminal:false}")
public class CheckTerminalController {
    private static Logger logger = LoggerFactory.getLogger(CheckTerminalController.class);

    @Autowired
    CheckTerminalService checkTerminalService;

    @PostMapping(value = "/api/checkterminal", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity checkterminal(@Validated @RequestBody TerminalIdValidator body, HttpServletRequest request) throws Exception {
        String outcoming = checkTerminalService.checkTerminal(body, request.getRemoteAddr());
        logger.info(String.format("request|%s|response|%s", body.getTerminalId(), new JSONObject(outcoming).get("message")));
        return ResponseEntity.ok(outcoming);
    }
}
