package me.fumiz.test.nano.cases.functions;

import me.fumiz.nano.NanoHTTPd;
import me.fumiz.test.nano.lib.NanoHTTPdTestCase;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Test to HTTP Request Header
 * User: fumiz
 * Date: 11/09/25
 * Time: 14:36
 */
public class RequestHeaderTest extends NanoHTTPdTestCase {
    /**
     * Send some headers and check server received whole headers with accuracy
     */
    public void testHeader() throws IOException {
        HttpGet request;
        String response;

        request = new HttpGet(getTestUrl("/header/test"));
        request.addHeader("Accept-Language","en-US");
        request.addHeader("Accept-Charset","UTF-8");
        request.addHeader("Authorization","Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
        request.addHeader("Connection","Keep-Alive");
        response = execute(new TestRequestHandler() {
            public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files) {
                assertEquals("/header/test", uri);
                assertEquals("GET", method);
                assertEquals(0,parms.size());
                assertEquals(0,files.size());

                assertEquals("en-US", header.getProperty("Accept-Language", ""));
                assertEquals("UTF-8", header.getProperty("Accept-Charset", ""));
                assertEquals("Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==", header.getProperty("Authorization", ""));
                assertEquals("Keep-Alive", header.getProperty("Connection", ""));

                return createSimpleResponse("");
            }
        }, request, new ResponseHandler<String>() {
            public String handleResponse(HttpResponse httpResponse) throws IOException {
                assertEquals(200, httpResponse.getStatusLine().getStatusCode());
                assertEquals("OK", httpResponse.getStatusLine().getReasonPhrase());
                return EntityUtils.toString(httpResponse.getEntity());
            }
        });
        assertEquals("", response);
    }
}
