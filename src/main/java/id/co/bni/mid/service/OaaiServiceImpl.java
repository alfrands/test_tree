package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceEcoll;
import id.co.bni.mid.model.LogServiceOaai;
import id.co.bni.mid.repository.LogServiceOaaiRepository;
import id.co.bni.mid.validator.EcollValidator;
import id.co.bni.mid.validator.OaaiValidator;
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
import java.util.Locale;

@Service
public class OaaiServiceImpl implements OaaiService {

    private static Logger loggers = LoggerFactory.getLogger(OaaiServiceImpl.class);

    @Autowired
    LogServiceOaaiRepository oaaiRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String oaaiMessaging(OaaiValidator body, String reqAddr) throws Exception {
        LogServiceOaai logService = new LogServiceOaai();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());

            validateInput(body);
            String cardNumber = body.getCardNumber();
            String amount = body.getAmount();
            if (amount.length() < 13) {
                amount = StringUtils.leftPad(amount, 13, "0");
            }
            String merchantNbr = body.getMerchantNbr();
            if (merchantNbr.length() < 9) {
                merchantNbr = StringUtils.leftPad(merchantNbr, 9, "0");
            }

            Date now = new Date();
            String localdate = new SimpleDateFormat("MMdd").format(now);
            String localtime = new SimpleDateFormat("HHmmss").format(now);
            String gmt = String.format("%s%s", localdate, localtime);

            Date expiry = helperService.getSimpleDateFormat("yyMM").parse(body.getExpiryDate());
            String expiryDate = helperService.getSimpleDateFormat("MMyy").format(expiry);
            String signOnID = env.getProperty("oaai.signonid");

            String f48 = String.format("%-16s%-3s%-9s%-13s%-4s%-6s%s", cardNumber, body.getMerchantOrg(),
                    merchantNbr, amount, expiryDate, signOnID, helperService.encodeURL(body.getSignOnName()));

            String url = env.getProperty("apicl.oaai");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("f48", f48).build(true);

            logService.setCardNbr(cardNumber);
            logService.setLocalDateTime(gmt);
            logService.setRequest(builder.toUriString());

            ResponseEntity<String> oaaiResult = APIRepository.apiOAAI(builder.toUri());
            if (oaaiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                oaaiRepo.save(logService);
                throw new ConnectException();
            }
            if (oaaiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(oaaiResult.getBody());
                oaaiRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(oaaiResult.getBody());

            logService.setResponse(object.toString());
            oaaiRepo.save(logService);

            JSONObject obj1 = object.getJSONObject("SOAC002B");
            JSONObject obj2 = obj1.getJSONObject("BNSOA_RECORD");
            JSONObject objFields = obj2.getJSONObject("BNSOA_ISO_FIELDS");
            String responseCode = objFields.get("BNSOA_F039_RESP_CDE").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            String responseMessage = helperService.getResponseMessage(responseCode);

            if (responseCode.equals("00")) {
                String authCode = objFields.get("BNSOA_F038_AUTH_CDE").toString();
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("authCode", authCode);
                result.put("message", "Submit OAAI sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Submit OAAI gagal");
            }

            LogServiceOaai outputLog = new LogServiceOaai(logService.getRequest_address(), logService.getCardNbr(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            oaaiRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            logService.setResponse(ste.getMessage());
            oaaiRepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    private void validateInput(OaaiValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String merchantOrg = input.getMerchantOrg();
        String merchantNbr = input.getMerchantNbr();
        String amount = input.getAmount();
        String expiryDate = input.getExpiryDate();
        String signOn = input.getSignOnName();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(merchantOrg) ||
                StringUtils.isEmpty(merchantNbr) || StringUtils.isEmpty(amount) ||
                StringUtils.isEmpty(expiryDate) || StringUtils.isEmpty(signOn)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
        if (merchantOrg.length() != 3) throw new Exception("Field 'merchantOrg': " + merchantOrg + " does not comply");
        if (merchantNbr.length() > 9) throw new Exception("Field 'merchantNbr': " + merchantNbr + " exceeds maximum length (9)");
        if (amount.length() > 13) throw new Exception("Field 'amount': " + amount + " exceeds maximum length (13)");
        if (signOn.length() > 14) throw new Exception("Field 'signOnName': " + signOn + " exceeds maximum length (14)");
        if (!StringUtils.isNumeric(merchantOrg)) throw new Exception("Field 'merchantOrg': " + merchantOrg + " does not comply");
        if (!StringUtils.isNumeric(merchantNbr)) throw new Exception("Field 'merchantNbr': " + merchantNbr + " does not comply");
        if (!StringUtils.isNumeric(amount)) throw new Exception("Field 'amount': " + amount + " does not comply");

        try {
            Date date = helperService.getSimpleDateFormat("yyMM").parse(expiryDate);
            helperService.getSimpleDateFormat("MMyy").format(date);

            if (!helperService.isExpiryDateValid(expiryDate)) {
                throw new Exception("Expired");
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Expired")) {
                throw new Exception("Field 'expiryDate': " + expiryDate + " is expired");
            }
            throw new Exception("Field 'expiryDate': " + expiryDate + " does not comply");
        }
    }
}
