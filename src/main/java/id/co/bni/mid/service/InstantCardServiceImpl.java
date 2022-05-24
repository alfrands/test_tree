package id.co.bni.mid.service;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.HelperService;
import id.co.bni.mid.helpers.StringHelpers;
import id.co.bni.mid.httpbuilder.APIRepository;
import id.co.bni.mid.model.LogServiceInstantCard;
import id.co.bni.mid.model.LogServiceOaai;
import id.co.bni.mid.repository.LogServiceInstantCardRepository;
import id.co.bni.mid.validator.OaaiValidator;
import id.co.bni.mid.validator.PCAHValidator;
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
public class InstantCardServiceImpl implements InstantCardService {

    private static Logger loggers = LoggerFactory.getLogger(InstantCardServiceImpl.class);

    @Autowired
    LogServiceInstantCardRepository instantCardRepo;

    @Autowired
    APIRepository APIRepository;

    @Autowired
    Config env;

    @Autowired
    HelperService helperService;

    @Override
    public String submitPcah(PCAHValidator body, String reqAddr) throws Exception {
        LogServiceInstantCard logService = new LogServiceInstantCard();
        JSONObject result = new JSONObject();
        try {
            logService.setRequest_address(reqAddr);
            logService.setRequest(body.toString());

            Date now = new Date();
            String date = helperService.getSimpleDateFormat("yyyyMMdd").format(now);
            String time = helperService.getSimpleDateFormat("HHmmss").format(now);
            String gmt = helperService.getSimpleDateFormat("MMddHHmmss").format(now);

            logService.setLocalDateTime(gmt);

            validateInput(body);

            String cardNumber = body.getCardNumber();
            String cycle = StringHelpers.leftPadZeroes(body.getCycle(), 2);
            String datapcah = String.format("%-3s%-3s%-2s%-2s", body.getCustomerOrg(), body.getCustomerNumber(),
                    body.getPostingFlag(), cycle);

            String url = env.getProperty("apicl.instantCard");
            UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("date", date)
                    .queryParam("cardnum", cardNumber)
                    .queryParam("org", body.getCardOrg())
                    .queryParam("time", time)
                    .queryParam("type", body.getCardType())
                    .queryParam("datapcah", datapcah).build(true);

            logService.setCardNumber(cardNumber);
            logService.setRequest(builder.toUriString());

            ResponseEntity<String> instantCardResult = APIRepository.apiInstantCard(builder.toUri());
            if (instantCardResult == null) {
                logService.setResponse(new ConnectException().getMessage());
                instantCardRepo.save(logService);
                throw new ConnectException();
            }
            if (instantCardResult.getStatusCodeValue() != HttpStatus.OK.value()) {
                logService.setResponse(instantCardResult.getBody());
                instantCardRepo.save(logService);
                throw new SocketTimeoutException();
            }

            JSONObject object = new JSONObject(instantCardResult.getBody());

            logService.setResponse(object.toString());
            instantCardRepo.save(logService);

            JSONObject obj1 = object.getJSONObject("CPS360A");
            JSONObject obj2 = obj1.getJSONObject("CP_COMMUNICATION_AREA");
            JSONObject objFields = obj2.getJSONObject("CPCA_USER_WORK_AREA");
            String fill7 = objFields.get("FILL_7").toString();
            String responseCode = fill7.substring(0, 2);
            if (StringUtils.isEmpty(fill7) || StringUtils.isEmpty(responseCode)) {
                throw new Exception("Response Code is empty");
            }
            String responseMessage = helperService.getResponseMessage(responseCode);

            result.put("responseCode", responseCode);
            result.put("responseMessage", responseMessage);
            result.put("cardNumber", cardNumber);
            if (responseCode.equals("00")) {
                result.put("message", "Submit sukses");
            } else {
                result.put("message", "Submit gagal");
            }

            LogServiceInstantCard outputLog = new LogServiceInstantCard(logService.getRequest_address(), logService.getCardNumber(), logService.getLocalDateTime(), logService.getRequest());
            outputLog.setRequest(body.toString());
            outputLog.setResponse(result.toString());
            instantCardRepo.save(outputLog);
        } catch (Exception ste) {
            loggers.error(ste.getMessage());
            logService.setResponse(ste.getMessage());
            instantCardRepo.save(logService);
            throw ste;
        }

        return result.toString();
    }

    private void validateInput(PCAHValidator input) throws Exception {
        String cardNumber = input.getCardNumber();
        String cardOrg = input.getCardOrg();
        String cardType = input.getCardType();
        String customerOrg = input.getCustomerOrg();
        String customerNumber = input.getCustomerNumber();
        String postingFlag = input.getPostingFlag();
        String cycle = input.getCycle();

        if (StringUtils.isEmpty(cardNumber) || StringUtils.isEmpty(cardOrg) ||
                StringUtils.isEmpty(cardType) || StringUtils.isEmpty(customerOrg) ||
                StringUtils.isEmpty(customerNumber) || StringUtils.isEmpty(postingFlag) ||
                StringUtils.isEmpty(cycle)) throw new Exception("Empty fields");
        if (!StringUtils.isNumeric(cardNumber) || cardNumber.length() != 16) throw new Exception("Field 'cardNumber': " + cardNumber + " does not comply");
        if (!StringUtils.isNumeric(cardOrg) || cardOrg.length() != 3) throw new Exception("Field 'cardOrg': " + cardOrg + " does not comply");
        if (!StringUtils.isNumeric(cardType) || cardType.length() != 3) throw new Exception("Field 'cardType': " + cardType + " does not comply");
        if (!StringUtils.isNumeric(customerOrg) || customerOrg.length() != 3) throw new Exception("Field 'customerOrg': " + customerOrg + " does not comply");
        if (!StringUtils.isNumeric(customerNumber) || customerNumber.length() != 16) throw new Exception("Field 'customerNumber': " + customerNumber + " does not comply");
        if (postingFlag.length() > 2) throw new Exception("Field 'postingFlag': " + postingFlag + " exceeds maximum length (2)");
        if (!StringUtils.isNumeric(cycle)) throw new Exception("Field 'cycle': " + cycle + " does not comply");
        if (Integer.parseInt(cycle) > 31) throw new Exception("Field 'cycle': " + cycle + " does not comply");
    }
}
