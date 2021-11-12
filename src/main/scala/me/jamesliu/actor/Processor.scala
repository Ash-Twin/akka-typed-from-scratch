package me.jamesliu.actor

import me.jamesliu.common.PaymentBase.{Amount, MerchantId, UserId}

object Processor {
  sealed trait ProcessorRequest
  case class Process(merchantId: MerchantId, amount: Amount, userId: UserId) extends ProcessorRequest

}
