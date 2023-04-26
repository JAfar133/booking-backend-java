package ru.wolves.bookingsite.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wolves.bookingsite.repositories.TokenRepo;

@Service
@Transactional(readOnly = true)
public class logoutService implements LogoutHandler {

    private final TokenRepo tokenRepo;

    @Autowired
    public logoutService(TokenRepo tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        if(authHeader == null || !authHeader.startsWith("Bearer ")){
//            return;
//        }
//        jwt = authHeader.substring(7);
//        var storedToken = tokenRepo.findByToken(jwt)
//                .orElse(null);
//        if(storedToken != null){
//            storedToken.setExpired(true);
//            storedToken.setRevoked(true);
//            tokenRepo.save(storedToken);
//        }
    }
}
