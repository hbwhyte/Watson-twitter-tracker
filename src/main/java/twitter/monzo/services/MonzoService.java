package twitter.monzo.services;

import twitter4j.Twitter;
import twitter.monzo.mappers.MonzoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import twitter.monzo.model.external.SearchResult;
import twitter.monzo.model.internal.MonzoResponse;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class MonzoService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MonzoMapper monzoMapper;

    public static List<Status> searchTwitter(String search) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setJSONStoreEnabled(true);

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        List<Status> tweets = null;
        try {
            Query query = new Query(search);
            query.setResultType(Query.ResultType.recent);
            query.count(10);
            QueryResult result = twitter.search(query);
            for (Status tweet : result.getTweets()) {
                String json = TwitterObjectFactory.getRawJSON(tweet);
                System.out.println(json);
            }
//            System.exit(0); //Shuts down API after search.
//            do {
//                SearchResult obj = new SearchResult();
//                result = twitter.search(query);
//                tweets = result.getTweets();
//                System.out.println(result.getTweets());
//            } while ((query = result.nextQuery()) != null);

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
        return tweets;
    }

    // TODO: Instead of printing the JSON to the console I need to map the json to SearchResults and return parsed json

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

//    public MonzoResponse searchTwitter(String searchTerm) {
//        String fQuery = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=realDonaldTrump&count=2";
//        return response = restTemplate.getForObject(fQuery, MonzoResponse.class);
//    }