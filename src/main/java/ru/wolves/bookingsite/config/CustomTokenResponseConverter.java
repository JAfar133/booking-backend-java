package ru.wolves.bookingsite.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenResponseConverter implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {

    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {
        String accessToken = (String) tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);
        int expiresInInt = (Integer) tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN);
        String expiresIn = String.valueOf(expiresInInt);

        OAuth2AccessToken.TokenType accessTokenType = OAuth2AccessToken.TokenType.BEARER;

        Map<String, Object> additionalParameters = new HashMap<>();

        tokenResponseParameters.forEach((s, s2) -> {
            additionalParameters.put(s, s2);
        });

        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(accessTokenType)
                .expiresIn((long)expiresInInt)
                .additionalParameters(additionalParameters)
                .build();
        System.out.println(tokenResponse.getAccessToken());
        System.out.println(tokenResponse.getAdditionalParameters());
        return tokenResponse;
    }
}