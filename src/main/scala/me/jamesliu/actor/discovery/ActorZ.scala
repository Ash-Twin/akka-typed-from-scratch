package me.jamesliu.actor.discovery

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import me.jamesliu.actor.discovery.ActorX.XreceivefromZ
import me.jamesliu.actor.discovery.SupplierProtocol.ZreplytoX

class ActorZ(actorContext: ActorContext[ZreplytoX]) extends AbstractBehavior[ZreplytoX](actorContext) {
  override def onMessage(msg: ZreplytoX): Behavior[ZreplytoX] = msg match {
    case ZreplytoX(source, target,z, replyTox) =>
      val z = XreceivefromZ(source, "Moonpay","moon")
      context.system.log.info(s"z reply to x:${z.toString}")
      replyTox ! z
      Behaviors.same
  }
}
object ActorZ {

  def apply(): Behavior[ZreplytoX] = Behaviors.setup[ZreplytoX] { ctx =>
    new ActorZ(ctx)
  }
}
