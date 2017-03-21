package im.actor.server.session

import scodec.codecs.DiscriminatorCodec

/**
  * Created by diego on 21/03/17.
  */
object ExtraCodecs {

  var extraRequestCoded:DiscriminatorCodec[im.actor.api.rpc.Request,Int] = null

}
