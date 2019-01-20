Памятка по логированию в Scala и вообще мире Java
---

* https://eax.me/scala-logging
* `scala-finagle-example` https://github.com/afiskon/scala-finagle-example


```scala
class FinagleExampleDaoImpl extends FinagleExampleDao {
  private val finagleExampleDao = TrieMap.empty[String, String]
//  finagleExampleDao.put("/some-key", "Hello some-key!")
```

* `GET` http://localhost:8090/some-key
    ```text
        Hello some-key!
    ```


