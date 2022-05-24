package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class TransactionValidator extends ValidateValidator{

    private String pin;
    private String termId;
    private String mid;
    private String merchantName;
    private String amount;
    private String channelID;

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + StringHelpers.maskCardnumber(getCardNumber()) + '\'' +
                ", expiryDate='" + getExpiryDate() + '\'' +
                ", dob='" + getDob() + '\'' +
                ", pin='" + pin + '\'' +
                ", termId='" + termId + '\'' +
                ", mid='" + mid + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", amount='" + amount + '\'' +
                ", channelID='" + channelID + '\'' +
                '}';
    }

}
