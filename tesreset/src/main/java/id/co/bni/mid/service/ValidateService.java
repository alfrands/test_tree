package id.co.bni.mid.service;

import id.co.bni.mid.validator.ValidateValidator;

public interface ValidateService {

    String validate(ValidateValidator body, String reqAddr) throws Exception;

}
