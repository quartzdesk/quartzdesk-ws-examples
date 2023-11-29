package com.quartzdesk.ws_examples.ws_connection.v10;

import com.quartzdesk.service.client.connection.v5_0.ConnectionServiceClient;
import com.quartzdesk.service.connection.v5_0.AddConnectionRequest;
import com.quartzdesk.service.connection.v5_0.AddConnectionResponse;
import com.quartzdesk.service.connection.v5_0.DeleteConnectionRequest;
import com.quartzdesk.service.connection.v5_0.DeleteConnectionResponse;
import com.quartzdesk.service.connection.v5_0.GetConnectionsRequest;
import com.quartzdesk.service.connection.v5_0.GetConnectionsResponse;
import com.quartzdesk.service.connection.v5_0.GetFoldersRequest;
import com.quartzdesk.service.connection.v5_0.GetFoldersResponse;
import com.quartzdesk.service.types.v5_0.connection.Connection;
import com.quartzdesk.service.types.v5_0.connection.ConnectionFolder;
import com.quartzdesk.service.types.v5_0.connection.SchedulerConnection;
import com.quartzdesk.service.types.v5_0.jmx.JmxProtocol;
import com.quartzdesk.service.types.v5_0.jmx.RemoteJmxService;
import com.quartzdesk.service.types.v5_0.scheduler.SchedulerType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prints the list of all registered scheduler connections on the standard output.
 */
public class ConnectionServiceExample
{
  private static final Logger log = LoggerFactory.getLogger( ConnectionServiceExample.class );

  /*
   * Connection Service endpoint parameters.
   */
  private static final String WS_CONNECTION_SERVICE_ENDPOINT_URL =
      "http://localhost:8080/quartzdesk/services/connection/v10_0/ConnectionService";

  private static final String WS_CONNECTION_SERVICE_USERNAME = "service";

  private static final String WS_CONNECTION_SERVICE_PASSWORD = "password";

  /**
   * Connection Service client (part of the QuartzDesk Public API).
   */
  private ConnectionServiceClient client;


  public ConnectionServiceExample()
  {
    // prepare the client that can be reused for all requests
    client = new ConnectionServiceClient( WS_CONNECTION_SERVICE_ENDPOINT_URL,
        WS_CONNECTION_SERVICE_USERNAME, WS_CONNECTION_SERVICE_PASSWORD );
  }


  public static void main( String[] args )
      throws Exception
  {
    ConnectionServiceExample example = new ConnectionServiceExample();
    example.execute();
  }


  private void execute()
      throws Exception
  {
    Long connectionId = registerConnection();

    printAllRegisteredConnectionsInFolder( 1L );  // 1 = root folder

    unregisterConnection( connectionId );
  }


  private Long registerConnection()
      throws Exception
  {
    AddConnectionRequest request = new AddConnectionRequest()
        .withConnection( new SchedulerConnection()
            .withName( "quartzdesk-ws-examples" )
            .withRemoteJmxService( new RemoteJmxService()
                .withHost( "localhost" )
                .withPort( 11099 )
                .withProtocol( JmxProtocol.JMXMP )
                .withSecure( false ) )
            .withSchedulerObjectName( "quartz:type=QuartzScheduler,name=quartzdesk-ws-examples" )
            .withSchedulerType( SchedulerType.QUARTZ ) )
        .withParentFolderId( 1L );  // 1 = root folder

    AddConnectionResponse response = client.addConnection( request );
    return response.getConnectionId();
  }


  private void unregisterConnection( Long connectionId )
      throws Exception
  {
    DeleteConnectionRequest request = new DeleteConnectionRequest()
        .withConnectionId( connectionId );

    DeleteConnectionResponse response = client.deleteConnection( request );
  }


  private void printAllRegisteredConnectionsInFolder( Long folderId )
      throws Exception
  {
    GetConnectionsRequest request = new GetConnectionsRequest()
        .withParentFolderId( folderId );

    GetConnectionsResponse response = client.getConnections( request );
    for ( Connection connection : response.getConnection() )
    {
      if ( connection instanceof SchedulerConnection )
      {
        printRegisteredConnection( (SchedulerConnection) connection );
      }
      else
      {
        // currently we only have SchedulerConnections, in future versions we plan to add support for ClusteredConnections
        throw new IllegalStateException( "Unsupported connection type: " + connection.getClass().getName() );
      }
    }

    GetFoldersRequest request2 = new GetFoldersRequest()
        .withParentFolderId( folderId );

    GetFoldersResponse response2 = client.getFolders( request2 );
    for ( ConnectionFolder folder : response2.getFolder() )
    {
      printAllRegisteredConnectionsInFolder( folder.getId() );
    }
  }


  /**
   * Prints the details of the specified scheduler connection.
   *
   * @param connection a scheduler connection.
   */
  private void printRegisteredConnection( SchedulerConnection connection )
      throws Exception
  {
    System.out.println( "Scheduler Connection (" + connection.getId() + ')' +
        ", name=" + connection.getName() +
        ", schedulerType=" + connection.getSchedulerType() +
        ", host=" + connection.getRemoteJmxService().getHost() +
        ", port=" + connection.getRemoteJmxService().getPort() +
        ", objectName=" + connection.getSchedulerObjectName() );
  }
}
