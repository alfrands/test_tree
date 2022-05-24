package id.co.bni.mid.service;

import id.co.bni.mid.validator.TerminalIdValidator;

public interface CheckEdclogService {
    String edclogA(TerminalIdValidator body, String reqAddr) throws Exception;

    String edclogB(TerminalIdValidator body, String reqAddr) throws Exception;
}
