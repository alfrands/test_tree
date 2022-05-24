package id.co.bni.mid.helpers;

import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Date;

public class StringHelpers {

    public static String maskCardnumber(String cardNumber) {
        if (StringUtils.isEmpty(cardNumber)) {
            return cardNumber;
        }
        String front = cardNumber.substring(0,4);
        String end = cardNumber.substring(cardNumber.length() - 4);
        String newCardNumber = front + "********" + end;
        return newCardNumber;
    }

    public static String maskPin(String pin) {
        String newPin = "******";
        return newPin;
    }

    public static Date getLastDayinMonth(Date args) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(args);
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDate);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static String leftPadZeroes(String source, int padLength) {
        return leftPad(source, padLength, "0");
    }

    public static String leftPad(String source, int padLength, String padChar) {
        if (source.length() < padLength) {
            return StringUtils.leftPad(source, padLength, padChar);
        }
        return source;
    }
}
