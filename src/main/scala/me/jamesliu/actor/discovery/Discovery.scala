package me.jamesliu.actor.discovery

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import me.jamesliu.actor.discovery.ActorX.XaskSome

import scala.concurrent.duration.DurationInt
import scala.util.Success

object Discovery {

  def main(args: Array[String]): Unit = {
    val sys = ActorSystem.create(
      Behaviors.setup[Done] {
        ctx =>
          implicit val timeout:Timeout=10.seconds
          implicit val actorSystem: ActorSystem[Nothing]          = ctx.system
          implicit val ec                                         = ctx.executionContext
          val actor_x = ctx.spawn(ActorX.apply(), "actor_x")
          val actor_y = ctx.spawn(ActorY.apply(), "actor_y")
          val actor_z = ctx.spawn(ActorZ.apply(), "actor_z")
          val eventualResponse = actor_x.ask(XaskSome("x", "y", actor_y, actor_z, _))
          eventualResponse onComplete {
            case Success(value) => ctx.system.log.info("eventualResponse:"+value.toString)
          }
          Behaviors.empty
      },
      "sys"
    )

  }
}
