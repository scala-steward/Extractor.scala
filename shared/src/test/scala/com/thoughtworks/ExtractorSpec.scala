package com.thoughtworks

import org.scalatest.Inside
import Extractor._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/**
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
class ExtractorSpec extends AnyFreeSpec with Matchers with Inside {

  "PartialFunction toExtractor" in {

    val pf: PartialFunction[Int, String] = {
      case 1 => "match"
    }

    1 match {
      case pf.extract(m) => m should be("match")
    }

  }

  "PartialFunction toExtractor for Seq" in {
    val pf: PartialFunction[Int, Seq[String]] = {
      case 1 => Seq("match1", "match2")
    }
    1 match {
      case pf.extract.seq(m1, m2) =>
        m1 should be("match1")
        m2 should be("match2")
    }
  }

  "PartialFunction toExtractor for all" in {
    val pf: PartialFunction[Int, String] = {
      case 1 => "match1"
    }
    inside(Seq(1, 1)) {
      case pf.extract.forall(m1) =>
        m1 should be(Seq("match1", "match1"))
    }
    Seq(1, 2) shouldNot matchPattern {
      case pf.extract.forall(m1) =>
    }
  }

  "PartialOptionFunction toExtractor" in {

    val pf: PartialFunction[Int, Option[String]] = {
      case 1 => Some("match")
    }

    1 match {
      case pf.extract(m) => m should be(Some("match"))
    }

  }

  "example" in {
    val pf: PartialFunction[Int, String] = {
      case 1 => "matched by PartialFunction"
    }
    val f: Int => Option[String] = { i =>
      if (i == 2) {
        Some("matched by optional function")
      } else {
        None
      }
    }
    val pf2: PartialFunction[Int, String] = Function.unlift(f)

    def test(i: Int) = {
      i match {
        case pf.extract(m) =>
          m
        case f.extract(m) =>
          m
        case pf2.extract.extract.extract.extract.extract(m) =>
          throw new AssertionError("This case should never occur because it has the same condition as `f.extract`.")
        case _ =>
          "Not matched"
      }
    }
    test(0) should be("Not matched")
    test(1) should be("matched by PartialFunction")
    test(2) should be("matched by optional function")
    test(3) should be("Not matched")
  }

}
