package com.study.isitcold.config.auth.oauth;

import com.study.isitcold.config.auth.PrincipalDetails;
import com.study.isitcold.config.auth.oauth.provider.GoogleUserInfo;
import com.study.isitcold.config.auth.oauth.provider.NaverUserInfo;
import com.study.isitcold.config.auth.oauth.provider.OAuth2UserInfo;
import com.study.isitcold.model.User;
import com.study.isitcold.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public PrincipalOauth2UserService(@Lazy BCryptPasswordEncoder bcryptPasswordEncoder){
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;

        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        }
        else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oauth2User.getAttributes().get("response"));
        }
        else {
            System.out.println("우리는 구글과 페이스북이랑 네이버만 지원함");
        }
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String password = bcryptPasswordEncoder.encode("겟엔데머");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if(userEntity == null){
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }
        else {
            System.out.println("중복");
        }
        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}
