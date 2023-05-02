package ru.wolves.bookingsite.security.socialOauth2.socialUserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import ru.wolves.bookingsite.repositories.PersonRepo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class YandexOAuth2UserInfo extends OAuth2UserInfo{

    private PersonRepo personRepo;

    public YandexOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("client_id");
    }

    @Override
    public String getFirstName() {
        return (String) attributes.get("first_name");
    }

    @Override
    public String getLastName() {
        return (String) attributes.get("last_name");
    }

    @Override
    public String getMiddleName() {
        return null;
    }

    @Override
    public String getPhoneNumber() {
        LinkedHashMap defaultPhone = (LinkedHashMap) attributes.get("default_phone");
        defaultPhone.get("default_phone");
        return (String) defaultPhone.get("number");
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
