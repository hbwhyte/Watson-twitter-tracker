package twitter.monzo.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import twitter.monzo.do_not_upload.MyCredentials;
import twitter.monzo.model.external.Watson.WatsonResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Service
public class WatsonService {

    @Autowired
    RestTemplate restTemplate;


    public static WatsonResponse callWatson(String textToAnalyze) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("/Users/hbwhyte/dev_stuff/coding_nomads/bali/projects/twittertracker/src/main/resources/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String username = prop.getProperty("watson.username");
        String password = prop.getProperty("watson.password");
        String endpoint = prop.getProperty("watson.endpoint");

        String data =
                "{\"textToAnalyze\": \"" + textToAnalyze + "\"," +
                        " \"username\"     : \""+username+"\"," +
                        " \"password\"     : \""+password+"\"," +
                        " \"endpoint\"     : \""+endpoint+"\"," +
                        " \"skip_authentication\": \"false\"}";
        JsonParser parser = new JsonParser();
        JsonObject jsonArgs = parser.parse(data).getAsJsonObject();
        WatsonResponse response = new Gson().fromJson(analyzer(jsonArgs).toString(), WatsonResponse.class);
        return response;
    }

    public static JsonObject analyzer(JsonObject args) {
        JsonParser parser = new JsonParser();

        ToneAnalyzer service = new ToneAnalyzer("2016-05-19");
        service.setUsernameAndPassword(args.get("username").getAsString(),
                args.get("password").getAsString());

        if (args.get("endpoint") != null)
            service.setEndPoint(args.get("endpoint").getAsString());

        if (args.get("skip_authentication") != null)
            service.setSkipAuthentication(args.get("skip_authentication")
                    .getAsString() == "true");

        ToneAnalysis result =
                service.getTone(args.get("textToAnalyze").getAsString(), null).
                        execute();

        System.out.println(result.toString());
        return parser.parse(result.toString()).getAsJsonObject();
    }

}

