package me.jamesliu

import akka.actor.typed.ActorSystem
import me.jamesliu.actor.PaymentProcessor

object Main extends {
  def main(args: Array[String]): Unit = {
    ActorSystem[Nothing](PaymentProcessor(),"typed-payment-processor")
  }
}
