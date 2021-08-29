package oop

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import me.jamesliu.actor.Configuration.{Found, MerchantConfiguration, NotFound, Retrieve}
import me.jamesliu.actor.PaymentHandler._
import me.jamesliu.actor.{Configuration, PaymentHandler}
import me.jamesliu.common.PaymentBase.{Amount, BankIdentifier, MerchantId, UserId}
import org.scalatest.wordspec.AnyWordSpecLike

class PaymentHandlerSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "Payment Request" should {
    "Handle " should  {
      val probe    = createTestProbe[Configuration.Cmd]
      val pactor   = spawn(PaymentHandler.apply())

      "Message error" in{
        pactor ! _
        probe.expectNoMessage()
      }

      "Retrieve configuration and Found 'James' " in{
        pactor ! Handle(HandleData(Amount(3.2), MerchantId("James"), UserId("bank_i")), probe.ref)
        val response = probe.expectMessageType[Configuration.Retrieve]
        response.merchantId shouldBe MerchantId("James")
        val resProbe = createTestProbe[Configuration.Response]
        val resActor = spawn(Configuration.apply())
        resActor ! Retrieve(response.merchantId, resProbe.ref)
        val foundOrNotRes = resProbe.expectMessageType[Configuration.Response]
        foundOrNotRes shouldBe Found(MerchantId("James"), MerchantConfiguration(BankIdentifier("bank_identifier_1")))
      }

      "Retrieve configuration and NotFound 'Tim' " in{
        pactor ! Handle(HandleData(Amount(31.2), MerchantId("Tim"), UserId("bank_k")), probe.ref)
        val response = probe.expectMessageType[Configuration.Retrieve]
        response.merchantId shouldBe MerchantId("Tim")
        val resProbe = createTestProbe[Configuration.Response]
        val resActor = spawn(Configuration.apply())
        resActor ! Retrieve(response.merchantId, resProbe.ref)
        val foundOrNotRes = resProbe.expectMessageType[Configuration.Response]
        foundOrNotRes shouldBe NotFound(MerchantId("Tim"))
      }

    }

  }

}
