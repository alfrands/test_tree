package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceEcoll;
import id.co.bni.mid.repository.LogServiceEcollRepository;
import id.co.bni.mid.validator.EcollValidator;
import id.co.bni.mid.validator.PaymentDebitValidator;
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
public class EcollServiceImpl implements EcollService {

    private static Logger loggers = LoggerFactory.getLogger(EcollServiceImpl.class);

    @Autowired
    LogServiceEcollRepository ecollRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String VAFlaggingMessaging(EcollValidator body, String reqAddr) throws Exception {
        LogServiceEcoll logService = new LogServiceEcoll();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());
            logService.setService("EColl");

            validateInput(body);
            String trxId = body.getTrxId();
            String crdacpttrm = helperService.encodeURL(env.getProperty("ecoll.crdacpttrm"));
            String crdacptnme = helperService.encodeURL(env.getProperty("ecoll.crdacptnme"));

            String url = env.getProperty("apicl.ecoll");
            String requestEcoll = requestEcoll(logService, body, null, url, crdacpttrm, crdacptnme);
            JSONObject object = new JSONObject(requestEcoll);

            logService.setResponse(object.toString());
            ecollRepo.save(logService);

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
                result.put("trxId", trxId);
                result.put("message", "Submit sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("trxId", trxId);
                result.put("message", "Submit gagal");
            }

