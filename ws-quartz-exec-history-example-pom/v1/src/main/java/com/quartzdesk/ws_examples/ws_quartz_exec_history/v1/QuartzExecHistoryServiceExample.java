package com.quartzdesk.ws_examples.ws_quartz_exec_history.v1;

import com.quartzdesk.service.client.quartz_exec_history.v1_0.QuartzExecHistoryServiceClient;
import com.quartzdesk.service.quartz_exec_history.v1_0.GetHistoryForSchedulerRequest;
import com.quartzdesk.service.quartz_exec_history.v1_0.GetHistoryForSchedulerResponse;
import com.quartzdesk.service.types.v3_0.connection.SchedulerConnection;
import com.quartzdesk.service.types.v3_0.jmx.JmxProtocol;
import com.quartzdesk.service.types.v3_0.jmx.RemoteJmxService;
import com.quartzdesk.service.types.v3_0.scheduler.SchedulerType;
import com.quartzdesk.service.types.v3_0.scheduler.quartz.QuartzExecHistory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzExecHistoryServiceExample
{
  private static final Logger log = LoggerFactory.getLogger( QuartzExecHistoryServiceExample.class );

  /*
   * Quartz Exec History Service endpoint parameters.
   */
  private static final String WS_QUARTZ_EXEC_HISTORY_SERVICE_ENDPOINT_URL =
      "http://localhost:8080/quartzdesk/services/quartz_exec_history/v1_0/QuartzExecHistoryService";

  private static final String WS_QUARTZ_EXEC_HISTORY_SERVICE_USERNAME = "service";
  private static final String WS_QUARTZ_EXEC_HISTORY_SERVICE_PASSWORD = "password";

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
   * Quartz Exec History Service client (part of the QuartzDesk Public API).
   */
  private QuartzExecHistoryServiceClient client;


  public QuartzExecHistoryServiceExample()
  {
    // prepare the client that can be reused for all requests
    client = new QuartzExecHistoryServiceClient( WS_QUARTZ_EXEC_HISTORY_SERVICE_ENDPOINT_URL,
        WS_QUARTZ_EXEC_HISTORY_SERVICE_USERNAME, WS_QUARTZ_EXEC_HISTORY_SERVICE_PASSWORD );
  }


  public static void main( String[] args )
      throws Exception
  {
    QuartzExecHistoryServiceExample example = new QuartzExecHistoryServiceExample();
    example.execute();
  }


  private void execute()
      throws Exception
  {
    GetHistoryForSchedulerRequest request = new GetHistoryForSchedulerRequest()
        .withConnection( SCHEDULER_CONNECTION )
        .withMaxFetchSize( 10 );  // get at most 10 exec history records

    GetHistoryForSchedulerResponse response = client.getHistoryForScheduler( request );
    if ( response.getHistory().isEmpty() )
    {
      System.out.println( "Found no Quartz execution history records for scheduler: " +
          request.getConnection().getSchedulerObjectName() );
    }
    else
    {
      for ( QuartzExecHistory execHistory : response.getHistory() )
      {
        System.out.println(
            "Quartz Exec History Record: ID=" + execHistory.getId() +
                ", status=" + execHistory.getExecStatus() +
                ", startedAt=" + execHistory.getStartedAt().getTime() +
                ", finishedAt=" + execHistory.getFinishedAt().getTime() +
                ", jobGroupName=" + execHistory.getJobGroupName() +
                ", jobName=" + execHistory.getJobName() +
                ", triggerGroupName=" + execHistory.getTriggerGroupName() +
                ", triggerName=" + execHistory.getTriggerName() +
                ", threadGroup=" + execHistory.getThreadGroupName() +
                ", threadName=" + execHistory.getThreadName() );
      }
    }
  }
}

