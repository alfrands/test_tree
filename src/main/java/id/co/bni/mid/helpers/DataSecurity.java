package id.co.bni.mid.helpers;

import id.co.bni.mid.config.Config;
import id.co.bni.cgt.lib.CgtEncryption;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataSecurity {

    @Autowired
    Config env;

    private CgtEncryption encryption;

    @PostConstruct
    public void init(){
        this.encryption = new CgtEncryption(env.getProperty("security.cert"), env.getProperty("security.secret"));
        this.encryption.loadKey();
    }

    public JSONObject decode(String str) {
        return new JSONObject(this.encryption.decode(str));
    }

    public String encode(String data) {
        return this.encryption.encode(data);
    }
}
