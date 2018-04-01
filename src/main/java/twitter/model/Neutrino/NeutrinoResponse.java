package twitter.model.Neutrino;

import com.fasterxml.jackson.annotation.JsonProperty;


// Feedback: each of your model classes could use a small javadoc comment at the class level, above the class
// definition with a quick description of what it is and what it's used for
public class NeutrinoResponse {
    @JsonProperty("bad-words-list")
    String[] badWordsList;

    @JsonProperty("bad-words-total")
    int badWordsTotal;

    @JsonProperty("censored-content")
    String censoredContent;

    @JsonProperty("is-bad")
    boolean isBad;

    public String getCensoredContent() {
        return censoredContent;
    }

    public void setCensoredContent(String censoredContent) {
        this.censoredContent = censoredContent;
    }

    public boolean isBad() {
        return isBad;
    }

    public void setBad(boolean bad) {
        isBad = bad;
    }

    public int getBadWordsTotal() {
        return badWordsTotal;
    }

    public void setBadWordsTotal(int badWordsTotal) {
        this.badWordsTotal = badWordsTotal;
    }

    public String[] getBadWordsList() {
        return badWordsList;
    }

    public void setBadWordsList(String[] badWordsList) {
        this.badWordsList = badWordsList;
    }
}
