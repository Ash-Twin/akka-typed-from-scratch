package me.jamesliu

import akka.actor.typed.ActorSystem
import me.jamesliu.actor.fp.PaymentProcessor

object Main {
  def main(args: Array[String]): Unit =
    ActorSystem[Nothing](PaymentProcessor.apply(), "typed-payment-processor")
}
