package twitter.monzo.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import twitter4j.*;
import twitter4j.Twitter;
import twitter4j.conf.ConfigurationBuilder;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonzoService {

    // Searches 10 most recent tweets for a search term and returns a JSON Object
    public static JSONObject searchTwitter(String search) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setJSONStoreEnabled(true);
        Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query(search);
        query.setResultType(Query.ResultType.recent);
        query.count(10);

        QueryResult result = null;

        try {
            result = twitter.search(query);

            RateLimitStatus rateLimit = twitter.getRateLimitStatus().get("/users/search");
            System.out.println("My rate limit status" + rateLimit);

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
        JSONObject jsonResults = new JSONObject(result);
        System.out.println(jsonResults);

        return jsonResults;
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

    // Returns tweets from my timeline
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


}
