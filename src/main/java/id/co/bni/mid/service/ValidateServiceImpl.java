package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceValidate;
import id.co.bni.mid.repository.LogServiceValidateRepository;
import id.co.bni.mid.validator.ValidateValidator;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidateServiceImpl implements ValidateService {

    private static Logger loggers = LoggerFactory.getLogger(ValidateServiceImpl.class);

    @Autowired
    LogServiceValidateRepository validateRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String validate(ValidateValidator body, String reqAddr) throws Exception {
        LogServiceValidate logService = new LogServiceValidate();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String gmt = new SimpleDateFormat("ddMMHHmmss").format(now);
            String localdate = new SimpleDateFormat("MMdd").format(now);
            String localtime = new SimpleDateFormat("HHmmss").format(now);
            String traceNbr = String.format("%06d", helperService.random(0, 999999), helperService.random(0, 999999));

            logService.setRequest_address(reqAddr);
            logService.setTraceNbr(traceNbr);
            logService.setLocalDateTime(gmt);
            logService.setRequest(body.toString());

            validateInput(body);

            String cardNumber = body.getCardNumber();
            String expiryDate = "";
            try {
                Date expiry = helperService.getSimpleDateFormat("yyMM").parse(body.getExpiryDate());
                expiryDate = helperService.getSimpleDateFormat("yyMM").format(expiry);
            } catch (Exception e) {
                return "";
            }

            String dob = "";
            try {
                Date dateOfBirth = helperService.getSimpleDateFormat("yyyy-MM-dd").parse(body.getDob());
                dob = helperService.getSimpleDateFormat("ddMMyyyy").format(dateOfBirth);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String track2 = cardNumber + "D" + expiryDate + dob + "00000000";

            String f41 = helperService.encodeURL(env.getProperty("validate.f41"));
            String f42 = env.getProperty("validate.f42");
            String f43 = helperService.encodeURL(env.getProperty("validate.f43"));

            String url = env.getProperty("apicl.validate");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("expdate", expiryDate)
                    .queryParam("track2", track2)
                    .queryParam("cardpin", body.getPin())
                    .queryParam("gmt", gmt)
                    .queryParam("tracenbr", traceNbr)
                    .queryParam("localtime", localtime)
                    .queryParam("localdate", localdate)
                    .queryParam("postdate", localdate)
                    .queryParam("f41", f41)
                    .queryParam("f42", f42)
                    .queryParam("f43", f43).build(true);

            logService.setRequest(maskingPin(builder.toUriString()));

            ResponseEntity<String> apiResult = APIRepository.apiValidate(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                validateRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                validateRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            validateRepo.save(logService);

            JSONObject obj1 = object.getJSONObject("BNS003");
            JSONObject obj2 = obj1.getJSONObject("BNICA_RECORD");
            JSONObject objFields = obj2.getJSONObject("BNICA_ISO_FIELDS");
            String responseCode = objFields.get("BNICA_F039_RESP_CDE").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            String responseMessage = helperService.getResponseMessage(responseCode);
            String phoneNumber = objFields.get("BNICA_F061_REQUEST_HEADER").toString();

            if (responseCode.equals("00")) {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("phoneNumber", phoneNumber.trim());
                result.put("message", "Validate sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Validate gagal");
            }

            LogServiceValidate outputLog = new LogServiceValidate(logService.getRequest_address(), logService.getTraceNbr(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            validateRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            validateRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    private String maskingPin(String url) {
        Pattern p = Pattern.compile("cardpin=(.*?)&");
        Matcher m = p.matcher(url);
        if (m.find()) {
            String result = m.replaceAll("cardpin=******&");
            return result;
        }
        return url;
    }

    private void validateInput(ValidateValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String expiryDate = input.getExpiryDate();
        String dob = input.getDob();
        String pin = input.getPin();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(expiryDate) ||
                StringUtils.isEmpty(dob) || StringUtils.isEmpty(pin)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");

        try {
            Date expiry = helperService.getSimpleDateFormat("yyMM").parse(expiryDate);
            helperService.getSimpleDateFormat("MMyy").format(expiry);

            if (!helperService.isExpiryDateValid(expiryDate)) {
                throw new Exception("Expired");
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Expired")) {
                throw new Exception("Field 'expiryDate': " + expiryDate + " is expired");
            }
            throw new Exception("Field 'expiryDate': " + expiryDate + " does not comply");
        }

        try {
            Date dateOfBirth = helperService.getSimpleDateFormat("yyyy-MM-dd").parse(dob);
            helperService.getSimpleDateFormat("ddMMyy").format(dateOfBirth);
        } catch (Exception e) {
            throw new Exception("Field 'dob': " + dob + " does not comply");
        }
    }

}
