package wemeet;

public class Main {


    public static void main(String[] args) {
        String idToken = null;
        try {
            idToken = JwtGenerator.generateIdToken("zhangsan", 3600, Config.privateKeyFilePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String url = UrlGenerator.generateUrl(Config.prefixUrl, idToken);
        String authCode = null;
        try {
            authCode = AuthCodeGenerator.getAuthCode(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("authCode:" + authCode);

        String joinScheme = UrlGenerator.generateJoinScheme("8739814", authCode);
        System.out.println("joinScheme:" + joinScheme);

//        String jumpUrl = UrlGenerator.generateJumpUrl(authCode, "https://meeting.tencent.com");
//        System.out.println("jumpUrl:" + jumpUrl);

    }

}