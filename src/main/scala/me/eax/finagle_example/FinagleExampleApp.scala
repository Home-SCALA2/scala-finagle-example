package me.eax.finagle_example

import com.escalatesoft.subcut.inject.NewBindingModule._
import com.twitter.finagle._
import com.twitter.finagle.http.{Http => _}
import com.twitter.util._
import me.eax.finagle_example.dao.{FinagleExampleStorage, FinagleExampleStorageImpl}
import me.eax.finagle_example.services._

object FinagleExampleApp extends App {
  implicit val bindings = newBindingModule
  {
    module =>
    import module._

    bind[FinagleExampleStorage] toSingle new FinagleExampleStorageImpl
  }

  val server = Http.serve(":8090", new FinagleExampleService)
  Await.ready(server)
}
