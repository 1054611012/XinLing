package com.xinling.framework.security.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xinling.common.core.domain.model.LoginUser;
import com.xinling.common.utils.SecurityUtils;
import com.xinling.common.utils.StringUtils;
import com.xinling.framework.web.service.TokenService;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "xinling.security.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)
                && SecurityUtils.getAuthentication() == null) {

            tokenService.verifyToken(loginUser);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            loginUser,
                            null,
                            loginUser.getAuthorities()
                    );
            authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
