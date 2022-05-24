import id.co.bni.cgt.lib.CgtEncryption;
import org.json.JSONObject;

public class TestDecrypt {

    public static void main(String[] args) throws Exception {
        CgtEncryption encryption = new CgtEncryption("config/iso.p12", "kX9Fkov2NEawraVmPOgahzRcz5GnWZljIDdBvsZksc/TNYhGTskj5xZLJS+MDYaQJdFLdVhW9f4bKVExwhnEI+sSZ54Hd/c4jRcCT5YEBbRL2d8YDkc0TpnyU8v2vkBTbG02H2MQD6rFkWOYhqmXRI/Nr1w+tfa3AbTBAx7A1V68UI0X");
        encryption.loadKey();
        String encrypted = "96F5+G8jlNXY5Hn1BcCBMkjBIfpBpCHZNn3VG2Ol62CuUTvk81rO04M5Be8ZpQXMIUErD8xUGTwyC7a0kTMX7rumF0MPAKSGbJw7pSuviZaIV5XEUXI3WUInx6n1nJ9xyqzDXiJzjUa3Ej/kOU34Ll/Rg5VE/DgUaAjl6y7Dxo8=";

        String decode = encryption.decode(encrypted);
        System.out.println("Decode: " + decode);
    }
}
