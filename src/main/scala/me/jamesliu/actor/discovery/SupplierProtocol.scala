package me.jamesliu.actor.discovery

import akka.actor.typed.ActorRef

object SupplierProtocol {
  trait Command
  case class ZreplytoX(source: String, target: String,z:String, replyTox: ActorRef[ActorX.Command]) extends Command
  case class YreplytoX(baseFiat: String, targetDigit: String, replyTox: ActorRef[ActorX.Command]) extends Command
}
