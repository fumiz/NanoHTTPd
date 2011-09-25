package me.fumiz.test.nano.cases;

import me.fumiz.nano.NanoHTTPd;
import me.fumiz.test.nano.lib.NanoHTTPdTestCase;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * Simple GET request test
 * User: fumiz
 * Date: 11/09/23
 * Time: 17:25
 */
public class GetTest extends NanoHTTPdTestCase {
    public void testSimpleGet() throws IOException {
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
        assertEquals("AERNVDSFj431o2413j1-=910-34i13981u9u(@$)*()_*U#MJFW)_CM<%=2", response);
    }

    public void testGetSimpleParameter() throws IOException {
        HttpGet request;
        String response;

        request = new HttpGet(getTestUrl("/path/to/test/url.php?param1=testparameter&param2=2147483647&param3=9223372036854775807&param4=true&param5=3.3000000000000003"));

        response = execute(new TestRequestHandler() {
            public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files) {
                assertEquals("/path/to/test/url.php", uri);
                assertEquals("GET", method);
                assertEquals(5, parms.size());
                assertEquals(0, files.size());

                assertEquals("testparameter", parms.getProperty("param1"));
                assertEquals("2147483647", parms.getProperty("param2"));
                assertEquals("9223372036854775807", parms.getProperty("param3"));
                assertEquals("true", parms.getProperty("param4"));
                assertEquals("3.3000000000000003", parms.getProperty("param5"));

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

    public void testGetUrlencodedParameter() throws IOException {
        HttpGet request;
        String response;

        request = new HttpGet(getTestUrl(String.format("/path/to/test/url.php?param1=%s&param2=%s", URLEncoder.encode("&$\"'%sd341831nfa", "utf-8"), URLEncoder.encode("second&parameter=params", "utf-8"))));

        response = execute(new TestRequestHandler() {
            public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files) {
                assertEquals("/path/to/test/url.php", uri);
                assertEquals("GET", method);
                assertEquals(2, parms.size());
                assertEquals(0, files.size());

                assertEquals("&$\"'%sd341831nfa", parms.getProperty("param1"));
                assertEquals("second&parameter=params", parms.getProperty("param2"));

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

    public void testGetMultibyteParameter() throws IOException {
        HttpGet request;
        String response;

        request = new HttpGet(getTestUrl(String.format("/path/to/test/url.php?param1=%s&param2=%s",
                URLEncoder.encode("&$\"'%sd34マルチバイト混じり1831nfa", "utf-8"),
                URLEncoder.encode("second&pa多字节字符串的试验rameter=params", "utf-8"))));

        response = execute(new TestRequestHandler() {
            public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files) {
                assertEquals("/path/to/test/url.php", uri);
                assertEquals("GET", method);
                assertEquals(2, parms.size());
                assertEquals(0, files.size());

                String param1 = parms.getProperty("param1", "");
                String param2 = parms.getProperty("param2", "");
                assertEquals("&$\"'%sd34マルチバイト混じり1831nfa", param1);
                assertEquals("second&pa多字节字符串的试验rameter=params", param2);
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

    public void testReturnMultibyteString() throws IOException {
        HttpGet request;
        String response;

        request = new HttpGet(getTestUrl("/path/to/test/url.php"));

        response = execute(new TestRequestHandler() {
            public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files) {
                assertEquals("/path/to/test/url.php", uri);
                assertEquals("GET", method);
                assertEquals(0, parms.size());
                assertEquals(0, files.size());

                return createSimpleResponse("&$\"'%sd34マルチバイト混じり1831nfasecond&pa多字节字符串的试验rameter=params");
            }
        }, request, new ResponseHandler<String>() {
            public String handleResponse(HttpResponse httpResponse) throws IOException {
                assertEquals(200, httpResponse.getStatusLine().getStatusCode());
                assertEquals("OK", httpResponse.getStatusLine().getReasonPhrase());
                return EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            }
        });
        assertEquals("&$\"'%sd34マルチバイト混じり1831nfasecond&pa多字节字符串的试验rameter=params", response);
    }
}
