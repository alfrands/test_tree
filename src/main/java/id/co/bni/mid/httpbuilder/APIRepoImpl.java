package id.co.bni.mid.httpbuilder;

import id.co.bni.mid.config.Config;
import id.co.bni.mid.helpers.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class APIRepoImpl implements APIRepository {

    @Autowired
    Config env;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public ResponseEntity<String> apiValidate(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiInquiry(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiEcoll(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiCardBlock(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiCardActivate(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiOaslogA(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiOaslogB(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiChangePin(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiCardFlagging(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiBnka(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiOAAI(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiICL(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiEdclogA(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiEdclogB(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiSyncLimit(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiCheckDueDate(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiCheckTerminal(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiCheckLastPayment(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiTransactionCC(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiInstantCard(URI uri) {
        return sendDataAuth(uri);
    }

    @Override
    public ResponseEntity<String> apiUnblockCard(URI uri) {
        return sendDataAuth(uri);
    }

    private ResponseEntity<String> sendDataGetAPICardlink(String urlBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(urlBuilder, HttpMethod.GET, request, String.class);
        return responseEntity;
    }

    private ResponseEntity<String> sendDataGetAPICardlink(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
        return responseEntity;
    }

    private ResponseEntity<String> sendDataAuth(URI uri) {
        HttpHeaders headers = new HttpHeaders();

        String userPass = env.getProperty("apicl.user") + ":" + env.getProperty("apicl.pwd");
        String encoded = Base64.encodeToString(userPass.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);

        headers.add("Authorization", "Basic " + encoded);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
        return responseEntity;
    }
}
