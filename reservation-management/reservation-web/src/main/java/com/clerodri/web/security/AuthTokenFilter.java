package com.clerodri.web.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private String getTokenFromRequest(HttpServletRequest request){
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(jwtToken) && jwtToken.startsWith("Bearer")
                && !Objects.equals(jwtToken.length(),6)){
            return jwtToken.substring(7);
        }
        log.info("AuthTokenFilter - Bearer token is NULL ");
        return null;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("AuthTokenFilter - Validating token in request!");

        String jwtToken = getTokenFromRequest(request);

        if(!Objects.equals(jwtToken,null)){

            try {
                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
                String username = jwtUtils.extractUsername(decodedJWT);
                String authorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();
                Collection<GrantedAuthority> authorityList = AuthorityUtils
                        .commaSeparatedStringToAuthorityList(authorities);

                var newContext = SecurityContextHolder.createEmptyContext();

                Authentication authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorityList);

                newContext.setAuthentication(authToken);

                SecurityContextHolder.setContext(newContext);
                log.info("AuthTokenFilter - token is valid");

            } catch (Exception e) {
                log.info("AuthTokenFilter - token isn't valid!");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Type","text/plain;charset=UTF-8");
                response.getWriter().write("You need an valid Bearer token for authenticate");
                return;
            }
        }

        filterChain.doFilter(request,response);
    }
}