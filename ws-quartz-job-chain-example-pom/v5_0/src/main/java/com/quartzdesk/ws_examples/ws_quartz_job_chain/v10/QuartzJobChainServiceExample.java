package com.quartzdesk.ws_examples.ws_quartz_job_chain.v10;

import com.quartzdesk.service.client.quartz_job_chain.v5_0.QuartzJobChainServiceClient;
import com.quartzdesk.service.quartz_job_chain.v5_0.GetJobChainsForSchedulerRequest;
import com.quartzdesk.service.quartz_job_chain.v5_0.GetJobChainsForSchedulerResponse;
import com.quartzdesk.service.types.v5_0.connection.SchedulerConnection;
import com.quartzdesk.service.types.v5_0.jmx.JmxProtocol;
import com.quartzdesk.service.types.v5_0.jmx.RemoteJmxService;
import com.quartzdesk.service.types.v5_0.scheduler.SchedulerType;
import com.quartzdesk.service.types.v5_0.scheduler.quartz.QuartzJobChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prints the list of all global (i.e. scheduler-wide) job chains rules of the specified Quartz
 * scheduler on the standard output.
 */
public class QuartzJobChainServiceExample
{
  private static final Logger log = LoggerFactory.getLogger( QuartzJobChainServiceExample.class );

  /*
   * Quartz Job Chain Service endpoint parameters.
   */
  private static final String WS_QUARTZ_JOB_CHAIN_SERVICE_ENDPOINT_URL =
      "http://localhost:8080/quartzdesk/services/quartz_job_chain/v10_0/QuartzJobChainService";

  private static final String WS_QUARTZ_JOB_CHAIN_SERVICE_USERNAME = "service";
  private static final String WS_QUARTZ_JOB_CHAIN_SERVICE_PASSWORD = "password";

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
   * Quartz Job Chain Service client (part of the QuartzDesk Public API).
   */
  private QuartzJobChainServiceClient client;


  public QuartzJobChainServiceExample()
  {
    // prepare the client that can be reused for all requests
    client = new QuartzJobChainServiceClient(
        WS_QUARTZ_JOB_CHAIN_SERVICE_ENDPOINT_URL,
        WS_QUARTZ_JOB_CHAIN_SERVICE_USERNAME, WS_QUARTZ_JOB_CHAIN_SERVICE_PASSWORD );
  }


  public static void main( String[] args )
      throws Exception
  {
    QuartzJobChainServiceExample example = new QuartzJobChainServiceExample();
    example.execute();
  }


  private void execute()
      throws Exception
  {
    GetJobChainsForSchedulerRequest request = new GetJobChainsForSchedulerRequest()
        .withConnection( SCHEDULER_CONNECTION );

    GetJobChainsForSchedulerResponse response = client.getJobChainsForScheduler( request );
    if ( response.getJobChain().isEmpty() )
    {
      System.out.println(
          "Found no global Quartz job chains for scheduler: " + request.getConnection().getSchedulerObjectName() );
    }
    else
    {
      for ( QuartzJobChain rule : response.getJobChain() )
      {
        System.out.println(
            "Quartz Job Chain: ID=" + rule.getId() +
                ", name=" + rule.getName() +
                ", enabled=" + rule.getEnabled() +
                ", conditionType=" + rule.getConditionType() +
                ", targets=" + rule.getTarget() );
      }
    }
  }
}

