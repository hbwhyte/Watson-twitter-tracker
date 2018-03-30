package twitter.services;


import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import twitter.model.NeutrinoResponse;
import twitter.model.Twitter.TwitterResponse;
import twitter.model.Watson.WatsonResponse;
import twitter4j.*;
import twitter4j.Twitter;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Service
public class TwitterService {

    @Autowired
    WatsonService watsonService;

    @Autowired
    RestTemplate restTemplate;

    @Value("${neutrino.user-id}")
    private String userId;
    @Value("${neutrino.api-key}")
    private String apiKey;


    /**
     * Finds the most recent tweet about a search subject
     *
     * @param search term to search Twitter for
     * @return String text of the most recent tweet
     */
    public static String latestTweet(String search) {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query(search);
        query.count(1).lang("en").setResultType(Query.ResultType.recent);
        QueryResult result = null;
        try {
            result = twitter.search(query);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
        String latestTweet = result.getTweets().get(0).getText();
        System.out.println(latestTweet);
        return latestTweet;
    }

    /**
     * Searches 20 most recent tweets for a search term and returns a mapped POJO
     *
     * @param search term to search Twitter for
     * @return TwitterResponse object that was mapped from the JSON response
     */
    public static TwitterResponse searchTwitterList(String search) {
        Twitter twitter = new TwitterFactory(
                new ConfigurationBuilder().setTweetModeExtended(true).setJSONStoreEnabled(true).build()).getInstance();
        TwitterResponse response = new TwitterResponse();
        try {
            Query query = new Query(search);
            query.count(20).lang("en");
            QueryResult result = twitter.search(query);
            Gson gson = new Gson();
            System.out.println(gson.toJson(result));
            response = gson.fromJson(gson.toJson(result), TwitterResponse.class);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
        return response;
    }

    /**
     * Posts a tweet to @randomJavaFun's twitter feed
     *
     * @param tweet String body of the tweet (280 char max)
     * @return String of the tweet
     */
    public String createTweet(String tweet) {
        Twitter twitter = new TwitterFactory().getInstance();
        Status status = null;
        try {
            status = twitter.updateStatus(tweet);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to post tweet: " + te.getMessage());
            System.exit(-1);
        }
        return status.getText();
    }

    /**
     *  Calls latestTweet() to search Twitter for the most recent tweet of the search topic
     *  then sends it to IBM Watson through emotionAnalyzer() to analyze the emotional tone of
     *  that tweet
     *
     * @param search term to search Twitter for
     * @return
     */
    public String analyzeTweet(String search) {
        String tweet = filterSwears(latestTweet(search));
        String tweetAnalysis = watsonService.emotionAnalyzer(tweet);
        return "The most recent tweet about \"" + filterSwears(search) + "\" was \"" + tweet + "\"\n" + tweetAnalysis;
    }


    /**
     * Takes the results of analyzeTweet(), then tweets it live as @randomJavaFun
     *
     * @param search term to search Twitter for
     * @return String of what was tweeted and a success message.
     */
    public String postAnalysis(String search) {
        String tweet = analyzeTweet(search);
        createTweet(tweet);
        return tweet + " Tweet successfully posted!";
    }

    public String filterSwears(String tweet) {

        String fQuery = "https://neutrinoapi.com/bad-word-filter?user-id="+userId+
                "&api-key="+apiKey+"&content="+tweet+"&censor-character=*";
        NeutrinoResponse response = restTemplate.getForObject(fQuery, NeutrinoResponse.class);
        String cleanTweet = response.getCensoredContent();
        return cleanTweet;
    }

}
