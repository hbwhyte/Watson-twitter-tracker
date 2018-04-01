package twitter.services;


import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import twitter.model.Neutrino.NeutrinoResponse;
import twitter.model.Twitter.TwitterResponse;
import twitter4j.*;
import twitter4j.Twitter;
import twitter4j.conf.ConfigurationBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class    TwitterService {

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
    public String latestTweet(String search) {
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query(search);
        query.count(1).lang("en").setResultType(Query.ResultType.recent);
        QueryResult result = null;
        try {
            result = twitter.search(query);
        } catch (TwitterException te) {
            // Feedback: printing the stack trace to the logs is good, but you should probably throw the exception to the controller
            // we haven't talked about it much yet, but we'll be adding controller level and global API error handling so that the end
            // user is aware something has gone wrong and doesn't get a stacktrace back or an empty response
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        // Feedback: this seems like it could be a nullpointer if the catch above was triggered
        // if it's not a nullpointer it will still return null if the catch was triggered
        // at this point, I'd recommend returning a string that says an error has occured,
        // ideally with some detail on the error that happened.
        String latestTweet = result.getTweets().get(0).getText();
        return latestTweet;
    }

    /**
     * Searches 20 most recent tweets for a search term and returns a mapped POJO
     *
     * @param search term to search Twitter for
     * @return TwitterResponse object that was mapped from the JSON response
     */
    public TwitterResponse searchTwitterList(String search) {
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
            // Feedback: similar feedback as in method above
            // I'd probably throw this exception up to the caling method
            // if the controller catches an exception it can format an object nicely and return it
            // to the user
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            // Feedback: System.exit terminates the currently running JVM
            // not sure that's what you want to do here. I'd throw the exception
            // we'll talk about controller api exception handling this week
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
            // Feedback: same as above
            // print the stacktrace to the log is not that useful
            // it's good, but the consumer of the code is not monitoring the logs
            // I'd throw this exception
            te.printStackTrace();
            System.out.println("Failed to post tweet: " + te.getMessage());
        }
        // Feedback: this is a nullpointer waiting to happen if the catch above was triggered
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

        // Feedback: both tweet and tweetAnalysis can be null or errors, so some more
        // error handling is required here
        return "The most recent tweet about \"" + search + "\" was \"" + tweet + "\"\n\n" + tweetAnalysis;
    }

    /**
     * Takes the results of analyzeTweet(), then tweets it live as @randomJavaFun
     *
     * @param search term to search Twitter for
     * @return String of what was tweeted and a success message.
     */
    public String postAnalysis(String search) {
        String tweet = analyzeTweet(search);
        boolean nsfw = false;
        try {
            nsfw = hasSwears(tweet);
        } catch (NullPointerException e) {
            // Feedback: same as above - totally understand why your printing the stacktrace and moving
            // on. But we'll need to make this a bit more usable for a proper deployment as just printing
            // the stacktrace in each of these catches will lead to several dead ends where the user gets an
            // error or an empty response
            e.printStackTrace();
            System.out.println("WARNING: Potentially NSFW, Bad Words Filter was not run.");
        }
        if (nsfw) {
            // Feedback: if the catch above was triggered - nsfw will always be false - which seems like it could
            // lead to a false negative and an NSFW tweet could make it through if any error in the hasSwears() method
            // is encountered
            return tweet + "\nTweet contains possible inappropriate language. Tweet was not posted.";
        } else {
            createTweet(tweet);
            return tweet + "\nTweet successfully posted!";
        }
    }

    /**
     *  Takes in a text string and runs it through Neutrino API's Bad Word Filter. Replaces
     *  any words from their list with a "*".
     *
     *  In addition to filtering the swears, the API also deletes any punctuation too, so the formatted
     *  text sometimes looks weird. There is a 25 call per day limit on the free tier API.
     *
     * @param text String to be sent through the Bad Word Filter
     * @return String of filtered text.
     */
    public String filterSwears(String text) {
        String encodedText = encodeHashtags(text);
            String fQuery = "https://neutrinoapi.com/bad-word-filter?user-id="+userId+
                    "&api-key="+apiKey+"&content="+encodedText+"&censor-character=*";
        try {
            NeutrinoResponse response = restTemplate.getForObject(fQuery, NeutrinoResponse.class);
            String cleanText = response.getCensoredContent();
            return cleanText;
        } catch (RestClientException e) {
            e.printStackTrace();
            System.out.println("NSFW: Warning, text was not cleaned!");
            return text;
        }
    }

    /**
     *  Takes in a text string and runs it through Neutrino API's Bad Word Filter and identifies
     *  whether or not there were identifiable swear words in the text.
     *
     *  If the call fails, it returns false so the
     *
     * @param text String to be sent through the Bad Word Filter
     * @return boolean true if text contained swears
     * @throws NullPointerException
     */

    public boolean hasSwears(String text) throws NullPointerException {
        String encodedText = encodeHashtags(text);
        String fQuery = "https://neutrinoapi.com/bad-word-filter?user-id="+userId+
                "&api-key="+apiKey+"&content="+encodedText+"&censor-character=*";
        NeutrinoResponse response = null;
        try {
            response = restTemplate.getForObject(fQuery, NeutrinoResponse.class);
        } catch (RestClientException e) {
            // Feedback: same as above about just printing the exception and moving on
            // this will lead to bugs and strange behavior
            e.printStackTrace();
            System.out.println("NSFW: Warning, text was not cleaned!");
        }
        if (response != null) {
            return response.isBad();
        } else {
            // Feedback: is it really a nullpointerexception? this could be a good spot
            // for a custom exception potentially
            throw new NullPointerException("NSFW: Warning, text was not cleaned!");
        }
    }

    /**
     * Makes text safe for URIs. Needed especially with tweets becuase the # symbol
     * isn't supported in URI/URLs.
     *
     * @param text String to be encoded
     * @return encoded String
     */

    public String encodeHashtags(String text) {
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    // Feedback: general feedback - this code looks really good. Impressive. We'll work on the error handling
    // I also think having some more in-line comments could be good. When you come back to this code in a year,
    // or when someone else has to work with it, you/they will be very grateful for verbose documentation throughout
    // your javadoc comments above each method are great
}
