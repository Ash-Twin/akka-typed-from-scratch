package me.jamesliu.actor.oop

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import me.jamesliu.actor.oop.PaymentHandler._
import me.jamesliu.common.PaymentBase._

class PaymentHandler(context: ActorContext[PaymentHandler.Cmd]) extends AbstractBehavior[PaymentHandler.Cmd](context) {
  override def onMessage(msg: PaymentHandler.Cmd): Behavior[PaymentHandler.Cmd] = {
    val configurationResponseAdaptor: ActorRef[Configuration.Response] =
      context.messageAdapter(response => WrappedConfigResponse(response))
    msg match {
      case Handle(data, replyTo) =>
        replyTo ! Configuration.Retrieve(data.merchantId, configurationResponseAdaptor)
        Behaviors.same
      case _                     =>
        context.log.error("error")
        Behaviors.same
    }
  }
}
object PaymentHandler {
  def apply(): Behavior[Cmd] = Behaviors.setup(context => new PaymentHandler(context))

  sealed trait Cmd

  case class Handle(data: HandleData, replyTo: ActorRef[Configuration.Cmd]) extends Cmd

  case class WrappedConfigResponse(response: Configuration.Response) extends Cmd

  case class HandleData(amount: Amount, merchantId: MerchantId, userId: UserId)

}
