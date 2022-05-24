package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceEdclogA;
import id.co.bni.mid.model.LogServiceEdclogB;
import id.co.bni.mid.repository.LogServiceEdclogARepository;
import id.co.bni.mid.repository.LogServiceEdclogBRepository;
import id.co.bni.mid.validator.TerminalIdValidator;
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
public class CheckEdclogServiceImpl implements CheckEdclogService {
    private static Logger loggers = LoggerFactory.getLogger(CheckEdclogServiceImpl.class);

    @Autowired
    LogServiceEdclogARepository edclogARepo;

    @Autowired
    LogServiceEdclogBRepository edclogBRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    private final String OUTPUT_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public String edclogA(TerminalIdValidator body, String reqAddr) throws Exception {
        LogServiceEdclogA logService = new LogServiceEdclogA();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());

            String terminalId = body.getTerminalId();
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            logService.setTerminalId(terminalId);
            logService.setLocalDateTime(gmt);

            validateInput(body);

            String url = env.getProperty("apicl.edclog");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("termid", terminalId);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiEdclogA(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                edclogARepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                edclogARepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            edclogARepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                resObj.put("mti", dataObj.get("bsl_mti").toString());
                resObj.put("procCode", dataObj.get("bsl_proc_code").toString());
                resObj.put("cardholderNbr", dataObj.get("bsl_cardholder_nbr").toString());
                resObj.put("origTxnRetrvlRef", dataObj.get("bsl_orig_txn_retrvl_ref").toString());
                resObj.put("txnAmount", dataObj.get("bsl_txn_amount").toString());
                resObj.put("posCondCode", dataObj.get("bsl_pos_cond_code").toString());
                resObj.put("edcTrnType", dataObj.get("bsl_edc_trn_type").toString());
                resObj.put("retrvlRef", dataObj.get("bsl_retrvl_ref").toString());
                resObj.put("responseCode", dataObj.get("bsl_response_code").toString());
                resObj.put("approvalCode", dataObj.get("bsl_approval_code").toString());
                resObj.put("merchNbr", dataObj.get("bsl_merch_nbr").toString());
                resObj.put("termId", dataObj.get("bsl_term_id").toString());
                resObj.put("txnDate", dataObj.get("bsl_txn_date").toString().substring(0,6));
                resObj.put("recType", dataObj.get("bsl_rec_type").toString());
                resObj.put("mcc", dataObj.get("bsl_mcc").toString());
                resObj.put("recStatus", dataObj.get("bsl_rec_status").toString());
                resObj.put("originalAmount", dataObj.get("bsl_original_amount").toString());
                resObj.put("txnTime", dataObj.get("bsl_txn_time").toString());
                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Cek EDC log sukses");

            LogServiceEdclogA outputLog = new LogServiceEdclogA(logService.getRequest_address(), logService.getTerminalId(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            edclogARepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            edclogARepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    @Override
    public String edclogB(TerminalIdValidator body, String reqAddr) throws Exception {
        LogServiceEdclogB logService = new LogServiceEdclogB();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());

            String terminalId = body.getTerminalId();
            Date now = new Date();
            String gmt = new SimpleDateFormat("MMddHHmmss").format(now);

            logService.setTerminalId(terminalId);
            logService.setLocalDateTime(gmt);

            validateInput(body);

            String url = env.getProperty("apicl.edclogb");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("termid", terminalId);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiEdclogB(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                edclogBRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                edclogBRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            edclogBRepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                resObj.put("retrvlRef", dataObj.get("oadcl_retrvl_ref").toString());
                resObj.put("recStatus", dataObj.get("oadcl_rec_status").toString());
                resObj.put("tBatchNbr", dataObj.get("oadcl_t_batch_nbr").toString());
                resObj.put("mcTotalAmount", dataObj.get("oadcl_mc_total_amount").toString());
                resObj.put("txmInd", dataObj.get("oadcl_txm_ind").toString());
                resObj.put("visaTotalAmount", dataObj.get("oadcl_visa_total_amount").toString());
                resObj.put("terminalTotalNbr", dataObj.get("oadcl_terminal_total_nbr").toString());
                resObj.put("prodCode", dataObj.get("oadcl_b061_prod_code").toString());
                resObj.put("recType", dataObj.get("oadcl_rec_type").toString());
                resObj.put("posCondCode", dataObj.get("oadcl_pos_cond_code").toString());
                resObj.put("tCardOrg", dataObj.get("oadcl_t_card_org").toString());
                resObj.put("planType", dataObj.get("oadcl_plan_type").toString());
                resObj.put("responseCode", dataObj.get("oadcl_response_code").toString());
                resObj.put("txnAmount", dataObj.get("oadcl_txn_amount").toString());
                resObj.put("payTerm", dataObj.get("oadcl_pay_term").toString());
                resObj.put("monInstlAmt", dataObj.get("oadcl_mon_instl_amt").toString());
                resObj.put("instlnd", dataObj.get("oadcl_instl_ind").toString());
                resObj.put("merchNbr", dataObj.get("oadcl_merch_nbr").toString());
                resObj.put("tCardType", dataObj.get("oadcl_t_card_type").toString());
                resObj.put("procCode", dataObj.get("oadcl_proc_code").toString());
                resObj.put("totalDebitAmount", dataObj.get("oadcl_total_debit_amount").toString());
                resObj.put("systemTrace", dataObj.get("oadcl_system_trace"));
                resObj.put("totalCreditAmount", dataObj.get("oadcl_total_credit_amount").toString());
                resObj.put("txnTime", dataObj.get("oadcl_txn_time").toString());
                resObj.put("mailPhoneInd", dataObj.get("oadcl_mail_phone_ind").toString());
                resObj.put("approvalCode", dataObj.get("oadcl_approval_code").toString());
                resObj.put("invoiceNbr", dataObj.get("oadcl_b062_invoice_nbr").toString());
                resObj.put("edcTrnType", dataObj.get("oadcl_edc_trn_type").toString());
                resObj.put("termId", dataObj.get("oadcl_term_id").toString());
                resObj.put("tProgId", dataObj.get("oadcl_t_prog_id").toString());
                resObj.put("edcTotalAmount", dataObj.get("oadcl_edc_total_amount").toString());
                resObj.put("tAmount", dataObj.get("oadcl_t_amount").toString());
                resObj.put("privateTotalNbr", dataObj.get("oadcl_private_total_nbr").toString());
                resObj.put("expiryDate", dataObj.get("oadcl_expiry_date").toString());
                resObj.put("salesAmt", dataObj.get("oadcl_b061_sales_amt").toString());
                resObj.put("batchNbr", dataObj.get("oadcl_batch_nbr").toString());
                resObj.put("cardholderNbr", dataObj.get("oadcl_cardholder_nbr").toString());
                resObj.put("edcTotalNbr", dataObj.get("oadcl_edc_total_nbr").toString());
                resObj.put("tCardNbr", dataObj.get("oadcl_t_card_nbr").toString());
                resObj.put("mcTotalNbr", dataObj.get("oadcl_mc_total_nbr").toString());
                resObj.put("terminalTotalAmount", dataObj.get("oadcl_terminal_total_amount").toString());
                resObj.put("balAmt", dataObj.get("oadcl_b061_bal_amt").toString());
                resObj.put("dynamicDesc", dataObj.get("oadcl_dynamic_desc").toString());
                resObj.put("posEmPan", dataObj.get("oadcl_b022_pos_em_pan").toString());
                resObj.put("ecommInd", dataObj.get("oadcl_ecomm_ind").toString());
                resObj.put("privateTotalAmount", dataObj.get("oadcl_private_total_amount").toString());
                resObj.put("declineReason", dataObj.get("oadcl_b061_redeemed_amt").toString());
                resObj.put("origTxnRetrvlRef", dataObj.get("oadcl_orig_txn_retrvl_ref").toString());
                resObj.put("instlPlan", dataObj.get("oadcl_instl_plan").toString());
                resObj.put("posEmPinCap", dataObj.get("oadcl_b022_pos_em_pin_cap").toString());
                resObj.put("visaTotalNbr", dataObj.get("oadcl_visa_total_nbr").toString());
                resObj.put("terminalBatchNbr", dataObj.get("oadcl_terminal_batch_nbr").toString());
                resObj.put("totalCreditNbr", dataObj.get("oadcl_total_credit_nbr").toString());
                resObj.put("mti", dataObj.get("oadcl_mti").toString());
                resObj.put("sTerminalBatchNbr", dataObj.get("oadcl_s_terminal_batch_nbr").toString());
                resObj.put("netSalesAmt", dataObj.get("oadcl_b061_net_sales_amt").toString());
                resObj.put("totalDebitNbr", dataObj.get("oadcl_total_debit_nbr").toString());
                resObj.put("tPoints", dataObj.get("oadcl_t_points").toString());
                resObj.put("originalAmount", dataObj.get("oadcl_original_amount").toString());
                resObj.put("tCode", dataObj.get("oadcl_t_code").toString());
                resObj.put("txnDate", helperService.julianToDateString(dataObj.get("oadcl_txn_date").toString(), OUTPUT_DATE_FORMAT));
                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Cek EDC log sukses");

            LogServiceEdclogB outputLog = new LogServiceEdclogB(logService.getRequest_address(), logService.getTerminalId(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            edclogBRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            edclogBRepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    private void validateInput(TerminalIdValidator input) throws Exception {
        String terminalId = input.getTerminalId();

        if (StringUtils.isEmpty(terminalId)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(terminalId) || terminalId.length() != 8)
            throw new Exception("Field 'terminalId': " + terminalId + " does not comply");
    }
}
