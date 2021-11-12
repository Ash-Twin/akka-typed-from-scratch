package me.jamesliu.actor.discovery

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, scaladsl}
import akka.util.Timeout
import me.jamesliu.actor.discovery.ActorX.{Command, Data, XaskSome, XreceivefromY, XreceivefromZ}
import me.jamesliu.actor.discovery.SupplierProtocol.{YreplytoX, ZreplytoX}

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}
class ActorX(actorContext: ActorContext[Command]) extends AbstractBehavior[Command](actorContext) {
  import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
  implicit val timeout: Timeout                           = 5.seconds
  implicit val actorSystem: ActorSystem[Nothing]          = actorContext.system
  implicit val ec                                         = actorContext.executionContext
  val map:mutable.Map[String,String] = mutable.Map.empty
  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case XaskSome(source, target, actorRefy, actorRefz,actorRefSelf) =>
        context.system.log.info("x ask y:")
        actorRefy.ask(YreplytoX(source, target, _)) onComplete {
          case Success(value)     =>
            value match {
              case XreceivefromY(source, target) =>
                map.put(source,target)
                context.system.log.info(map.toString())
                context.system.log.info(source + " " + target)
            }
          case Failure(exception) => context.system.log.info(exception.getMessage)
        }
        context.system.log.info("x ask z:")
        actorRefz.ask(ZreplytoX(source, target, "mmon", _)) onComplete {
          case Success(value)     =>
            value match {
              case XreceivefromZ(baseFiat, target, z) =>
                map.put(z,target)
                context.system.log.info(map.toString())
                context.system.log.info(baseFiat + " " + target + " " + z)
            }
          case Failure(exception) => context.system.log.info(exception.getMessage)
        }
        context.system.log.info(map.toString())
        actorRefSelf ! Data(map)
        Behaviors.same
      case XreceivefromY(source,target) =>

        Behaviors.same
      case XreceivefromZ(baseFiat, target, z) =>

        Behaviors.same
    }
}
object ActorX {
  sealed trait Command
  case class XaskSome(
      source: String,
      target: String,
      actorRefy: ActorRef[SupplierProtocol.YreplytoX],
      actorRefz: ActorRef[SupplierProtocol.ZreplytoX],
      actorRefSelf:ActorRef[Response]
  )                                                                     extends Command
  case class XreceivefromY(source: String, target: String)              extends Command
  case class XreceivefromZ(baseFiat: String, target: String, z: String) extends Command
  sealed trait Response
  case class Data(map:mutable.Map[String,String])extends Response

  def apply(): Behavior[Command] = Behaviors.setup[Command] { ctx =>
    new ActorX(ctx)
  }
}
