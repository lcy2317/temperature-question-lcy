import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {

    // 最大重试次数
    private static final int MAX_RETRY = 5;

    // 客户端池
    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(1, TimeUnit.MINUTES);

    public HttpClientUtil() {
        // 配置客户端池
        connectionManager.setMaxTotal(100); // 最大连接数
        connectionManager.setDefaultMaxPerRoute(5); // 每个路由最大连接数
    }

    // 定义重试规则
    private final static HttpRequestRetryHandler httpRequestRetryHandler = (exception, executionCount, context) -> {
        if (executionCount >= MAX_RETRY) return false;  // 指定最大重试次数
        if (exception instanceof NoHttpResponseException) return true;
        if (exception instanceof InterruptedIOException) return false;
        if (exception instanceof UnknownHostException) return false;
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        return !(request instanceof HttpEntityEnclosingRequest);
    };

    /**
     * 获取客户端
     */
    public CloseableHttpClient getCloseableHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setRetryHandler(httpRequestRetryHandler)
                .build();
    }
}
