package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.helpers.StringHelpers;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceChangePin;
import id.co.bni.mid.repository.LogServiceChangePinRepository;
import id.co.bni.mid.validator.ChangePinValidator;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ChangePinServiceImpl implements ChangePinService {

    private static Logger logger = LoggerFactory.getLogger(ChangePinServiceImpl.class);

    @Autowired
    LogServiceChangePinRepository changePinRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String changePin(ChangePinValidator body, String reqAddr) throws Exception {
        LogServiceChangePin logService = new LogServiceChangePin();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String localdate = new SimpleDateFormat("MMdd").format(now);
            String localtime = new SimpleDateFormat("HHmmss").format(now);
            String traceNbr = String.format("%06d", helperService.random(0, 999999), helperService.random(0, 999999));
            String gmt = String.format("%s%s", localdate, localtime);

            logService.setRequest_address(reqAddr);
            logService.setChannelID(body.getChannelID());
            logService.setTraceNbr(traceNbr);
            logService.setLocalDateTime(gmt);
            logService.setRequest(body.toString());

            validateInput(body);

            String cardNumber = body.getCardNumber();
            String oldpin = body.getOldpin();
            String newpin = body.getNewpin();

            String f41 = helperService.encodeURL(env.getProperty("changepin.f41"));
            String f43 = helperService.encodeURL(env.getProperty("changepin.f43"));

            String url = env.getProperty("apicl.changepin");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("localdte", localdate)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("oldpin", oldpin)
                    .queryParam("newpin", newpin)
                    .queryParam("41", f41)
                    .queryParam("43", f43)
                    .queryParam("tracenum", traceNbr)
                    .queryParam("postdte", localdate)
                    .queryParam("localtme", localtime)
                    .queryParam("gmt", gmt).build(true);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiChangePin(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                changePinRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                changePinRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            changePinRepo.save(logService);

            JSONObject obj1 = object.getJSONObject("BNS003");
            JSONObject obj2 = obj1.getJSONObject("BNICA_RECORD");
            JSONObject objFields = obj2.getJSONObject("BNICA_ISO_FIELDS");
            String responseCode = objFields.get("BNICA_F039_RESP_CDE").toString();

            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            String responseMessage = helperService.getResponseMessage(responseCode);

            if (responseCode.equals("00")) {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Change PIN sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Change PIN gagal");
            }

            LogServiceChangePin outputLog = new LogServiceChangePin(logService);
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            changePinRepo.save(outputLog);

        } catch (Exception ste) {
            logger.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            changePinRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    private void validateInput(ChangePinValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String oldpin = input.getOldpin();
        String newpin = input.getNewpin();
        String channelID = input.getChannelID();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(oldpin) ||
                StringUtils.isEmpty(newpin) || StringUtils.isEmpty(channelID)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
        if (newpin.equals(oldpin)) throw new Exception("Field 'newpin' and 'oldpin' has same value");
        if (channelID.length() > 20) throw new Exception("Field 'channelID': " + channelID + " exceeds maximum length (20)");
    }
}
