package wemeet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.security.SecureRandom;

public class UrlGenerator {
    public static String generateJumpUrl(String authCode, String redirectLink) {
        try {
            String encodedRedirect = URLEncoder.encode(redirectLink, StandardCharsets.UTF_8.name())
                    .replace("+", "%20");  // 将+替换为%20保持URL规范[7](@ref)

            return String.format("https://meeting.tencent.com?sso_auth_code=%s&redirect_link=%s",
                    authCode, encodedRedirect);

        } catch (UnsupportedEncodingException e) {
            // UTF-8编码必然支持，此处仅作异常处理兜底
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    // 生成可重复使用的免登链接
    public static String generateUrl(String prefixUrl, String idToken) {
        // 定义actionString
        String actionString = "{\"action\":\"jump\",\"params\":{\"redirect_url\":\"https://meeting.tencent.com\",\"mode\":\"1\"}}";

        // 1. 对actionString进行URL安全的Base64编码
        String encodedAction = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(actionString.getBytes(StandardCharsets.UTF_8));

        // 2. 拼接URL参数
        StringBuilder urlBuilder = new StringBuilder(prefixUrl);
        urlBuilder.append("?action=").append(encodedAction);
        urlBuilder.append("&id_token=").append(URLEncoder.encode(idToken, StandardCharsets.UTF_8));

        return urlBuilder.toString();
    }

    public static String generateJoinScheme(String meetingCode, String userCode) {
        try {
            // 生成16位十六进制随机字符串
            String launchId = generateSecureHex(16);

            // 对参数进行URL编码
            String encodedMeeting = URLEncoder.encode(meetingCode, StandardCharsets.UTF_8.name());
            String encodedUserCode = URLEncoder.encode(userCode, StandardCharsets.UTF_8.name());

            // 拼接URL参数
            return String.format("wemeet://page/inmeeting?meeting_code=%s&token=&launch_id=%s&user_code=%s",
                    encodedMeeting, launchId, encodedUserCode);
        } catch (Exception e) {
            throw new RuntimeException("URL生成失败", e);
        }
    }

    private static String generateSecureHex(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[length/2]; // 1字节=2十六进制字符
        secureRandom.nextBytes(bytes);

        // 字节转十六进制
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}

