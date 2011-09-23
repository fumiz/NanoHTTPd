package me.fumiz.test.nano.cases;

import me.fumiz.nano.NanoHTTPd;
import me.fumiz.test.nano.lib.NanoHTTPdTestCase;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Simple GET request test
 * User: fumiz
 * Date: 11/09/23
 * Time: 17:25
 */
public class GetTest extends NanoHTTPdTestCase {
    public void testSimple() throws IOException {
        HttpGet request;
        String response;

        request = new HttpGet(getTestUrl("/path/to/test/url.html"));
        response = execute(new TestRequestHandler() {
            public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files) {
                assertEquals("/path/to/test/url.html", uri);
                assertEquals("GET", method);
                assertEquals(0,parms.size());
                assertEquals(0,files.size());
                return createSimpleResponse("AERNVDSFj431o2413j1-=910-34i13981u9u(@$)*()_*U#MJFW)_CM<%=2");
            }
        }, request, new ResponseHandler<String>() {
            public String handleResponse(HttpResponse httpResponse) throws IOException {
                assertEquals(200, httpResponse.getStatusLine().getStatusCode());
                assertEquals("OK", httpResponse.getStatusLine().getReasonPhrase());
                return EntityUtils.toString(httpResponse.getEntity());
            }
        });
        assertEquals(response, "AERNVDSFj431o2413j1-=910-34i13981u9u(@$)*()_*U#MJFW)_CM<%=2");
    }
}
