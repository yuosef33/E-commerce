package com.yuosef.e_commerce.config.JWT;

import com.yuosef.e_commerce.models.User;
import com.yuosef.e_commerce.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.SystemException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenHandler tokenHandler;
    private final UserService userService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;


    private static final List<String> EXACT_PUBLIC_PATHS = List.of(
            "/auth/signup",
            "/auth/Login",
            "/auth/createUser",
            "/auth/refresh-token",
            "/auth/oauth2/callback",
            "/auth/exchange-code",
            "/auth/createUserOtp",
            "/auth/verifyOtp"

    );

    private static final List<String> PREFIX_PUBLIC_PATHS = List.of(
            "/swagger-ui",
            "/v3/api-docs",
            "/h2-console",
            "/actuator",
            "/oauth2",
            "/login/oauth2"

    );

    public TokenFilter(TokenHandler tokenHandler, UserService userService, JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.tokenHandler = tokenHandler;
        this.userService = userService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        String path = request.getServletPath();
        String method = request.getMethod();

        // exact public paths (all methods)
        if (EXACT_PUBLIC_PATHS.contains(path)) return true;

        // prefix public paths (all methods)
        if (PREFIX_PUBLIC_PATHS.stream().anyMatch(path::startsWith)) return true;

        // GET /products and GET /products/{id} are public
        if (method.equals("GET") && path.startsWith("/products")) return true;

        // GET /categories is public
        if (method.equals("GET") && path.startsWith("/categories")) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token=request.getHeader("Authorization");
        if ( token ==null||!token.startsWith("Bearer")){
            response.reset();
            authenticationEntryPoint.commence(request, response,
                    new org.springframework.security.core.AuthenticationException("Invalid token") {});
            return;
        }
        token=token.substring(7);
        String email = tokenHandler.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {

                User user = userService.getUserByEmail(email);
                if (tokenHandler.isTokenValid(token, user)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        } else {
            authenticationEntryPoint.commence(request, response,
                    new org.springframework.security.core.AuthenticationException("Invalid token") {});
            return;
        }

        filterChain.doFilter(request, response);

    }


}
