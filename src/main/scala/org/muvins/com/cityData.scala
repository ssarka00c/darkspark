package org.muvins.com

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import org.jsoup.nodes.{Element, Node}

import scala.collection.mutable.ListBuffer
import java.io._

import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupElement
import net.ruippeixotog.scalascraper.model.{ElementNode, TextNode}

import scala.util.matching.Regex





/**
  * Created by subhankardeysarkar on 6/14/17.
  */
class cityData {

  val lb = new ListBuffer[CityInfo]()
  val cityDetail = new CityDetail()
  val browser = JsoupBrowser()



  def getAllCityLinks(url: String): List[CityInfo] = {


    try {

      val browser = JsoupBrowser()
      val doc = browser.get(url)
      val items = doc >> elementList("#menu_item_02_drop ul table tbody tr td a")

      if(items.isEmpty)
        return lb.toList



      val allStates = items >> extractor("a", element).map { x =>
        if (x.hasAttr("href"))
          (x.text, x.attr("href"))
        else
          ("", "")
      }

      val stateMainPage =  allStates.foreach{ y =>

        if(!y._1.equalsIgnoreCase(""))  {


          val allCitiesTr = browser.get(url + y._2) >> elementList("#cityTAB tbody tr")


          val allCitiesInfo = allCitiesTr.foreach { x =>
              if (x.hasAttr("id")) {
                val id = x.attr("id")
                val tds = x.extract("td")
                var population = 0L
                var name = ""
                var cityLink = ""
                var onclickStr = ""
                tds.foreach { td =>

                  if (td.hasAttr("onclick"))
                    onclickStr = td.attr("onclick")
                  else {
                    if (td.extract("a").toList.size > 0) {
                      name = td.extract("a").head.text
                      cityLink = td.extract("a").head.attr("href")
                    }

                    else {
                      try {
                        population = td.text.trim.replaceAll(",", "").toLong
                      }
                      catch {
                        case _ =>
                      }
                    }
                  }


                }

                lb.append(CityInfo(y._1, name, cityLink, population, onclickStr))
                //println(CityInfo(y._1, name, cityLink, population, onclickStr))
              }


            }

          }

      }

      lb.toList

    }
    catch {
      case e: Exception => e.printStackTrace()
        lb.toList
    }

  }




  def writeToCsv(fileName:String,lst:List[CityInfo]) = {
    import purecsv.safe._
    lst.writeCSVToFileName(fileName)

  }

  def readFromCsv(fileName:String):List[CityInfo] = {
    import purecsv.unsafe._
    CSVReader[CityInfo].readCSVFromFileName(fileName)
  }

  def downloadCityHtml(url:String, writeDir:String):Boolean = {
    try {
      val arr = url.split("/",-1)
      val fname = arr(arr.size -1 )
      val file = new File(writeDir + "/" + fname)
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(scala.io.Source.fromURL(url).mkString)
      bw.close()

      true
    }
    catch
      {
        case e:Exception => e.printStackTrace
          false

      }

  }

