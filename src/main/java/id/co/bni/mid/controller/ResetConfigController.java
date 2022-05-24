package id.co.bni.mid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResetConfigController {

    @Autowired
    DefaultListableBeanFactory beanFactory;

    @GetMapping(path = "/api/reset")
    public ResponseEntity reset(){
        beanFactory.destroySingleton("responseDictionaryList");
        beanFactory.destroySingleton("statusKartuDictionaryList");

        return ResponseEntity.ok("config reset success");
    }
}
