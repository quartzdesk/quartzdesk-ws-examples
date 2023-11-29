package com.quartzdesk.ws_examples.ws_quartz.v10;

import com.quartzdesk.service.client.quartz.v5_0.QuartzServiceClient;
import com.quartzdesk.service.quartz.v5_0.AddJobRequest;
import com.quartzdesk.service.quartz.v5_0.AddJobResponse;
import com.quartzdesk.service.quartz.v5_0.DeleteJobRequest;
import com.quartzdesk.service.quartz.v5_0.DeleteJobResponse;
import com.quartzdesk.service.quartz.v5_0.GetJobDetailsRequest;
import com.quartzdesk.service.quartz.v5_0.GetJobDetailsResponse;
import com.quartzdesk.service.quartz.v5_0.GetSchedulerMonitorStateRequest;
import com.quartzdesk.service.quartz.v5_0.GetSchedulerMonitorStateResponse;
import com.quartzdesk.service.quartz.v5_0.GetTriggersRequest;
import com.quartzdesk.service.quartz.v5_0.GetTriggersResponse;
import com.quartzdesk.service.quartz.v5_0.TriggerJobRequest;
import com.quartzdesk.service.quartz.v5_0.TriggerJobResponse;
import com.quartzdesk.service.types.v5_0.connection.SchedulerConnection;
import com.quartzdesk.service.types.v5_0.jmx.JmxProtocol;
import com.quartzdesk.service.types.v5_0.jmx.RemoteJmxService;
import com.quartzdesk.service.types.v5_0.scheduler.SchedulerType;
import com.quartzdesk.service.types.v5_0.scheduler.quartz.QuartzJobDataMap;
import com.quartzdesk.service.types.v5_0.scheduler.quartz.QuartzJobDataMapEntry;
import com.quartzdesk.service.types.v5_0.scheduler.quartz.QuartzJobDetail;
import com.quartzdesk.service.types.v5_0.scheduler.quartz.QuartzJobGroup;
import com.quartzdesk.service.types.v5_0.scheduler.quartz.QuartzTrigger;
import com.quartzdesk.service.types.v5_0.scheduler.quartz.monitor.QuartzMonitorSchedulerState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a sample QuartzService web-service client that performs the following operations:
 * <ol>
 * <li>Obtains and prints the status of a remote Quartz scheduler instance.</li>
 * <li>Prints the list of all jobs registered in the remote Quartz scheduler.</li>
 * <li>Prints the list of all triggers registered in the remote Quartz scheduler.</li>
 * <li>Registers a new test job to the remote Quartz scheduler.</li>
 * <li>Triggers the newly added test job in the remote Quartz scheduler.</li>
 * <li>Removes the newly added test job from the remote Quartz scheduler.</li>
 * </ol>
 *
 * @version $Id:$
 */
public class QuartzServiceExample
{
  private static final Logger log = LoggerFactory.getLogger( QuartzServiceExample.class );

  /*
   * Web service endpoint parameters.
   */
  private static final String WS_ENDPOINT_URL =
      "http://localhost:8080/quartzdesk/services/quartz/v10_0/QuartzService";

  private static final String WS_USERNAME = "service";
  private static final String WS_PASSWORD = "password";

  private static final String SCHEDULER_OBJECT_NAME =
      "quartz:type=QuartzScheduler,name=quartzdesk-test-quartz-v2-3-x-logback";

  /*
   * Quartz scheduler JMX connection.
   */
  private static final SchedulerConnection
      SCHEDULER_CONNECTION = new SchedulerConnection()
      .withRemoteJmxService( new RemoteJmxService()
          .withProtocol( JmxProtocol.JMXMP )
          .withSecure( false )
          .withHost( "localhost" )
          .withPort( 11170 ) )
      .withSchedulerType( SchedulerType.QUARTZ )
      .withSchedulerObjectName( SCHEDULER_OBJECT_NAME );

  /*
   * Reusable Quartz Service web-service client provided by the QuartzDesk Public API.
   */
  private QuartzServiceClient client;


  public QuartzServiceExample()
  {
    // prepare the client that can be reused for all requests
    client = new QuartzServiceClient( WS_ENDPOINT_URL, WS_USERNAME, WS_PASSWORD );
  }


  public static void main( String[] args )
      throws Exception
  {
    QuartzServiceExample client = new QuartzServiceExample();

    client.printSchedulerStatus();

    client.listAllJobs();

    client.listAllTriggers();

    // add a new job test/SampleQuartzServiceClientTestJob
    client.addNewJob();

    // trigger the newly added job test/SampleQuartzServiceClientTestJob
    client.triggerJob();

    // delete the newly added job test/SampleQuartzServiceClientTestJob
    client.deleteJob();
  }


