# Watson Twitter Tracker

You might think of Twitter and think of scary news, funny cats, or angry mobs, but what is it *really* feeling? Let's find out!

The **Watson Twitter Tracker** allows you to search Twitter for the most recent tweet of anything, then shoots it off to Watson to analyze the true emotions of the tweet. The emotions Watson evaluates are:

 - Anger
 - Fear
 - Disgust
 - Sadness
 - Joy

This program figures out which of those emotions was strongest, and *how* strong it was, as a percentage.

If you want to get really crazy, you can automagically post those search terms right to a Twitter account! Go checkout [@randomJavaFun](https://twitter.com/randomJavaFun) on Twitter to see some of the random stuff analyzed so far. 

If you want to do this yourself, sorry, I'm not going to give you access to my Twitter, even this random side Twitter account with 3 followers. But feel free to add your own Twitter credentials to `twitter4j.properties` and go nuts! However, this is also why I added in Neutrino's Bad Word Filter API. It blocks posting any tweet with potentiall explicit content, just in case. Trust me, it's come in handy once or twice already during live demos. 

Have things you want to see analyzed on @randomJavaFun? Have suggestions for how I can improve or build on this? Let me know! 
 
### Tech Stack
 - Java 8
 - Spring Boot
 - Twitter4j
 - JUnit & Mockito


### Getting Setup
In order to run this application, you will need API keys from the following:
 - [IBM Watson Tone Analyzer API](https://www.ibm.com/watson/developercloud/tone-analyzer/)
 - [Twitter API](https://developer.twitter.com/en/docs) through [Twitter4j](http://twitter4j.org/)
 - [Neutrino Bad Word Filter API](https://www.neutrinoapi.com/api/bad-word-filter/) (because who knows what searching Twitter will bring up...)

Your IBM & Neutrino API key information goes in `application.properties` here:
```
watson.username= {USERNAME}
watson.password= {PASSWORD}

neutrino.user-id= {USERNAME}
neutrino.api-key= {APIKEY}
```

Your Twitter information goes in `twitter4j.properties`:
```
oauth.consumerKey = {KEY}
oauth.consumerSecret = {SECRET}
oauth.accessToken = {TOKEN}
oauth.accessTokenSecret = {TOKENSECRET}
```
**WARNING** - that will actually allow this app to post on your Twitter. 
If you don't want to do this on your personal account, either create a 
dummy account, or you can contact me directly.


### Endpoints

```
[GET] /twitter/analyze?q={search_term}
```
IBM Watson analyzes the emotions for the most recent tweet of your search term

```
[POST] /twitter/analyze?q={search_term}
```
Takes the Watson tweet analysis, and then posts it to the @JavaRandomFun Twitter feed (or yours if you put in your twitter credentials)

``` 
[GET] /twitter/?q={search_term} 
```
Basic Twitter: Searches twitter for the text of the most recent tweet of the search term.

``` 
[POST] /watson 
```
Basic Watson: IBM Watson analyzes the tone of the text string. Requires a RequestBody of raw text to analyze.
