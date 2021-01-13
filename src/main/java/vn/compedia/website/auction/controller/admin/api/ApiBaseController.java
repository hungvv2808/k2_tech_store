package vn.compedia.website.auction.controller.admin.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.xml.TokenAdministrativeUnits;

import javax.inject.Named;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Named
@Scope(value = "session")
public class ApiBaseController implements Serializable {
    static String tokenURL = "https://lgsp.danang.gov.vn/token";

    public String api(String endpoint, Map<String, String> replaces) throws IOException {
        String apiToken = null;
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(tokenURL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", "I0KQeEz8pRkU7VvEWFflTb7zizIa"));
        params.add(new BasicNameValuePair("client_secret", "OP2_nGylXNLfEV3HEWmUe_UUaY0a"));
        params.add(new BasicNameValuePair("grant_type", "client_credentials"));
        post.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = httpclient.execute(post);
        String body = EntityUtils.toString(response.getEntity());
        String json = "[" + body + "]";
        ObjectMapper mapper = new ObjectMapper();

        try {
            TokenAdministrativeUnits[] pp1 = mapper.readValue(json, TokenAdministrativeUnits[].class);

            for (TokenAdministrativeUnits token : pp1) {
                System.out.println(token.getAccess_token());
                apiToken = token.getAccess_token();
            }

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Authorization", "Bearer " + "" + apiToken + "");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String output;
            StringBuilder responseApi = new StringBuilder();
            while ((output = in.readLine()) != null) {
                responseApi.append(output);
            }
            in.close();
            String str = responseApi.toString();

            for (String key : replaces.keySet()) {
                str = str.replace(key, replaces.get(key));
            }

            return str;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
}
