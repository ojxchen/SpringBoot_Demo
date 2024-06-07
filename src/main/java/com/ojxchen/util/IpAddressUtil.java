package com.ojxchen.util;

import com.ojxchen.service.Impl.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class IpAddressUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "0:0:0:0:0:0:0:1";
    private static final String SEPARATOR = ",";
    private static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";
    private static final String HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    public static String getRealIpAddress(HttpServletRequest request) {


        String ipAddress;
        try {
            // 1.根据常见的代理服务器转发的请求ip存放协议，从请求头获取原始请求ip。值类似于203.98.182.163, 203.98.182.163
            ipAddress = request.getHeader(HEADER_X_FORWARDED_FOR);
            if (isIpAddressInvalid(ipAddress)) {
                ipAddress = request.getHeader(HEADER_PROXY_CLIENT_IP);
            }
            if (isIpAddressInvalid(ipAddress)) {
                ipAddress = request.getHeader(HEADER_WL_PROXY_CLIENT_IP);
            }

            // 2.如果没有转发的ip，则取当前通信的请求端的ip
            if (isIpAddressInvalid(ipAddress)) {
                ipAddress = request.getRemoteAddr();

                // 如果是127.0.0.1，则取本地真实ip
                if (LOCALHOST.equals(ipAddress)) {
                    try {
                        ipAddress = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException e) {
                        LOG.error("获取本地IP地址失败", e);
                    }
                }
            }

            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***"
            if (ipAddress != null) {
                ipAddress = ipAddress.split(SEPARATOR)[0].trim();
            }
        } catch (Exception e) {
            LOG.error("解析请求IP失败", e);
            ipAddress = "";
        }
        return ipAddress == null ? "" : ipAddress;
    }

    private static boolean isIpAddressInvalid(String ipAddress) {
        return ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress);
    }
}
