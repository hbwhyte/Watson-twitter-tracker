package twitter.monzo.model.external.Watson;

public class SentencesTone {

    int sentence_id;
    int input_from;
    int input_to;
    String text;
    ToneCategory[] tone_categories;

    public int getInput_from() {
        return input_from;
    }

    public void setInput_from(int input_from) {
        this.input_from = input_from;
    }

    public int getInput_to() {
        return input_to;
    }

    public void setInput_to(int input_to) {
        this.input_to = input_to;
    }

    public int getSentence_id() {
        return sentence_id;
    }

    public void setSentence_id(int sentence_id) {
        this.sentence_id = sentence_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ToneCategory[] getTone_categories() {
        return tone_categories;
    }

    public void setTone_categories(ToneCategory[] tone_categories) {
        this.tone_categories = tone_categories;
    }
}
