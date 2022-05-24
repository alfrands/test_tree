package id.co.bni.mid.service;

import id.co.bni.mid.validator.CardNumberValidator;

public interface CheckLastTransactionService {

    String oaslog(CardNumberValidator body, String reqAddr) throws Exception;

    String oaslogB(CardNumberValidator body, String reqAddr) throws Exception;

    String checkAuthlog(CardNumberValidator body, String reqAddr) throws Exception;

    String checkAuthlogB(CardNumberValidator body, String reqAddr) throws Exception;
}
