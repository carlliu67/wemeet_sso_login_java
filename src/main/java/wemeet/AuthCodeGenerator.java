package wemeet;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AuthCodeGenerator {
    private static final int MAX_REDIRECTS = 10;  // 防止无限重定向
    private static final String TARGET_PARAM = "sso_auth_code";

    public static String getAuthCode(String initialUrl) throws Exception {
        BasicCookieStore cookieStore = new BasicCookieStore();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .disableRedirectHandling()  // 禁用自动重定向
                .build()) {

            URI currentUri = new URI(initialUrl);
            for (int redirectCount = 0; redirectCount < MAX_REDIRECTS; redirectCount++) {
                // 打印当前请求URL（包含参数）
                System.out.println("Requesting URL: " + currentUri);

                // 发起请求
                HttpGet request = new HttpGet(currentUri);
                HttpResponse response = httpClient.execute(request);
                EntityUtils.consumeQuietly(response.getEntity());  // 释放资源

                // 参数检查（先检查当前URL是否携带参数）
                String authCode = extractParamFromUrl(currentUri, TARGET_PARAM);
                if (authCode != null) return authCode;

                // 处理重定向
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 302 || statusCode == 303) {
                    currentUri = handleRedirect(response, currentUri);
                } else {
                    return null;  // 非重定向且无目标参数
                }
            }
            throw new Exception("Exceeded maximum redirect attempts");
        }
    }

    private static URI handleRedirect(HttpResponse response, URI currentUri) throws Exception {
        String location = response.getFirstHeader("Location").getValue();
        return new URIBuilder(location).build();  // 自动处理相对路径转绝对路径
    }

    private static String extractParamFromUrl(URI uri, String paramName) {
        Map<String, String> params = parseQueryParams(uri.getQuery());
        return params.getOrDefault(paramName, null);
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null) return params;

        for (String pair : query.split("&")) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                params.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
        }
        return params;
    }
}