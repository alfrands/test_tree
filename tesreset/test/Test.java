import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static id.co.bni.mid.helpers.StringHelpers.getLastDayinMonth;

public class Test {

    public static void main(String[] args) {
//        testDates();
        testEncode();
//        testParseTime();
//        testPadding();
    }

    static void testPadding() {
        String amount = "10000";
        if (amount.length() < 13) {
            amount = StringUtils.leftPad(amount, 13, "0");
        }
        System.out.println(amount);
    }

    static void testParseTime() {
        String time = "5915";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("mmHH");
            sdf.setLenient(false);
            Date tm = sdf.parse(time);
            String newTime = new SimpleDateFormat("HHmm").format(tm);
            System.out.println(newTime);
        } catch (Exception e) {
        }
    }

    static void testEncode() {
        String url = "http://this.url.com/api";
        org.apache.catalina.util.URLEncoder encoder = new org.apache.catalina.util.URLEncoder();
        String emptyspaces = "  ";
        String nonascii = "儿子去哪";
        System.out.println(encoder.encode(emptyspaces, StandardCharsets.UTF_8));
        System.out.println(encoder.encode(nonascii, StandardCharsets.UTF_8));

        String param1 = "this is a param with space";
        String param2 = "params non ascii 儿子去哪";
        String param3 = "params with /";
        String param4 = "param chars !@#$%^&*(),;";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("p1", param1)
                .queryParam("p2", param2)
                .queryParam("p3", param3)
                .queryParam("p4", param4);

        URI uri = builder.build().encode().toUri();
        System.out.println(uri.toString());
        System.out.println(uri.toASCIIString());
    }

    static void testDates() {
        String dateValue = "20211015";
        String format = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        Date date = null;
        Date dtNow = null;
        try {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            dtNow = now.getTime();

            date = sdf.parse(dateValue);
            Calendar calValue = Calendar.getInstance();
            calValue.setTime(date);
            calValue.set(Calendar.HOUR, 0);
            calValue.set(Calendar.MINUTE, 0);
            calValue.set(Calendar.SECOND, 0);
            date = calValue.getTime();

            if (date.after(dtNow)) {
                System.out.println("date after");
            } else {
                System.out.println("date not after " + date);
            }
        } catch (ParseException ex) {

        }
    }
}
