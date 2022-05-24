package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceIcl;
import id.co.bni.mid.repository.LogServiceIclRepository;
import id.co.bni.mid.validator.IclValidator;
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

@Service
public class IclServiceImpl implements IclService {

    private static Logger loggers = LoggerFactory.getLogger(IclServiceImpl.class);

    @Autowired
    LogServiceIclRepository iclRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String iclTemp(IclValidator body, String reqAddr) throws Exception {
        LogServiceIcl logService = new LogServiceIcl();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String date = new SimpleDateFormat("MMdd").format(now);
            String time = new SimpleDateFormat("HHmmss").format(now);
            String gmt = String.format("%s%s", date, time);

            logService.setRequest_address(reqAddr);
            logService.setService("ICL Temp");
            logService.setLocalDateTime(gmt);
            logService.setRequest(body.toString());

            validateInput(body, false);

            logService.setCardNbr(body.getCardNumber());

            String inquiryObject = submitICL(body, reqAddr, date, time, false);
            if (inquiryObject == null || StringUtils.isEmpty(inquiryObject)) {
                logService.setResponse(new ConnectException().getMessage());
                iclRepo.save(logService);
                throw new ConnectException();
            }

            JSONObject object = new JSONObject(inquiryObject);
            String responseCode = object.get("responseCode").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }

            if (responseCode.equals("00")) {
                result.put("message", "Submit Increase Limit Temp sukses");
            } else {
                result.put("message", "Submit Increase Limit Temp gagal");
            }
            result.put("responseCode", object.get("responseCode"));
            result.put("responseMessage", object.get("responseMessage"));
            result.put("desc", object.get("desc"));
            result.put("cardNumber", body.getCardNumber());

            logService.setResponse(result.toString());
            iclRepo.save(logService);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            iclRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    @Override
    public String iclPerm(IclValidator body, String reqAddr) throws Exception {
        LogServiceIcl logService = new LogServiceIcl();
        JSONObject result = new JSONObject();
        try {
            Date now = new Date();
            String date = new SimpleDateFormat("MMdd").format(now);
            String time = new SimpleDateFormat("HHmmss").format(now);
            String gmt = String.format("%s%s", date, time);

            logService.setRequest_address(reqAddr);
            logService.setService("ICL Perm");
            logService.setLocalDateTime(gmt);
            logService.setRequest(body.toString());

            validateInput(body, true);

            logService.setCardNbr(body.getCardNumber());

            String inquiryObject = submitICL(body, reqAddr, date, time, true);
            if (inquiryObject == null || StringUtils.isEmpty(inquiryObject)) {
                logService.setResponse(new ConnectException().getMessage());
                iclRepo.save(logService);
                throw new ConnectException();
            }

            JSONObject object = new JSONObject(inquiryObject);
            String responseCode = object.get("responseCode").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            if (responseCode.equals("00")) {
                result.put("message", "Submit Increase Limit Permanen sukses");
            } else {
                result.put("message", "Submit Increase Limit Permanen gagal");
            }
            result.put("responseCode", object.get("responseCode"));
            result.put("responseMessage", object.get("responseMessage"));
            result.put("desc", object.get("desc"));
            result.put("cardNumber", body.getCardNumber());

            logService.setResponse(result.toString());
            iclRepo.save(logService);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            iclRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    private String submitICL(IclValidator body, String reqAddr, String date, String time, boolean isPerm) throws Exception {
        LogServiceIcl logService = new LogServiceIcl();
        JSONObject result = new JSONObject();
        try {
            String cardNumber = body.getCardNumber();
            String permAmt = "000000000";
            String tempAmt = "000000000";
            String effDate = "00000000";
            String tempExp = "00000000";
            String service = "ICL Temp";
            if (isPerm) {
                service = "ICL Perm";
            }

            if (isPerm) {
                permAmt = body.getAmount();
                if (permAmt.length() < 9) {
                    permAmt = StringUtils.leftPad(permAmt, 9, "0");
                }
            } else {
                try {
                    tempAmt = body.getAmount();
                    if (tempAmt.length() < 9) {
                        tempAmt = StringUtils.leftPad(tempAmt, 9, "0");
                    }

                    Date efDt = helperService.getSimpleDateFormat("yyyy-MM-dd").parse(body.getEffDate());
                    effDate = helperService.getSimpleDateFormat("ddMMyyyy").format(efDt);

                    Date expDt = helperService.getSimpleDateFormat("yyyy-MM-dd").parse(body.getTempExpDate());
                    tempExp = helperService.getSimpleDateFormat("ddMMyyyy").format(expDt);
                } catch (Exception e) {
                    return "";
                }
            }

            String dataICL = String.format("%-9s%-9s%-8s%-8s",
                    permAmt,
                    tempAmt,
                    effDate,
                    tempExp);

            String url = env.getProperty("apicl.icl");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("date", date)
                    .queryParam("time", time)
                    .queryParam("dataicl", dataICL)
                    .queryParam("cardnum", cardNumber).build(true);

            String gmt = String.format("%s%s", date, time);
            logService.setRequest_address(reqAddr);
            logService.setCardNbr(cardNumber);
            logService.setService(service);
            logService.setLocalDateTime(gmt);
            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiICL(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                iclRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                iclRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            iclRepo.save(logService);

            JSONObject obj1 = object.getJSONObject("CPS360A");
            JSONObject obj2 = obj1.getJSONObject("CP_COMMUNICATION_AREA");
            JSONObject objFields = obj2.getJSONObject("CPCA_USER_WORK_AREA");
            String fill7 = objFields.get("FILL_7").toString();
            String responseCode = fill7.substring(0, 2);
            if (StringUtils.isEmpty(fill7) || StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            String desc = fill7.substring(2);
            String responseMessage = helperService.getResponseMessage(responseCode);

            result.put("responseCode", responseCode);
            result.put("responseMessage", responseMessage);
            result.put("desc", desc);
            result.put("cardNumber", cardNumber);

        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            iclRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    private void validateInput(IclValidator input, boolean isPerm) throws Exception {
        String dateFormat = "yyyy-MM-dd";
        String cardNumber = input.getCardNumber();
        String amount = input.getAmount();
        String effDate = input.getEffDate();
        String expDate = input.getTempExpDate();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(amount)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
        if (!StringUtils.isNumeric(amount)) throw new Exception("Field 'amount': " + amount + " does not comply");
        if (amount.length() > 9) throw new Exception("Field 'amount': " + amount + " exceeds maximum length (9)");
        if (!isPerm) {
            if(StringUtils.isEmpty(effDate)) throw new Exception("Empty fields");
            if(StringUtils.isEmpty(expDate)) throw new Exception("Empty fields");
            if (effDate.equals(expDate)) throw new Exception("Field 'effDate': " + effDate + ", 'tempExpDate': " + expDate + " does not comply");

            try {
                Date efDt = helperService.getSimpleDateFormat(dateFormat).parse(effDate);
                helperService.getSimpleDateFormat("ddMMyyyy").format(efDt);
            } catch (Exception e) {
                throw new Exception("Field 'effDate': " + effDate + " does not comply");
            }

            try {
                Date exDt = helperService.getSimpleDateFormat(dateFormat).parse(expDate);
                helperService.getSimpleDateFormat("ddMMyyyy").format(exDt);
            } catch (Exception e) {
                throw new Exception("Field 'tempExpDate': " + expDate + " does not comply");
            }

            if (helperService.dateIsAfterDate(effDate, expDate, dateFormat)) throw new Exception("Field 'effDate': " + effDate + ", 'tempExpDate': " + expDate + " does not comply");
        }
    }
}
