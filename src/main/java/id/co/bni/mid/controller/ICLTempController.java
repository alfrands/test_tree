package id.co.bni.mid.controller;

import id.co.bni.mid.helpers.EncryptingKey;
import id.co.bni.mid.service.IclService;
import id.co.bni.mid.validator.IclValidator;
import id.co.bni.mid.validator.OaaiValidator;
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
@ConditionalOnExpression("${apiendpoint.icltemp:false}")
public class ICLTempController {

    private static Logger logger = LoggerFactory.getLogger(ICLTempController.class);

    @Autowired
    IclService iclService;

    @Autowired
    EncryptingKey encryptingKey;

    @PostMapping(value = "/api/icl/temp", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity iclTemp(@Validated @RequestBody IclValidator body, HttpServletRequest request) throws Exception {
        String outcoming = iclService.iclTemp(body, request.getRemoteAddr());
        logger.info(String.format("request|%s|response|%s", encryptingKey.encrypt(body.getCardNumber()), new JSONObject(outcoming).get("message")));
        return ResponseEntity.ok(outcoming);
    }
}
