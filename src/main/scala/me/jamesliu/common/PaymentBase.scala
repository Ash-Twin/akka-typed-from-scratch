package me.jamesliu.common

object PaymentBase {

  case class MerchantId(id: String)     extends AnyVal
  case class BankIdentifier(id: String) extends AnyVal
  case class Amount(amount: Double) extends AnyVal
  case class UserId(id:String) extends AnyVal
}
