import id.co.bni.cgt.lib.CgtEncryption;
import org.json.JSONObject;

public class TestEncrypt {

    public static void main(String[] args) throws Exception {
        CgtEncryption encryption = new CgtEncryption("config/iso.p12", "kX9Fkov2NEawraVmPOgahzRcz5GnWZljIDdBvsZksc/TNYhGTskj5xZLJS+MDYaQJdFLdVhW9f4bKVExwhnEI+sSZ54Hd/c4jRcCT5YEBbRL2d8YDkc0TpnyU8v2vkBTbG02H2MQD6rFkWOYhqmXRI/Nr1w+tfa3AbTBAx7A1V68UI0X");
        encryption.loadKey();
        JSONObject object = new JSONObject();
        object.put("cardNumber", "5100550100012014");
//        object.put("expiry", "2504");
//        object.put("cardNumber", "5426400030010091");
        object.put("pin", "923506");

        String encode = encryption.encode(object.toString());
        System.out.println("Encode: " + encode);
    }
}
