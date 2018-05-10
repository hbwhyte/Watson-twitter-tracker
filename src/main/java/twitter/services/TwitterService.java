package twitter.services;


import com.google.gson.Gson;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import twitter.exceptions.custom_exceptions.BadWordsFilterException;
import twitter.exceptions.custom_exceptions.EmptySearchException;
import twitter.model.Neutrino.NeutrinoResponse;
import twitter.model.Twitter.Tweet;
import twitter.model.Twitter.TwitterResponse;
import twitter.model.Twitter.TwitterUser;
import twitter4j.*;
import twitter4j.Twitter;
import twitter4j.conf.ConfigurationBuilder;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * TwitterService handles the connection to the Twitter API and the
 * methods that interact directly with Twitter or with the
 * corresponding tweets, such as the bad word filter.
 */
@Service
public class TwitterService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    WatsonService watsonService;

    @Autowired
    RestTemplate restTemplate;

    @Value("${neutrino.user-id}")
    private String userId;
    @Value("${neutrino.api-key}")
    private String apiKey;

    /**
     * Finds the most recent tweet about a search subject.
     * You can modify:
     * - The number of tweets it returns
     * - Truncated (140 char) or full (280 char) tweets returned
     * - Language filter
     * - Search by popular, recent, or mixed
     *
     * @param search term to search Twitter for
     * @return String text of the most recent tweet
     * @throws TwitterException     if unable to search Twitter
     * @throws EmptySearchException if Twitter returns no results for that search term
     */
    public TwitterResponse latestTweet(String search) throws TwitterException, EmptySearchException {
        // Searches for full tweet (280 chars) and enables JSON
        Twitter twitter = new TwitterFactory(
                new ConfigurationBuilder().setTweetModeExtended(true).setJSONStoreEnabled(true).build()).getInstance();
        // Instantiate query
        Query query = new Query(search);
        QueryResult result = null;

        // Search for 1 most recent tweet, in English
        query.count(1).lang("en").setResultType(Query.ResultType.recent);

        // Search Twitter
        try {
            result = twitter.search(query);
            // If search is successful, but there aren't any tweets for that term
            if (result.getTweets().isEmpty()) {
                logger.info("Search complete, no results found empty");
                throw new EmptySearchException("Sorry, not even Twitter is talking about that.");
            } else {
                logger.info("Tweet(s) found");
            }
        } catch (TwitterException te) {
            logger.warn("Unable to search tweets: " + te);
            throw new TwitterException("Unable to search tweets");
        }
        // Convert Twitter4j response to JSON, then maps to custom TwitterResponse
        Gson gson = new Gson();
        TwitterResponse response = gson.fromJson(gson.toJson(result), TwitterResponse.class);

        return response;
    }

    /**
     * Searches for a list of recent tweets for a search term and
     * returns a mapped POJO of all tweets
     * <p>
     * You can modify:
     * - The number of tweets it returns
     * - Truncated (140 char) or full (280 char) tweets returned
     * - Language filter
     * - Search by popular, recent, or mixed
     *
     * @param search term to search Twitter for
     * @return TwitterResponse object that was mapped from the JSON response
     * @throws TwitterException     if unable to search Twitter
     * @throws EmptySearchException if Twitter returns no results for that search term
     */
    public TwitterResponse searchTwitterList(String search) throws TwitterException, EmptySearchException {
        // Searches for full tweet (280 chars) and enables JSON
        Twitter twitter = new TwitterFactory(
                new ConfigurationBuilder().setTweetModeExtended(true).setJSONStoreEnabled(true).build()).getInstance();
        // Instantiate query
        Query query = new Query(search);
        QueryResult result = null;

        // Search for up to 20 tweets in English, with a mix of popular and most recent
        query.count(20).lang("en").setResultType(Query.ResultType.mixed);

        // Search Twitter
        try {
            result = twitter.search(query);
            // If search is successful, but there aren't any tweets for that term
            if (result.getTweets().isEmpty()) {
                logger.info("Search complete, no results found empty");
                throw new EmptySearchException("Sorry, not even Twitter is talking about that.");
            } else {
                logger.info("Tweet(s) found");
            }
        } catch (TwitterException te) {
            logger.warn("Unable to search tweets: " + te);
            throw new TwitterException("Unable to search tweets");
        }

        // Convert Twitter4j response to JSON, then maps to custom TwitterResponse
        Gson gson = new Gson();
        TwitterResponse response = gson.fromJson(gson.toJson(result), TwitterResponse.class);

        return response;
    }

    /**
     * Posts a tweet to @randomJavaFun's twitter feed
     * To tweet to your account update twitter4j.properties
     *
     * @param text String body of the tweet (280 char max)
     * @return String of the tweet
     * @throws TwitterException if tweet failed to post either because of excessive
     *                          character limit or because of a Twitter connection issue
     */
    public Tweet createTweet(String text) throws TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        Tweet tweet = new Tweet();
        TwitterUser user = new TwitterUser();
        Status status = null;
        // Verify text fits Twitter's character restrictions.
        if (text.length() > 280) {
            throw new TwitterException("Too many characters. Twitter has a 280 character limit per tweet.");
        }
        // Post tweet
        try {
            status = twitter.updateStatus(text);
        } catch (TwitterException te) {
            throw new TwitterException("Failed to post tweet.");
        }

        // Maps successfully posted tweet to Java object
        tweet.setId(status.getId());
        tweet.setText(status.getText());
        tweet.setLang(status.getLang());
        user.setId(status.getId());
        user.setName(status.getUser().getName());
        user.setScreenName(status.getUser().getScreenName());
        tweet.setTwitterUser(user);

        return tweet;
    }

    /**
     * Calls latestTweet() to search Twitter for the most recent tweet of the search topic
     * then sends it to IBM Watson through emotionAnalyzer() to analyze the emotional tone of
     * that tweet
     *
     * @param search term to search Twitter for
     * @return String of the tweet that was analyzed, and what the analysis was
     * @throws TwitterException             if latestTweet() was unable to search Twitter
     * @throws EmptySearchException         if latestTweet() found that Twitter returns
     *                                      no results for that search term
     * @throws UnsupportedEncodingException if the character encoding is not supported
     */
    public String analyzeTweet(String search) throws TwitterException, EmptySearchException, UnsupportedEncodingException {
        String tweet = latestTweet(search).getTweets()[0].getText();
        String tweetAnalysis = watsonService.emotionAnalyzer(tweet);
        return "The most recent tweet about \"" + search + "\" was \"" + tweet + "\"\n\n" + tweetAnalysis;
    }

    /**
     * Takes the results of analyzeTweet(), then tweets it live as @randomJavaFun
     *
     * @param search term to search Twitter for
     * @return String of what was tweeted and a success message.
     * @throws TwitterException             if latestTweet() was unable to search Twitter
     * @throws EmptySearchException         if latestTweet() found that Twitter returns
     *                                      no results for that search term
     * @throws UnsupportedEncodingException if the character encoding is not supported
     * @throws BadWordsFilterException      if there was an error using the Bad Words Filter API
     */
    public String postAnalysis(String search)
            throws TwitterException, EmptySearchException, UnsupportedEncodingException, BadWordsFilterException {
        String tweet = analyzeTweet(search);
        boolean nsfw = false;
        try {
            nsfw = hasSwears(tweet);
        } catch (NullPointerException e) {
            throw new BadWordsFilterException("WARNING: Potentially NSFW, Bad Words Filter was not run.");
        }
        if (nsfw) {
            return tweet + "\nTweet contains possible inappropriate language. Tweet was not posted.";
        } else {
            createTweet(tweet);
            return tweet + "\nTweet successfully posted!";
        }
    }

    /**
     * Takes in a text string and runs it through Neutrino API's Bad Word Filter. Replaces
     * any words from their list with a "*".
     * <p>
     * In addition to filtering the swears, the API also deletes any punctuation too, so the formatted
     * text sometimes looks weird. There is a 25 call per day limit on the free tier API.
     *
     * @param text String to be sent through the Bad Word Filter
     * @return String of filtered text.
     * @throws UnsupportedEncodingException if the character encoding is not supported
     * @throws BadWordsFilterException      if there was an issue connection the the Neutrino API
     */
    public String filterSwears(String text) throws UnsupportedEncodingException, BadWordsFilterException {
        String encodedText = encodeHashtags(text);
        String fQuery = "https://neutrinoapi.com/bad-word-filter?user-id=" + userId +
                "&api-key=" + apiKey + "&content=" + encodedText + "&censor-character=*";
        try {
            NeutrinoResponse response = restTemplate.getForObject(fQuery, NeutrinoResponse.class);
            String cleanText = response.getCensoredContent();
            return cleanText;
        } catch (RestClientException e) {
            throw new BadWordsFilterException("Text could not be cleaned, sorry.");
        }
    }

    /**
     * Takes in a text string and runs it through Neutrino API's Bad Word Filter and identifies
     * whether or not there were identifiable swear words in the text.
     * <p>
     * If the call fails, it returns false so the
     *
     * @param text String to be sent through the Bad Word Filter
     * @return boolean true if text contained swears
     * @throws UnsupportedEncodingException if the character encoding is not supported
     * @throws BadWordsFilterException      if there is an error connecting to the filter API
     */

    public boolean hasSwears(String text) throws BadWordsFilterException, UnsupportedEncodingException {
        // encode hashtags and other unsupported symbols for the fQuery
        String encodedText = encodeHashtags(text);
        String fQuery = "https://neutrinoapi.com/bad-word-filter?user-id=" + userId +
                "&api-key=" + apiKey + "&content=" + encodedText + "&censor-character=*";
        try {
            // Sends string to be verified by Bad Words Filter API
            NeutrinoResponse response = restTemplate.getForObject(fQuery, NeutrinoResponse.class);
            return response.isBad();
        } catch (RestClientException e) {
            logger.error("Error Accessing Neutrino Bad Words Filter API");
            throw new BadWordsFilterException("Error Accessing Neutrino Bad Words Filter API, " +
                    "please check your credentials");
        }
    }

    /**
     * Makes text safe for URIs. Needed especially with tweets becuase the # symbol
     * isn't supported in URI/URLs.
     *
     * @param text String to be encoded
     * @return encoded String
     * @throws UnsupportedEncodingException if the character encoding is not supported
     */

    public String encodeHashtags(String text) throws UnsupportedEncodingException {
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException: Failed to encode hashtags");
            throw new UnsupportedEncodingException("Could not encode hashtags");
        }
        return text;
    }

}
