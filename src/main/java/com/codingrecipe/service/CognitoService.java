package com.codingrecipe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CognitoService {

    private static final String USER_POOL_ID = "ap-northeast-2_Ip4oaQ2c6";
    private static final String CLIENT_ID = "your_app_client_id"; // 사용자 풀 생성 시 생성된 앱 클라이언트 ID
    private static final String REGION = "ap-northeast-2";

    public Map<String, Object> initiateAuth(String username, String password) throws Exception {
        String url = "https://cognito-idp." + REGION + ".amazonaws.com/";
        HttpPost post = new HttpPost(url);

        Map<String, Object> params = new HashMap<>();
        params.put("AuthFlow", "USER_PASSWORD_AUTH");
        params.put("ClientId", CLIENT_ID);
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", username);
        authParams.put("PASSWORD", password);
        params.put("AuthParameters", authParams);

        post.setHeader("Content-Type", "application/json");
        post.setHeader("X-Amz-Target", "AWSCognitoIdentityProviderService.InitiateAuth");
        post.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(params)));

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            String responseBody = EntityUtils.toString(response.getEntity());
            return new ObjectMapper().readValue(responseBody, Map.class);
        }
    }

    public Map<String, Object> respondToAuthChallenge(String username, String sessionToken, String otpCode) throws Exception {
        String url = "https://cognito-idp." + REGION + ".amazonaws.com/";
        HttpPost post = new HttpPost(url);

        Map<String, Object> params = new HashMap<>();
        params.put("ChallengeName", "SMS_MFA");
        params.put("ClientId", CLIENT_ID);
        params.put("Session", sessionToken);
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", username);
        authParams.put("SMS_MFA_CODE", otpCode);
        params.put("ChallengeResponses", authParams);

        post.setHeader("Content-Type", "application/json");
        post.setHeader("X-Amz-Target", "AWSCognitoIdentityProviderService.RespondToAuthChallenge");
        post.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(params)));

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            String responseBody = EntityUtils.toString(response.getEntity());
            return new ObjectMapper().readValue(responseBody, Map.class);
        }
    }
}
