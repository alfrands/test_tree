package id.co.bni.mid.validator;

public class TerminalIdValidator {
    private String terminalId;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    @Override
    public String toString() {
        return "{" +
                "terminalId='" + terminalId + '\'' +
                '}';
    }
}
