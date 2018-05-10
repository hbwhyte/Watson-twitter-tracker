package twitter.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TwitterServiceTest {

    TwitterService twitterService;

    @Before
    public void setUp() {
        twitterService = new TwitterService();
    }

    @Test
    public void encodeHashtags() throws Exception {
        String tweet = "RT @randomJavaFun #codingnomads";
        String expected = "RT+randomJavaFun+%23codingnomads";
        assertEquals(expected, twitterService.encodeHashtags(tweet));
    }
}