package twitter.monzo.services;

import twitter.monzo.mappers.MonzoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import twitter.monzo.model.MonzoResponse;
import twitter4j.*;

import java.util.List;

@Service
public class MonzoService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MonzoMapper monzoMapper;

    public List<Status> searchTwitter(String user) throws TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        List<Status> tweets = null;
        try {
            Query query = new Query("@monzo");
            QueryResult result;
            do {
                result = twitter.search(query);
                tweets = result.getTweets();
            } while ((query = result.nextQuery()) != null);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
        return tweets;
    }

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