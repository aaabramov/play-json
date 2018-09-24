/*
 * Copyright (C) 2009-2018 Lightbend Inc. <https://www.lightbend.com>
 */

package play.api.libs.json

import scala.util.{ Failure, Success }

import play.api.libs.functional.Functor

import JsResult.functorJsResult

import org.scalatest._

class JsResultSpec extends WordSpec with MustMatchers {
  "JSON Result" should {
    "be functor" in {
      val jsres = JsSuccess("jsStr")

      implicitly[Functor[JsResult]].
        fmap[String, List[Char]](jsres, _.toList).mustEqual(
          JsSuccess(List('j', 's', 'S', 't', 'r'))
        )
    }

    "be converted to Success" in {
      JsResult.toTry(JsSuccess("foo")) mustEqual Success("foo")
    }

    "be converted to basic Failure" in {
      val err = JsError("bar")
      JsResult.toTry(err) mustEqual Failure(JsResult.Exception(err))
    }
  }

  "JsSuccess" should {
    "be recovered" in {
      val succ = JsSuccess("success")

      succ.recoverWith { _ => JsSuccess("fallback") } mustEqual succ
    }
  }

  "JsError" should {
    "be recovered" in {
      val succ = JsSuccess("success")

      JsError("err").recoverWith { _ => succ } mustEqual succ
    }
  }

  "JsSuccess#forall" should {

    "return true for JsSuccess(x * 2).forall(_ % 2 == 0)" in {
      JsSuccess(2).forall(_ % 2 == 0) mustBe true
    }

    "return false for JsSuccess(x, {x < 0}).forall(_ >)" in {
      JsSuccess(-1).forall(_ > 0) mustBe false
    }

  }

  "JsError#forall" should {

    "return true" in {
      JsError("").forall(_ => false) mustBe true
    }

  }
}
