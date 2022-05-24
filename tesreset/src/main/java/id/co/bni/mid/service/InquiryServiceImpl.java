package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceInquiry;
import id.co.bni.mid.model.StatusKartuDictionary;
import id.co.bni.mid.repository.LogServiceInquiryRepository;
import id.co.bni.mid.validator.CardValidator;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static id.co.bni.mid.helpers.StringHelpers.getLastDayinMonth;

@Service
public class InquiryServiceImpl implements InquiryService {

    private static Logger loggers = LoggerFactory.getLogger(InquiryServiceImpl.class);

    @Autowired
    LogServiceInquiryRepository inquiryRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String checkBlock(CardValidator body, String reqAddr) throws Exception {
        LogServiceInquiry logService = new LogServiceInquiry();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setService("Check Block");
            logService.setRequest(body.toString2());

            validateInput(body);

            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);
            logService.setCardNbr(body.getCardNumber());
            logService.setLocalDateTime(gmt);

            String inquiryObject = inquirySummary(body, reqAddr);
            if (inquiryObject == null || StringUtils.isEmpty(inquiryObject)) {
                logService.setResponse(new ConnectException().getMessage());
                inquiryRepo.save(logService);
                throw new ConnectException();
            }

            JSONObject object = new JSONObject(inquiryObject);
            String responseCode = object.get("responseCode").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            if (responseCode.equals("00")) {
                JSONObject data = object.getJSONObject("data");
                StatusKartuDictionary statusKartu = helperService.mappingStatusKartu(data.get("blockCode").toString());
                if (statusKartu == null) {
                    logService.setResponse("Status kartu null");
                    inquiryRepo.save(logService);
                    throw new Exception("Status kartu tidak dapat ditemukan");
                }

                result.put("blockCode", statusKartu.getBlockCode());
                result.put("statusKartu", statusKartu.getTitle());
                result.put("message", "Inquiry status kartu sukses");
            } else {
                result.put("message", "Inquiry status kartu gagal");
            }
            result.put("responseCode", object.get("responseCode"));
            result.put("responseMessage", object.get("responseMessage"));
            result.put("cardNumber", body.getCardNumber());

