package id.co.bni.mid.service;

import id.co.bni.mid.validator.EcollValidator;
import id.co.bni.mid.validator.PaymentDebitValidator;

public interface EcollService {

    String VAFlaggingMessaging(EcollValidator body, String reqAddr) throws Exception;

    String paymentCC(EcollValidator body, String reqAddr) throws Exception;

    String paymentDebit(PaymentDebitValidator body, String reqAddr) throws Exception;

}
