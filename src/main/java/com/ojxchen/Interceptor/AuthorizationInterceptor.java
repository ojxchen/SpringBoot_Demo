package com.ojxchen.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.util.StringUtils;
import com.ojxchen.util.JwtUtil;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    JwtUtil jwtUtil = new JwtUtil();

    JedisPool jedisPool = new JedisPool();

    Jedis jedis = jedisPool.getResource();

    String token = "";
    String roles = "";
    String username = "";


    @Override
    public boolean preHandle(HttpServletRequest httpRequest, HttpServletResponse response, Object handler)
            throws Exception{


        if (HttpMethod.OPTIONS.toString().equals(httpRequest.getMethod())) {
            System.out.println("OPTIONS请求，放行");
            return true;
        }

        // 获取请求中的 JWT Token
        try {
            token  = httpRequest.getHeader("Authorization").replace("Bearer ","");
            roles  = httpRequest.getHeader("Roles");
            username = jwtUtil.getUsernameFromRequest(httpRequest);
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if(StringUtils.isEmpty(username)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String s = jedis.get(username);

        if(!StringUtils.isEmpty(token) && !token.equals(s)){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        //验证token是否过期
        if(!StringUtils.isEmpty(token) && jwtUtil.isTokenExpired(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if(!"Admin".equals(roles)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // 在请求处理之后，视图渲染之前进行拦截
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // 在请求完成之后进行拦截，通常用于资源清理等操作
    }
}
