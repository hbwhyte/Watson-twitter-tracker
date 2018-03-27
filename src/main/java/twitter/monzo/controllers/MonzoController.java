package twitter.monzo.controllers;

import twitter.monzo.model.internal.MonzoResponse;
import twitter.monzo.services.MonzoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.util.List;

@RestController
@RequestMapping("/twitter")
public class MonzoController {

    @Autowired
    MonzoService monzoService;

    @RequestMapping(method=RequestMethod.GET, value="/")
    public List<Status> searchTwitter(@RequestParam(value = "search", defaultValue = "@monzo") String search) {
        return MonzoService.searchTwitter(search);
    }

//    @RequestMapping(method=RequestMethod.POST, value="/")
//    public void updateStatus(@RequestParam(value = "search", defaultValue = "@monzo") String search) {
//        Twitter twitter = TwitterFactory.getSingleton();
//        Status status = twitter.updateStatus(latestStatus);
//    }

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
