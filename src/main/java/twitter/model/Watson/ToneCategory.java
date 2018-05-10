package twitter.model.Watson;

/**
 * Watson response object of the higher level tone categories.
 */
public class ToneCategory {

    private String category_id;
    private String category_name;
    private Tone[] tones;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public Tone[] getTones() {
        return tones;
    }

    public void setTones(Tone[] tones) {
        this.tones = tones;
    }
}
