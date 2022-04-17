package com.halobin.community;

import com.halobin.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveFilterTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "赌博吸毒嫖娼嫖娼不可以";
        System.out.println(text.length());
        System.out.println(sensitiveFilter.filterText(text));
    }
}
