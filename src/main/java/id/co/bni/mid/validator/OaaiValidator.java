package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class OaaiValidator {

    private String cardNumber;
    private String merchantOrg;
    private String merchantNbr;
    private String amount;
    private String expiryDate;
    private String signOnName;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getMerchantOrg() {
        return merchantOrg;
    }

    public void setMerchantOrg(String merchantOrg) {
        this.merchantOrg = merchantOrg;
    }

    public String getMerchantNbr() {
        return merchantNbr;
    }

    public void setMerchantNbr(String merchantNbr) {
        this.merchantNbr = merchantNbr;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSignOnName() {
        return signOnName;
    }

    public void setSignOnName(String signOnName) {
        this.signOnName = signOnName;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + StringHelpers.maskCardnumber(cardNumber) + '\'' +
                ", merchantOrg='" + merchantOrg + '\'' +
                ", merchantNbr='" + merchantNbr + '\'' +
                ", amount='" + amount + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", signOnName='" + signOnName + '\'' +
                '}';
    }
}
