package id.co.bni.mid.validator;

public class IclValidator {

    private String cardNumber;
    private String amount;
    private String effDate;
    private String tempExpDate;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEffDate() {
        return effDate;
    }

    public void setEffDate(String effDate) {
        this.effDate = effDate;
    }

    public String getTempExpDate() {
        return tempExpDate;
    }

    public void setTempExpDate(String tempExpDate) {
        this.tempExpDate = tempExpDate;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + cardNumber + '\'' +
                ", amount='" + amount + '\'' +
                ", effDate='" + effDate + '\'' +
                ", expDate='" + tempExpDate + '\'' +
                '}';
    }
}
