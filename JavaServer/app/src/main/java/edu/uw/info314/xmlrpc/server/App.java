package edu.uw.info314.xmlrpc.server;

import java.util.*;
import java.util.logging.*;
import java.util.ArrayList; // import the ArrayList class
import static spark.Spark.*;

class Call {
    public String name;
    public List<Object> args = new ArrayList<Object>();
}

public class App {
    public static final Logger LOG = Logger.getLogger(App.class.getCanonicalName());

    public static void main(String[] args) {
        LOG.info("Starting up on port 8080");
        port(8080); //sets port to 8080

        // This is the mapping for POST requests to "/RPC";
        // this is where you will want to handle incoming XML-RPC requests
        post("/", (request, response) -> { //will need to change this to /RPC
            response.status(200); 
            ArrayList<Integer> nums = new ArrayList<Integer>(); // Create an ArrayList object
            String requestBody = request.body();
            String methodName = requestBody.substring(requestBody.indexOf("<methodName>") + 12, requestBody.indexOf("<params>"));
            int index = requestBody.indexOf("<id>"); //seems like the number is always next to a <id> tag
            int number = 0;

            //PARSES BODY
            while (index != -1) {  //gets each instance of <id> tag
                requestBody = requestBody.substring(index + 1);
                number = Integer.parseInt(requestBody.substring(3, requestBody.indexOf("</i4>")));
                nums.add(number); //adds each number to be added to the arraylist
                index = requestBody.indexOf("<id>");
            }

            LOG.info(methodName);
            LOG.info(nums + "");

            int[] arr = nums.stream().mapToInt(i -> i).toArray();

            //CREATE INSTANCE OF CALCULATOR CLASS
            Calc calculator = new Calc();

            //CALLS CORRECT METHOD
            if (methodName.equals("add")){
                return calculator.add(arr);
            } else if(methodName.equals("subtract")) {
                return calculator.subtract(arr[0], arr[1]);
            } else if(methodName.equals("multiply")) {
                LOG.info(calculator.multiply(arr) + ""); //returns 0
                int temp = arr[0] * arr[1];
                LOG.info(temp + ""); //returns 12
                return calculator.multiply(arr); //returns 0
            } else if(methodName.equals("divide")){
                return calculator.divide(arr[0], arr[1]);
            } else{
                return calculator.modulo(arr[0], arr[1]);
            }
        // Each of the verbs has a similar format: get() for GET,
        // put() for PUT, delete() for DELETE. There's also an exception()
        // for dealing with exceptions thrown from handlers.
        // All of this is documented on the SparkJava website (https://sparkjava.com/).
        });
    }
}
