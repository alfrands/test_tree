package id.co.bni.mid.service;

import id.co.bni.mid.validator.EcollValidator;
import id.co.bni.mid.validator.OaaiValidator;

public interface OaaiService {

    String oaaiMessaging(OaaiValidator body, String reqAddr) throws Exception;
}
