package twitter.monzo.controllers;

import twitter.monzo.model.external.Watson.WatsonResponse;
import twitter.monzo.services.MonzoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import twitter.monzo.services.WatsonService;
import twitter4j.JSONObject;

import java.util.List;

@RestController
@RequestMapping("/twitter")
public class MonzoController {

    @Autowired
    MonzoService monzoService;

    @Autowired
    WatsonService watsonService;


    @RequestMapping(method=RequestMethod.GET, value="/")
    public String searchTwitter(@RequestParam(value = "search", defaultValue = "cats") String search) {
        return monzoService.latestTweet(search);
    }

    @RequestMapping(method=RequestMethod.GET, value="/list")
    public List<String> searchTwitterList(@RequestParam(value = "search", defaultValue = "dogs") String search) {
        return monzoService.searchTwitterList(search);
    }
    // yes this works
    @RequestMapping(method=RequestMethod.POST, value="/watson")
    public WatsonResponse callWatson(@RequestBody String tweet) {
        return watsonService.callWatson(tweet);
    }
    // yes this works
    @RequestMapping(method=RequestMethod.GET, value="/watson")
    public String howEmotional(@RequestParam(value = "tweet") String tweet) {
        return watsonService.emotionAnalyzer(tweet);
    }

    @RequestMapping(method=RequestMethod.GET, value="/analyze")
    public String analyzeTweet(@RequestParam(value = "search", defaultValue = "monzo") String search) {
        return monzoService.analyzeTweet(search);
    }

    @RequestMapping(method=RequestMethod.POST, value="/analyze")
    public String postAnalysis(@RequestParam(value = "search", defaultValue = "monzo") String search) {
        return monzoService.postAnalysis(search);
    }

    //Posts a tweet
    @RequestMapping(method=RequestMethod.POST, value="/")
    public String createTweet(@RequestBody String tweet) {
        return monzoService.createTweet(tweet);
    }

//    @RequestMapping(method=RequestMethod.GET, value="/")
//    public String helloTwitter(Model model) {
//        if (connectionRepository.findPrimaryConnection(Twitter.class) == null) {
//            return "redirect:connect/twitter";
//        }
//
//        model.addAttribute(twitter.userOperations().getUserProfile());
//        CursoredList<TwitterProfile> friends = twitter.friendOperations().getFriends();
//        model.addAttribute("friends", friends);
//        return "twitter/hello";
//    }

}
