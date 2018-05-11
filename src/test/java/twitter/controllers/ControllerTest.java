package twitter.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import twitter.model.twitter.Tweet;
import twitter.model.twitter.TwitterResponse;
import twitter.model.twitter.TwitterUser;
import twitter.services.TwitterService;
import twitter.services.WatsonService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class ControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WatsonService watsonService;

    @Mock
    private TwitterService twitterService;

    @InjectMocks
    private Controller Controller;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(Controller)
//                .addFilters(new CORSFilter())
                .build();
    }

    /**
     * Tests the JSON response of a GET request to twitter/
     * @throws Exception
     */
    @Test
    public void latestTweetJSONTest() throws Exception {

        Tweet[] tweets = new Tweet[1];
        Tweet tweet = new Tweet();
        tweets[0] = tweet;
        tweet.setId(994611095049515008L);
        tweet.setText("Tweet content");
        tweet.setLang("en");

        TwitterUser user = new TwitterUser();
        user.setId(718291520680566784L);
        user.setName("Professor Harris");
        user.setScreenName("spcoffeemachine");
        tweet.setUser(user);

        TwitterResponse twitterResponse = new TwitterResponse();
        twitterResponse.setQuery("coffee");
        twitterResponse.setTweets(tweets);

        when(twitterService.latestTweet(anyString())).thenReturn(twitterResponse);

        mockMvc.perform(get("/twitter/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.status", is("Success")))
                .andExpect(jsonPath("$.response_code", is("OK")))
                .andExpect(jsonPath("$.data.query", is("coffee")))
                .andExpect(jsonPath("$.data.tweets", hasSize(1)))
                .andExpect(jsonPath("$.data.tweets.[0].id", is(994611095049515008L)))
                .andExpect(jsonPath("$.data.tweets.[0].text", is("Tweet content")))
                .andExpect(jsonPath("$.data.tweets.[0].lang", is("en")))
                .andExpect(jsonPath("$.data.tweets.[0].user.id", is(718291520680566784L)))
                .andExpect(jsonPath("$.data.tweets.[0].user.name", is("Professor Harris")))
                .andExpect(jsonPath("$.data.tweets.[0].user.screenName", is("spcoffeemachine")))
                .andExpect(jsonPath("$.error", is(nullValue())));

    }

    @Test
    public void searchTwitterListJSONTest() throws Exception {

        Tweet[] tweets = new Tweet[20];
        Tweet tweet = new Tweet();
        tweets[19] = tweet;
        tweet.setId(994611095049515008L);
        tweet.setText("Tweet content");
        tweet.setLang("en");

        TwitterUser user = new TwitterUser();
        user.setId(718291520680566784L);
        user.setName("Professor Harris");
        user.setScreenName("spcoffeemachine");
        tweet.setUser(user);

        TwitterResponse twitterResponse = new TwitterResponse();
        twitterResponse.setQuery("coffee");
        twitterResponse.setTweets(tweets);

        when(twitterService.searchTwitterList(anyString())).thenReturn(twitterResponse);

        mockMvc.perform(get("/twitter/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.status", is("Success")))
                .andExpect(jsonPath("$.response_code", is("OK")))
                .andExpect(jsonPath("$.data.query", is("coffee")))
                .andExpect(jsonPath("$.data.tweets", hasSize(20)))
                .andExpect(jsonPath("$.data.tweets.[19].id", is(994611095049515008L)))
                .andExpect(jsonPath("$.data.tweets.[19].text", is("Tweet content")))
                .andExpect(jsonPath("$.data.tweets.[19].lang", is("en")))
                .andExpect(jsonPath("$.data.tweets.[19].user.id", is(718291520680566784L)))
                .andExpect(jsonPath("$.data.tweets.[19].user.name", is("Professor Harris")))
                .andExpect(jsonPath("$.data.tweets.[19].user.screenName", is("spcoffeemachine")))
                .andExpect(jsonPath("$.error", is(nullValue())));
    }

    @Test
    public void createTweetJSONTest() throws Exception {

        Tweet tweet = new Tweet();
        tweet.setId(994611095049515008L);
        tweet.setText("I made a tweet");
        tweet.setLang("en");

        TwitterUser user = new TwitterUser();
        user.setId(994820104239099904L);
        user.setName("Random Java Fun Times");
        user.setScreenName("randomJavaFun");
        tweet.setUser(user);

        when(twitterService.createTweet(anyString())).thenReturn(tweet);

        mockMvc.perform(post("/twitter/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.status", is("Success")))
                .andExpect(jsonPath("$.response_code", is("OK")))
                .andExpect(jsonPath("$.data.id", is(994611095049515008L)))
                .andExpect(jsonPath("$.data.text", is("I made a tweet")))
                .andExpect(jsonPath("$.data.lang", is("en")))
                .andExpect(jsonPath("$.data.user.id", is(994820104239099904L)))
                .andExpect(jsonPath("$.data.user.name", is("Random Java Fun Times")))
                .andExpect(jsonPath("$.data.user.screenName", is("randomJavaFun")))
                .andExpect(jsonPath("$.error", is(nullValue())));

    }


//    @Test
//    public void test_get_by_id_success() throws Exception {
//        User user = new User(1, "Daenerys Targaryen");
//
//        when(userService.findById(1)).thenReturn(user);
//
//        mockMvc.perform(get("/users/{id}", 1))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.username", is("Daenerys Targaryen")));
//
//    }
//
//    @Test
//    public void test_get_by_id_fail_404_not_found() throws Exception {
//
//        when(userService.findById(1)).thenReturn(null);
//
//        mockMvc.perform(get("/users/{id}", 1))
//                .andExpect(status().isNotFound());
//
//    }

}