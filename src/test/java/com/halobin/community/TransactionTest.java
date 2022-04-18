package com.halobin.community;

import com.halobin.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransactionTest {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testTransaction(){
        alphaService.save1();
    }
}
