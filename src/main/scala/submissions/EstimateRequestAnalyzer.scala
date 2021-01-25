package submissions

import scala.collection.mutable.{ArrayBuffer, ListBuffer, Map}
import io.Source._
import scala.collection.immutable.ListMap
import java.io.{BufferedWriter, FileWriter}
import scala.jdk.CollectionConverters._
import au.com.bytecode.opencsv.CSVWriter
import java.util.Scanner
import scala.sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.LazyLogging
import tour.Helpers._
import org.mongodb.scala._

/**
  * Reads ERS(Estimate Request Submissions).csv file,
  * and to analyze the data at user's disposal. 
  * Users have an option to save the analyzed data into .csv file.
  */

object EstimateRequestAnalyzer extends LazyLogging {

  logger.warn("Starting the Application")
  logger.error("Starting the Application")

  def main(args: Array[String]): Unit = {
    logger.warn("Executing the Main method")
    logger.error("Executing the Main method")

    //Check Args and run, could possibly add menu option here
    if(args.length >= 0){
      estimateRequestAnalyzer()
    }
  }

  def estimateRequestAnalyzer(): Boolean = {
    logger.warn("Calling the estimateRequestAnalyzer method")
    logger.error("Calling the estimateRequestAnalyzer method")
    println("\n\n")

    println("Starting the program...\n")
    print("Showing the list of files in your folder -->   ")

    //Showing the list of files in the folder
    var keyValuePair = Map[Int, String]()
    var fileListArray = ("ls" !!).split("\\s")
    fileListArray.foreach(x => print(s"| $x "))

    fileListArray = ("ls" !!).split("\\s|\\\\n", -1).map(_.toUpperCase())
    println()
    println()

    print(
      "Which file you want to import? Please type in the name of the file: "
    )
    var fileTyped = inputStringReader()
    println("Importing and opening the file...")

    println()
    //wait 5seconds to load

    //Fetching the selected file into my scala application
    while (
      //Catching any exception or errors
      fileTyped.isEmpty() || !fileListArray.contains(fileTyped.toUpperCase())
    ) {
      println("No such file is found. Please type in the name of the file: ")
      fileTyped = inputStringReader()
    }
    val file = fromFile(fileTyped)
    var lineToArrayBuffer = ArrayBuffer[String]()
    //Parsing the file into lines
    for (lines <- file.getLines()) {
      lineToArrayBuffer += lines
    }

    println(s"${fileTyped.toUpperCase} imported successfully")
    println()

    println("Below are the shcemas/headings of the file: ")
    val firstLine = lineToArrayBuffer.apply(0).toUpperCase().split(",")

    //Printing out optings to choose from
    for (i <- 0 to firstLine.length - 1) {
      println(s"${i + 1}. ${firstLine(i)}")
    }

    //Accessing information by the number selected
    print("Type in a number for information you want to access: ")
    var schemaSelected: Int = inputReader()

    println()
    schemaSelected match {
      case 1 => informationBySchema(1, lineToArrayBuffer)
      case 2 => informationBySchema(2, lineToArrayBuffer)
      case 3 => informationBySchema(3, lineToArrayBuffer)
      case 4 => informationBySchema(4, lineToArrayBuffer)
      case 5 => informationBySchema(5, lineToArrayBuffer)
      case 6 => informationBySchema(6, lineToArrayBuffer)
      case _ => informationBySchema(0, lineToArrayBuffer)
    }
    return true
  }

