package id.co.bni.mid.service;

import id.co.bni.mid.validator.ChangePinValidator;

public interface ChangePinService {

    String changePin(ChangePinValidator body, String reqAddr) throws  Exception;

}
