package me.fumiz.test.nano.cases;

import me.fumiz.nano.NanoHTTPd;
import me.fumiz.test.nano.lib.NanoHTTPdTestCase;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * User: fumiz
 * Date: 11/09/25
 * Time: 14:53
 */
public class PostTest extends NanoHTTPdTestCase {
    public void testSimplePost() throws IOException {
        HttpPost request;
        String response;

        request = new HttpPost(getTestUrl("/post.php"));

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("foo","body"));
        params.add(new BasicNameValuePair("multibyte","外语的字符串"));
        params.add(new BasicNameValuePair("multilinecrlf","\r\n\r\n外\r\n语\r\n\r\n的\r\n字\r\n符\r\n\r\n串\r\n\r\n\r\n"));
        params.add(new BasicNameValuePair("multilinelf","\n\nマルチ\nバイト\n文字列\n改行込みテストLF\n\n"));
        params.add(new BasicNameValuePair("multilinecr","\rマルチ\rバイト\r文字列\r改行込みテストCR\r\r"));
        request.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

        response = execute(new TestRequestHandler() {
            public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files) {
                assertEquals("/post.php", uri);
                assertEquals("POST", method);

                assertEquals(5, parms.size());
                assertEquals(0, files.size());
                assertEquals("body", parms.getProperty("foo"));
                assertEquals("外语的字符串", parms.getProperty("multibyte"));
                assertEquals("\r\n\r\n外\r\n语\r\n\r\n的\r\n字\r\n符\r\n\r\n串\r\n\r\n\r\n", parms.getProperty("multilinecrlf"));
                assertEquals("\n\nマルチ\nバイト\n文字列\n改行込みテストLF\n\n", parms.getProperty("multilinelf"));
                assertEquals("\rマルチ\rバイト\r文字列\r改行込みテストCR\r\r", parms.getProperty("multilinecr"));

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

    public void testPostResponse() throws IOException {
        HttpPost request;
        String response;

        request = new HttpPost(getTestUrl("/post.php"));

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("foo","body"));
        request.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

        response = execute(new TestRequestHandler() {
            public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files) {
                assertEquals("/post.php", uri);
                assertEquals("POST", method);

                assertEquals(1, parms.size());
                assertEquals(0, files.size());
                assertEquals("body", parms.getProperty("foo"));

                return new NanoHTTPd.Response(NanoHTTPd.HTTP_OK, NanoHTTPd.MIME_PLAINTEXT, "\n多\n字节\n\n进行反应的确认\n\n");
            }
        }, request, new ResponseHandler<String>() {
            public String handleResponse(HttpResponse httpResponse) throws IOException {
                assertEquals(200, httpResponse.getStatusLine().getStatusCode());
                assertEquals("OK", httpResponse.getStatusLine().getReasonPhrase());
                assertEquals("text/plain", httpResponse.getHeaders("Content-Type")[0].getValue());
                return EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            }
        });
        assertEquals("\n多\n字节\n\n进行反应的确认\n\n", response);
    }
}
