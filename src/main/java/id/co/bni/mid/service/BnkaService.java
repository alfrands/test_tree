package id.co.bni.mid.service;

import id.co.bni.mid.validator.BnkaValidator;

public interface BnkaService {

    String bnkaMessaging(BnkaValidator body, String reqAddr) throws Exception;

}
