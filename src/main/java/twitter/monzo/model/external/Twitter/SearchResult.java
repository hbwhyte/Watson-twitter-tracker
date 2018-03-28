package twitter.monzo.model.external.Twitter;

public class SearchResult {

    int id;
    String name;
    String text;
    String link;
    int retweet_count;
    int favourite_count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    public int getFavourite_count() {
        return favourite_count;
    }

    public void setFavourite_count(int favourite_count) {
        this.favourite_count = favourite_count;
    }
}
