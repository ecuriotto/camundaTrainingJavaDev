package com.camunda.training;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@Deployment(resources = "twitterDemo.bpmn")
@ExtendWith(ProcessEngineExtension.class)
public class ProcessJUnitTest {

  @Test
  public void testHappyPath() {
    // Create a HashMap to put in variables for the process instance
    Map variables = new HashMap<String, Object>();
    variables.put("approved", true);
    ProcessInstance processInstance =
        runtimeService().startProcessInstanceByKey("twitterProcess", variables);
    assertThat(processInstance).isEnded();

  }

}
