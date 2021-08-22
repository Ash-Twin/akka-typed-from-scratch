package me.jamesliu.actor.discovery

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import me.jamesliu.actor.discovery.ActorX.XreceivefromY
import me.jamesliu.actor.discovery.SupplierProtocol.YreplytoX

class ActorY(actorContext: ActorContext[YreplytoX]) extends AbstractBehavior[YreplytoX](actorContext) {
  override def onMessage(msg: YreplytoX): Behavior[YreplytoX] = msg match {
    case YreplytoX(source, target, replyTox) =>
      val y = XreceivefromY(source, "Simplex")
      context.system.log.info(s"y reply to x:${y.toString}")
      replyTox ! y
      Behaviors.same
  }
}
object ActorY {

  def apply(): Behavior[YreplytoX] = Behaviors.setup[YreplytoX] { ctx =>
    new ActorY(ctx)
  }
}
