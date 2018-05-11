package twitter.model.twitter;

/**
 * twitter response object that collects the relevant information
 * about the tweet
 */
public class Tweet {
    long id;
    String text;
    String lang;
    TwitterUser user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public TwitterUser getUser() {
        return user;
    }

    public void setUser(TwitterUser user) {
        this.user = user;
    }
}