            LogServiceEcoll outputLog = new LogServiceEcoll(logService.getRequest_address(), logService.getTraceNbr(), logService.getService(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            ecollRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            logService.setResponse(ste.getMessage());
            ecollRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    @Override
    public String paymentCC(EcollValidator body, String reqAddr) throws Exception {
        LogServiceEcoll logService = new LogServiceEcoll();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());
            logService.setService("Payment CC");

            validateInput(body);
            String trxId = body.getTrxId();
            String crdacpttrm = helperService.encodeURL(env.getProperty("ecollcc.crdacpttrm"));
            String crdacptnme = helperService.encodeURL(env.getProperty("ecollcc.crdacptnme"));

            String url = env.getProperty("apicl.ecoll");
            String requestEcoll = requestEcoll(logService, body, null, url, crdacpttrm, crdacptnme);
            JSONObject object = new JSONObject(requestEcoll);

            logService.setResponse(object.toString());
            ecollRepo.save(logService);

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
                result.put("trxId", trxId);
                result.put("message", "Payment sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("trxId", trxId);
                result.put("message", "Payment gagal");
            }

            LogServiceEcoll outputLog = new LogServiceEcoll(logService.getRequest_address(), logService.getTraceNbr(), logService.getService(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            ecollRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            logService.setResponse(ste.getMessage());
            ecollRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    @Override
    public String paymentDebit(PaymentDebitValidator body, String reqAddr) throws Exception {
        LogServiceEcoll logService = new LogServiceEcoll();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());
            logService.setService("Payment Debit");

            validateInput(body);
            String trxId = body.getTrxId();
            String crdacpttrm = helperService.encodeURL(env.getProperty("ecolldebit.crdacpttrm"));
            String crdacptnme = helperService.encodeURL(env.getProperty("ecolldebit.crdacptnme"));

            String url = env.getProperty("apicl.paymentDebit");
            String requestEcoll = requestEcoll(logService, null, body, url, crdacpttrm, crdacptnme);
            JSONObject object = new JSONObject(requestEcoll);

            logService.setResponse(object.toString());
            ecollRepo.save(logService);

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
                result.put("trxId", trxId);
                result.put("message", "Payment sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("trxId", trxId);
                result.put("message", "Payment gagal");
            }

            LogServiceEcoll outputLog = new LogServiceEcoll(logService.getRequest_address(), logService.getTraceNbr(), logService.getService(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            ecollRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            logService.setResponse(ste.getMessage());
            ecollRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    private void validateInput(EcollValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String paymentNtb = input.getPaymentNtb();
        String trxId = input.getTrxId();
        String amount = input.getAmount();
        String paymentDate = input.getDateTimePayment();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(paymentNtb) ||
                StringUtils.isEmpty(trxId) || StringUtils.isEmpty(amount) ||
                StringUtils.isEmpty(paymentDate)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
        if (paymentNtb.length() != 6) throw new Exception("Field 'paymentNtb': " + paymentNtb + " does not comply");
        if (!StringUtils.isNumeric(trxId)) throw new Exception("Field 'trxId': " + trxId + " does not comply");
        if (!StringUtils.isNumeric(amount)) throw new Exception("Field 'amount': " + amount + " does not comply");
        try {
            Date date = helperService.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(paymentDate);
            helperService.getSimpleDateFormat("HHmmss").format(date);
            helperService.getSimpleDateFormat("MMdd").format(date);
        } catch (Exception e) {
            throw new Exception("Field 'dateTimePayment': " + paymentDate + " does not comply");
        }
    }

    private void validateInput(PaymentDebitValidator input) throws Exception {
        String cardNumber = input.getCcCardNumber();
        String debitNumber = input.getDebitCardNumber();
        String paymentNtb = input.getPaymentNtb();
        String trxId = input.getTrxId();
        String amount = input.getAmount();
        String paymentDate = input.getDateTimePayment();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(debitNumber) ||
                StringUtils.isEmpty(paymentNtb) || StringUtils.isEmpty(trxId) ||
                StringUtils.isEmpty(amount) || StringUtils.isEmpty(paymentDate)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'ccCardNumber': " + cardNumber + " does not comply");
        if (!StringUtils.isNumeric(debitNumber) || debitNumber.length() != 16) throw new Exception("Field 'debitCardNumber': " + debitNumber + " does not comply");
        if (paymentNtb.length() != 6) throw new Exception("Field 'paymentNtb': " + paymentNtb + " does not comply");
        if (!StringUtils.isNumeric(trxId)) throw new Exception("Field 'trxId': " + trxId + " does not comply");
        if (!StringUtils.isNumeric(amount)) throw new Exception("Field 'amount': " + amount + " does not comply");
        try {
            Date date = helperService.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(paymentDate);
            helperService.getSimpleDateFormat("HHmmss").format(date);
            helperService.getSimpleDateFormat("MMdd").format(date);
        } catch (Exception e) {
            throw new Exception("Field 'dateTimePayment': " + paymentDate + " does not comply");
        }
    }

    private String requestEcoll(LogServiceEcoll logService, EcollValidator body, PaymentDebitValidator bodyDebit, String url, String crdacpttrm, String crdacptnme) throws Exception {
        String localtime = "";
        String localdate = "";
        String traceNbr;
        String dateTimePayment;
        boolean isDebit = (body == null);
        if (isDebit) {
            dateTimePayment = bodyDebit.getDateTimePayment();
            traceNbr = bodyDebit.getPaymentNtb();
        } else {
            dateTimePayment = body.getDateTimePayment();
            traceNbr = body.getPaymentNtb();
        }

        try {
            Date paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateTimePayment);
            localtime = new SimpleDateFormat("HHmmss").format(paymentDate);
            localdate = new SimpleDateFormat("MMdd").format(paymentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String gmt = String.format("%s%s", localdate, localtime);

        UriComponents builder;
        if (isDebit) {
            builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("localtim", localtime)
                    .queryParam("localdte", localdate)
                    .queryParam("postingdte", localdate)
                    .queryParam("toacct", bodyDebit.getCcCardNumber())
                    .queryParam("fromacct", bodyDebit.getDebitCardNumber())
                    .queryParam("crdacpttrm", crdacpttrm)
                    .queryParam("crdacptnme", crdacptnme)
                    .queryParam("refnbr", bodyDebit.getTrxId())
                    .queryParam("txnamt", bodyDebit.getAmount())
                    .queryParam("tracenbr", traceNbr)
                    .queryParam("gmt", gmt).build(true);
        } else {
            builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("localtim", localtime)
                    .queryParam("localdte", localdate)
                    .queryParam("postingdte", localdate)
                    .queryParam("toacct", body.getCardNumber())
                    .queryParam("crdacpttrm", crdacpttrm)
                    .queryParam("crdacptnme", crdacptnme)
                    .queryParam("refnbr", body.getTrxId())
                    .queryParam("txnamt", body.getAmount())
                    .queryParam("tracenbr", traceNbr)
                    .queryParam("gmt", gmt).build(true);
        }

        logService.setTraceNbr(traceNbr);
        logService.setLocalDateTime(gmt);
        logService.setRequest(builder.toUriString());

        ResponseEntity<String> ecollResult = APIRepository.apiEcoll(builder.toUri());
        if (ecollResult == null) {
            logService.setResponse(new ConnectException().getMessage());
            ecollRepo.save(logService);
            throw new ConnectException();
        }
        if (ecollResult.getStatusCodeValue() != HttpStatus.OK.value()) {
            logService.setResponse(ecollResult.getBody());
            ecollRepo.save(logService);
            throw new SocketTimeoutException();
        }

        return ecollResult.getBody();
    }
}
