package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class ChangePinValidator {

    private String cardNumber;
    private String oldpin;
    private String newpin;
    private String channelID;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getOldpin() {
        return oldpin;
    }

    public void setOldpin(String oldpin) {
        this.oldpin = oldpin;
    }

    public String getNewpin() {
        return newpin;
    }

    public void setNewpin(String newpin) {
        this.newpin = newpin;
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
                "cardNumber='" + StringHelpers.maskCardnumber(cardNumber) + '\'' +
                ", oldpin='" + StringHelpers.maskPin(oldpin) + '\'' +
                ", newpin='" + StringHelpers.maskPin(newpin) + '\'' +
                ", channelID='" + channelID + '\'' +
                '}';
    }
}
