package ru.wolves.bookingsite.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.wolves.bookingsite.exceptions.OAuth2AuthenticationProcessingException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.enums.PersonRole;
import ru.wolves.bookingsite.repositories.PersonRepo;
import ru.wolves.bookingsite.security.socialOauth2.AuthProvider;
import ru.wolves.bookingsite.security.socialOauth2.socialUserInfo.OAuth2UserInfo;
import ru.wolves.bookingsite.security.socialOauth2.socialUserInfo.OAuth2UserInfoFactory;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final PersonRepo personRepo;

    @Autowired
    public CustomOAuth2UserService(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) throws OAuth2AuthenticationProcessingException, PersonNotFoundException {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<Person> personOptional = personRepo.findByEmail(oAuth2UserInfo.getEmail());
        Person person;
        if(personOptional.isPresent()) {
            person = personOptional.get();
            if (!person.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        person.getProvider() + " account. Please use your " + person.getProvider() +
                        " account to login.");
            }
            person = updateExistingUser(person, oAuth2UserInfo);
        } else {
            person = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        PersonDetails personDetails = new PersonDetails(person);
        personDetails.setAttributes(oAuth2User.getAttributes());
        return personDetails;
    }

    private Person registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Person person = new Person();
            person.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
            person.setProviderId(oAuth2UserInfo.getId());
            person.setFirstName(oAuth2UserInfo.getFirstName());
            person.setLastName(oAuth2UserInfo.getLastName());
            person.setMiddleName(oAuth2UserInfo.getMiddleName());
            if(oAuth2UserInfo.getPhoneNumber()!=null && !personRepo.findByPhoneNumber(oAuth2UserInfo.getPhoneNumber()).isPresent()){
                person.setPhoneNumber(oAuth2UserInfo.getPhoneNumber());
            }
            if(oAuth2UserInfo.getEmail()!=null && !personRepo.findByEmail(oAuth2UserInfo.getEmail()).isPresent()){
                person.setEmail(oAuth2UserInfo.getEmail());
            }
            person.setRole(PersonRole.USER);
        return personRepo.save(person);

    }

    private Person updateExistingUser(Person existingPerson, OAuth2UserInfo oAuth2UserInfo) {
        if(oAuth2UserInfo.getFirstName() != null && existingPerson.getFirstName() == null)
            existingPerson.setFirstName(oAuth2UserInfo.getFirstName());
        if(oAuth2UserInfo.getLastName() != null && existingPerson.getLastName() == null)
            existingPerson.setLastName(oAuth2UserInfo.getLastName());
        if(oAuth2UserInfo.getMiddleName() != null && existingPerson.getMiddleName() == null)
            existingPerson.setMiddleName(oAuth2UserInfo.getMiddleName());
        if(oAuth2UserInfo.getPhoneNumber() != null && existingPerson.getPhoneNumber() == null)
            existingPerson.setPhoneNumber(oAuth2UserInfo.getPhoneNumber());

        return personRepo.save(existingPerson);
    }
}
