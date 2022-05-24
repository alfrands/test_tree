package id.co.bni.mid.service;

import id.co.bni.mid.validator.CardValidator;

public interface InquiryService {

    String checkBlock(CardValidator body, String reqAddr) throws Exception;

    String checkExpired(CardValidator body, String reqAddr) throws Exception;

    String inquiryLimit(CardValidator body, String reqAddr) throws Exception;

}
