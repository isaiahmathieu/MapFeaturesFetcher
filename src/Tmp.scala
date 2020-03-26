object Tmp {

  def main(args: Array[String]): Unit = {
    val strs = List("jjj", "oiioi", "jjjj")
    val str =
      s"""
        |aojiiojoijoij
        |iojjoioijoij
        |${strs.mkString(",")}
        |""".stripMargin
    println(str)
  }


}
