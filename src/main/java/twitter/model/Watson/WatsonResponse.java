package twitter.model.Watson;

public class WatsonResponse {

    private DocumentTone document_tone;
    private SentencesTone[] sentences_tone;

    public DocumentTone getDocument_tone() {
        return document_tone;
    }

    public void setDocument_tone(DocumentTone document_tone) {
        this.document_tone = document_tone;
    }

    public SentencesTone[] getSentences_tone() {
        return sentences_tone;
    }

    public void setSentences_tone(SentencesTone[] sentences_tone) {
        this.sentences_tone = sentences_tone;
    }
}


