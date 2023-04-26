package ru.wolves.bookingsite.security.socialOauth2.socialUserInfo;

import java.util.Map;

public class YandexOAuth2UserInfo extends OAuth2UserInfo{

    public YandexOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("client_id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("login");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("default_email");
    }

    @Override
    public String getImageUrl() {
        return null;
    }
}
