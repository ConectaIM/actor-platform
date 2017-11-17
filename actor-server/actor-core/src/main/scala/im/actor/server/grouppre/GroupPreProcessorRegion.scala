package im.actor.server.grouppre

/**
  * Created by 98379720172 on 31/01/17.
  */
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import im.actor.server.GroupPreEnvelope

object GroupPreProcessorRegion {

  private def extractEntityId(system: ActorSystem): ShardRegion.ExtractEntityId = {
    case c:GroupPreCommand => (c.groupId.toString, c)
    case q:GroupPreQuery => (q.userId.toString, q)
    case env @ GroupPreEnvelope(groupId, command, query) ⇒
      (
        groupId.toString,
        if (query.isDefined) {
          env.getField(GroupPreEnvelope.javaDescriptor.findFieldByNumber(query.number))
        } else {
          env.getField(GroupPreEnvelope.javaDescriptor.findFieldByNumber(command.number))
        }
      )
  }

  private def extractShardId(system: ActorSystem): ShardRegion.ExtractShardId = {
    case c:GroupPreCommand => (c.groupId % 100).toString
    case q: GroupPreQuery => (q.userId % 100).toString
    case e: GroupPreEnvelope ⇒ (e.groupId % 100).toString
  }


  private val typeName = "GroupPreProcessor"

  private def start(props: Props)(implicit system: ActorSystem): GroupPreProcessorRegion =
    GroupPreProcessorRegion(ClusterSharding(system).start(
      typeName = typeName,
      entityProps = props,
      settings = ClusterShardingSettings(system),
      extractEntityId = extractEntityId(system),
      extractShardId = extractShardId(system)
    ))

  def start()(implicit system: ActorSystem): GroupPreProcessorRegion = start(GroupPreProcessor.props)

  def startProxy()(implicit system: ActorSystem): GroupPreProcessorRegion =
    GroupPreProcessorRegion(ClusterSharding(system).startProxy(
      typeName = typeName,
      role = None,
      extractEntityId = extractEntityId(system),
      extractShardId = extractShardId(system)
    ))
}

case class GroupPreProcessorRegion(ref: ActorRef)

case class GroupPreViewRegion(ref: ActorRef)
