package id.co.bni.mid.controller;

import id.co.bni.mid.helpers.EncryptingKey;
import id.co.bni.mid.service.CardMaintenanceService;
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
@ConditionalOnExpression("${apiendpoint.cardactivate:false}")
public class CardActivateController {

    private static Logger logger = LoggerFactory.getLogger(CardActivateController.class);

    @Autowired
    CardMaintenanceService cardMaintenanceService;

    @Autowired
    EncryptingKey encryptingKey;

    @PostMapping(value = "/api/card/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity cardActivate(@Validated @RequestBody CardValidator body, HttpServletRequest request) throws Exception {
        String outcoming = cardMaintenanceService.cardActivate(body, request.getRemoteAddr());
        logger.info(String.format("request|%s|response|%s", encryptingKey.encrypt(body.getCardNumber()), new JSONObject(outcoming).get("message")));
        return ResponseEntity.ok(outcoming);
    }
}
