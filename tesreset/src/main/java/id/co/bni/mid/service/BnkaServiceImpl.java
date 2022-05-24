package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceBnka;
import id.co.bni.mid.repository.LogServiceBnkaRepository;
import id.co.bni.mid.validator.BnkaValidator;
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
import java.util.Date;

@Service
public class BnkaServiceImpl implements BnkaService {

    private static Logger loggers = LoggerFactory.getLogger(BnkaServiceImpl.class);

    @Autowired
    LogServiceBnkaRepository bnkaRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String bnkaMessaging(BnkaValidator body, String reqAddr) throws Exception {
        LogServiceBnka logService = new LogServiceBnka();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());

            validateInput(body);

            String cardNumber = body.getCardNumber();
            String date = "";
            try {
                Date dt = helperService.getSimpleDateFormat("ddMMyy").parse(body.getDate());
                date = helperService.getSimpleDateFormat("MMdd").format(dt);
            } catch (Exception e) {
                return "";
            }
            String time = body.getTime();
            String traceNbr = body.getStan();
            String gmt = String.format("%s%s", date, time);

            logService.setTraceNbr(traceNbr);
            logService.setLocalDateTime(gmt);

            /* F48 */
            String line1 = body.getLine1();
            if (line1.length() < 55) {
                line1 = StringUtils.rightPad(line1, 55, "");
            }
            String line2 = body.getLine2();
            if (line2.length() < 55) {
                line2 = StringUtils.rightPad(line2, 55, "");
            }

            String f48 = helperService.encodeURL(String.format("%-16s%-6s%-4s%-3s%-3s%-3s%-55s%-55s",
                    cardNumber,
                    body.getDate(),
                    body.getTime(),
                    body.getMemoType(),
                    body.getOperator(),
                    body.getAct(),
                    line1,
                    line2));

            /* F60 */
            String line3 = body.getLine3();
            if (line3.length() < 55) {
                line3 = StringUtils.rightPad(line3, 55, "");
            }
            String line4 = body.getLine4();
            if (line4.length() < 55) {
                line4 = StringUtils.rightPad(line4, 55, "");
            }

            String f60 = helperService.encodeURL(String.format("%-55s%-55s", line3, line4));

            String url = env.getProperty("apicl.bnka");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("date", date)
                    .queryParam("f60", f60)
                    .queryParam("refnum", body.getRefNumber())
                    .queryParam("tracenum", traceNbr)
                    .queryParam("time", body.getTime())
                    .queryParam("f48", f48).build(true);

            logService.setRequest(builder.toUriString());

            ResponseEntity<String> apiResult = APIRepository.apiBnka(builder.toUri());
            if (apiResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                bnkaRepo.save(logService);
                throw new ConnectException();
            }
            if (apiResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(apiResult.getBody());
                bnkaRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(apiResult.getBody());
            logService.setResponse(object.toString());
            bnkaRepo.save(logService);

            JSONObject obj1 = object.getJSONObject("SOAC002");
            JSONObject obj2 = obj1.getJSONObject("BNSOA_RECORD");
            JSONObject objFields = obj2.getJSONObject("BNSOA_ISO_FIELDS");
            String responseCode = objFields.get("BNSOA_F039_RESP_CDE").toString();
            if (StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            String responseMessage = helperService.getResponseMessage(responseCode);

            if (responseCode.equals("00")) {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Submit BNKA sukses");
            } else {
                result.put("responseCode", responseCode);
                result.put("responseMessage", responseMessage);
                result.put("cardNumber", cardNumber);
                result.put("message", "Submit BNKA gagal");
            }

            LogServiceBnka outputLog = new LogServiceBnka(logService.getRequest_address(), logService.getTraceNbr(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            bnkaRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            ste.printStackTrace();
            logService.setResponse(ste.getMessage());
            bnkaRepo.save(logService);
            throw ste;
        }
        return result.toString();
    }

    private void validateInput(BnkaValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String stan = input.getStan();
        String refNumber = input.getRefNumber();
        String date = input.getDate();
        String time = input.getTime();
        String memoType = input.getMemoType();
        String operator = input.getOperator();
        String act = input.getAct();
        String line1 = input.getLine1();
        String line2 = input.getLine2();
        String line3 = input.getLine3();
        String line4 = input.getLine4();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(stan) ||
                StringUtils.isEmpty(date) || StringUtils.isEmpty(time) ||
                StringUtils.isEmpty(refNumber) || StringUtils.isEmpty(memoType) ||
                StringUtils.isEmpty(operator) || StringUtils.isEmpty(act)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
        if (!StringUtils.isNumeric(stan) || stan.length() != 6) throw new Exception("Field 'stan' does not comply");
        if (!StringUtils.isNumeric(refNumber) || refNumber.length() != 12) throw new Exception("Field 'refNumber' does not comply");
        if (!StringUtils.isNumeric(date) || date.length() != 6) throw new Exception("Field 'date' does not comply");
        if (!StringUtils.isNumeric(time) || time.length() != 4) throw new Exception("Field 'time' does not comply");
        if (memoType.length() != 3) throw new Exception("Field 'memoType' does not comply");
        if (operator.length() != 3) throw new Exception("Field 'operator' does not comply");
        if (act.length() != 3) throw new Exception("Field 'act' does not comply");
        if (!StringUtils.isEmpty(line1)) {
            if (line1.length() > 55) throw new Exception("Field 'line1' does not comply");
        }
        if (!StringUtils.isEmpty(line2)) {
            if (line2.length() > 55) throw new Exception("Field 'line2' does not comply");
        }
        if (!StringUtils.isEmpty(line3)) {
            if (line3.length() > 55) throw new Exception("Field 'line3' does not comply");
        }
        if (!StringUtils.isEmpty(line4)) {
            if (line4.length() > 55) throw new Exception("Field 'line4' does not comply");
        }

        try {
            Date dt = helperService.getSimpleDateFormat("ddMMyy").parse(date);
            helperService.getSimpleDateFormat("ddMMyy").format(dt);
        } catch (Exception e) {
            throw new Exception("Field 'date': " + date + " does not comply");
        }

        try {
            Date tm = helperService.getSimpleDateFormat("HHmm").parse(time);
            helperService.getSimpleDateFormat("HHmm").format(tm);
        } catch (Exception e) {
            throw new Exception("Field 'time': " + time + " does not comply");
        }
    }

}
