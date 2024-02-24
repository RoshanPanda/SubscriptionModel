import com.roshan.models.SignUpReq
import org.scalatest.wordspec.AnyWordSpec
import zio.json.EncoderOps

class BasicTestSpec extends AnyWordSpec {

  "pattern test" in {
    val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$".r
    val res1 = pattern.matches("abc")
    val res2 = pattern.matches("Aikaaaaa@2")
    assert(!res1)
    assert(res2)
  }

  "json test" in {
    val req = SignUpReq("Roshan","Panda","RP","Roshan",30)
    println(req.toJson)
    assert(true)
  }

}
