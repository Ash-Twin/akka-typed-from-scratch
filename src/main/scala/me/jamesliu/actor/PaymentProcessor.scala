package me.jamesliu.actor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object PaymentProcessor {
  def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
    context.log.info("Typed Payment Processor started")
    context.spawn(Configuration(), "config")
    Behaviors.empty
  }
}
