package twitter.services;


import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import twitter.model.Twitter.TwitterResponse;
import twitter4j.*;
import twitter4j.Twitter;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class TwitterService {

    @Autowired
    WatsonService watsonService;

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
        String tweet = latestTweet(search);
        String tweetAnalysis = watsonService.emotionAnalyzer(tweet);
        return "The most recent tweet about \"" + search + "\" was: \"" + tweet + "\". " + tweetAnalysis;
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

}
