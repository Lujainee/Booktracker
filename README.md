<<<<<<< HEAD
# BookTracker

A command-line Java application for managing reading habit data using a SQLite database.

## How to run

Make sure you have Java and Maven installed!!

First, build the project:

mvn package

Then run it from the project root folder:

java -jar target/booktracker-1.0-SNAPSHOT-jar-with-dependencies.jar

The database and tables are created automatically on the first run. The data from the xlsx file is also loaded automatically.

You can ignore everything above written before "Database Initialized"

## Functionalities

The program shows a menu with the following options:

1. Add a user - enter age and gender to add a new user
2. View reading habits for a user - enter a user ID to see all their records
3. Change a book title - enter the old title and new title to update it
4. Delete a reading habit record - enter a habit ID to delete that record
5. Show mean age of all users
6. Show number of users that read a specific book - enter a book title
7. Show total pages read by all users
8. Show number of users that read more than one book
9. Add a Name column to the User table
0. Exit

## Requirements

- Java 21
- Maven

## Note on Maven

I used Maven as a tool to manage the external libraries this project depends on (Such as SQLite JDBC and Apache POI for reading the xlsx dataset). Maven automatically downloads these libraries and packages everything into a single runnable JAR file. Without Maven I  would have to download and add these library files manually. I found this on youtube.

## Database

The app uses a SQLite database file called booktracker.db. It has two tables:

- User: stores userID, age and gender
- ReadingHabit: stores habitID, book, pagesRead, submissionMoment and the user ID
=======
# Booktracker
Booktracker is a website with which users can track what books they have read and what books they might want to read in the future. Booktracker has a dataset with some data about the reading habits of its users. The data contains information about the users and how many pages they have read of certain books.  (This is a assignment)
>>>>>>> f3fb90dd4bdf0f651728f707de8025a501a92072
