package ru.wolves.bookingsite.security.socialOauth2.socialUserInfo;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo{
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getFirstName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public String getMiddleName() {
        return null;
    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public String getEmail() {
        String email = (String) attributes.get("email");
        if(email==null){
            email = (String) attributes.get("login") +"@github.com";
        }
        return email;
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
