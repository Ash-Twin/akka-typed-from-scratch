package me.jamesliu.actor.processor

import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import me.jamesliu.actor.Processor.ProcessorRequest

object CreditCardProcessor {
  def process: Behavior[ProcessorRequest] = Behaviors.setup { context =>
  //to make this actor discoverable
    context.system.receptionist ! Receptionist.Register(Key, context.self)
    //TODO need to fill the real handle func
    Behaviors.unhandled
  }
  val Key: ServiceKey[ProcessorRequest]   = ServiceKey("creditCardProcessor")
}