  def cityAnalyzer( cityFile:String ) = {

    val doc = browser.parseFile(cityFile)



    /*Get Population Information
    *
      cityDetail.populationIncreaseSinceYear
      cityDetail.percentPopulationIncrease
      cityDetail.populationSinceYear = pvalue
      cityDetail.population = ptxt
     */
    val elems = doc >> elementList("#city-population")
    if(elems.isDefinedAt(0))
      {
        val item=elems.head
        if(item.hasAttr("data-toc-header") && item.attr("data-toc-header").equalsIgnoreCase("Population"))
          {

            println(item.innerHtml)

            val d = item.childNodes.grouped(2)
              .map(_.toList)

              d.toList //.filter(_.size==2)
              .foreach{ x=>
              val y = (x(0),x(1))
              val pattern1 = "[\\W|\\w]*[P][a-z ]*(\\d*)[ \\: ]+[\\W|\\w]*".r

              y match {
                case c:Tuple2[ElementNode[JsoupElement],TextNode] =>
                  var pvalue = retPatternData( pattern1, c._1.asInstanceOf[ElementNode[JsoupElement]].element.text.trim).getOrElse("").replaceAll(",","").trim
                  var ptxt = c._2.asInstanceOf[TextNode].content.replaceAll(",","").replaceAll("\\+","").replaceAll("%","").trim
                  if(pvalue.endsWith(".") && pvalue.length > 1 ) pvalue = pvalue.substring(0,pvalue.length -1)
                  if(ptxt.endsWith(".")   && ptxt.length > 1 ) ptxt = ptxt.substring(0,ptxt.length -1)

                  if(c._1.asInstanceOf[ElementNode[JsoupElement]].element.text.trim.contains("change"))
                    {
                      cityDetail.populationIncreaseSinceYear = pvalue
                      cityDetail.percentPopulationIncrease = ptxt
                    }
                  else
                    {
                      cityDetail.populationSinceYear = pvalue
                      cityDetail.population = ptxt
                    }

                case _ => println("No Luck")

              }

            }


          }
      }



    val str = "#population-by-sex div table tbody tr td"
    val ptrn = "[\\W|\\w]*[P][a-z ]*(\\d*)[ \\: ]+[\\W|\\w]*".r

genericExtract(doc,str ,ptrn)





    println("ok")
    println(cityDetail.toString)

  }


  def retPatternData(pattern:Regex, str:String):Option[String] = {

    try {
      val pattern(ret) = str
      Some(ret.toString)
    }
    catch {
      case e:Exception => e.printStackTrace
        None
    }
  }


  def genericExtract(doc:browser.DocumentType , extractStr:String, pattern1:Regex) =
  {

    val matchList=List("Males","Females")

    val elems = doc >> elementList(extractStr)
    if(elems.isDefinedAt(0))
    {
      val item=elems.head

      println(item.innerHtml)

      val d = elems.filter{ x =>

        if(x.isInstanceOf[JsoupElement])
          {
            if(matchList.exists(x.asInstanceOf[JsoupElement].outerHtml.contains(_)))
              true
            else
              false
          }
        else
          false

      }
        .grouped(2)
        .map({ case List(key, value) => Tuple2(key,value)})
        .foreach{ x=>
          if( x._1.isInstanceOf[ElementNode[JsoupElement]] )
            println(x._2.asInstanceOf[TextNode].content)
      }

//      val d = item.childNodes.filter{ x=>
//        x.asInstanceOf[ElementNode[JsoupElement]].element.underlying.getElementsByTag("img").size() == 0
//      }

//      d.toList
//        .foreach{ x=>
//        val y = (x(0),x(1))
//
//        y match {
//          case c:Tuple2[ElementNode[JsoupElement],TextNode] =>
//            var pvalue = retPatternData( pattern1, c._1.asInstanceOf[ElementNode[JsoupElement]].element.text.trim).getOrElse("").replaceAll(",","").trim
//            var ptxt = c._2.asInstanceOf[TextNode].content.replaceAll(",","").replaceAll("\\+","").replaceAll("%","").trim
//            if(pvalue.endsWith(".") && pvalue.length > 1 ) pvalue = pvalue.substring(0,pvalue.length -1)
//            if(ptxt.endsWith(".")   && ptxt.length > 1 ) ptxt = ptxt.substring(0,ptxt.length -1)
//
//            if(c._1.asInstanceOf[ElementNode[JsoupElement]].element.text.trim.contains("change"))
//            {
//              cityDetail.populationIncreaseSinceYear = pvalue
//              cityDetail.percentPopulationIncrease = ptxt
//            }
//            else
//            {
//              cityDetail.populationSinceYear = pvalue
//              cityDetail.population = ptxt
//            }
//
//          case _ => println("No Luck")
//
//        }
//
//      }



    }



  }






}


