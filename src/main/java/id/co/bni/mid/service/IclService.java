package id.co.bni.mid.service;

import id.co.bni.mid.validator.CardValidator;
import id.co.bni.mid.validator.IclValidator;

public interface IclService {

    String iclTemp(IclValidator body, String reqAddr) throws Exception;

    String iclPerm(IclValidator body, String reqAddr) throws Exception;
}
