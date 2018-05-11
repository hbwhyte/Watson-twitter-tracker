package twitter.model.watson;

/**
 * watson response object analyzing the tone of the overall text.
 */
public class DocumentTone {

    private ToneCategory[] tone_categories;

    public ToneCategory[] getTone_categories() {
        return tone_categories;
    }

    public void setTone_categories(ToneCategory[] tone_categories) {
        this.tone_categories = tone_categories;
    }
}
