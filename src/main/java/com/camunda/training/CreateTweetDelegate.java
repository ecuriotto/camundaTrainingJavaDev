package com.camunda.training;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.camunda.training.service.TwitterService;

@Component("createTweetDelegate")
public class CreateTweetDelegate implements JavaDelegate {

  private TwitterService twitterService;

  @Autowired
  public CreateTweetDelegate(TwitterService twitterService) {
    this.twitterService = twitterService;
  }

  public void execute(DelegateExecution execution) throws Exception {
    String content = (String) execution.getVariable("content");
    System.out.println("Content......" + content);

    if (content.equals("Network error")) {
      // throw new RuntimeException("simulated network error");
      throw new BpmnError("tweetErrorCode", "problem with twitter, do something");
    }

    twitterService.tweet(content);
  }

}
