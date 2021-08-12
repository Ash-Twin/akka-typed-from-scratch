package me.jamesliu.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import me.jamesliu.actor.Configuration.{MerchantConfiguration, MerchantId}

object Configuration {
  sealed trait Message
  final case class Retrieve(merchantId: MerchantId, replyTo: ActorRef[Response]) extends Message

  sealed trait Response
  final case class Found(merchantId: MerchantId, merchantConfiguration: MerchantConfiguration) extends Response
  final case class NotFound(merchantId: MerchantId)                                            extends Response

  case class MerchantId(id: String)     extends AnyVal
  case class BankIdentifier(id: String) extends AnyVal

  case class MerchantConfiguration(bankIdentifier: BankIdentifier)
  var configuration: Map[MerchantId, MerchantConfiguration] = Map.empty

  def apply(): Behavior[Configuration.Message] = Behaviors.receive { (context, message) =>
    context.log.info(s"arriving message type: ${message.getClass.toString} -> ${message.toString}")
    message match {
      case Configuration.Retrieve(merchantId, replyTo) =>
        configuration.get(merchantId) match {
          case Some(configuration) =>
            replyTo ! Configuration.Found(merchantId,configuration)
            Behaviors.same
          case None                =>
            replyTo ! Configuration.NotFound(merchantId)
            Behaviors.same
        }
    }
  }
}