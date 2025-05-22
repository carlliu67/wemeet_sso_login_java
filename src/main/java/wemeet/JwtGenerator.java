package wemeet;

import io.jsonwebtoken.Jwts;
import java.security.PrivateKey;
import java.util.Date;

public class JwtGenerator {
    public static String generateIdToken(String userId, long expiredSeconds, String pemFilePath) throws Exception {
        PrivateKey privateKey = PemUtils.loadPrivateKey(pemFilePath);

        long currentTimeMillis = System.currentTimeMillis();
        Date iat = new Date(currentTimeMillis);
        Date exp = new Date(currentTimeMillis + expiredSeconds * 1000);

        return Jwts.builder()
                // Header设置
                .header()
                .add("typ", "JWT")
                .and()
                // Payload设置
                .claims()
                .subject(userId)
                .issuer(Config.issuer)
                .issuedAt(iat)
                .expiration(exp)
                .and()
                // 签名算法
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
}