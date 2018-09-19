package test.com.cota.interview

import com.cota.interview.TextAnalysis
import com.cota.interview.TextAnalysis._
import org.scalatest._

/**
  * Test cases for the TextAnalysis task
  */
class TextAnalysisTestSpec extends FunSuite {

  val input1 = "John downloaded the Pokemon Go app on 07/15/2017. By 07/22/2017, he was on level 24. Initially, he was very happy with the app. However, he soon became very disappointed with the app because it was crashing very often. As soon as he reached level 24 he uninstalled the app."
  val input2 = input1.replace(" he ", " she ")
  val input3 = "John downloaded the Pokemon Go app on 07/15/2017."
  val input4 = "Hua Min liked playing tennis. She first started playing on her 8th birthday - 07/07/1996. Playing tennis always made her happy. She won her first tournament on 08/12/2010. However, on 04/15/2015 when she was playing at the Flushing Meadows, she had a serious injury and had to retire from her tennis career"
  val input5 = "Ram looks sad today"

  test("extract the gender types and validate")  {
    assert("male".equals(findGender(Some(input1)).get))
    assert("female".equals(findGender(Some(input2)).get))
    assert("unknown".equals(findGender(Some(input3)).get))
  }

  test("extract the sentiment and validate")  {
    assert("positive".equals(findSentiment(Some(input4)).get))
    assert("negative".equals(findSentiment(Some(input5)).get))
    assert("mixed".equals(findSentiment(Some(input1)).get))
    assert("unknown".equals(findSentiment(Some(input3)).get))
  }

  test("calculate the duration between two dates")  {
    assert(8l.equals(calculateTimeDuration(Some(input1)).get))
    assert(0l.equals(calculateTimeDuration(Some(input5)).get))
    assert(6857l.equals(calculateTimeDuration(Some(input4)).get))
  }
}