package id.co.bni.mid.service;

import id.co.bni.mid.validator.TerminalIdValidator;

public interface CheckTerminalService {
    String checkTerminal(TerminalIdValidator body, String reqAddr) throws Exception;
}
