package id.co.bni.mid.controller;

import id.co.bni.mid.helpers.EncryptingKey;
import id.co.bni.mid.service.InquiryService;
import id.co.bni.mid.validator.CardValidator;
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
@ConditionalOnExpression("${apiendpoint.creditlimit:false}")
public class InquiryLimitController {

    private static Logger logger = LoggerFactory.getLogger(InquiryLimitController.class);

    @Autowired
    InquiryService inquiryService;

    @Autowired
    EncryptingKey encryptingKey;

    @PostMapping(value = "/api/creditlimit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity creditlimit(@Validated @RequestBody CardValidator body, HttpServletRequest request) throws Exception {
        String outcoming = inquiryService.inquiryLimit(body, request.getRemoteAddr());
        logger.info(String.format("request|%s|response|%s", encryptingKey.encrypt(body.getCardNumber()), new JSONObject(outcoming).get("message")));
        return ResponseEntity.ok(outcoming);
    }
}