    def informationBySchema(schema: Int, lines: ArrayBuffer[String]): Boolean = {
      //Catching any exception or errors
      if (schema < 1 || schema > 6) {
        print("No such schema is found. Type in a number again: ")
        informationBySchema(inputReader(), lines)
      } else {
        println("Fetching the information you requested...")
        println()

        //Storing the information fetched from the selected column into an ArrayBuffer
        var columnSelectedToArray = ArrayBuffer[String]()
        for (i <- lines) {
          val outOfBoundExceptionCheck = i.split(",", -1).length
          if (outOfBoundExceptionCheck > 1) { //Making sure each line contains at least two columns - Name of the info & # of requests submitted
            val column = i.split(",", -1)(schema - 1).toUpperCase()
            if (!column.isEmpty()) {
              columnSelectedToArray += column
            }
          }
        }

        //Content Check to make sure the stored info matches with info users want
        //putting it in a map and counting the # of requests submitted
        val contentToMap = Map[String, Int]()
        for (x <- columnSelectedToArray) {
          val contentFilter = x.split("-|\\s", -1).mkString(" ")
          val filteredContentSize = x.split("-|\\s", -1).length
          if (filteredContentSize < 3) {
            if (contentToMap.contains(contentFilter)) {
              contentToMap(contentFilter) += 1
            } else {
              contentToMap(contentFilter) = 1
            }
          }
        }

        println(
          "How would you like to see the information?\n" +
            "1. Alphabetical Order\n" +
            "2. Ascending Order (Least # of submissions -> Most # of submissions)\n" +
            "3. Descending Order (Most # of submissions -> Least # of submissions)\n" +
            "Type in a number: "
        )

        //Sorting the Map to display it in three different ordering options
        var orderSelected = inputReader()
        while (orderSelected < 0 || orderSelected > 3) {
          print("Invalid Input. Please re-type in a number: ")
          orderSelected = inputReader()
        }
        val sortedSchema = schemaSort(orderSelected, contentToMap)

        //Printing out the Map in a selected order
        //Calculating the number of total requests submitted
        var numOfTotalRequests = contentToMap.foldLeft(0)(_ + _._2)
        var carMakesList = ListBuffer[String]()
        var requestsList = ListBuffer[String]()
        sortedSchema.keys.foreach { x =>
          println(f"${x}%-13s - ${sortedSchema(x)}%2d")
          carMakesList += x
          requestsList += sortedSchema(x).toString()
        }
        println()
        println(
          "If you want to know the percentage of a specific car make's reqeusts out of the total requests, \n" +
            "type in the name of a car make: \n" +
            "(Type 'Quit' to quit the application here.)"
        )

        //Making sure that the user input is String value only
        var carMakeTyped = inputStringReader()
        var isLetterCheck: Boolean = true
        carMakeTyped.foreach(x =>
          if (!isLetter(x) || x.toString.isEmpty) {
            isLetterCheck = false
          }
        )

        //Catching any exception or errors
        while (!isLetterCheck) {
          print("Invalid Input. Please type in a letter format: ")
          carMakeTyped = inputStringReader()
          carMakeTyped.foreach(x =>
            if (!isLetter(x) | x.toString.isEmpty) {
              isLetterCheck = false
            } else {
              isLetterCheck = true
            }
          )
        }
        //When "Quit" is entered, ask users if they save this file in .csv format
        if (carMakeTyped.toUpperCase().contains("QUIT")) {
          saveToCSV(
            carMakesList,
            requestsList
          )
        } 

        //Calculate the percentage of a specific mark make's requests out of total requests
        else {
          println(s"$carMakeTyped is selected. Calculating...")
          println()

          //Catching any exception or errors
          while (!sortedSchema.contains(carMakeTyped)) {
            println(s"There is no information about $carMakeTyped.")
            print("Please re-type in the name of a car make: ")
            carMakeTyped = inputStringReader
            println()
          }
          percentageCalculator(
            (sortedSchema(carMakeTyped)),
            numOfTotalRequests,
            carMakeTyped
          )
          println()
          println("Program completed successfully\n")

          //Ask users if they save this file in .csv format
          saveToCSV(
            (sortedSchema(carMakeTyped)),
            numOfTotalRequests,
            carMakeTyped
          )
        }
      }
      return true
    }

  //Getting the percentage value  of a specific car make's requests out of total requests
  def percentageCalculator(num1: Int, num2: Int, string: String): Double = {

    val percentage = (num1.toFloat / num2.toFloat) * 100
    println(
      "|    Car Make    " + "|  Estimate Reqeusts submitted  " + "|  Percentage calculated  |\n" +
        s"|__________________________________________________________________________|\n" +
        s"|                                                                          |\n" +
        f"|     ${string}%-11s" +
        f"|         ${num1}%d" + " out of " + f"${num2}%-12d" +
        f"|       ${percentage}%2.2f" + "%            |\n"
    )
    return percentage
  }

