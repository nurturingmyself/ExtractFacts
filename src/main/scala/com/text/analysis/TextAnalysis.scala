package com.text.analysis

import java.io._

import net.liftweb.json.JsonParser.ParseException
import net.liftweb.json._
import net.liftweb.json.Serialization.write

import scala.io.Source

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
  * SentimentAnalysis is used to find out the below things from a json file
  * - duration if there are at least two dates,
  * - gender of the subject
  * - sentiment expressed in the text
  *
  * Assumptions - Each file contains the single paragraph,
  *               Input File Name to be passed as argument to the program For EX: testinput.json - args(0)
  *               Output File Name also to be passed as argument to the program For EX: textanalysisresult.json - args(1)
  *
  */

object TextAnalysis {
  val DATE_FORMAT = "MM/DD/YYYY"
  val alphaNumbericSpace = "[^a-zA-Z0-9 ]+"

  case class InputText(paragraph: String)
  case class ExtractInfo(timeDuration: Long, gender: String, sentiment: String)

  //It is required to extract
  implicit val formats = DefaultFormats
  var extractInfo: Option[ExtractInfo] = None

  /**
    * It parses the input json file and extract InputText instance
    * @param fileName input filename
    * @return Option[InputText] InputText instance
    */
  def retrieveInput(fileName:String): Option[InputText] = {
    var input:Option[InputText] = None
    try {
      val inputData = Source.fromFile(fileName).mkString
      // convert String to a InputText instance
      input = Some(parse(inputData).extract[InputText])
    } catch {
      case fileNotFound: FileNotFoundException => println(s"File Not Found in the classpath - ${fileNotFound.getMessage}")
      case parseException:ParseException => println(s"Json parse exception - ${parseException.getMessage}")
      case ex: Throwable => println("Got some other kind of exception while processing the json")
    }
    input
  }

  /**
    * It writes the output file with the findings predicted from input
    * @param info ExtractInfo instance
    * @param outputFileName output file name
    */
  def writeFindings(info:ExtractInfo, outputFileName: String): Unit = {
    try {
      val infoJson = write(info)
      val outputFile = new File(outputFileName)
      val bw = new BufferedWriter(new FileWriter(outputFile))
      bw.write(infoJson)
      bw.close()
    } catch {
      case ioException: IOException => println(s"File Not Found in the classpath - ${ioException.getMessage}")
      case ex: Throwable => println("Got some other kind of exception while writing the json")
    }
  }

  /**
    * This method used to find the gender in the given text
    * @param paragraph - Input text to be used to find gender
    * @return The gender of person in paragraph - male, female, unknown
    */
  def findGender(paragraph:Option[String]): Option[String] = {
    //Return Value
    var gender: Option[String] = None

    //To Find Gender from the paragraph
    val MALE_PRONOUN = "he"
    val FEMALE_PRONOUN = "she"

    //Initiating with count 0 for male and female prediction count
    var genderCount = (0, 0)

    //To make sure the paragraph is not empty
    if (paragraph.exists(_.trim.length > 0)) {
      val inputAlphaNumericText = paragraph.get.replaceAll(alphaNumbericSpace,"")
      inputAlphaNumericText.split("\\s+").map {
        word =>
          word match {
            case MALE_PRONOUN => genderCount = (genderCount._1 + 1, genderCount._2)
            case FEMALE_PRONOUN => genderCount = (genderCount._1, genderCount._2 + 1)
            case _ => None
          }
      }
      genderCount match {
        case (x, 0) if (x > 0) => gender = Some("male") // male pronoun > 0 and female pronoun == 0
        case (0, y)if (y > 0) => gender = Some("female")// male pronoun == 0 and female pronoun > 0
        case z => gender = Some("unknown") // rest all cases
      }
    }

    gender
  }

  /**
    * Used to find the sentiment involved in the input text
    * @param paragraph - Input text to be used to find sentiment
    * @return sentiment in given text - positive, negative, mixed, unknown
    */
  def findSentiment(paragraph: Option[String]): Option[String] = {
    //Return Value
    var sentiment: Option[String] = None

    //Segreggate positive and negative sentiment keywords
    val POSITIVE_SENTIMENTS = Seq("Happy", "Glad", "Jubilant", "Satisfied")
    val NEGATIVE_SENTIMENTS = Seq("Sad", "Disappointed", "Angry", "Frustrated")


    //To make sure the paragraph is not empty
    if (paragraph.exists(_.trim.length > 0)) {
      val inputAlphaNumericText = paragraph.get.replaceAll(alphaNumbericSpace,"")
      val positiveList = inputAlphaNumericText.split("\\s+").filter(word => POSITIVE_SENTIMENTS.exists{s => s.equalsIgnoreCase(word)})
      val negativeList = inputAlphaNumericText.split("\\s+").filter(word => NEGATIVE_SENTIMENTS.exists{s => s.equalsIgnoreCase(word)})
      if((!positiveList.isEmpty && positiveList.length>0) && (!negativeList.isEmpty && negativeList.length>0)){
        sentiment = Some("mixed")
      } else if(!positiveList.isEmpty && positiveList.length>0){
        sentiment = Some("positive")
      } else if (!negativeList.isEmpty && negativeList.length>0){
        sentiment = Some("negative")
      } else {
        sentiment = Some("unknown")
      }
    }
    sentiment
  }

  /**
    * convert date string to LocalDate Instance
    * @param dateStr - input date string
    * @return the LocalDate instance
    */
  def convertStringToDate(dateStr: String): Option[LocalDate] = {
    var retVal:Option[LocalDate] = None
    try {
      retVal = Some(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy")))
    } catch {
      case ex:Throwable => println(""+ex.getMessage)
    }
    retVal
  }

  /**
    * Calculate time duration from fist and last date present in the input. Returns 0 if 0 or 1 dates present
    * @param paragraph - Input text to be used to calculate time duration
    * @return duration between first and last date (only if atleast 2 dates present in input)
    */
  def calculateTimeDuration(paragraph: Option[String]): Option[Long] = {
    //Return Value
    var duration: Option[Long] = Some(0)

    val dateRegex = raw"(\d{2})/(\d{2})/(\d{4})".r

    //To make sure the paragraph is not empty
    if (paragraph.exists(_.trim.length > 0)) {
      val dateStringList:List[String] = dateRegex.findAllIn(paragraph.get).toList
      val datesList:List[Option[LocalDate]] = dateStringList.map(x=>convertStringToDate(x))
      if(!datesList.isEmpty && datesList.length>=2){
        val firstDate = (datesList(0).get)
        val lastDate = (datesList(datesList.length-1).get)
        duration = Some(lastDate.toEpochDay() - firstDate.toEpochDay() + 1) //added 1 to include the last day
      }
    }
    duration
  }

  def main(args: Array[String]) {
      if(args.length==2) {
        val input:Option[String] = Some(retrieveInput(args(0)).get.paragraph)
        if(input.exists(_.trim.length > 0)){
          val output = ExtractInfo(calculateTimeDuration(input).get, findGender(input).get, findSentiment(input).get)
          writeFindings(output, args(1))
        } else {
          println(s"Input file is not a valid one - $args(0)")
        }
      } else {
        println("Please Provide input and output file name")
      }

    }

}