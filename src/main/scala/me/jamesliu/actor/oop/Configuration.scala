package me.jamesliu.actor.oop

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import me.jamesliu.actor.oop.Configuration._
import me.jamesliu.common.PaymentBase._

class Configuration(context: ActorContext[Configuration.Cmd]) extends AbstractBehavior[Configuration.Cmd](context) {
  var configuration: Map[MerchantId, MerchantConfiguration] = Map(
    MerchantId("James") -> MerchantConfiguration(BankIdentifier("bank_identifier_1"))
  )

  override def onMessage(msg: Cmd): Behavior[Cmd] = msg match {
    case Retrieve(merchantId, replyTo) =>
      configuration.get(merchantId) match {
        case Some(value) => replyTo ! Found(merchantId, value)
        case None        => replyTo ! NotFound(merchantId)
      }
      Behaviors.same
  }
}
object Configuration {

  def apply(): Behavior[Cmd] = Behaviors.setup { context =>
    new Configuration(context)
  }

  sealed trait Cmd

  sealed trait Response

  final case class Retrieve(merchantId: MerchantId, replyTo: ActorRef[Response]) extends Cmd

  final case class Found(merchantId: MerchantId, merchantConfiguration: MerchantConfiguration) extends Response

  final case class NotFound(merchantId: MerchantId) extends Response

  case class MerchantConfiguration(bankIdentifier: BankIdentifier)
}
