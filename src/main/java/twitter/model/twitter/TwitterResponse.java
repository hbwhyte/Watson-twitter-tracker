package twitter.model.twitter;

/**
 * Main response object being returned by the twitter API
 */
public class TwitterResponse {
    String query;
    Tweet[] tweets;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Tweet[] getTweets() {
        return tweets;
    }

    public void setTweets(Tweet[] tweets) {
        this.tweets = tweets;
    }
}
