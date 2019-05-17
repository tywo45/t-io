package org.tio.websocket.client.kit;

import java.net.URI;

public class UriKit {
  public static URI parseURI(String uri){
    return URI.create(uri);
  }
}
