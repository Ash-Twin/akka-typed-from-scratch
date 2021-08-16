package oop

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import me.jamesliu
import me.jamesliu.actor.oop.Configuration.MerchantConfiguration
import me.jamesliu.actor.oop.Configuration.{Found, Retrieve}
import me.jamesliu.actor.oop.{Configuration, PaymentHandler}
import me.jamesliu.actor.oop.PaymentHandler._
import me.jamesliu.common.PaymentBase.{Amount, BankIdentifier, MerchantId, UserId}
import org.scalatest.wordspec.AnyWordSpecLike

class PaymentHandlerSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike{

  "Payment Request" should {
    "Handle and retrieve configuration" in {
      val probe = createTestProbe[Configuration.Cmd]
      val pactor = spawn(PaymentHandler.apply())
      pactor ! Handle(HandleData(Amount(3.2),MerchantId("James"),UserId("bank_i")),probe.ref)
      val response = probe.expectMessageType[Configuration.Retrieve]
      response.merchantId shouldBe MerchantId("James")

      val cprobe = createTestProbe[Configuration.Response]
      val cactor = spawn(Configuration.apply())
      cactor ! Retrieve(response.merchantId,cprobe.ref)
      val cresponse = cprobe.expectMessageType[Configuration.Response]
      cresponse shouldBe Found(MerchantId("James"),MerchantConfiguration(BankIdentifier("bank_identifier_1")))

    }
  }

}
