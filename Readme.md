# Estimate Requests Analysis Program

## Overview
A scala-based program to grab data from the **ERS(Eestimate Requests Submission).csv** file and to analyze the data at user's disposal. Users have an option to save the analyzed data into .csv file or in MongoDB.

## Technologies
- JDK Version 8 or 11
- Scala Version 2.13.3
- sbt Version 1.4.6
- OpenCSV API 2.4
- Docker
- MongoDB 4.1.1

## Features
- Shows a list of files in a directory.
- Goes through the entire file line by line and exclude any empty rows or empty columns.
- Catches most, if not all, of errors or exceptions when users type in invalid type input and re-prompt users to type in valid input.
- Allows users to select information of how many requests are submitted by month, by car make, by car model, and by a vehicle type.
- Calcuate the percentage of a specific car make's submissions out of the total submissions.
- Gives users an option to save the result in .csv file or in MongoDB.

## Getting Started / Usage
In order to run this program properly, you will need to use commands below:
  ### Git Clone
  ```
  git clone https://github.com/spark131008/Estimate_Reqeust_Analysis_Program.git
  ```
  ### 1. Compile
  ```
  sbt compile
  ```

  ### 2. Test
  ```
  sbt test
  ```

  ### 3. Run
  ```
  sbt run
  ```
  Once an output file is created, you can find csv file located within /Estimate_Request_Analysis_Program directory.

## Contributors
spark131008