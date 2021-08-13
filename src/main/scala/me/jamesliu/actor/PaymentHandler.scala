package me.jamesliu.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import me.jamesliu.common.PaymentBase._
object PaymentHandler {
  def apply(configuration: ActorRef[Configuration.Message]): Behavior[PaymentMessage] =
    Behaviors.setup[PaymentMessage] { context =>
      // TODO request configuration and handle payment request
      val configurationResponseAdaptor: ActorRef[Configuration.Response] =
        context.messageAdapter(response => WrappedConfigResponse(response))
      def handle(request:Map[MerchantId,Handle]):Behavior[PaymentMessage] = Behaviors.receiveMessage{
        case paymentRequest:Handle =>
          context.log.info("handle payment request -> configuration.retrieve")
        configuration ! Configuration.Retrieve(paymentRequest.merchantId,configurationResponseAdaptor)
          handle(request.updated(paymentRequest.merchantId,paymentRequest))
        case wrapped:WrappedConfigResponse =>
        wrapped.response match {
          case Configuration.NotFound(merchantId) =>
            context.log.info("Cannot handle request since no configuration found")
            Behaviors.same
          case Configuration.Found(merchantId,merchantConfiguration) =>
            context.log.info(s"handle request with configuration:${merchantId.id}->${merchantConfiguration.bankIdentifier.id}")
            Behaviors.same
        }
      }
      handle(request = Map.empty)
    }

  sealed trait PaymentMessage
  case class Handle(amount: Amount, merchantId: MerchantId, userId: UserId) extends PaymentMessage
  case class WrappedConfigResponse(response: Configuration.Response)        extends PaymentMessage
}