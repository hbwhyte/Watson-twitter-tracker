package twitter.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import twitter.model.watson.DocumentTone;
import twitter.model.watson.Tone;
import twitter.model.watson.ToneCategory;
import twitter.model.watson.WatsonResponse;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class WatsonServiceTest {

    @InjectMocks
    @Spy
    private WatsonService watsonService;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void emotionAnalyzer() throws UnsupportedEncodingException {
        String textToAnalyze = "AI is going to take over the world!";

        Tone[] mockTones = new Tone[5];
        Tone obj = new Tone();
        obj.setScore(0.096837);
        obj.setTone_id("anger");
        mockTones[0] = obj;
        obj = new Tone();
        obj.setScore(0.020432);
        obj.setTone_id("disgust");
        mockTones[1] = obj;
        obj = new Tone();
        obj.setScore(0.06107);
        obj.setTone_id("fear");
        mockTones[2] = obj;
        obj = new Tone();
        obj.setScore(0.240791);
        obj.setTone_id("joy");
        mockTones[3] = obj;
        obj = new Tone();
        obj.setScore(0.132099);
        obj.setTone_id("sadness");
        mockTones[4] = obj;

        ToneCategory mockToneCategory = new ToneCategory();
        mockToneCategory.setTones(mockTones);
        ToneCategory[] mockToneCategories = new ToneCategory[1];
        mockToneCategories[0] = mockToneCategory;

        DocumentTone mockDocumentTone = new DocumentTone();
        mockDocumentTone.setTone_categories(mockToneCategories);

        WatsonResponse mockResponse = new WatsonResponse();
        mockResponse.setDocument_tone(mockDocumentTone);

        Mockito.doReturn(mockResponse).when(watsonService).callWatson(textToAnalyze);

        String expected = "IBM Watson thinks this tweet was 24% joyful :)";
        String test = watsonService.emotionAnalyzer(textToAnalyze);
        assertThat(test, is(equalTo(expected)));
    }

    @Test
    public void emotionFormat() {
        String emotion = "joy";
        String expected = "joyful :)";
        assertEquals(expected, watsonService.emotionFormat(emotion));
    }


}