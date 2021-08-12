import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import me.jamesliu.actor.Configuration
import me.jamesliu.actor.Configuration.MerchantId
import org.scalatest.wordspec.AnyWordSpecLike

class ConfigurationSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike{
  "The Configuration actor" should {
    "not find a configuration for an unknown merchant" in {
      val probe = createTestProbe[Configuration.Response]()
      val configActor = spawn(Configuration.apply())
      configActor ! Configuration.Retrieve(MerchantId("unknown"),probe.ref)
      val response = probe.expectMessageType[Configuration.NotFound]
      response.merchantId shouldBe MerchantId("unknown")
    }
  }
}
