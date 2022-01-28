package com.camunda.training;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.camunda.training.service.TwitterService;
import twitter4j.TwitterException;


@ExtendWith({SpringExtension.class})
@ExtendWith(ProcessEngineCoverageExtension.class)
public class ProcessJUnitTest {

  @Mock
  TwitterService tweetService;

  @Test
  @Deployment(resources = {"tweetApproval.dmn", "twitterDemo.bpmn"})
  public void testHappyPath() throws TwitterException {
    // If we mock the service used in the delegate then in the task we should use "Delegate
    // expression"
    Mocks.register("createTweetDelegate", new CreateTweetDelegate(tweetService));

    when(tweetService.tweet(anyString())).thenReturn(anyLong());

    // Create a HashMap to put in variables for the process instance
    Map<String, Object> variables = new HashMap();
    variables.put("approved", true);
    variables.put("content", "Exercise - enrico " + System.currentTimeMillis());
    variables.put("email", "gigetto@gmail.com");
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceByKey("twitterProcess", variables);

    assertThat(processInstance).isStarted();
    List<String> activityList = runtimeService().getActiveActivityIds(processInstance.getId());
    System.out.println(activityList);

    // assertThat(processInstance).isWaitingAt("publishTweet");

    List<Job> jobList = jobQuery().processInstanceId(processInstance.getId()).list();
    /*
     * List<Job> jobList = jobQuery().processInstanceId(processInstance.getId()).list();
     * assertThat(jobList).hasSize(1); Job job = jobList.get(0); execute(job);
     */

    execute(job());
    assertThat(processInstance).isEnded();

  }


  @Deployment(resources = "twitterDemo.bpmn")
  @Test
  public void testTweetRejected() throws TwitterException {

    Map<String, Object> varMap = new HashMap();
    varMap.put("approved", false);
    varMap.put("content", "Exercise  - enrico " + System.currentTimeMillis());

    ProcessInstance processInstance = runtimeService().createProcessInstanceByKey("twitterProcess")
        .setVariables(varMap).startAfterActivity(findId("Review tweet")).execute();

    assertThat(processInstance).isWaitingAt(findId("Tweet rejected")).externalTask()
        .hasTopicName("notification");
    complete(externalTask());

    assertThat(processInstance).isEnded().hasPassed(findId("Tweet rejected"));
  }

  @Deployment(resources = "twitterDemo.bpmn")
  @Test
  public void testStartWithMessage() throws TwitterException {
    Map<String, Object> varMap = new HashMap();
    varMap.put("approved", false);
    varMap.put("content", "Exercise  - enrico " + System.currentTimeMillis());

    ProcessInstance processInstance = runtimeService().createMessageCorrelation("superUserTweet")
        .setVariable("content", "My Exercise 11 Tweet Enrico - " + System.currentTimeMillis())
        .correlateWithResult().getProcessInstance();

    assertThat(processInstance).isStarted();

    /*
     * The following is causing a MismatchingMessageCorrelationException because the process is not
     * expecting a message at this stage
     * 
     * runtimeService().createMessageCorrelation("tweetWithdrawn").correlateWithResult();
     */

    // We need to execute the job because we defined the service task "Publish tweet" as
    // asynchronous before
    execute(job());

  }


  @Test
  // @Deployment(resources = "tweetApproval.dmn")
  @Deployment(resources = {"tweetApproval.dmn", "twitterDemo.bpmn"})
  public void testTweetFromCeo() {
    Map<String, Object> variables =
        withVariables("email", "gigetto@gmail.com", "content", "this should be published");
    DmnDecisionTableResult decisionResult =
        decisionService().evaluateDecisionTableByKey("tweetApproval", variables);
    assertThat(decisionResult.getFirstResult()).contains(entry("approved", true));
  }

}
