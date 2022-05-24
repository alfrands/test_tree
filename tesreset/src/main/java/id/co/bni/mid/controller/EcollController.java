package id.co.bni.mid.controller;

import id.co.bni.mid.helpers.EncryptingKey;
import id.co.bni.mid.service.EcollService;
import id.co.bni.mid.validator.EcollValidator;
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
@ConditionalOnExpression("${apiendpoint.ecoll:false}")
public class EcollController {

    private static Logger logger = LoggerFactory.getLogger(EcollController.class);

    @Autowired
    EcollService ecollService;

    @Autowired
    EncryptingKey encryptingKey;

    @PostMapping(value = "/api/ecoll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity bnka(@Validated @RequestBody EcollValidator body, HttpServletRequest request) throws Exception {
        String outcoming = ecollService.VAFlaggingMessaging(body, request.getRemoteAddr());
        logger.info(String.format("request|%s|response|%s", encryptingKey.encrypt(body.getCardNumber()), new JSONObject(outcoming).get("message")));
        return ResponseEntity.ok(outcoming);
    }
}
