package me.jamesliu.actor

import akka.actor.ActorRef
import akka.actor.typed.scaladsl.Behaviors

object PaymentHandler {
  def apply(configuration:ActorRef[Configuration.Message]) = Behaviors.setup(
    context =>
    Behaviors.empty
  )

  sealed trait PaymentMessage
  case class Handle(amount:Money,merchantId: MerchantId,userId:UserId) extends PaymentMessage
}
