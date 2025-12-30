package com.homestay.interceptor;

import com.homestay.common.exception.BusinessException;
import com.homestay.common.util.JwtUtil;
import com.homestay.context.UserContext;
import com.homestay.context.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "请先登录");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String userType = jwtUtil.getUserType(token);
        String role = jwtUtil.getRole(token);

        UserContext context = new UserContext();
        context.setUserId(userId);
        context.setUsername(username);
        context.setUserType(userType);
        context.setRole(role);
        UserContextHolder.setContext(context);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.clear();
    }
}
