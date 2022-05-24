package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceTransCC;
import id.co.bni.mid.repository.LogServiceTransCCRepository;
import id.co.bni.mid.validator.TransactionSPCValidator;
import id.co.bni.mid.validator.TransactionValidator;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
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
public class TransCCServiceImpl implements TransCCService {

    private static Logger loggers = LoggerFactory.getLogger(TransCCServiceImpl.class);

    @Autowired
    LogServiceTransCCRepository transCCRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String transactionCC(TransactionValidator body, String reqAddr) throws Exception {
        LogServiceTransCC logService = new LogServiceTransCC();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String gmt = new SimpleDateFormat("ddMMHHmmss").format(now);
            String localdate = new SimpleDateFormat("MMdd").format(now);
            String localtime = new SimpleDateFormat("HHmmss").format(now);
            String traceNbr = String.format("%06d", helperService.random(0, 999999), helperService.random(0, 999999));

            logService.setRequest_address(reqAddr);
            logService.setChannelID(body.getChannelID());
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

            String f41 = helperService.encodeURL(body.getTermId());
            String f42 = body.getMid();
            String f43 = helperService.encodeURL(body.getMerchantName());
            String pem = env.getProperty("transcc.pem");

            String url = env.getProperty("apicl.transcc");
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
                    .queryParam("amount", body.getAmount())
                    .queryParam("pem", pem)
                    .queryParam("f41", f41)
                    .queryParam("f42", f42)
                    .queryParam("f43", f43).build(true);

            logService.setRequest(maskingPin(builder.toUriString()));

            ResponseEntity<String> apiResult = APIRepository.apiTransactionCC(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                transCCRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                transCCRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            transCCRepo.save(logService);

            JSONObject obj1 = object.getJSONObject("BNS003");
            JSONObject obj2 = obj1.getJSONObject("BNICA_RECORD");
            JSONObject objFields = obj2.getJSONObject("BNICA_ISO_FIELDS");
            String responseCode = objFields.get("BNICA_F039_RESP_CDE").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            String responseMessage = helperService.getResponseMessage(responseCode);
            String phoneNumber = objFields.get("BNICA_F061_REQUEST_HEADER").toString();
            String authCode = objFields.get("BNICA_F038_AUTH_CDE").toString();

            if (responseCode.equals("00")) {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("approvalCode", authCode);
                result.put("phoneNumber", phoneNumber.trim());
                result.put("message", "Transaksi sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Transaksi gagal");
            }

            LogServiceTransCC outputLog = new LogServiceTransCC(logService.getRequest_address(), logService.getChannelID(), logService.getTraceNbr(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            transCCRepo.save(outputLog);
        } catch (ResourceAccessException timeoutException) {
            loggers.error(timeoutException.getMessage());
            timeoutException.printStackTrace();
            logService.setResponse(timeoutException.getMessage());
            transCCRepo.save(logService);
            throw new Exception("Request Timeout");
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            transCCRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    @Override
    public String transactionCCSPC(TransactionSPCValidator body, String reqAddr) throws Exception {
        LogServiceTransCC logService = new LogServiceTransCC();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String gmt = new SimpleDateFormat("ddMMHHmmss").format(now);
            String localdate = new SimpleDateFormat("MMdd").format(now);
            String localtime = new SimpleDateFormat("HHmmss").format(now);
            String traceNbr = String.format("%06d", helperService.random(0, 999999), helperService.random(0, 999999));

            logService.setRequest_address(reqAddr);
            logService.setChannelID(body.getChannelID());
            logService.setTraceNbr(traceNbr);
            logService.setLocalDateTime(gmt);
            logService.setRequest(body.toString());

            validateSPCInput(body);

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

            String f41 = helperService.encodeURL(body.getTermId());
            String f42 = body.getMid();
            String f43 = helperService.encodeURL(body.getMerchantName());
            String f60 = helperService.encodeURL(String.format("MYCRBICI+000    APISPC%s%s%s", body.getUtilCode(), body.getRegionCode(), body.getFee()));
            String pem = env.getProperty("transcc.pem");

            String url = env.getProperty("apicl.transccspc");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("expdate", expiryDate)
                    .queryParam("track2", track2)
                    .queryParam("gmt", gmt)
                    .queryParam("tracenbr", traceNbr)
                    .queryParam("localtime", localtime)
                    .queryParam("localdate", localdate)
                    .queryParam("postdate", localdate)
                    .queryParam("amount", body.getAmount())
                    .queryParam("pem", pem)
                    .queryParam("f41", f41)
                    .queryParam("f42", f42)
                    .queryParam("f43", f43)
                    .queryParam("f60", f60).build(true);

            logService.setRequest(maskingPin(builder.toUriString()));

            ResponseEntity<String> apiResult = APIRepository.apiTransactionCC(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                transCCRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                transCCRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            transCCRepo.save(logService);

            JSONObject obj1 = object.getJSONObject("BNS003");
            JSONObject obj2 = obj1.getJSONObject("BNICA_RECORD");
            JSONObject objFields = obj2.getJSONObject("BNICA_ISO_FIELDS");
            String responseCode = objFields.get("BNICA_F039_RESP_CDE").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            String responseMessage = helperService.getResponseMessage(responseCode);
            String phoneNumber = objFields.get("BNICA_F061_REQUEST_HEADER").toString();
            String authCode = objFields.get("BNICA_F038_AUTH_CDE").toString();
            String systrace = objFields.get("BNICA_F011_TRACE_NBR").toString();

            result.put("responseCode", responseCode);
            result.put("responseMessage", responseMessage);
            result.put("cardNumber", cardNumber);
            result.put("systrace", systrace);

            if (responseCode.equals("00")) {
                result.put("approvalCode", authCode);
                result.put("phoneNumber", phoneNumber.trim());
                result.put("message", "Transaksi sukses");
            } else {
                result.put("message", "Transaksi gagal");
            }

            LogServiceTransCC outputLog = new LogServiceTransCC(logService.getRequest_address(), logService.getChannelID(), logService.getTraceNbr(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            transCCRepo.save(outputLog);
        } catch (ResourceAccessException timeoutException) {
            loggers.error(timeoutException.getMessage());
            timeoutException.printStackTrace();
            logService.setResponse(timeoutException.getMessage());
            transCCRepo.save(logService);
            throw new Exception("Request Timeout");
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            transCCRepo.save(logService);
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

    private void validateInput(TransactionValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String expiryDate = input.getExpiryDate();
        String dob = input.getDob();
        String pin = input.getPin();
        String amount = input.getAmount();
        String termId = input.getTermId();
        String mid = input.getMid();
        String merchant = input.getMerchantName();
        String channelID = input.getChannelID();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(expiryDate) ||
                StringUtils.isEmpty(dob) || StringUtils.isEmpty(pin) ||
                StringUtils.isEmpty(amount) || StringUtils.isEmpty(termId) ||
                StringUtils.isEmpty(mid) || StringUtils.isEmpty(merchant) ||
                StringUtils.isEmpty(channelID)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
        if (!StringUtils.isNumeric(amount)) throw new Exception("Field 'amount': " + amount + " does not comply");
        if (mid.length() > 15) throw new Exception("Field 'mid': " + mid + " exceeds maximum length (15)");
        if (!StringUtils.isNumeric(mid)) throw new Exception("Field 'mid': " + mid + " does not comply");
        if (termId.length() > 16) throw new Exception("Field 'termId': " + termId + " exceeds maximum length (16)");
        if (merchant.length() > 40) throw new Exception("Field 'merchantName': " + merchant + " exceeds maximum length (40)");

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

    private void validateSPCInput(TransactionSPCValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String expiryDate = input.getExpiryDate();
        String dob = input.getDob();
        String amount = input.getAmount();
        String fee = input.getFee();
        String termId = input.getTermId();
        String mid = input.getMid();
        String utilCode = input.getUtilCode();
        String regionCode = input.getRegionCode();
        String merchant = input.getMerchantName();
        String channelID = input.getChannelID();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(expiryDate) ||
                StringUtils.isEmpty(dob) || StringUtils.isEmpty(fee) ||
                StringUtils.isEmpty(amount) || StringUtils.isEmpty(termId) ||
                StringUtils.isEmpty(mid) || StringUtils.isEmpty(merchant) ||
                StringUtils.isEmpty(channelID) || StringUtils.isEmpty(utilCode) ||
                StringUtils.isEmpty(regionCode)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
        if (!StringUtils.isNumeric(amount)) throw new Exception("Field 'amount': " + amount + " does not comply");
        if (!StringUtils.isNumeric(fee)) throw new Exception("Field 'fee': " + fee + " does not comply");
        if (amount.length() > 12) throw new Exception("Field 'amount': " + amount + " exceeds maximum length (12)");
        if (fee.length() > 12) throw new Exception("Field 'fee': " + fee + " exceeds maximum length (12)");
        if (mid.length() > 15) throw new Exception("Field 'mid': " + mid + " exceeds maximum length (15)");
        if (!StringUtils.isNumeric(mid)) throw new Exception("Field 'mid': " + mid + " does not comply");
        if (termId.length() > 16) throw new Exception("Field 'termId': " + termId + " exceeds maximum length (16)");
        if (merchant.length() > 40) throw new Exception("Field 'merchantName': " + merchant + " exceeds maximum length (40)");
        if (utilCode.length() != 4) throw new Exception("Field 'utilCode': " + utilCode + " does not comply");
        if (regionCode.length() != 4) throw new Exception("Field 'regionCode': " + regionCode + " does not comply");

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
