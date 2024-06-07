package com.ojxchen;

import com.ojxchen.util.EmailUtil;
import com.ojxchen.util.VerificationCodeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailUtilTest {

    private EmailUtil emailUtil = new EmailUtil();

    @Test
    public void sendEmailCode(){
        String code = VerificationCodeUtil.generateVerificationCode();

        emailUtil.sendEmail("1758754717@qq.com",code);
    }


}
