package org.muvins.com

/**
  * Created by ssarka201 on 6/28/17.
  */
object main {


  val cd = new cityData

  def main(args: Array[String]): Unit = {

    //cd.writeToCsv("/tmp/allCityInfo.csv",cd.getAllCityLinks("http://www.city-data.com"))
    //    val cityInfo = cd.readFromCsv("/tmp/allCityInfo.csv").filter(_.cityLink.endsWith(".html"))
    //    cityInfo.foreach{ city =>
    //      cd.downloadCityHtml("http://www.city-data.com/city/" + city.cityLink,"/Users/subhankardeysarkar/city-data")
    //    }

    cd.cityAnalyzer("/Users/ssarka201/Documents/gdrive/city-data/Castro-Valley-California.html")


  }

}
