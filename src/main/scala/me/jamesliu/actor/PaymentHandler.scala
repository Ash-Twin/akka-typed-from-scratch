package me.jamesliu.actor

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.Receptionist.Listing
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import me.jamesliu.actor.Configuration.{Found, NotFound}
import me.jamesliu.actor.PaymentHandler._
import me.jamesliu.actor.processor.CreditCardProcessor
import me.jamesliu.common.PaymentBase._

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class PaymentHandler(context: ActorContext[PaymentHandler.Cmd]) extends AbstractBehavior[PaymentHandler.Cmd](context) {
  override def onMessage(msg: PaymentHandler.Cmd): Behavior[PaymentHandler.Cmd] = {
//    def handle(requests: Map[MerchantId, Handle]): Behavior[PaymentHandler.Cmd] =
      msg match {
//        case AddProcessorReference(listing) =>
//          apply(paymentProcessors+listing)
        case Handle(data, replyTo)                                                    =>
          implicit val timeout: Timeout                                        = 1.second
          def buildConfigurationRequest(ref: ActorRef[Configuration.Response]) =
            Configuration.Retrieve(data.merchantId, ref)
          context.ask(replyTo, buildConfigurationRequest) {
            case Success(response: Configuration.Response) => AdaptedConfigResponse(response, Handle(data, replyTo))
            case Failure(exception)                        => ConfigurationFailure(exception)
          }
//          handle(requests.updated(data.merchantId,Handle(data,replyTo)))
          Behaviors.same
        case AdaptedConfigResponse(Found(merchantId, merchantConfiguration), request) =>
          // TODO relay the request to the proper payment processor
          Behaviors.unhandled
        case AdaptedConfigResponse(NotFound(merchantId), _)                           =>
          context.log.warn(s"$merchantId not found")
          Behaviors.same
        case ConfigurationFailure(exception)                                          =>
          context.log.warn(s"cannot retrieve configuration:${exception.getMessage}")
          Behaviors.same
      }
//    handle(requests = Map.empty)
  }
}
object PaymentHandler {
  def apply(): Behavior[Cmd] = Behaviors.setup {
    context =>
//      val listingAdaptor: ActorRef[Receptionist.Listing] = context.messageAdapter { listing =>
//        AddProcessorReference(listing)
//      }
//      context.system.receptionist ! Receptionist.Subscribe(CreditCardProcessor.Key, listingAdaptor)
      new PaymentHandler(context)
  }

  sealed trait Cmd

  case class Handle(data: HandleData, replyTo: ActorRef[Configuration.Cmd]) extends Cmd

  case class AdaptedConfigResponse(response: Configuration.Response, request: Handle) extends Cmd

  case class ConfigurationFailure(exception: Throwable) extends Cmd

  case class AddProcessorReference(listing: Receptionist.Listing) extends Cmd

  case class HandleData(amount: Amount, merchantId: MerchantId, userId: UserId)

}
