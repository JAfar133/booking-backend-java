package ru.wolves.bookingsite.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.wolves.bookingsite.repositories.TokenRepo;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final PersonDetailsService personDetailsService;
    private final TokenRepo tokenRepo;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, PersonDetailsService personDetailsService, TokenRepo tokenRepo) {
        this.jwtService = jwtService;
        this.personDetailsService = personDetailsService;
        this.tokenRepo = tokenRepo;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String personEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        jwt = authHeader.substring(7);
        personEmail = jwtService.extractUsername(jwt);

        if(personEmail !=null && SecurityContextHolder.getContext().getAuthentication() == null){
            PersonDetails personDetails = (PersonDetails) this.personDetailsService.loadUserByUsername(personEmail);
//            LOGOUT CODE
//            boolean isTokenValid = tokenRepo.findByToken(jwt)
//                    .map(t -> !t.isExpired() && !t.isRevoked())
//                    .orElse(false);
//            if(jwtService.isTokenValid(jwt, personDetails.getPerson()) && isTokenValid) {
            if(jwtService.isTokenValid(jwt, personDetails.getPerson())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        personDetails, null, personDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
