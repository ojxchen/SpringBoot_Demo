package com.ojxchen.util;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class MD5Utils {

    public static String encrypt(String input) {
        try {
            // 创建 MessageDigest 实例并指定使用 MD5 算法
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用指定的 byte 数组更新摘要
            md.update(input.getBytes());
            // 计算摘要
            byte[] digest = md.digest();
            // 将摘要转换为十六进制字符串
            BigInteger bigInt = new BigInteger(1, digest);
            String result = bigInt.toString(16);
            // 如果结果长度不够 32 位，前面补零
            while (result.length() < 32) {
                result = "0" + result;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