  /**
   * Obtains the monitoring state of the Quartz scheduler and prints the status in the log.
   *
   * @throws Exception if an error occurs.
   */
  private void printSchedulerStatus()
      throws Exception
  {
    GetSchedulerMonitorStateRequest request = new GetSchedulerMonitorStateRequest()
        .withConnection( SCHEDULER_CONNECTION );

    GetSchedulerMonitorStateResponse response = client.getSchedulerMonitorState( request );
    QuartzMonitorSchedulerState state = response.getState();
    System.out.println( "State of scheduler: " + state.name() );
  }


  /**
   * Obtains the list of all jobs from the Quartz scheduler and dumps the list in the log.
   *
   * @throws Exception if an error occurs.
   */
  private void listAllJobs()
      throws Exception
  {
    System.out.println( "Getting the list of all jobs registered in Quartz scheduler: " + SCHEDULER_OBJECT_NAME );

    GetJobDetailsRequest request = new GetJobDetailsRequest()
        .withConnection( SCHEDULER_CONNECTION );

    GetJobDetailsResponse response = client.getJobDetails( request );

    for ( QuartzJobDetail jobDetail : response.getJobDetail() )
    {
      System.out.println( "Job: " + jobDetail.getGroup().getName() + '/' + jobDetail.getName() +
          ", impl class: " + jobDetail.getJobClass() +
          ", data map: " + jobDetail.getJobDataMap() );
    }
  }


  /**
   * Obtains the list of all triggers from the Quartz scheduler and dumps the list in the log.
   *
   * @throws Exception if an error occurs.
   */
  private void listAllTriggers()
      throws Exception
  {
    System.out.println( "Getting the list of all triggers registered in Quartz scheduler: " + SCHEDULER_OBJECT_NAME );

    GetTriggersRequest request = new GetTriggersRequest()
        .withConnection( SCHEDULER_CONNECTION );

    GetTriggersResponse response = client.getTriggers( request );

    for ( QuartzTrigger trigger : response.getTrigger() )
    {
      System.out.println( "Trigger: " + trigger.getGroup().getName() + '/' + trigger.getName() +
          ", type: " + trigger.getClass().getSimpleName() +
          ", data map: " + trigger.getJobDataMap() );
    }
  }


  /**
   * Adds a new job test/SampleQuartzServiceClientTestJob to the Quartz scheduler.
   *
   * @throws Exception if an error occurs.
   */
  private void addNewJob()
      throws Exception
  {
    System.out.println( "Adding test job to Quartz scheduler: " + SCHEDULER_OBJECT_NAME );

    AddJobRequest request = new AddJobRequest()
        .withConnection( SCHEDULER_CONNECTION )

        .withJobDetail( new QuartzJobDetail()
            .withGroup( new QuartzJobGroup().withName( "test" ) )
            .withName( "SampleQuartzServiceClientTestJob" )
            .withJobClass( "com.quartzdesk.test.quartz.v2.TestJob" )
            .withDurability( true )
            .withShouldRecover( false )

            .withJobDataMap( new QuartzJobDataMap()
                .withEntry( new QuartzJobDataMapEntry()
                    .withName( "testKey" )
                    .withValue( "testValue" ) ) ) );

    AddJobResponse response = client.addJob( request );

    System.out.println( "Successfully added new job: " +
        request.getJobDetail().getGroup().getName() + '/' + request.getJobDetail().getName() );
  }


  /**
   * Triggers the added job test/SampleQuartzServiceClientTestJob in the Quartz scheduler.
   *
   * @throws Exception if an error occurs.
   */
  private void triggerJob()
      throws Exception
  {
    System.out.println( "Triggering test job in Quartz scheduler: " + SCHEDULER_OBJECT_NAME );

    TriggerJobRequest request = new TriggerJobRequest()
        .withConnection( SCHEDULER_CONNECTION )

        .withJobName( "SampleQuartzServiceClientTestJob" )
        .withJobGroupName( "test" )
        .withJobDataMap( new QuartzJobDataMap()
            .withEntry( new QuartzJobDataMapEntry()
                .withName( "newTestKey" )
                .withValue( "newTestValue" ) ) );

    TriggerJobResponse response = client.triggerJob( request );

    System.out.println( "Successfully triggered job: " + request.getJobGroupName() + '/' + request.getJobName() );
  }


  /**
   * Deletes the added job test/SampleQuartzServiceClientTestJob from the Quartz scheduler.
   *
   * @throws Exception if an error occurs.
   */
  private void deleteJob()
      throws Exception
  {
    System.out.println( "Removing test job from Quartz scheduler: " + SCHEDULER_OBJECT_NAME );

    DeleteJobRequest request = new DeleteJobRequest()
        .withConnection( SCHEDULER_CONNECTION )

        .withJobName( "SampleQuartzServiceClientTestJob" )
        .withJobGroupName( "test" );

    DeleteJobResponse response = client.deleteJob( request );

    System.out.println( "Successfully deleted job: " + request.getJobGroupName() + '/' + request.getJobName() );
  }
}
