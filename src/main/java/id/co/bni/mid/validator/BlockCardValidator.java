package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class BlockCardValidator extends CardValidator{

    private String reasonCode;

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + StringHelpers.maskCardnumber(getCardNumber()) + '\'' +
                ", expiryDate='" + getExpiryDate() + '\'' +
                ", dob='" + getDob() + '\'' +
                "reasonCode='" + reasonCode + '\'' +
                '}';
    }
}
