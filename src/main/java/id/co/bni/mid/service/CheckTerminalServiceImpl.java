package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceCheckTerminal;
import id.co.bni.mid.repository.LogServiceCheckTerminalRepository;
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
public class CheckTerminalServiceImpl implements CheckTerminalService {
    private static Logger loggers = LoggerFactory.getLogger(CheckTerminalServiceImpl.class);

    @Autowired
    LogServiceCheckTerminalRepository checkTerminalRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    private final String OUTPUT_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public String checkTerminal(TerminalIdValidator body, String reqAddr) throws Exception {
        LogServiceCheckTerminal logService = new LogServiceCheckTerminal();
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

            String url = env.getProperty("apicl.checkterminal");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("termid", terminalId);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiCheckTerminal(builder.build().toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                checkTerminalRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                checkTerminalRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            checkTerminalRepo.save(logService);

            JSONArray jsonArrayDataRecords = object.getJSONArray("Records");
            JSONArray outRes = new JSONArray();

            for (int i = 0; i < jsonArrayDataRecords.length(); i++) {
                JSONObject dataObj = jsonArrayDataRecords.getJSONObject(i);
                JSONObject resObj = new JSONObject();

                resObj.put("termId", dataObj.get("oadct_term_id").toString());
                resObj.put("merchNbr", dataObj.get("oadct_merch_nbr").toString());
                resObj.put("trmOwner", dataObj.get("oadct_trm_owner").toString());
                resObj.put("eodCutoffBatchNbr", dataObj.get("oadct_eod_cutoff_batch_nbr").toString());
                resObj.put("offlineCapFlag", dataObj.get("oadct_offline_cap_flag").toString());
                resObj.put("trmChipInd", dataObj.get("oadct_trm_chip_ind").toString());
                resObj.put("holdPayment", dataObj.get("oadct_hold_payment").toString());
                resObj.put("curTotalCreditAmt", dataObj.get("oadct_cur_total_credit_amt").toString());
                resObj.put("visaTotalAmount", dataObj.get("oadct_visa_total_amount").toString());
                resObj.put("mcTotalAmount", dataObj.get("oadct_mc_total_amount").toString());
                resObj.put("freqMon", dataObj.get("oadct_freq_mon").toString());
                resObj.put("edcInd", dataObj.get("oadct_edc_ind").toString());
                resObj.put("previousBatchNbr", dataObj.get("oadct_t4_previous_batch_nbr").toString());
                resObj.put("trmType", dataObj.get("oadct_trm_type").toString());
                resObj.put("keyInCapFlag", dataObj.get("oadct_key_in_cap_flag").toString());
                resObj.put("mcTotalNbr", dataObj.get("oadct_mc_total_nbr").toString());
                resObj.put("curTotalCreditNbr", dataObj.get("oadct_cur_total_credit_nbr").toString());
                resObj.put("totalCreditNbr1", dataObj.get("oadct_total_credit_nbr_1").toString());
                resObj.put("totalCreditNbr2", dataObj.get("oadct_total_credit_nbr_2").toString());
                resObj.put("privateTotalNbr", dataObj.get("oadct_private_total_nbr").toString());
                resObj.put("totalDebitAmt1", dataObj.get("oadct_total_debit_amt_1").toString());
                resObj.put("totalDebitAmt2", dataObj.get("oadct_total_debit_amt_2").toString());
                resObj.put("totalCreditAmt1", dataObj.get("oadct_total_credit_amt_1").toString());
                resObj.put("totalCreditAmt2", dataObj.get("oadct_total_credit_amt_2").toString());
                resObj.put("trmPinCaptCode", dataObj.get("oadct_trm_pin_capt_code").toString());
                resObj.put("refundCapFlag", dataObj.get("oadct_refund_cap_flag").toString());
                resObj.put("totalDebitNbr1", dataObj.get("oadct_total_debit_nbr_1").toString());
                resObj.put("totalDebitNbr2", dataObj.get("oadct_total_debit_nbr_2").toString());
                resObj.put("actionInd", dataObj.get("oadct_action_ind").toString());
                resObj.put("logXferInd", dataObj.get("oadct_log_xfer_ind").toString());
                resObj.put("clcbInd", dataObj.get("oadct_clcb_ind").toString());
                resObj.put("termInd", dataObj.get("oadct_term_ind").toString());
                resObj.put("currentBatchNbr", dataObj.get("oadct_current_batch_nbr").toString());
                resObj.put("curTotalDebitNbr", dataObj.get("oadct_cur_total_debit_nbr").toString());
                resObj.put("trmCv2Resp", dataObj.get("oadct_trm_cv2_resp").toString());
                resObj.put("partialSaleInd", dataObj.get("oadct_partial_sale_ind").toString());
                resObj.put("curTotalDebitAmt", dataObj.get("oadct_cur_total_debit_amt").toString());
                resObj.put("visaTotalNbr", dataObj.get("oadct_visa_total_nbr").toString());
                resObj.put("openDate", helperService.julianToDateString(dataObj.get("oadct_open_date").toString(), OUTPUT_DATE_FORMAT));
                resObj.put("lastSettlementDate", helperService.julianToDateString(dataObj.get("oadct_last_settlement_date").toString(), OUTPUT_DATE_FORMAT));
                resObj.put("lastMaintDate", helperService.julianToDateString(dataObj.get("oadct_last_maint_date").toString(), OUTPUT_DATE_FORMAT));
                resObj.put("privateTotalAmount", dataObj.get("oadct_private_total_amount").toString());
                resObj.put("trmCv2Ind", dataObj.get("oadct_trm_cv2_ind").toString());
                resObj.put("riskInd", dataObj.get("oadct_risk_ind").toString());
                outRes.put(resObj);
            }

            result.put("data", outRes);
            result.put("message", "Cek terminal log sukses");

            LogServiceCheckTerminal outputLog = new LogServiceCheckTerminal(logService.getRequest_address(), logService.getTerminalId(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            checkTerminalRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            checkTerminalRepo.save(logService);
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
