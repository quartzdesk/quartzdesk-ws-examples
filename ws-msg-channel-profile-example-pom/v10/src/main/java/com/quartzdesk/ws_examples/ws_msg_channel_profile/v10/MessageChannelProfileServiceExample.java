package com.quartzdesk.ws_examples.ws_msg_channel_profile.v10;

import com.quartzdesk.service.client.msg_channel_profile.v10_0.MessageChannelProfileServiceClient;
import com.quartzdesk.service.msg_channel_profile.v10_0.GetFoldersRequest;
import com.quartzdesk.service.msg_channel_profile.v10_0.GetFoldersResponse;
import com.quartzdesk.service.msg_channel_profile.v10_0.GetProfilesRequest;
import com.quartzdesk.service.msg_channel_profile.v10_0.GetProfilesResponse;
import com.quartzdesk.service.types.v10.message.channel.MessageChannelProfile;
import com.quartzdesk.service.types.v10.message.channel.MessageChannelProfileFolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prints the list of all registered message channel profiles on the standard output.
 */
public class MessageChannelProfileServiceExample
{
  private static final Logger log = LoggerFactory.getLogger( MessageChannelProfileServiceExample.class );

  /*
   * Message Channel Profile Service endpoint parameters.
   */
  private static final String WS_MSG_CHANNEL_PROFILE_SERVICE_ENDPOINT_URL =
      "http://localhost:8080/quartzdesk/services/msg_channel_profile/v10_0/MessageChannelProfileService";

  private static final String WS_MSG_CHANNEL_PROFILE_SERVICE_USERNAME = "service";
  private static final String WS_MSG_CHANNEL_PROFILE_SERVICE_PASSWORD = "password";

  /**
   * Message Channel Profile Service client (part of the QuartzDesk Public API).
   */
  private MessageChannelProfileServiceClient client;


  public MessageChannelProfileServiceExample()
  {
    // prepare the client that can be reused for all requests
    client = new MessageChannelProfileServiceClient( WS_MSG_CHANNEL_PROFILE_SERVICE_ENDPOINT_URL,
        WS_MSG_CHANNEL_PROFILE_SERVICE_USERNAME, WS_MSG_CHANNEL_PROFILE_SERVICE_PASSWORD );
  }


  public static void main( String[] args )
      throws Exception
  {
    MessageChannelProfileServiceExample example = new MessageChannelProfileServiceExample();
    example.execute();
  }


  private void execute()
      throws Exception
  {
    printAllProfilesInFolder( 1L );  // 1 = root folder
  }


  private void printAllProfilesInFolder( Long folderId )
      throws Exception
  {
    GetProfilesRequest request = new GetProfilesRequest()
        .withParentFolderId( folderId );

    GetProfilesResponse response = client.getProfiles( request );
    for ( MessageChannelProfile profile : response.getProfile() )
    {
      printProfile( profile );
    }

    GetFoldersRequest request2 = new GetFoldersRequest()
        .withParentFolderId( folderId );

    GetFoldersResponse response2 = client.getFolders( request2 );
    for ( MessageChannelProfileFolder folder : response2.getFolder() )
    {
      printAllProfilesInFolder( folder.getId() );
    }
  }


  /**
   * Prints the details of the specified message channel profile.
   *
   * @param profile a message channel profile.
   */
  private void printProfile( MessageChannelProfile profile )
      throws Exception
  {
    System.out.println( "Message Channel Profile (" + profile.getId() + ')' +
        ", name=" + profile.getName() +
        ", config=" + profile.getChannelConfig() );
  }
}
