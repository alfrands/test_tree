package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceCardActivate;
import id.co.bni.mid.model.LogServiceCardBlock;
import id.co.bni.mid.model.LogServiceCardFlagging;
import id.co.bni.mid.model.LogServiceCardUnblock;
import id.co.bni.mid.repository.LogServiceCardActivateRepository;
import id.co.bni.mid.repository.LogServiceCardBlockRepository;
import id.co.bni.mid.repository.LogServiceCardFlaggingRepository;
import id.co.bni.mid.repository.LogServiceCardUnblockRepository;
import id.co.bni.mid.validator.BlockCardValidator;
import id.co.bni.mid.validator.CardValidator;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CardMaintenanceServiceImpl implements CardMaintenanceService {

    private static Logger loggers = LoggerFactory.getLogger(CardMaintenanceServiceImpl.class);

    @Autowired
    LogServiceCardBlockRepository cardBlockRepo;

    @Autowired
    LogServiceCardUnblockRepository cardUnblockRepo;

    @Autowired
    LogServiceCardActivateRepository cardActivateRepo;

    @Autowired
    LogServiceCardFlaggingRepository cardFlaggingRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String cardBlock(BlockCardValidator body, String reqAddr) throws Exception {
        LogServiceCardBlock logService = new LogServiceCardBlock();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String localdate = new SimpleDateFormat("MMdd").format(now);
            String localtime = new SimpleDateFormat("HHmmss").format(now);
            String traceNbr = String.format("%06d", helperService.random(0, 999999), helperService.random(0, 999999));
            String gmt = String.format("%s%s", localdate, localtime);

            logService.setRequest_address(reqAddr);
            logService.setTraceNbr(traceNbr);
            logService.setLocalDateTime(gmt);
            logService.setRequest(body.toString());

            validateInputBlock(body);

            String cardNumber = body.getCardNumber();

            String f41 = helperService.encodeURL(env.getProperty("cardblock.f41"));
            String f43 = helperService.encodeURL(env.getProperty("cardblock.f43"));

            String blok = env.getProperty("cardblock.blokCode");

            String f35 = generatef35(cardNumber, body.getExpiryDate(), body.getDob(), blok, body.getReasonCode());

            String url = env.getProperty("apicl.cardblock");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("localdte", localdate)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("f41", f41)
                    .queryParam("f43", f43)
                    .queryParam("tracenum", traceNbr)
                    .queryParam("f35", f35)
                    .queryParam("postdte", localdate)
                    .queryParam("localtme", localtime)
                    .queryParam("gmt", gmt).build(true);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiCardBlock(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                cardBlockRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                cardBlockRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            cardBlockRepo.save(logService);

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
                result.put("message", "Blok kartu sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Blok kartu gagal");
            }

            LogServiceCardBlock outputLog = new LogServiceCardBlock(logService.getRequest_address(), logService.getTraceNbr(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            cardBlockRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setRequest(body.toString());
            logService.setResponse(ste.getMessage());
            cardBlockRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }


    @Override
    public String cardUnblock(CardValidator body, String reqAddr) throws Exception {
        LogServiceCardUnblock logService = new LogServiceCardUnblock();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String localdate = new SimpleDateFormat("MMdd").format(now);
            String localtime = new SimpleDateFormat("HHmmss").format(now);
            String traceNbr = String.format("%06d", helperService.random(0, 999999), helperService.random(0, 999999));
            String gmt = String.format("%s%s", localdate, localtime);

            logService.setRequest_address(reqAddr);
            logService.setTraceNbr(traceNbr);
            logService.setLocalDateTime(gmt);
            logService.setRequest(body.toString());

            validateInput(body);

            String cardNumber = body.getCardNumber();

            String f41 = helperService.encodeURL(env.getProperty("cardunblock.f41"));
            String f43 = helperService.encodeURL(env.getProperty("cardunblock.f43"));

            String blok = " ";
            String reason = "  ";

            String f35 = generatef35(cardNumber, body.getExpiryDate(), body.getDob(), helperService.encodeURL(blok),helperService.encodeURL(reason));

            String url = env.getProperty("apicl.unblockCard");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("localdte", localdate)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("f41", f41)
                    .queryParam("f43", f43)
                    .queryParam("tracenum", traceNbr)
                    .queryParam("f35", f35)
                    .queryParam("postdte", localdate)
                    .queryParam("localtme", localtime)
                    .queryParam("gmt", gmt).build(true);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiUnblockCard(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                cardUnblockRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                cardUnblockRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            cardUnblockRepo.save(logService);

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
                result.put("message", "Unblock kartu sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Unblock kartu gagal");
            }

            LogServiceCardUnblock outputLog = new LogServiceCardUnblock(logService.getRequest_address(), logService.getTraceNbr(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            cardUnblockRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setRequest(body.toString());
            logService.setResponse(ste.getMessage());
            cardUnblockRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }


    @Override
    public String cardActivate(CardValidator body, String reqAddr) throws Exception {
        LogServiceCardActivate logService = new LogServiceCardActivate();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String localdate = new SimpleDateFormat("MMdd").format(now);
            String localtime = new SimpleDateFormat("HHmmss").format(now);
            String traceNbr = String.format("%06d", helperService.random(0, 999999), helperService.random(0, 999999));
            String gmt = String.format("%s%s", localdate, localtime);

            logService.setRequest_address(reqAddr);
            logService.setTraceNbr(traceNbr);
            logService.setLocalDateTime(gmt);
            logService.setRequest(body.toString());

            validateInput(body);

            String cardNumber = body.getCardNumber();

            String f41 = helperService.encodeURL(env.getProperty("cardactivate.f41"));
            String f43 = helperService.encodeURL(env.getProperty("cardactivate.f43"));

            String blok = env.getProperty("cardactivate.blokCode");
            String reason = env.getProperty("cardactivate.reasonCode");

            String f35 = helperService.encodeURL(generatef35(cardNumber, body.getExpiryDate(), body.getDob(), blok, reason));

            String url = env.getProperty("apicl.cardactivate");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("localdte", localdate)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("f41", f41)
                    .queryParam("f43", f43)
                    .queryParam("tracenum", traceNbr)
                    .queryParam("f35", f35)
                    .queryParam("postdte", localdate)
                    .queryParam("localtme", localtime)
                    .queryParam("gmt", gmt).build(true);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiCardActivate(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                cardActivateRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                cardActivateRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            cardActivateRepo.save(logService);

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
                result.put("message", "Aktivasi kartu sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Aktivasi kartu gagal");
            }

            LogServiceCardActivate outputLog = new LogServiceCardActivate(logService.getRequest_address(), logService.getTraceNbr(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            cardActivateRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            logService.setResponse(ste.getMessage());
            cardActivateRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    @Override
    public String cardFlagging(CardValidator body, String reqAddr) throws Exception {
        LogServiceCardFlagging logService = new LogServiceCardFlagging();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String localdate = new SimpleDateFormat("MMdd").format(now);
            String localtime = new SimpleDateFormat("HHmmss").format(now);
            String traceNbr = String.format("%06d", helperService.random(0, 999999), helperService.random(0, 999999));
            String gmt = String.format("%s%s", localdate, localtime);

            logService.setRequest_address(reqAddr);
            logService.setTraceNbr(traceNbr);
            logService.setLocalDateTime(gmt);
            logService.setRequest(body.toString());

            validateInput(body);

            String cardNumber = body.getCardNumber();

            String f41 = helperService.encodeURL(env.getProperty("cardflagging.f41"));
            String f43 = helperService.encodeURL(env.getProperty("cardflagging.f43"));

            String blok = "0";
            String reason = "00";

            String f35 = generatef35(cardNumber, body.getExpiryDate(), body.getDob(), blok, reason);

            String url = env.getProperty("apicl.cardflagging");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("localdte", localdate)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("f41", f41)
                    .queryParam("f43", f43)
                    .queryParam("tracenum", traceNbr)
                    .queryParam("f35", f35)
                    .queryParam("postdte", localdate)
                    .queryParam("localtme", localtime)
                    .queryParam("gmt", gmt).build(true);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiCardFlagging(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                cardFlaggingRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                cardFlaggingRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            cardFlaggingRepo.save(logService);

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
                result.put("message", "Flagging kartu sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Flagging kartu gagal");
            }

            LogServiceCardFlagging outputLog = new LogServiceCardFlagging(logService.getRequest_address(), logService.getTraceNbr(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            cardFlaggingRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            logService.setResponse(ste.getMessage());
            cardFlaggingRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    private void validateInput(CardValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String expiryDate = input.getExpiryDate();
        String dob = input.getDob();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(expiryDate) ||
                StringUtils.isEmpty(dob)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");

        try {
            Date expiry = helperService.getSimpleDateFormat("yyMM").parse(expiryDate);
            helperService.getSimpleDateFormat("yyMM").format(expiry);

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

    private void validateInputBlock(BlockCardValidator input) throws Exception {
        validateInput(input);

        String reasonCode = input.getReasonCode();
        if (StringUtils.isEmpty(reasonCode)) throw new Exception("Empty fields");

        if (!StringUtils.isNumeric(reasonCode) ||reasonCode.length() != 2) throw new Exception("Field 'reasonCode': " + reasonCode + " does not comply");
    }

    private String generatef35(String cardNumber, String expiry, String dob, String blok, String reasonCode) {
        try {
            Date dateOfBirth = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dob);
            dob = new SimpleDateFormat("ddMMyyyy").format(dateOfBirth);
        } catch (Exception e) {
            return "";
        }
        try {
            Date expiryDate = new SimpleDateFormat("yyMM", Locale.getDefault()).parse(expiry);
            expiry = new SimpleDateFormat("yyMM").format(expiryDate);
        } catch (Exception e) {
            return "";
        }
        String f35 = String.format("%-16s%-1s%-4s%-8s%-1s%-2s%-5s", cardNumber, "D", expiry, dob, blok, reasonCode, "00000");
        return f35;
    }

}
