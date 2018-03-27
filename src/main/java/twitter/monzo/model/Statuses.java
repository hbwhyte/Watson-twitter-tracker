package twitter.monzo.model;

public class Statuses {

    String created_at;
    int id;
    String id_str;
    String text;
    boolean truncated;

    TwitterUser user;

    int retweet_count;
    int favourite_count;
}
