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
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
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

  @Deployment(resources = "twitterDemo.bpmn")
  @Test
  public void testHappyPath() throws TwitterException {
    // If we mock the service used in the delegate then in the task we should use "Delegate
    // expression"
    Mocks.register("createTweetDelegate", new CreateTweetDelegate(tweetService));
    when(tweetService.tweet(anyString())).thenReturn(anyLong());

    // Create a HashMap to put in variables for the process instance
    Map variables = new HashMap<String, Object>();
    variables.put("approved", true);
    variables.put("content", "Exercise 4 test - enrico " + System.currentTimeMillis());
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceByKey("twitterProcess", variables);

    assertThat(processInstance).isStarted();

    List<Task> taskList = taskService().createTaskQuery().taskCandidateGroup("management")
        .processInstanceId(processInstance.getId()).list();

    assert (taskList.size() == 1);

    // In order to complete the task
    complete(task(), (withVariables("approved", true)));
    assertThat(processInstance).isEnded();

  }

}
