package com.camunda.training.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@Service
public class TwitterService {

  private final Logger LOGGER = LoggerFactory.getLogger(TwitterService.class.getName());

  public Long tweet(String content) throws TwitterException {

    try {

      LOGGER.info("Publishing tweet: " + content);
      AccessToken accessToken =
          new AccessToken("220324559-CO8TfUmrcoCrvFHP4TacgToN5hLC8cMw4n2EwmHo",
              "WvVureFv5TBWTGhESgGe3fqZM7XbGMuyIhxB84zgcoUER");
      Twitter twitter = new TwitterFactory().getInstance();
      twitter.setOAuthConsumer("lRhS80iIXXQtm6LM03awjvrvk",
          "gabtxwW8lnSL9yQUNdzAfgBOgIMSRqh7MegQs79GlKVWF36qLS");
      twitter.setOAuthAccessToken(accessToken);
      Status status = twitter.updateStatus(content);
      return status.getId();

    } catch (TwitterException te) {
      throw new org.camunda.bpm.engine.delegate.BpmnError("publishTweetErrorCode",
          te.getErrorMessage());
    }

  }
}
