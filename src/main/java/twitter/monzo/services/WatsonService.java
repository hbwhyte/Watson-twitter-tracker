package twitter.monzo.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import twitter.monzo.model.external.Watson.Tone;
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
                        " \"username\"     : \"" + username + "\"," +
                        " \"password\"     : \"" + password + "\"," +
                        " \"endpoint\"     : \"" + endpoint + "\"," +
                        " \"skip_authentication\": \"false\"}";
        JsonParser parser = new JsonParser();
        JsonObject jsonArgs = parser.parse(data).getAsJsonObject();
        WatsonResponse response = new Gson().fromJson(toneAnalyzer(jsonArgs).toString(), WatsonResponse.class);
        return response;
    }

    public static JsonObject toneAnalyzer(JsonObject args) {
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

    public static String emotionAnalyzer(String tweet) {
        WatsonResponse response = callWatson(tweet);
        Tone biggestEmotion = new Tone();
        biggestEmotion.setScore(-1.0);
        for (Tone emotion : response.getDocument_tone().getTone_categories()[0].getTones()) {
            if (emotion.getScore() > biggestEmotion.getScore()) {
                biggestEmotion.setScore(emotion.getScore());
                biggestEmotion.setTone_id(emotion.getTone_id());
                biggestEmotion.setTone_name(emotion.getTone_name());
                System.out.println(biggestEmotion.getScore());
            }
        }
        int percentScore = (int) (biggestEmotion.getScore() * 100);
        String analysis = "IBM Watson thinks this tweet was " + percentScore + "% " + emotionFormat(biggestEmotion.getTone_id());
        System.out.println(analysis);
        return analysis;
        // Returns String "This tweet was a little / "" / very angry - Watson rated it 11% angry"
    }

    public static String emotionFormat(String emotion) {
        String formattedEmo = emotion;
        switch (emotion) {
            case "anger":
                formattedEmo = "angry :(";
                break;
            case "disgust":
                formattedEmo = "disgusted :(";
                break;
            case "fear":
                formattedEmo = "fearful :(";
                break;
            case "joy":
                formattedEmo = "joyful :)";
                break;
            case "sadness":
                formattedEmo = "sad :(";
                break;
        }
        return formattedEmo;
    }

}

