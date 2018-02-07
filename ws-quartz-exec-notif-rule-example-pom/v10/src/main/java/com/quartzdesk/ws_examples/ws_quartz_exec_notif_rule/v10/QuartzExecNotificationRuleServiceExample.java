package com.quartzdesk.ws_examples.ws_quartz_exec_notif_rule.v10;

import com.quartzdesk.service.client.quartz_exec_notif_rule.v10_0.QuartzExecNotificationRuleServiceClient;
import com.quartzdesk.service.quartz_exec_notif_rule.v10_0.GetRulesForSchedulerRequest;
import com.quartzdesk.service.quartz_exec_notif_rule.v10_0.GetRulesForSchedulerResponse;
import com.quartzdesk.service.types.v10.connection.SchedulerConnection;
import com.quartzdesk.service.types.v10.jmx.JmxProtocol;
import com.quartzdesk.service.types.v10.jmx.RemoteJmxService;
import com.quartzdesk.service.types.v10.scheduler.SchedulerType;
import com.quartzdesk.service.types.v10.scheduler.quartz.QuartzExecNotificationRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzExecNotificationRuleServiceExample
{
  private static final Logger log = LoggerFactory.getLogger( QuartzExecNotificationRuleServiceExample.class );

  /*
   * Quartz Exec Notification Rule Service endpoint parameters.
   */
  private static final String WS_QUARTZ_EXEC_NOTIF_RULE_SERVICE_ENDPOINT_URL =
      "http://localhost:8080/quartzdesk/services/quartz_exec_notif_rule/v10_0/QuartzExecNotificationRuleService";


  private static final String WS_QUARTZ_EXEC_NOTIF_RULE_SERVICE_USERNAME = "service";
  private static final String WS_QUARTZ_EXEC_NOTIF_RULE_SERVICE_PASSWORD = "password";

  private static final String SCHEDULER_OBJECT_NAME =
      "quartz:type=QuartzScheduler,name=quartzdesk-test-quartz-v2-3-x-logback";

  /*
   * Quartz scheduler JMX connection.
   */
  private static final SchedulerConnection SCHEDULER_CONNECTION = new SchedulerConnection()
      .withRemoteJmxService( new RemoteJmxService()
          .withProtocol( JmxProtocol.JMXMP )
          .withSecure( false )
          .withHost( "localhost" )
          .withPort( 11170 ) )
      .withSchedulerType( SchedulerType.QUARTZ )
      .withSchedulerObjectName( SCHEDULER_OBJECT_NAME );

  /**
   * Quartz Exec Notification Rule Service client (part of the QuartzDesk Public API).
   */
  private QuartzExecNotificationRuleServiceClient client;


  public QuartzExecNotificationRuleServiceExample()
  {
    // prepare the client that can be reused for all requests
    client = new QuartzExecNotificationRuleServiceClient(
        WS_QUARTZ_EXEC_NOTIF_RULE_SERVICE_ENDPOINT_URL,
        WS_QUARTZ_EXEC_NOTIF_RULE_SERVICE_USERNAME, WS_QUARTZ_EXEC_NOTIF_RULE_SERVICE_PASSWORD );
  }


  public static void main( String[] args )
      throws Exception
  {
    QuartzExecNotificationRuleServiceExample example = new QuartzExecNotificationRuleServiceExample();
    example.execute();
  }


  private void execute()
      throws Exception
  {
    GetRulesForSchedulerRequest request = new GetRulesForSchedulerRequest()
        .withConnection( SCHEDULER_CONNECTION );

    GetRulesForSchedulerResponse response = client.getRulesForScheduler( request );
    if ( response.getExecNotifRule().isEmpty() )
    {
      System.out.println(
          "Found no global Quartz execution notification rules for scheduler: " +
              request.getConnection().getSchedulerObjectName() );
    }
    else
    {
      for ( QuartzExecNotificationRule rule : response.getExecNotifRule() )
      {
        System.out.println(
            "Quartz Exec Notification Rule: ID=" + rule.getId() +
                ", name=" + rule.getName() +
                ", enabled=" + rule.getEnabled() +
                ", conditionType=" + rule.getConditionType() +
                ", messageChannelProfileId=" + rule.getMessageChannelProfileId() );
      }
    }
  }
}

