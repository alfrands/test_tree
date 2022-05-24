package id.co.bni.mid.httpbuilder;

import org.springframework.http.ResponseEntity;

import java.net.URI;

public interface APIRepository {

    /* VALIDATE API */
    ResponseEntity<String> apiValidate(URI uri);

    /* INQUIRY API */
    ResponseEntity<String> apiInquiry(URI uri);

    /* ECOLL API */
    ResponseEntity<String> apiEcoll(URI uri);

    /* CARD BLOCK API */
    ResponseEntity<String> apiCardBlock(URI uri);

    /* CARD ACTIVATE API */
    ResponseEntity<String> apiCardActivate(URI uri);

    /* OASLOGA API */
    ResponseEntity<String> apiOaslogA(URI uri);

    /* OASLOGB API */
    ResponseEntity<String> apiOaslogB(URI uri);

    /* CHANGE PIN */
    ResponseEntity<String> apiChangePin(URI uri);

    /* CARD FLAGGING API */
    ResponseEntity<String> apiCardFlagging(URI uri);

    /* BNKA API */
    ResponseEntity<String> apiBnka(URI uri);

    /* OAAI API */
    ResponseEntity<String> apiOAAI(URI uri);

    /* ICL API */
    ResponseEntity<String> apiICL(URI uri);

    /* EDCLOGA API */
    ResponseEntity<String> apiEdclogA(URI uri);

    /* EDCLOGB API */
    ResponseEntity<String> apiEdclogB(URI uri);

    /* SYNC LIMIT API */
    ResponseEntity<String> apiSyncLimit(URI uri);

    /* CHECK DUE DATE API */
    ResponseEntity<String> apiCheckDueDate(URI uri);

    /* CHECK TERMINAL API */
    ResponseEntity<String> apiCheckTerminal(URI uri);
    
    /* CHECK LAST PAYMENT API */
    ResponseEntity<String> apiCheckLastPayment(URI uri);

    /* TRANSACTION CC */
    ResponseEntity<String> apiTransactionCC(URI uri);

    /* INSTANT CARD */
    ResponseEntity<String> apiInstantCard(URI uri);

    /* UNBLOCK CARD */
    ResponseEntity<String> apiUnblockCard(URI uri);
}
