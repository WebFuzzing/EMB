package nl.knaw.huygens.timbuctoo.v5.security.twitterexample;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Path("/v5/twitter")
public class TwitterLogin {
  private String consumerKey = "bzY4pW3bdU3UbcDmsotJE5S54";
  private String consumerSecret = "9Dp2IfbDPpS8mMhB8CA9eBgOQP1fGTLguE0oAzrY98m02kBkmp";
  private Twitter twitter;
  private Map<String, RequestToken> tokenSecrets = new HashMap<>();


  public TwitterLogin() {
    twitter = TwitterFactory.getSingleton();
    twitter.setOAuthConsumer(consumerKey, consumerSecret);
  }

  @GET
  public Response login() throws TwitterException {
    RequestToken requestToken = twitter.getOAuthRequestToken();

    tokenSecrets.put(requestToken.getToken(), requestToken);

    return Response.temporaryRedirect(URI.create(requestToken.getAuthorizationURL())).build();
  }

  @Path("/callback")
  @GET
  public Response callback(@QueryParam("oauth_token") String token, @QueryParam("oauth_verifier") String verifier) {

    AccessToken accessToken;
    try {
      accessToken = twitter.getOAuthAccessToken(tokenSecrets.get(token), verifier);

      return Response.ok(accessToken.getToken() + "_" + accessToken.getTokenSecret()).build();
    } catch (Exception e) {
      return Response.status(400).entity("Access Token not found").build();
    }

  }
}
