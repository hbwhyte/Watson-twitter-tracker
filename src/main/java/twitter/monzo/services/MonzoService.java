package twitter.monzo.services;


import org.springframework.stereotype.Service;

import twitter4j.*;
import twitter4j.Twitter;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static twitter.monzo.services.WatsonService.emotionAnalyzer;

@Service
public class MonzoService {

    // Finds the text of the most recent tweet about a search subject
    public static String latestTweet(String search) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setTweetModeExtended(true);
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

    public static String analyzeTweet(String search) {
        String tweet = latestTweet(search);
        String tweetAnalysis = emotionAnalyzer(tweet);
        return "The most recent tweet about \"" + search + "\" was: \"" + tweet + "\". " + tweetAnalysis;
    }

    // Posts a tweet to my user (@randomJavaFun)
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

    public String postAnalysis(String search) {
        String tweet = analyzeTweet(search);
        createTweet(tweet);
        return tweet;
    }

    // Searches 10 most recent tweets for a search term and returns a String List
    public static List<String> searchTwitterList(String search) {
        Twitter twitter = new TwitterFactory().getInstance();
        List<String> tweets = null;
        try {
            Query query = new Query(search);
            query.setResultType(Query.ResultType.recent);
            query.count(10);
            QueryResult result = twitter.search(query);
            tweets = result.getTweets().stream().map(item -> item.getText()).collect(Collectors.toList());
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
        return tweets;
    }

//     Returns tweets from my timeline
    public List<Status> getMyTimeLine() throws TwitterException {
        List<Status> statuses = null;
        try {
            // gets Twitter instance with default credentials
            Twitter twitter = new TwitterFactory().getInstance();
            User user = twitter.verifyCredentials();
            statuses = twitter.getHomeTimeline();
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
        return statuses;
    }
}