            logService.setResponse(result.toString());
            inquiryRepo.save(logService);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            inquiryRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }



    @Override
    public String inquiryLimit(CardValidator body, String reqAddr) throws Exception {
        LogServiceInquiry logService = new LogServiceInquiry();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setService("Inquiry Limit");
            logService.setRequest(body.toString2());

            validateInput(body);

            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);
            logService.setCardNbr(body.getCardNumber());
            logService.setLocalDateTime(gmt);

            String inquiryObject = inquirySummary(body, reqAddr);
            if (inquiryObject == null || StringUtils.isEmpty(inquiryObject)) {
                logService.setResponse(new ConnectException().getMessage());
                inquiryRepo.save(logService);
                throw new ConnectException();
            }

            JSONObject object = new JSONObject(inquiryObject);
            String responseCode = object.get("responseCode").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            if (responseCode.equals("00")) {

                JSONObject data = object.getJSONObject("data");
                result.put("creditLimit", data.get("creditLimit"));
                result.put("memoBalance", data.get("memoBalance"));
                result.put("message", "Inquiry Limit Sukses");
            } else {
                result.put("message", "Inquiry Limit Gagal");
            }
            result.put("responseCode", object.get("responseCode"));
            result.put("responseMessage", object.get("responseMessage"));
            result.put("cardNumber", body.getCardNumber());


            logService.setResponse(result.toString());
            inquiryRepo.save(logService);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            inquiryRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }




    @Override
    public String checkExpired(CardValidator body, String reqAddr) throws Exception {
        LogServiceInquiry logService = new LogServiceInquiry();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setService("Check Expired");
            logService.setRequest(body.toString2());

            validateInput(body);

            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);
            logService.setCardNbr(body.getCardNumber());
            logService.setLocalDateTime(gmt);

            String inquiryObject = inquirySummary(body, reqAddr);
            if (inquiryObject == null || StringUtils.isEmpty(inquiryObject)) {
                logService.setResponse(new ConnectException().getMessage());
                inquiryRepo.save(logService);
                throw new ConnectException();
            }

            JSONObject object = new JSONObject(inquiryObject);
            String responseCode = object.get("responseCode").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            if (responseCode.equals("00")) {
                result.put("message", "Inquiry status kartu sukses. Kartu Belum Expired");
            } else if (responseCode.equals("33") || responseCode.equals("54")) {
                result.put("message", "Inquiry status kartu sukses. Kartu Expired");
            } else {
                result.put("message", "Inquiry status kartu gagal");
            }
            result.put("responseCode", object.get("responseCode"));
            result.put("responseMessage", object.get("responseMessage"));
            result.put("cardNumber", body.getCardNumber());

            logService.setResponse(result.toString());
            inquiryRepo.save(logService);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            inquiryRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    private String inquirySummary(CardValidator body, String reqAddr) throws Exception {
        LogServiceInquiry logService = new LogServiceInquiry();
        JSONObject result = new JSONObject();
        try {
            String cardNumber = body.getCardNumber();
            String expiryDate = "";
            try {
                Date expiry = helperService.getSimpleDateFormat("yyMM").parse(body.getExpiryDate());
                expiryDate = helperService.getSimpleDateFormat("yyMM").format(expiry);
            } catch (Exception e) {
                return "";
            }
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            String url = env.getProperty("apicl.inquirySummary");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("expdate", expiryDate);

            logService.setRequest_address(reqAddr);
            logService.setCardNbr(cardNumber);
            logService.setService("Inquiry");
            logService.setLocalDateTime(gmt);
            logService.setRequest(builder.build().toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiInquiry(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                inquiryRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                inquiryRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            inquiryRepo.save(logService);

            JSONObject obj1 = object.getJSONObject("SOAC002");
            JSONObject obj2 = obj1.getJSONObject("BNSOA_RECORD");
            JSONObject objFields = obj2.getJSONObject("BNSOA_ISO_FIELDS");
            String responseCode = objFields.get("BNSOA_F039_RESP_CDE").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            String responseMessage = helperService.getResponseMessage(responseCode);

            if (responseCode.equals("00")) {
                JSONObject data = parsingISOSummary(objFields);
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("data", data);

            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
            }

        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            inquiryRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    private JSONObject parsingISOSummary(JSONObject objct) {
        JSONObject output = new JSONObject();
        String outputDateFormat = "yyyy-MM-dd";

        /* Bit 120 */
        String bit120 = objct.get("BNSOA_F120_PRIV_FLD1").toString();
        String originNum = bit120.substring(0, 3);
        String typeNum = bit120.substring(3, 6);
        String cardNumber = bit120.substring(6, 22);
        String postingFlag = bit120.substring(30, 32);
        String cardExpiryDate = bit120.substring(32, 36);
        String openedDate = bit120.substring(36, 42);
        String status = bit120.substring(115, 116);
        String blockCode = bit120.substring(116, 117);
        String onlinePaymentAmount = bit120.substring(176, 186);
        String creditLimit = bit120.substring(186, 195);
        String statementBalance = bit120.substring(195, 205);
        String lastStatementDate = bit120.substring(205, 211);
        String nextStatementDate = bit120.substring(211, 217);
        String availableCredit = bit120.substring(217, 227);
        
        /* Bit 121 */
        String bit121 = objct.get("BNSOA_F121_PRIV_FLD2").toString();
        String currentDue = bit121.substring(0, 9);
        String paymentDueDate = bit121.substring(9, 15);
        String delqHistory = bit121.substring(15, 39);
        String cashLimit = bit121.substring(39, 48);
        String availableCash = bit121.substring(63, 72);
        String totalAmountDue = bit121.substring(72, 81);
        String cashBalance = bit121.substring(87, 96);
        String lastPaymentAmount = bit121.substring(96, 105);
        String lastPaymentDate = bit121.substring(105, 111);
        String currentBalance = bit121.substring(135, 144);
        String memoBalance = bit121.substring(159, 168);
        String customerEmail = bit121.substring(204, 254);
        
        /* Bit 122 */
        String bit122 = objct.get("BNSOA_F122_PRIV_FLD3").toString();
        String customerDob = bit122.substring(209, 215);

        /* Bit 123 */
        String bit123 = objct.get("BNSOA_F123_PRIV_FLD4").toString();
        String coHomePhone =  bit123.substring(0, 18);
        String availablePoint =  bit123.substring(230, 239);

        /* Bit 124 */
        String bit124 = objct.get("BNSOA_F124_PRIV_FLD5").toString();
        String customerName =  bit124.substring(0, 30);
        String embosserName =  bit124.substring(121, 147);

        output.put("originNumber", originNum);
        output.put("typeNumber", typeNum);
        output.put("cardNumber", cardNumber);
        output.put("postingFlag", postingFlag);

        output.put("expiry", helperService.parseDate(cardExpiryDate, "MMyy", "yyMM"));
        output.put("openedDate", helperService.parseDate(openedDate, "ddMMyy", outputDateFormat));
        output.put("status", status.replaceAll("\\s+", "").trim());
        output.put("blockCode", blockCode);
        output.put("onlinePaymentAmt", onlinePaymentAmount.replaceAll("\\s+", "").trim());
        output.put("creditLimit", creditLimit.replaceAll("\\s+", "").trim());
        output.put("statementBalance", statementBalance.replaceAll("\\s+", "").trim());
        output.put("lastStatementDate", helperService.parseDate(lastStatementDate, "ddMMyy", outputDateFormat));
        output.put("nextStatementDate", helperService.parseDate(nextStatementDate, "ddMMyy", outputDateFormat));
        output.put("availableCredit", availableCredit.replaceAll("\\s+", "").trim());
        output.put("currentDue", currentDue.replaceAll("\\s+", "").trim());
        output.put("paymentDueDate", helperService.parseDate(paymentDueDate, "ddMMyy", outputDateFormat));
        output.put("delqHistory", delqHistory);
        output.put("cashLimit", cashLimit.replaceAll("\\s+", "").trim());
        output.put("availableCash", availableCash.replaceAll("\\s+", "").trim());
        output.put("totalAmountDue", totalAmountDue.replaceAll("\\s+", "").trim());
        output.put("cashBalance", cashBalance.replaceAll("\\s+", "").trim());
        output.put("lastPaymentAmount", lastPaymentAmount.replaceAll("\\s+", "").trim());
        output.put("lastPaymentDate", helperService.parseDate(lastPaymentDate, "ddMMyy", outputDateFormat));
        output.put("currentBalance", currentBalance.replaceAll("\\s+", "").trim());
        output.put("memoBalance", memoBalance.replaceAll("\\s+", "").trim());
        output.put("customerEmail", customerEmail.replaceAll("\\s+", ""));
        output.put("customerDob", helperService.parseDate(customerDob, "ddMMyy", outputDateFormat));
        output.put("coHomePhone", coHomePhone.replaceAll("\\s+", "").trim());
        output.put("availablePoint", availablePoint.replaceAll("\\s+", "").trim());
        output.put("customerName", customerName.trim());
        output.put("embossName", embosserName.trim());
        return output;
    }

    private void validateInput(CardValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String expiryDate = input.getExpiryDate();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(expiryDate)) throw new Exception("Empty fields");
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
    }

}
