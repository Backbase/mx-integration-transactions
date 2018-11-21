package com.backbase.presentation.transaction.service;

import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("it")
public class SystemPropertyConfigurer {

    @PostConstruct
    public void setProperty(){
        System.setProperty("SIG_SECRET_KEY", "JWTSecretKeyDontUseInProduction!");
    }
}