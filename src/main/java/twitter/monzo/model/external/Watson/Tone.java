package twitter.monzo.model.external.Watson;

public class Tone {

    /**
     * The score that is returned lies in the range of 0 to 1.
     * A score less than 0.5 indicates that the tone is unlikely to be perceived in the content;
     * a score greater than 0.75 indicates a high likelihood that the tone is perceived.
     */
    double score;

    String tone_id;
    String tone_name;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getTone_id() {
        return tone_id;
    }

    public void setTone_id(String tone_id) {
        this.tone_id = tone_id;
    }

    public String getTone_name() {
        return tone_name;
    }

    public void setTone_name(String tone_name) {
        this.tone_name = tone_name;
    }
}
