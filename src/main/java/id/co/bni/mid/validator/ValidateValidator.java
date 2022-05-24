package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class ValidateValidator extends CardValidator{

    private String pin;

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + StringHelpers.maskCardnumber(getCardNumber()) + '\'' +
                ", expiryDate='" + getExpiryDate() + '\'' +
                ", dob='" + getDob() + '\'' +
                ", pin='" + pin + '\'' +
                '}';
    }


}
