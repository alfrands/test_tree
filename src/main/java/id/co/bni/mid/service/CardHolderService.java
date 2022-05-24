package id.co.bni.mid.service;

import id.co.bni.mid.validator.CPCRDValidator;
import id.co.bni.mid.validator.CardNumberValidator;

public interface CardHolderService {

    String syncLimit(CPCRDValidator body, String reqAddr) throws Exception;

    String checkDueDate(CPCRDValidator body, String reqAddr) throws Exception;

    String checkLastPayment(CPCRDValidator body, String reqAddr) throws Exception;

}
