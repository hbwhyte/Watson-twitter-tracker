package twitter.controllers;

import twitter.model.external.Watson.WatsonResponse;
import twitter.services.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import twitter.services.WatsonService;

import java.util.List;

@RestController
@RequestMapping("/twitter")
public class TwitterController {

    @Autowired
    TwitterService twitterService;

    @Autowired
    WatsonService watsonService;

    /**
     * Searches twitter for the text of the most recent tweet of the search term
     *
     * @param search term to search Twitter for
     * @return String text body of the most recent tweet. 140char limit.
     */
    //
    @RequestMapping(method=RequestMethod.GET, value="/")
    public String searchTwitter(@RequestParam(value = "search", defaultValue = "cats") String search) {
        return twitterService.latestTweet(search);
    }

    /**
     * IBM Watson analyzes the tone of the text string.
     *
     * @param textToAnalyze - String to be analyzed
     * @return Watson's JSON response mapped onto a WatsonResponse POJO
     */
    @RequestMapping(method=RequestMethod.POST, value="/watson")
    public WatsonResponse callWatson(@RequestBody String textToAnalyze) {
        return watsonService.callWatson(textToAnalyze);
    }

    /**
     * IBM Watson specifically analyzes the emotional aspects of the text string
     *
     * @param textToAnalyze - String to be analyzed
     * @return String saying which emotion was the strongest, and what % Watson thought it was
     */
    @RequestMapping(method=RequestMethod.GET, value="/watson")
    public String howEmotional(@RequestParam(value = "tweet") String textToAnalyze) {
        return watsonService.emotionAnalyzer(textToAnalyze);
    }

    /**
     * IBM Watson analyzes the emotions for the most recent tweet of your search term
     *
     * @param search term to search Twitter for
     * @return String stating what was searched, the most recent tweet, and Watson's analysis
     */
    @RequestMapping(method=RequestMethod.GET, value="/analyze")
    public String analyzeTweet(@RequestParam(value = "search", defaultValue = "monzo") String search) {
        return twitterService.analyzeTweet(search);
    }

    /**
     * Takes the Watson tweet analysis, and then posts it to @JavaRandomFun twitter feed
     * @param search term to search Twitter for
     * @return String of what was posted to Twitter and a success message (if succesful)
     */
    @RequestMapping(method=RequestMethod.POST, value="/analyze")
    public String postAnalysis(@RequestParam(value = "search", defaultValue = "monzo") String search) {
        return twitterService.postAnalysis(search);
    }

    /**
     * Posts a tweet to the @JavaRandomFun twitter feed
     * @param tweet the String to be posted.
     * @return String of what was posted to twitter
     */
    @RequestMapping(method=RequestMethod.POST, value="/")
    public String createTweet(@RequestBody String tweet) {
        return twitterService.createTweet(tweet);
    }
//
//
//    @RequestMapping(method=RequestMethod.GET, value="/list")
//    public List<String> searchTwitterList(@RequestParam(value = "search", defaultValue = "dogs") String search) {
//        return twitterService.searchTwitterList(search);
//    }

}
