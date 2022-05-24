package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.helpers.StringHelpers;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceCardholder;
import id.co.bni.mid.repository.LogServiceCardHolderRepository;
import id.co.bni.mid.validator.CPCRDValidator;
import id.co.bni.mid.validator.CardNumberValidator;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
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
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CardHolderServiceImpl implements CardHolderService {

    private static Logger loggers = LoggerFactory.getLogger(CardHolderServiceImpl.class);

    @Autowired
    LogServiceCardHolderRepository cardHolderRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    private final String OUTPUT_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public String syncLimit(CPCRDValidator body, String reqAddr) throws Exception {
        LogServiceCardholder logService = new LogServiceCardholder();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());
            logService.setService("Sync Limit");

            validateInput(body);

            String cardNumber = body.getCardNumber();
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            logService.setCardNbr(cardNumber);
            logService.setLocalDateTime(gmt);

            String url = env.getProperty("apicl.syncLimit");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("org", body.getOrg())
                    .queryParam("type", body.getType());

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiSyncLimit(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                cardHolderRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                cardHolderRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            cardHolderRepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                String customerNbr = dataObj.get("cm_customer_nmbr").toString();
                String type = dataObj.get("cm_type").toString();
                String nmbrOutsAuth = dataObj.get("cm_nmbr_outst_auth").toString();

                Long cashLimit = Long.parseLong(dataObj.get("cm_cash_limit").toString()) * 100;
                Long crLimit = Long.parseLong(dataObj.get("cm_crlimit").toString()) * 100;

                resObj.put("customerNmbr", StringHelpers.leftPadZeroes(customerNbr, 16));
                resObj.put("availCredit", dataObj.get("cm_avail_credit").toString());
                resObj.put("rtlBalance", dataObj.get("cm_rtl_balance").toString());
                resObj.put("currBalance", dataObj.get("cm_curr_balance").toString());
                resObj.put("cashBalance", dataObj.get("cm_cash_balance").toString());
                resObj.put("olCashPymt", dataObj.get("cm_ol_cash_pymt").toString());
                resObj.put("orgNmbr", dataObj.get("cm_org_nmbr").toString());
                resObj.put("cashAdvOsAuth", dataObj.get("cm_cash_adv_os_auth").toString());
                resObj.put("olRtlPymt", dataObj.get("cm_ol_rtl_pymt").toString());
                resObj.put("customerOrg", dataObj.get("cm_customer_org").toString());
                resObj.put("cashLimit", cashLimit.toString());
                resObj.put("type", StringHelpers.leftPadZeroes(type, 3));
                resObj.put("nmbrOutstAuth", StringHelpers.leftPadZeroes(nmbrOutsAuth, 3));
                resObj.put("crLimit", crLimit.toString());
                resObj.put("availCash", dataObj.get("cm_avail_cash").toString());
                resObj.put("amntOutstAuth", dataObj.get("cm_amnt_outst_auth").toString());
                resObj.put("instlBal", dataObj.get("cm_instl_bal").toString());
                resObj.put("cardNmbr", dataObj.get("cm_card_nmbr").toString());
                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Inquiry Limit sukses");

            LogServiceCardholder outputLog = new LogServiceCardholder(logService.getRequest_address(), logService.getCardNbr(), logService.getService(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            cardHolderRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            cardHolderRepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    @Override
    public String checkDueDate(CPCRDValidator body, String reqAddr) throws Exception {
        LogServiceCardholder logService = new LogServiceCardholder();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());
            logService.setService("Check Due Date");

            validateInput(body);

            String cardNumber = body.getCardNumber();
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            logService.setCardNbr(cardNumber);
            logService.setLocalDateTime(gmt);

            String url = env.getProperty("apicl.checkduedate");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("org", body.getOrg())
                    .queryParam("type", body.getType());

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiCheckDueDate(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                cardHolderRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                cardHolderRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            cardHolderRepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                String customerNbr = dataObj.get("cm_customer_nmbr").toString();
                String type = dataObj.get("cm_type").toString();

                resObj.put("18daysDelq", dataObj.get("cm_180days_delq").toString());
                resObj.put("30daysDelq", dataObj.get("cm_30days_delq").toString());
                resObj.put("60daysDelq", dataObj.get("cm_60days_delq").toString());
                resObj.put("90daysDelq", dataObj.get("cm_90days_delq").toString());
                resObj.put("120daysDelq", dataObj.get("cm_120days_delq").toString());
                resObj.put("150daysDelq", dataObj.get("cm_150days_delq").toString());
                resObj.put("210daysDelq", dataObj.get("cm_210days_delq").toString());
                resObj.put("dteLstStmt", helperService.julianToDateString(dataObj.get("cm_dte_lst_stmt").toString(), OUTPUT_DATE_FORMAT));
                resObj.put("dteLstDelq", helperService.julianToDateString(dataObj.get("cm_dte_lst_delq").toString(), OUTPUT_DATE_FORMAT));
                resObj.put("orgNmbr", dataObj.get("cm_org_nmbr").toString());
                resObj.put("customerOrg", dataObj.get("cm_customer_org").toString());
                resObj.put("customerNmbr", StringHelpers.leftPadZeroes(customerNbr, 16));
                resObj.put("cashBegBalance", dataObj.get("cm_cash_beg_balance").toString());
                resObj.put("dtePriorDelq", helperService.julianToDateString(dataObj.get("cm_dte_prior_delq").toString(), OUTPUT_DATE_FORMAT));
                resObj.put("pastDue", dataObj.get("cm_past_due").toString());
                resObj.put("type", StringHelpers.leftPadZeroes(type, 3));
                resObj.put("dtePymtDue", helperService.julianToDateString(dataObj.get("cm_dte_pymt_due").toString(), OUTPUT_DATE_FORMAT));
                resObj.put("currDue", dataObj.get("cm_curr_due").toString());
                resObj.put("rtlBegBalance", dataObj.get("cm_rtl_beg_balance").toString());
                resObj.put("cardNmbr", dataObj.get("cm_card_nmbr").toString());
                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Check Due Date sukses");

            LogServiceCardholder outputLog = new LogServiceCardholder(logService.getRequest_address(), logService.getCardNbr(), logService.getService(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            cardHolderRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            cardHolderRepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    @Override
    public String checkLastPayment(CPCRDValidator body, String reqAddr) throws Exception {
        LogServiceCardholder logService = new LogServiceCardholder();
        JSONObject result = new JSONObject();

        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());
            logService.setService("Last Payment");

            validateInput(body);

            String cardNumber = body.getCardNumber();
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            logService.setCardNbr(cardNumber);
            logService.setLocalDateTime(gmt);

            String url = env.getProperty("apicl.checklastpayment");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("org", body.getOrg())
                    .queryParam("type", body.getType());

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiCheckLastPayment(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                cardHolderRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                cardHolderRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            cardHolderRepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                String customerNbr = dataObj.get("cm_customer_nmbr").toString();
                String type = dataObj.get("cm_type").toString();

                resObj.put("customerNmbr", StringHelpers.leftPadZeroes(customerNbr, 16));
                resObj.put("lstPymtAmnt", dataObj.get("cm_lst_pymt_amnt").toString());
                resObj.put("type", StringHelpers.leftPadZeroes(type, 3));
                resObj.put("olCashPymt", dataObj.get("cm_ol_cash_pymt").toString());
                resObj.put("dteLstPymt", helperService.julianToDateString(dataObj.get("cm_dte_lst_pymt").toString(), OUTPUT_DATE_FORMAT));
                resObj.put("orgNmbr", dataObj.get("cm_org_nmbr").toString());
                resObj.put("olRtlPymt", dataObj.get("cm_ol_rtl_pymt").toString());
                resObj.put("cardNmbr", dataObj.get("cm_card_nmbr").toString());
                resObj.put("customerOrg", dataObj.get("cm_customer_org").toString());
                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Cek Last Payment sukses");

            LogServiceCardholder outputLog = new LogServiceCardholder(logService.getRequest_address(), logService.getCardNbr(), logService.getService(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            cardHolderRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            cardHolderRepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    private void validateInput(CPCRDValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String org = input.getOrg();
        String type = input.getType();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(org) ||
            StringUtils.isEmpty(type)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16)
            throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
        if (!StringUtils.isNumeric(org) || org.length() != 3)
            throw new Exception("Field 'org': " + org + " does not comply");
        if (!StringUtils.isNumeric(type) || type.length() != 3)
            throw new Exception("Field 'type': " + type + " does not comply");
    }

}
