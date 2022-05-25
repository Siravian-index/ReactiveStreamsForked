package com.example.functional;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class FunctionalApplication {


    public static void main(String[] args) {
        //I stablish the connection
        MongoClient mongoClient = MongoClients.create("mongodb+srv://mdyagual:mdyagual@clusterferreteria.aum6z.mongodb.net/?retryWrites=true&w=majority");
        // I'm getting the table/collection that i want to use
        MongoCollection<Document> collection = mongoClient.getDatabase("sample_restaurants").getCollection("restaurants");
        //I'm saving all the data from the previous line into an ArrayList of Document
        ArrayList<Document> dataRestaurants = collection.find().into(new ArrayList<>());

        System.out.println("\n\n------OUTPUT------------\n\n");
		/* Use this line to remember how the data looks like
		dataRestaurants.stream().map(Document::toJson).limit(5).forEach(System.out::println);*/

        //1: Get the boroughs that start with letter 'B'
        List<Document> restaurantNameStartsWithB = dataRestaurants
                .stream()
                .filter(document -> document.get("borough").toString().startsWith("B"))
                .collect(Collectors.toList());

//		restaurantNameStartsWithB.forEach(System.out::println);

        //2: Get restaurants that have as cuisine 'American'

        List<Document> americanCuisine = dataRestaurants
                .stream()
                .filter(document -> document.get("cuisine").toString().equals("American"))
                .collect(Collectors.toList());

        //TO DO
		/*3: Get the amount of restaurants whose name is just one word
		Keep it in mind that a restaurant e.g McDonals have some locals in different directions and also some records hasn't names assigned
		HINT: Remember that if the restaurant's name has spaces that means it has more than 1 word*/
        long singleNameRestaurantCount = dataRestaurants.stream()
                .filter(document -> {
                    String name = document.get("name").toString();
                    boolean oneWord = name.split(".*\\s+.*").length == 1;
                    boolean notEmpty = name.length() > 1;
                    return oneWord && notEmpty;
                }).map(document -> document.get("name"))
                .distinct()
                .count();

		/*4: Get all the restaurants that received grade C in the most recent data
		HINT: The recent score is always the first one inside the list of the key "grades".*/
        /*
         * in javascript it would be
         *
         * dataRestaurant
         * 	.filter(restaurant => restaurant.grades.length && restaurant.grades[0].grade === "C")
         *
         * */

        List<Document> ResturantsWhoseLatestGradeIsC = dataRestaurants.stream()
                .filter(document -> {
                    List<Document> grades = document.getList("grades", Document.class);
                    if (grades.size() > 0) {
                        Document document1 = grades.get(0);
                        return document1.get("grade").toString().equals("C");
                    }
                    return false;
                })
                .collect(Collectors.toList());
		/*5: Sort all the restaurants by the grade that has received in the most recent date.
			If the are not receiving a grade yet (grade="Not Yet Graded"), ignore them.
		* HINT: Consider create a small Restaurant object with the data that you need to archive this exercise*/

        dataRestaurants.stream()
                .filter(document -> {
                    List<Document> grades = document.getList("grades", Document.class);
                    if (grades.size() > 0) {
                        String grade = grades.get(0).get("grade").toString();
                        return  grade.length() == 1;
                    }
                    return false;
                })
                .sorted(Comparator.comparing(document -> document.getList("grades", Document.class).get(0).get("grade").toString()))
                .forEach(System.out::println);


        //6: Get the restaurant with B category with the highest score

		/*7 (Optional): Investigate zip function (import org.springframework.data.util.StreamUtils;) to generate a list of strings with the next elements:
		-A stream that contains all the names of the restaurant
		-Another stream that contains the amount of words used on the restaurants's name
		Output expected
		xxxxx has 1 word
		xxxx yyyyy zz have 3 words
		xxxx yyyy have 2 words
		.
		.
		If you can solved it and if in the calculator activity you don't complete/implement the division operation, this will get considerer.
		*/


    }

}


//                .map(document -> {
//                    List<Document> grades = document.getList("grades", Document.class);
//                    String grade = grades.get(0).get("grade").toString();
//                    if (grade.equals("Not Yet Graded")) {
//                        return document.append("latestGrade", "");
//                    }
//                    return document.append("latestGrade", grade);
//                })
//                .map(document -> {
//                    List<Document> grades = document.getList("grades", Document.class);
//                    if (grades.size() > 0 && grades.get(0).get("grade").toString().equals("Not Yet Graded")) {
//                        Document gradeList = grades.get(0);
//
//                        String grade = gradeList.get("grade").toString();
////						check that grade is one of the possible values
//                        return document.append("latestGrade", grade);
//                    }
////					add an empty latestGrade to this document to avoid nullPointer
//                    return document;
//                })