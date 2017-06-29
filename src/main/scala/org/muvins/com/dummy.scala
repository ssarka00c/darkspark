package org.muvins.com

import net.ruippeixotog.scalascraper.model.ElementQuery
import org.jsoup.nodes.Element

/**
  * Created by subhankardeysarkar on 6/14/17.
  */
class dummy {

}

case class CityInfo(state:String,city:String,cityLink:String, population:Long,cityOnclickStr:String)

case class CityDetail()
{
  var populationSinceYear=""
  var population = "0"
  var populationIncreaseSinceYear=""
  var percentPopulationIncrease="0"

  override def toString: String =
    s"""populationSinceYear=$populationSinceYear
       |population=$population
       |populationIncreaseSinceYear=$populationIncreaseSinceYear
       |percentPopulationIncrease=$percentPopulationIncrease
     """.stripMargin
}

//{
//  override def toString: String = s"CityInfo($state,$city,$cityLink,$population$cityOnclickStr)"
//}

//trait HtmlExtractor[-E <: Element, +A] {
//  def extract(doc: ElementQuery[E]): A
//}

//class abc extends HtmlExtractor[Element,String] {
//
//  override def extract(doc: ElementQuery[Element]): String = {
//
//    //doc >>
//""
//  }

//}

