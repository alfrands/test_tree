package id.co.bni.mid.service;

import id.co.bni.mid.validator.BlockCardValidator;
import id.co.bni.mid.validator.CardValidator;

public interface CardMaintenanceService {

    String cardBlock(BlockCardValidator body, String reqAddr) throws Exception;

    String cardActivate(CardValidator body, String reqAddr) throws Exception;

    String cardFlagging(CardValidator body, String reqAddr) throws Exception;

    String cardUnblock(CardValidator body, String reqAddr) throws Exception;
}
