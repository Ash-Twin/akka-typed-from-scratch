package me.jamesliu.actor.oop

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import me.jamesliu.actor.oop.PaymentHandler._
import me.jamesliu.common.PaymentBase._

class PaymentHandler(context:ActorContext[PaymentHandler.Cmd]) extends AbstractBehavior[PaymentHandler.Cmd](context){
   override def onMessage(paymentMsg: PaymentHandler.Cmd): Behavior[PaymentHandler.Cmd] =
    Behaviors.setup[PaymentHandler.Cmd] { context =>
      // TODO request configuration and handle payment request
      val configurationResponseAdaptor: ActorRef[Configuration.Response] =
        context.messageAdapter(response => WrappedConfigResponse(response))

      def handle(request: Map[MerchantId, Handle]): Behavior[PaymentHandler.Cmd] = Behaviors.receiveMessage {
        case Handle(data,replyTo) =>
          context.log.info("handle payment request -> configuration.retrieve")
          replyTo ! Configuration.Retrieve(data.merchantId,configurationResponseAdaptor)
//          handle(request.updated(data.merchantId, replyTo))
          Behaviors.same
        case wrapped: WrappedConfigResponse =>
          wrapped.response match {
            case Configuration.NotFound(merchantId) =>
              context.log.info("Cannot handle request since no configuration found")
              Behaviors.same
            case Configuration.Found(merchantId, merchantConfiguration) =>
              context.log.info(
                s"handle request with configuration:${merchantId.id}->${merchantConfiguration.bankIdentifier.id}"
              )
              Behaviors.same
          }
      }

      handle(request = Map.empty)
    }

}
object PaymentHandler{
  sealed trait Cmd

  case class Handle(data:HandleData,replyTo:ActorRef[Configuration.Cmd]) extends Cmd

  case class WrappedConfigResponse(response: Configuration.Response) extends Cmd

  case class HandleData(amount: Amount, merchantId: MerchantId, userId: UserId)

}
