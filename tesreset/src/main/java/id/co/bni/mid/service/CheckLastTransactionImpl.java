package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceOaslogA;
import id.co.bni.mid.model.LogServiceOaslogB;
import id.co.bni.mid.repository.LogServiceOaslogARepository;
import id.co.bni.mid.repository.LogServiceOaslogBRepository;
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
public class CheckLastTransactionImpl implements CheckLastTransactionService {

    private static Logger loggers = LoggerFactory.getLogger(CheckLastTransactionImpl.class);

    @Autowired
    LogServiceOaslogARepository isoOaslogARepo;

    @Autowired
    LogServiceOaslogBRepository isoOaslogBRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Override
    public String oaslog(CardNumberValidator body, String reqAddr) throws Exception {
        LogServiceOaslogA logService = new LogServiceOaslogA();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());

            String cardNumber = body.getCardNumber();
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            logService.setCardNumber(cardNumber);
            logService.setLocalDateTime(gmt);

            validateInput(body);

            String url = env.getProperty("apicl.oaslog");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiOaslogA(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                isoOaslogARepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                isoOaslogARepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            isoOaslogARepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                resObj.put("org", dataObj.get("dvmub_org").toString());
                resObj.put("type", dataObj.get("dvmub_type").toString());
                resObj.put("merchantOrg", dataObj.get("dvmub_merchant_org").toString());
                resObj.put("merchantNbr", dataObj.get("dvmub_merchant_nbr").toString());
                resObj.put("tc", dataObj.get("dvmub_tc").toString());
                resObj.put("refNbr", dataObj.get("dvmub_ref_nbr").toString());
                resObj.put("procCode", dataObj.get("dvmub_b003_proc_code").toString());
                resObj.put("txnAmt", dataObj.get("dvmub_b004_txn_amt").toString());
                resObj.put("chBlngAmt", dataObj.get("dvmub_b006_ch_blng_amt").toString());
                resObj.put("merchType", dataObj.get("dvmub_b018_merch_type").toString());
                resObj.put("cntryCode", dataObj.get("dvmub_b019_cntry_code").toString());
                resObj.put("posEntryMode", dataObj.get("dvmub_b022_pos_entry_mode").toString());
                resObj.put("authCode", dataObj.get("dvmub_b038_auth_code").toString().trim());
                resObj.put("crdAccptStore", dataObj.get("dvmub_b041_crd_accpt_store").toString());
                resObj.put("crdAccptTerm", dataObj.get("dvmub_b041_crd_accpt_term").toString());
                resObj.put("declineReason", dataObj.get("dvmub_decline_reason").toString());
                resObj.put("redemptionOriginalAmt", dataObj.get("dvmub_redemption_original_amt").toString());
                resObj.put("currCode", dataObj.get("dvmub_b049_curr_code").toString());
                resObj.put("logRba", dataObj.get("dvmub_log_rba").toString());
                resObj.put("installmentInd", dataObj.get("dvmub_log_rba").toString());
                resObj.put("typebMerchId", dataObj.get("dvmub_b042_typeb_merch_id").toString());
                resObj.put("card", dataObj.get("dvmub_card").toString());

                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Cek transaksi terakhir sukses");

            LogServiceOaslogA outputLog = new LogServiceOaslogA(logService.getRequest_address(), logService.getCardNumber(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            isoOaslogARepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            isoOaslogARepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    @Override
    public String checkAuthlog(CardNumberValidator body, String reqAddr) throws Exception {
        LogServiceOaslogA logService = new LogServiceOaslogA();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());

            String cardNumber = body.getCardNumber();
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            logService.setCardNumber(cardNumber);
            logService.setLocalDateTime(gmt);

            validateInput(body);

            String url = env.getProperty("apicl.oaslog");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiOaslogA(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                isoOaslogARepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                isoOaslogARepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            isoOaslogARepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                resObj.put("card", dataObj.get("dvmub_card").toString());
                resObj.put("logRba", dataObj.get("dvmub_log_rba").toString());
                resObj.put("org", dataObj.get("dvmub_org").toString());
                resObj.put("type", dataObj.get("dvmub_type").toString());
                resObj.put("merchantOrg", dataObj.get("dvmub_merchant_org").toString());
                resObj.put("merchantNbr", dataObj.get("dvmub_merchant_nbr").toString());
                resObj.put("tc", dataObj.get("dvmub_tc").toString());
                resObj.put("refNbr", dataObj.get("dvmub_ref_nbr").toString());
                resObj.put("procCode", dataObj.get("dvmub_b003_proc_code").toString());
                resObj.put("txnAmt", dataObj.get("dvmub_b004_txn_amt").toString());
                resObj.put("chBlngAmt", dataObj.get("dvmub_b006_ch_blng_amt").toString());
                resObj.put("merchType", dataObj.get("dvmub_b018_merch_type").toString());
                resObj.put("cntryCode", dataObj.get("dvmub_b019_cntry_code").toString());
                resObj.put("posEntryMode", dataObj.get("dvmub_b022_pos_entry_mode").toString());
                resObj.put("authCode", dataObj.get("dvmub_b038_auth_code").toString().trim());

                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Cek auth log file sukses");

            LogServiceOaslogA outputLog = new LogServiceOaslogA(logService.getRequest_address(), logService.getCardNumber(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            isoOaslogARepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            isoOaslogARepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    @Override
    public String oaslogB(CardNumberValidator body, String reqAddr) throws Exception {
        LogServiceOaslogB logService = new LogServiceOaslogB();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());

            String cardNumber = body.getCardNumber();
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            logService.setCardNumber(cardNumber);
            logService.setLocalDateTime(gmt);

            validateInput(body);

            String url = env.getProperty("apicl.oaslogb");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiOaslogB(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                isoOaslogBRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                isoOaslogBRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            isoOaslogBRepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                resObj.put("org", dataObj.get("oasa_org").toString());
                resObj.put("type", dataObj.get("oasa_type").toString());
                resObj.put("acct", dataObj.get("oasa_acct").toString());
                resObj.put("merchantOrg", dataObj.get("oasa_merchant_org").toString());
                resObj.put("merchantNbr", dataObj.get("oasa_merchant_nbr").toString());
                resObj.put("tc", dataObj.get("oasa_tc").toString());
                resObj.put("refNbr", dataObj.get("oasa_ref_nbr").toString());
                resObj.put("procCode", dataObj.get("oasa_b003_proc_code").toString());
                resObj.put("txnAmt", dataObj.get("oasa_b004_txn_amt").toString());
                resObj.put("chBlngAmt", dataObj.get("oasa_b006_ch_blng_amt").toString());
                resObj.put("expDate", dataObj.get("oasa_b014_exp_date").toString());
                resObj.put("merchType", dataObj.get("oasa_b018_merch_type").toString());
                resObj.put("cntryCode", dataObj.get("oasa_b019_cntry_code").toString());
                resObj.put("posEntryMode", dataObj.get("oasa_b022_pos_entry_mode").toString());
                resObj.put("authCode", dataObj.get("oasa_b038_auth_code").toString());
                resObj.put("crdAccptStore", dataObj.get("oasa_b041_crd_accpt_store").toString());
                resObj.put("crdAccptTerm", dataObj.get("oasa_b041_crd_accpt_term").toString());
                resObj.put("timeSent", dataObj.get("oasa_time_sent").toString());
                resObj.put("timeReceived", dataObj.get("oasa_time_received").toString());
                resObj.put("declineReason", dataObj.get("oasa_decline_reason").toString());
                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Cek transaksi terakhir sukses");

            LogServiceOaslogB outputLog = new LogServiceOaslogB(logService.getRequest_address(), logService.getCardNumber(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            isoOaslogBRepo.save(outputLog);

        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            logService.setResponse(ste.getMessage());
            isoOaslogBRepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    @Override
    public String checkAuthlogB(CardNumberValidator body, String reqAddr) throws Exception {
        LogServiceOaslogB logService = new LogServiceOaslogB();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());

            String cardNumber = body.getCardNumber();
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            logService.setCardNumber(cardNumber);
            logService.setLocalDateTime(gmt);

            validateInput(body);

            String url = env.getProperty("apicl.oaslogb");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("cardnum", cardNumber);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiOaslogB(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                isoOaslogBRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                isoOaslogBRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            isoOaslogBRepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                resObj.put("org", dataObj.get("oasa_org").toString());
                resObj.put("type", dataObj.get("oasa_type").toString());
                resObj.put("acct", dataObj.get("oasa_acct").toString());
                resObj.put("merchantOrg", dataObj.get("oasa_merchant_org").toString());
                resObj.put("merchantNbr", dataObj.get("oasa_merchant_nbr").toString());
                resObj.put("tc", dataObj.get("oasa_tc").toString());
                resObj.put("refNbr", dataObj.get("oasa_ref_nbr").toString());
                resObj.put("procCode", dataObj.get("oasa_b003_proc_code").toString());
                resObj.put("txnAmt", dataObj.get("oasa_b004_txn_amt").toString());
                resObj.put("chBlngAmt", dataObj.get("oasa_b006_ch_blng_amt").toString());
                resObj.put("expDate", dataObj.get("oasa_b014_exp_date").toString());
                resObj.put("merchType", dataObj.get("oasa_b018_merch_type").toString());
                resObj.put("cntryCode", dataObj.get("oasa_b019_cntry_code").toString());
                resObj.put("posEntryMode", dataObj.get("oasa_b022_pos_entry_mode").toString());
                resObj.put("acqId", dataObj.get("oasa_b032_acq_id").toString());
                resObj.put("authCode", dataObj.get("oasa_b038_auth_code").toString());
                resObj.put("crdAccptStore", dataObj.get("oasa_b041_crd_accpt_store").toString());
                resObj.put("crdAccptTerm", dataObj.get("oasa_b041_crd_accpt_term").toString());
                resObj.put("merchId", dataObj.get("oasa_b042_typeb_merch_id").toString());
                resObj.put("currCode", dataObj.get("oasa_b049_curr_code").toString());
                resObj.put("inputSource", dataObj.get("oasa_input_source").toString());
                resObj.put("cardType", dataObj.get("oasa_card_type").toString());
                resObj.put("sourceTerminal", dataObj.get("oasa_source_terminal").toString());
                resObj.put("timeSent", dataObj.get("oasa_time_sent").toString());
                resObj.put("timeReceived", dataObj.get("oasa_time_received").toString());
                resObj.put("declineReason", dataObj.get("oasa_decline_reason").toString());
                resObj.put("usrid", dataObj.get("oasa_usrid").toString());
                resObj.put("installmentInd", dataObj.get("oasa_installment_ind").toString());
                resObj.put("origTxnAmt", dataObj.get("oasa_orig_b004_txn_amt").toString());
                resObj.put("origChBlngAmt", dataObj.get("oasa_orig_b006_ch_blng_amt").toString());
                resObj.put("redemptionFlag", dataObj.get("oasa_redemption_flag").toString());
                resObj.put("redemptionOriginalAmt", dataObj.get("oasa_redemption_original_amt").toString());
                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Cek auth log file sukses");

            LogServiceOaslogB outputLog = new LogServiceOaslogB(logService.getRequest_address(), logService.getCardNumber(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            isoOaslogBRepo.save(outputLog);

        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            logService.setResponse(ste.getMessage());
            isoOaslogBRepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    private void validateInput(CardNumberValidator input) throws Exception {
        String cardNumber = input.getCardNumber();

        if (StringUtils.isEmpty(cardNumber)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16)
            throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
    }
}
