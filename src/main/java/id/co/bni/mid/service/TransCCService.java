package id.co.bni.mid.service;

import id.co.bni.mid.validator.TransactionSPCValidator;
import id.co.bni.mid.validator.TransactionValidator;

public interface TransCCService {

    String transactionCC(TransactionValidator body, String reqAddr) throws Exception;

    String transactionCCSPC(TransactionSPCValidator body, String reqAddr) throws Exception;

}
