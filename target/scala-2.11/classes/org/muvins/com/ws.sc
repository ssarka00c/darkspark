//val pattern = "[\\W|\\w]*[P][a-z ]*(\\d*)[ \\:]+[\\W|\\w]*".r
//
//val pattern(num) = "Population in 23:"
//
//println("mmm" + num)
//
//val v = List(1,2,3,4)
//v.sliding(2).toList

import org.scalatest._

val lst = List("x","y")
lst.exists("aory".contains(_))