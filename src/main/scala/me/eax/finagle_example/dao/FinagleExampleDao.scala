package me.eax.finagle_example.dao

import scala.collection.concurrent.TrieMap
import scala.concurrent._

trait FinagleExampleDao {
  def get(key: String): Future[Option[String]]
  def update(key: String, value: String): Future[Unit]
  def remove(key: String): Future[Unit]
}

class FinagleExampleDaoImpl extends FinagleExampleDao {
  private val finagleExampleDao = TrieMap.empty[String, String]
  finagleExampleDao.put("/some-key", "Hello some-key!") //TODO: fix test...

  def get(key: String): Future[Option[String]] = {
    val result = finagleExampleDao.get(key)
    Future.successful(result)
  }

  def update(key: String, value: String): Future[Unit] = {
    val result = finagleExampleDao.update(key, value)
    Future.successful(result)
  }

  def remove(key: String): Future[Unit] = {
    finagleExampleDao.remove(key)
    Future.successful({})
  }
}