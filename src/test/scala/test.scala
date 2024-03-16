import com.roshan.services.{AtomicServices, State}
import zio._

import java.util.UUID

object test extends ZIOAppDefault {

  val initid = UUID.randomUUID()

  val n = new AtomicServices()

  //val ref: ZIO[Any, Nothing, TRef[Map[UUID, Option[State]]]] = TRef.make[Map[UUID, Option[State]]](Map(initid -> None)).commit

  val state1 = State(Some(UUID.randomUUID()),"r","a","m","m",2,None)
  val state2 = State(Some(UUID.randomUUID()),"k","a","m","m",2,None)

   def program1(ref:Ref[Map[UUID, Option[State]]]) = {
     for {
       //re <- ref
       _ <- n.insertData(Some(state1),ref) *> Console.printLine(n.getData(state1.id.get,ref))
     } yield ()

   }
  def program12(ref:Ref[Map[UUID, Option[State]]]) =
    for {
      _ <- n.insertData(Some(state2),ref) *> Console.printLine(n.getData(state1.id.get,ref))
    } yield ()

val t = Ref.make[Map[UUID, Option[State]]](Map(initid -> None))//.commit
  def program(ref:Ref[Map[UUID, Option[State]]]) = for {
    //ref: Ref[Map[UUID, Option[State]]] <- t //TRef.make[Map[UUID, Option[State]]](Map(initid -> None)).commit
    f1 <- program1(ref).fork
    f2 <- program12(ref).fork
    _ <- f1.join
    _ <- f2.join
    rf <- ref.get
    _ <- ZIO.log(s"keys are finally ${rf.keys.toString}")
  } yield()

  def program3 = for {
    ref <- t //TRef.make[Map[UUID, Option[State]]](Map(initid -> None)).commit
    //f1 <- program1(ref).fork
    f2 <- program12(ref).fork
   // _ <- f1.join
    _ <- f2.join
  } yield ()

  def program4(ref:Ref[Map[UUID, Option[State]]]) = for {
    //ref <- t
    mp <- ref.get//.commit
    _ <- ZIO.succeed(Thread.sleep(2000))
    _ <- ZIO.log(s"keys are finally2 ${mp.keys.toString}")
  } yield ()

  val p = for {
    ref <- t
    _ <- program(ref) *> program4(ref)
  } yield ()



  def run = p *> t.flatMap(ref => program4(ref))

}
