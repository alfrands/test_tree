package id.co.bni.mid.controller;

import id.co.bni.mid.helpers.EncryptingKey;
import id.co.bni.mid.service.TransCCService;
import id.co.bni.mid.validator.TransactionValidator;
import id.co.bni.mid.validator.ValidateValidator;
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
@ConditionalOnExpression("${apiendpoint.transcc:false}")
public class TransCCController {

    private static Logger logger = LoggerFactory.getLogger(TransCCController.class);

    @Autowired
    TransCCService transCCService;

    @Autowired
    EncryptingKey encryptingKey;

    @PostMapping(value = "/api/transCC", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity transCC(@Validated @RequestBody TransactionValidator body, HttpServletRequest request) throws Exception {
        String outcoming = transCCService.transactionCC(body, request.getRemoteAddr());
        logger.info(String.format("request|%s|response|%s", encryptingKey.encrypt(body.getCardNumber()), new JSONObject(outcoming).get("message")));
        return ResponseEntity.ok(outcoming);
    }
}
