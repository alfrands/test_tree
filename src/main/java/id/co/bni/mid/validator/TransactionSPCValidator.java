package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class TransactionSPCValidator extends CardValidator{

    private String termId;
    private String mid;
    private String merchantName;
    private String amount;
    private String fee;
    private String utilCode;
    private String regionCode;
    private String channelID;

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

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getUtilCode() {
        return utilCode;
    }

    public void setUtilCode(String utilCode) {
        this.utilCode = utilCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
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
                ", termId='" + termId + '\'' +
                ", mid='" + mid + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", amount='" + amount + '\'' +
                ", fee='" + fee + '\'' +
                ", utilCode='" + utilCode + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", channelID='" + channelID + '\'' +
                '}';
    }

}