  //Saving the information of a selected schema's list and the requests submitted into .csv file
  def saveToCSV(list1: ListBuffer[String], list2: ListBuffer[String]): Boolean = {

    println(
      "Before you quit, do you want to save this information into .csv file?\n" +
        "Hit 'Y' to save or 'N' to exit: "
    )
    var response = inputStringReader()
    if (response.contains("Y")) {

      println("Saving it into .csv file...")
      val outputFile = new BufferedWriter(
        new FileWriter(
          "./output.csv"
        )
      )

      val csvWriter = new CSVWriter(outputFile)
      val fileSchema = List("Car Make", "Estimate Requests by the Make")
      val combinedLists = list1.zip(list2)

      val rows = combinedLists
        .foldLeft(List[List[String]]()) { case (acc, (a, b)) =>
          List(a, b) +: acc
        }
        .reverse

      csvWriter.writeAll((fileSchema +: rows).map(_.toArray).asJava)
      outputFile.close()
    }
    return true
  }

  //Saving the information of a selected car make's requests and a percentage calculated into .csv file
  def saveToCSV(num1: Int, num2: Int, string: String): Boolean = {
    println(
      "Before you quit, do you want to save this information into .csv file?\n" +
        "Hit 'Y' to save or 'N' to exit: "
    )
    var response = inputStringReader()
    while (response.isEmpty) {
      print("No value entered. Please re-type in your command: ")
      response = inputStringReader()
      println()
    }
    if (response.contains("Y")) {
      var percentage = (num1.toFloat / num2.toFloat) * 100
      println("Saving it into .csv file...")
      val outputFile = new BufferedWriter(
        new FileWriter(
          "./output.csv"
        )
      )
      val csvWriter = new CSVWriter(outputFile)
      val fileSchema = Array(
        "Car Make",
        "Estimate Requests by the Make",
        "Total Estimate Requests",
        "Percentage calculated"
      )
      var values = Array(
        string,
        num1.toString(),
        num2.toString(),
        s"${percentage - (percentage % 0.01)}%" //showing only 2 decimal places
      )

      var listOfValues = List(fileSchema, values)
      csvWriter.writeAll(listOfValues.asJava)
      outputFile.close()
      println()
      println("Saved successfully. Do you also want to save it to MongoDB?\n" +
        "Hit 'Y' to save or 'N' to exit: ")
    
        var response1 = inputStringReader()
        while (response1.isEmpty) {
          print("No value entered. Please re-type in your command: ")
          response1 = inputStringReader()
          println()
          }
          if (response1.contains("Y")) {
            val data = SaveToMongoDB.getPercentage(string, num1.toString, num2.toString, percentage.toString)
            SaveToMongoDB.insertPercentage(data)
          }
        }     
    return true
  }

  //Prompting a user, reading the user's String input only, and returning it in a String format
  def inputStringReader(): String = {

    var scanner = new Scanner(System.in)
    var inputTyped = ""
    inputTyped = scanner.nextLine().toUpperCase()
    return inputTyped
  }

  //prompting a user, reading the user's Integer input only, and returning it in an Integer format
  def inputReader(): Int = {
    var scanner = new Scanner(System.in)
    var numSelected = 0
    var quit = false
    while (!quit) {
      if (scanner.hasNextInt()) {
        numSelected = scanner.nextInt()
        scanner.nextLine()
        println(s"#${numSelected} is selected.")

        quit = true
      } else {
        println()
        print("Invalid Input. Please type in a number: ")
        scanner.next()
      }
    }
    return numSelected
  }

  //Sorting a map in three ordering options and returning a sorted ListMap
  def schemaSort(
      orderSelected: Int,
      contentToMap: Map[String, Int]
  ): ListMap[String, Int] = {

    val sortedMap: ListMap[String, Int] = orderSelected match {
      case 1 => ListMap(contentToMap.toSeq.sortWith(_._1 < _._1): _*)
      case 2 => ListMap(contentToMap.toSeq.sortWith(_._2 < _._2): _*)
      case 3 => ListMap(contentToMap.toSeq.sortWith(_._2 > _._2): _*)
    }
    return sortedMap
  }

  //Checking if an input is a Character value only
  def isLetter(c: Char) = c.isLetter && c <= 'z'

}
