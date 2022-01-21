package com.camunda.training;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import com.camunda.training.service.TwitterService;


@Deployment(resources = "twitterDemo.bpmn")
@ExtendWith(ProcessEngineCoverageExtension.class)
public class ProcessJUnitTest {

  @Mock
  TwitterService tweetService;

  @Test
  public void testHappyPath() throws Exception {
    // Mocks.register("createTweetDelegate", new CreateTweetDelegate(tweetService));
    when(tweetService.tweet(anyString())).thenReturn(anyLong());

    // Create a HashMap to put in variables for the process instance
    Map variables = new HashMap<String, Object>();
    variables.put("approved", true);
    variables.put("content", "Exercise 4 test - enrico " + System.currentTimeMillis());
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceByKey("twitterProcess", variables);
    assertThat(processInstance).isEnded();

  }

}
