package me.fumiz.test.nano.lib;

import junit.framework.TestCase;
import me.fumiz.nano.NanoHTTPd;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Properties;

/**
 * Server function TestCase
 * User: fumiz
 * Date: 11/09/23
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
public class NanoHTTPdTestCase extends TestCase {
    protected interface TestRequestHandler {
        public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files);
    }
    private class TestServer extends NanoHTTPd {
        private TestRequestHandler mRequestHandler;

        public TestServer(int port) throws IOException {
            super(port);
        }

        public void setRequestHandler(TestRequestHandler requestHandler) {
            mRequestHandler = requestHandler;
        }

        @Override
        public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
            if (mRequestHandler == null) {
                throw new RuntimeException("missing handler");
            }
            return mRequestHandler.onRequest(uri, method, header, parms, files);
        }
    }

    private static final int DEFAULT_PORT_NUMBER = 9821;
    private static final String DEFAULT_LOCALHOST = "http://127.0.0.1";
    private TestServer mServer;
    private int mPortNumber;

    public NanoHTTPdTestCase(int portNumber) {
        mPortNumber = portNumber;
    }
    public NanoHTTPdTestCase() {
        this(DEFAULT_PORT_NUMBER);
    }

    @Override
    public void setUp() throws IOException {
        mServer = new TestServer(mPortNumber);
    }
    
    @Override
    public void tearDown() {
        mServer.stop();
    }

    protected int getPortNumber() {
        return mPortNumber;
    }

    // handler
    protected void setRequestHandler(TestRequestHandler handler) {
        mServer.setRequestHandler(handler);
    }

    /**
     * Get URL for test
     * @param path absolute path from server document root (ex /static/page.html translate to http://127.0.0.1:PORT/static/page.html)
     * @return url to test server
     */
    protected String getTestUrl(String path) {
        return String.format("%s:%d%s", DEFAULT_LOCALHOST, DEFAULT_PORT_NUMBER, path);
    }

    protected NanoHTTPd.Response createSimpleResponse(String responseText) {
        return new NanoHTTPd.Response(NanoHTTPd.HTTP_OK, NanoHTTPd.MIME_PLAINTEXT, responseText);
    }

    protected <T> T execute(TestRequestHandler handler, org.apache.http.client.methods.HttpUriRequest httpUriRequest, org.apache.http.client.ResponseHandler<? extends T> responseHandler) throws java.io.IOException {
        setRequestHandler(handler);
        HttpClient client = new DefaultHttpClient();
        T ret = client.execute(httpUriRequest, responseHandler);
        client.getConnectionManager().shutdown();
        setRequestHandler(null);
        return ret;
    }

    protected <T> T execute(TestRequestHandler handler, org.apache.http.client.methods.HttpUriRequest httpUriRequest, org.apache.http.client.ResponseHandler<? extends T> responseHandler, org.apache.http.protocol.HttpContext httpContext) throws java.io.IOException {
        setRequestHandler(handler);
        HttpClient client = new DefaultHttpClient();
        T ret = client.execute(httpUriRequest, responseHandler, httpContext);
        client.getConnectionManager().shutdown();
        setRequestHandler(null);
        return ret;
    }

    protected <T> T execute(TestRequestHandler handler, org.apache.http.HttpHost httpHost, org.apache.http.HttpRequest httpRequest, org.apache.http.client.ResponseHandler<? extends T> responseHandler) throws java.io.IOException {
        setRequestHandler(handler);
        HttpClient client = new DefaultHttpClient();
        T ret = client.execute(httpHost, httpRequest, responseHandler);
        client.getConnectionManager().shutdown();
        setRequestHandler(null);
        return ret;
    }

    protected <T> T execute(org.apache.http.HttpHost httpHost, org.apache.http.HttpRequest httpRequest, org.apache.http.client.ResponseHandler<? extends T> responseHandler, org.apache.http.protocol.HttpContext httpContext) throws java.io.IOException {
        HttpClient client = new DefaultHttpClient();
        T ret = client.execute(httpHost, httpRequest, responseHandler, httpContext);
        client.getConnectionManager().shutdown();
        setRequestHandler(null);
        return ret;
    }
}
