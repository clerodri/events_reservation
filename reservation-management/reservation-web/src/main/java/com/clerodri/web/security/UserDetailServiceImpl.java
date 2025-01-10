package com.clerodri.web.security;

import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.web.dto.request.RequestLoginDTO;
import com.clerodri.core.exception.NotAuthorizationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl  implements UserDetailsService {


    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    private  final JwtUtils jwtUtils;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User with username:"+username+" not found"));

        // Obtener el rol y convertirlos como simplegrantedAutority.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authority)
                .build();
    }

    public String loginUser(RequestLoginDTO loginDTO){

        Authentication authentication = this.authToken(loginDTO.username(), loginDTO.password());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("USER DETAIL IMPL - TOKEN GENERATED");
        return jwtUtils.createToken(authentication);
    }

    private Authentication authToken(String username, String password) {
        UserDetails user =  this.loadUserByUsername(username);
        if(user == null){
            log.info("USER DETAIL IMPL - USER DETAILS IS NULL");
            throw  new NotAuthorizationException("User:"+username+" is not Authorization");
        }

        if(!passwordEncoder.matches(password, user.getPassword())){
            log.info("USER DETAIL IMPL - PASSWORD INVALID");
            throw  new BadCredentialsException("Invalid password");
        }
        // all good authenticate the user.
        log.info("USER DETAIL IMPL - USER:{}  AUTHENTICATE SUCCESSFULLY", username );
        return new UsernamePasswordAuthenticationToken(username,user.getPassword(),user.getAuthorities());
    }

    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("USER LOGGED -  {}", authentication.getPrincipal().toString());
        return authentication.getPrincipal().toString();

    }


}
