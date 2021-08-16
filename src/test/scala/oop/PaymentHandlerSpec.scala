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
      val probe    = createTestProbe[PaymentHandler.Cmd]
      val pactor   = spawn(PaymentHandler.apply())

      "Message error" in{
        pactor ! _
        probe.expectNoMessage()
      }

      "Retrieve configuration and Found 'James' " in{
        val cmdProbe = createTestProbe[Configuration.Cmd]
        pactor ! Handle(HandleData(Amount(3.2), MerchantId("James"), UserId("bank_i")), cmdProbe.ref)
        val retrieve = cmdProbe.expectMessageType[Retrieve]
        val cactor = spawn(Configuration.apply())
        val resProbe = createTestProbe[Configuration.Response]
        cactor ! Configuration.Retrieve(retrieve.merchantId,resProbe.ref)
        val cRes  = resProbe.expectMessageType[Configuration.Found]
        cRes shouldBe Found(MerchantId("James"),MerchantConfiguration(BankIdentifier("bank_identifier_1")))
        //        response
//        resActor ! Retrieve(response.merchantId, cmdProbe.ref)
//        val foundOrNotRes = cmdProbe.expectMessageType[Configuration.Response]
//        foundOrNotRes shouldBe Found(MerchantId("James"), MerchantConfiguration(BankIdentifier("bank_identifier_1")))
      }

      "Retrieve configuration and NotFound 'Tim' " in{

        val cmdProbe = createTestProbe[Configuration.Cmd]
        pactor ! Handle(HandleData(Amount(31.2), MerchantId("Tim"), UserId("bank_k")), cmdProbe.ref)
        val adapted = cmdProbe.expectMessageType[Configuration.Retrieve]
//        adapted.response shouldBe NotFound
//        response.merchantId shouldBe MerchantId("Tim")
//        val cmdProbe = createTestProbe[Configuration.Response]
//        val resActor = spawn(Configuration.apply())
//        resActor ! Retrieve(response.merchantId, cmdProbe.ref)
//        val foundOrNotRes = cmdProbe.expectMessageType[Configuration.Response]
//        foundOrNotRes shouldBe NotFound(MerchantId("Tim"))
      }

    }

  }

}
