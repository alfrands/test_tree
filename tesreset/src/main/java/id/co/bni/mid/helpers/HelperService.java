package id.co.bni.mid.helpers;

import id.co.bni.mid.model.ResponseDictionary;
import id.co.bni.mid.model.StatusKartuDictionary;
import org.apache.catalina.util.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static id.co.bni.mid.helpers.StringHelpers.getLastDayinMonth;

@Component
public class HelperService {

    @Autowired
    List<ResponseDictionary> responseDictionaryList;

    @Autowired
    List<StatusKartuDictionary> statusKartuDictionaryList;

    public String getResponseMessage(String respCode) {
        for (ResponseDictionary response : responseDictionaryList) {
            if (response.getRespCode().equals(respCode)) {
                return response.getRespMessage();
            }
        }
        return "";
    }

    public StatusKartuDictionary mappingStatusKartu(String blockCode) {
        for (StatusKartuDictionary status : statusKartuDictionaryList) {
            if (status.getBlockCode().equals(blockCode)) {
                return status;
            }
        }
        return null;
    }

    public int random(int from, int until) {
        if (from >= until)
            throw new IllegalArgumentException("until must be greater than from");
        return new Random().nextInt((until - from) + 1) + from;
    }

    public String encodeURL(String url) {
        URLEncoder encoder = new URLEncoder();
        return encoder.encode(url, StandardCharsets.UTF_8);
    }

    public SimpleDateFormat getSimpleDateFormat(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        return sdf;
    }

    public String parseDate(String date, String inputFormat, String outputFormat) {
        try {
            Date dt = getSimpleDateFormat(inputFormat).parse(date);
            return getSimpleDateFormat(outputFormat).format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isExpiryDateValid(String dateValue) {
        Date date = null;
        try {
            date = getSimpleDateFormat("yyMM").parse(dateValue);
            date = getLastDayinMonth(date);
            Date dateNow = new Date();
            if (!date.after(dateNow)) {
                date = null;
            }
        } catch (ParseException ex) {

        }
        return date != null;
    }

    public boolean dateIsAfterDate(String date, String dateAfter, String format) {
        try {
            Date date1 = getSimpleDateFormat(format).parse(date);
            Calendar calValue = Calendar.getInstance();
            calValue.setTime(date1);
            calValue.set(Calendar.HOUR, 0);
            calValue.set(Calendar.MINUTE, 0);
            calValue.set(Calendar.SECOND, 0);
            date1 = calValue.getTime();

            Date date2 = getSimpleDateFormat(format).parse(dateAfter);
            Calendar calValue2 = Calendar.getInstance();
            calValue2.setTime(date2);
            calValue2.set(Calendar.HOUR, 0);
            calValue2.set(Calendar.MINUTE, 0);
            calValue2.set(Calendar.SECOND, 0);
            date2 = calValue2.getTime();

            return date1.after(date2);
        } catch (ParseException ex) {
            return false;
        }
    }

    public String julianToDateString(String julianDay, String format) {
        try {
            DateFormat dateFormat = getSimpleDateFormat("yyyyDDD");
            Date date = dateFormat.parse(julianDay);
            DateFormat output = getSimpleDateFormat(format);
            return output.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
