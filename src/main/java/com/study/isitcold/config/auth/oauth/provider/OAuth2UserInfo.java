package com.study.isitcold.config.auth.oauth.provider;

public interface OAuth2UserInfo {
    String getProvider();
    String getProviderId();
    String getName();
    String getEmail();
}
