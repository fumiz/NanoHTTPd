package me.fumiz.test.nano.cases;

import me.fumiz.nano.NanoHTTPd;
import me.fumiz.test.nano.lib.NanoHTTPdTestCase;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * User: fumiz
 * Date: 11/09/25
 * Time: 14:52
 */
public class MultipartTest extends NanoHTTPdTestCase {
    public void testMultipartSend() throws IOException {
        HttpPost request;
        String response;

        final byte[] originalFile = new byte[]{64,32,51,78,91,80,10,0,5};

        request = new HttpPost(getTestUrl("/path/to/multipart.cgi"));
        MultipartEntity entity = new MultipartEntity();
        entity.addPart("foo", new StringBody("body", Charset.forName("utf-8")));
        entity.addPart("file", new ByteArrayBody(originalFile, "filename"));
        entity.addPart("multibyte", new StringBody("外语的字符串", Charset.forName("utf-8")));
        entity.addPart("multilinecrlf", new StringBody("\r\n\r\n外\r\n语\r\n\r\n的\r\n字\r\n符\r\n\r\n串\r\n\r\n\r\n", Charset.forName("utf-8")));
        entity.addPart("multilinelf", new StringBody("\n\nマルチ\nバイト\n文字列\n改行込みテストLF\n\n", Charset.forName("utf-8")));
        entity.addPart("multilinecr", new StringBody("\rマルチ\rバイト\r文字列\r改行込みテストCR\r\r", Charset.forName("utf-8")));
        request.setEntity(entity);

        response = execute(new TestRequestHandler() {
            public NanoHTTPd.Response onRequest(String uri, String method, Properties header, Properties parms, Properties files) {
                assertEquals("/path/to/multipart.cgi", uri);
                assertEquals("POST", method);

                assertEquals(6, parms.size());
                assertEquals(1, files.size());
                assertEquals("body", parms.getProperty("foo"));
                assertEquals("filename", parms.getProperty("file"));
                assertEquals("外语的字符串", parms.getProperty("multibyte"));
                assertEquals("\r\n\r\n外\r\n语\r\n\r\n的\r\n字\r\n符\r\n\r\n串\r\n\r\n\r\n", parms.getProperty("multilinecrlf"));
                assertEquals("\n\nマルチ\nバイト\n文字列\n改行込みテストLF\n\n", parms.getProperty("multilinelf"));
                assertEquals("\rマルチ\rバイト\r文字列\r改行込みテストCR\r\r", parms.getProperty("multilinecr"));

                String temporaryPath = files.getProperty("file");
                File temporaryFile = new File(temporaryPath);
                try {
                    FileInputStream fis = new FileInputStream(temporaryFile);
                    try {
                        assertEquals(64, fis.read());
                        assertEquals(32, fis.read());
                        assertEquals(51, fis.read());
                        assertEquals(78, fis.read());
                        assertEquals(91, fis.read());
                        assertEquals(80, fis.read());
                        assertEquals(10, fis.read());
                        assertEquals(0, fis.read());
                        assertEquals(5, fis.read());
                        // end of file
                        assertEquals(-1, fis.read());
                    } finally {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

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
