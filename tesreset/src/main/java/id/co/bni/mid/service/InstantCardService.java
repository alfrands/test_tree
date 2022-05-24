package id.co.bni.mid.service;

import id.co.bni.mid.validator.PCAHValidator;

public interface InstantCardService {

    String submitPcah(PCAHValidator body, String reqAddr) throws Exception;
}
