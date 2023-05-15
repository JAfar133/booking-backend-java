package ru.wolves.bookingsite.security.socialOauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.wolves.bookingsite.repositories.PersonRepo;
import ru.wolves.bookingsite.security.CookieUtils;
import ru.wolves.bookingsite.security.JwtService;
import ru.wolves.bookingsite.security.PersonDetails;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static ru.wolves.bookingsite.security.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final PersonRepo personRepo;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(JwtService jwtService, PersonRepo personRepo) {
        this.jwtService = jwtService;
        this.personRepo = personRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
//            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String targetUrl = "/auth/redirect";


        PersonDetails person = (PersonDetails) authentication.getPrincipal();
        var jwtToken = jwtService.generateToken(person.getPerson());
        var refreshToken = jwtService.generateRefreshToken(person.getPerson());

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("access_token", jwtToken)
                .queryParam("refresh_token", refreshToken)
                .build().toUriString();
    }

}

