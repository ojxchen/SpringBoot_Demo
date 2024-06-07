package com.ojxchen.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerificationCodeUtil {

    public static String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // 生成0到9之间的随机数字
        }
        return code.toString();
    }

}
