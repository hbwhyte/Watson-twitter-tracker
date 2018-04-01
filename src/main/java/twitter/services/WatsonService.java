package twitter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import twitter.model.Watson.Tone;
import twitter.model.Watson.WatsonResponse;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;


@Service
public class WatsonService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TwitterService twitterService;

    @Value("${watson.username}")
    private String username;
    @Value("${watson.password}")
    private String password;

    /**
     * Connects to IBM Watson Tone Analyzer API with authentication grabbed from authorize()
     *
     * @param textToAnalyze
     * @return WatsonResponse mapped object
     */
    public WatsonResponse callWatson(String textToAnalyze) {
        String encodedText = twitterService.encodeHashtags(textToAnalyze);
        String fQuery = "https://gateway.watsonplatform.net/tone-analyzer/api/v3/tone?version=2016-05-19&text=" + encodedText;

        ResponseEntity<WatsonResponse> responseEntity
                = restTemplate.exchange(fQuery, HttpMethod.GET, new HttpEntity<>(authorize(
                        username,password)), WatsonResponse.class);

        // Feedback: could this be a nullpointer waiting to happen?
        WatsonResponse response = responseEntity.getBody();

        return response;
    }

    /**
     * Creates the HTTP Header to authorize the IBM Watson Tone Analyzer API call
     *
     * @param username IBM Watson API username
     * @param password IBM Watson API password
     * @return HTTPHeaders for authorization
     */
    public static HttpHeaders authorize(String username, String password) {

        HttpHeaders header = new HttpHeaders();

        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String headerAuth = "Basic " + new String(encodedAuth);
        header.set( "Authorization", headerAuth );
        return header;
    }

    /**
     * Evaluates the emotional aspect of the text, as per IBM Watson, and ranks which emotion
     * was most strongly demonstrated in the text.
     *
     * @param textToAnalyze String
     * @return String of which emotion was strongest, and how present it was in the text in %
     */
    public String emotionAnalyzer(String textToAnalyze) {
        WatsonResponse response = callWatson(textToAnalyze);
        Tone biggestEmotion = new Tone();
        biggestEmotion.setScore(-1.0);
        for (Tone emotion : response.getDocument_tone().getTone_categories()[0].getTones()) {
            if (emotion.getScore() > biggestEmotion.getScore()) {
                biggestEmotion.setScore(emotion.getScore());
                biggestEmotion.setTone_id(emotion.getTone_id());
                biggestEmotion.setTone_name(emotion.getTone_name());
            }
        }

        // Feedback: what happens if the WatsonResponse response comes back null? Is this situation accounted for?
        int percentScore = (int) (biggestEmotion.getScore() * 100);
        String analysis = "IBM Watson thinks this tweet was " + percentScore + "% " + emotionFormat(biggestEmotion.getTone_id());
        return analysis;
    }

    /**
     * Changes the IBM Watson tone ids from nouns to adjectives.
     *
     * @param emotion String getTone_id()
     * @return String of adjective and happy/sad face
     */
    public String emotionFormat(String emotion) {
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

    // Feedback: more in-line comments would be good. Otherwise this looks really good. Just need to beef up the
    // exception handling. We have to assume that everything that can go wrong will go wrong. Usually very quickly.
}

