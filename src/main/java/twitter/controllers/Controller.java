package twitter.controllers;

import twitter.exceptions.custom_exceptions.BadWordsFilterException;
import twitter.exceptions.custom_exceptions.EmptySearchException;
import twitter.model.GeneralResponse;
import twitter.model.Watson.WatsonResponse;
import twitter.services.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import twitter.services.WatsonService;
import twitter4j.TwitterException;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/twitter")
public class Controller {

    @Autowired
    TwitterService twitterService;

    @Autowired
    WatsonService watsonService;

    /**
     * Searches Twitter for the text of the most recent tweet of the search term
     *
     * @param search term to search Twitter for
     * @return String text body of the most recent tweet. 140char limit.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/")
    public GeneralResponse searchTwitter(@RequestParam(value = "q", defaultValue = "coffee") String search) throws TwitterException, EmptySearchException {
        try {
            GeneralResponse gr = new GeneralResponse();
            gr.setData(twitterService.latestTweet(search));
            return gr;
        } catch (TwitterException e) {
            throw e;
        }
    }

    /**
     * Searches Twitter for a search term, and returns 20 popular hits in json
     *
     * @param search term to search Twitter for
     * @return Twitter Response mapped object (not full json)
     */
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public GeneralResponse searchTwitterList(@RequestParam(value = "q", defaultValue = "coffee") String search) throws TwitterException, EmptySearchException {
        GeneralResponse gr = new GeneralResponse();
        gr.setData(twitterService.searchTwitterList(search));
        return gr;
    }

    /**
     * Posts a tweet to the @JavaRandomFun Twitter feed
     *
     * @param tweet the String to be posted.
     * @return String of what was posted to twitter
     */
    @RequestMapping(method = RequestMethod.POST, value = "/")
    public GeneralResponse createTweet(@RequestBody String tweet) throws TwitterException {
        GeneralResponse gr = new GeneralResponse();
        gr.setData(twitterService.createTweet(tweet));
        return gr;
    }

    /**
     * IBM Watson analyzes the tone of the text string.
     *
     * @param textToAnalyze - String to be analyzed
     * @return Watson's JSON response mapped onto a WatsonResponse POJO
     */
    @RequestMapping(method = RequestMethod.POST, value = "/watson")
    public WatsonResponse callWatson(@RequestBody String textToAnalyze) throws UnsupportedEncodingException {
        return watsonService.callWatson(textToAnalyze);
    }

    /**
     * IBM Watson specifically analyzes the emotional aspects of the text string
     *
     * @param textToAnalyze - String to be analyzed
     * @return String saying which emotion was the strongest, and what % Watson thought it was
     */
    @RequestMapping(method = RequestMethod.GET, value = "/emo")
    public String howEmotional(@RequestParam(value = "q", defaultValue = "blockchain") String textToAnalyze) throws UnsupportedEncodingException {
        return watsonService.emotionAnalyzer(textToAnalyze);
    }

    /**
     * IBM Watson analyzes the emotions for the most recent tweet of your search term
     *
     * @param search term to search Twitter for
     * @return String stating what was searched, the most recent tweet, and Watson's analysis
     */
    @RequestMapping(method = RequestMethod.GET, value = "/analyze")
    public String analyzeTweet(@RequestParam(value = "q", defaultValue = "monzo") String search) throws TwitterException, EmptySearchException, UnsupportedEncodingException {
        return twitterService.analyzeTweet(search);
    }

    /**
     * Takes the Watson tweet analysis, and then posts it to @JavaRandomFun Twitter feed
     *
     * @param search term to search Twitter for
     * @return String of what was posted to Twitter and a success message (if succesful)
     */
    @RequestMapping(method = RequestMethod.POST, value = "/analyze")
    public String postAnalysis(@RequestParam(value = "q", defaultValue = "monzo") String search)
            throws TwitterException, EmptySearchException, UnsupportedEncodingException, BadWordsFilterException {
        return twitterService.postAnalysis(search);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/filter")
    public String filter(@RequestParam(value = "text", defaultValue = "fuck this shit") String text)
            throws UnsupportedEncodingException, BadWordsFilterException {
        return twitterService.filterSwears(text);
    }
}
