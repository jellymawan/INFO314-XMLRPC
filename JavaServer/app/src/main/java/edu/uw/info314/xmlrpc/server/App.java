package edu.uw.info314.xmlrpc.server;

import java.util.*;
import java.util.logging.*;

import org.eclipse.jetty.http.MetaData.Request;

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
        
        before((request, response) -> {
            if(!request.requestMethod().equals("POST")) {
                halt(405, "Method Not Supported");
            } else if(!request.contextPath().equals("/RPC")) {
                halt(404, "Not Found");
            }
        });

        post("/RPC", (request, response) -> { //will need to change this to /RPC
            response.status(200); 
            response.header("Host", request.host());

            ArrayList<Integer> nums = new ArrayList<Integer>(); // Create an ArrayList object
            String requestBody = request.body();
            String methodName = requestBody.substring(requestBody.indexOf("<methodName>") + 12, requestBody.indexOf("<params>"));
            int index = requestBody.indexOf("<i4>"); //seems like the number is always next to a <i4> tag
            int number = 0;

            if(!requestBody.contains("<i4>") && !requestBody.contains(methodName)) {
                return faultString(3, "illegal argument type");
            }

            //PARSES BODY
            while (index != -1) {  //gets each instance of <id> tag
                requestBody = requestBody.substring(index + 1);
                number = Integer.parseInt(requestBody.substring(3, requestBody.indexOf("</i4>")));
                nums.add(number); //adds each number to be added to the arraylist
                index = requestBody.indexOf("<i4>");
            }
            int[] arr = nums.stream().mapToInt(i -> i).toArray();

            //CREATE INSTANCE OF CALCULATOR CLASS
            Calc calculator = new Calc();

            //CALLS CORRECT METHOD
            if (methodName.equals("add")){
                return returnXML(calculator.add(arr));
            } else if(methodName.equals("subtract")) {
                return returnXML(calculator.subtract(arr[0], arr[1]));
            } else if(methodName.equals("multiply")) {
                return returnXML(calculator.multiply(arr)); 
            } else if(methodName.equals("divide")){
                if (arr[1] == 0) {
                    return faultString(1, "divide by zero");
                }
                return returnXML(calculator.divide(arr[0], arr[1]));
            } else{
                if (arr[1] == 0) {
                    return faultString(1, "divide by zero");
                }
                return returnXML(calculator.modulo(arr[0], arr[1]));
            }
        // Each of the verbs has a similar format: get() for GET,
        // put() for PUT, delete() for DELETE. There's also an exception()
        // for dealing with exceptions thrown from handlers.
        // All of this is documented on the SparkJava website (https://sparkjava.com/).
        });
    }

    public static String returnXML(int answer){
        String parameters = "<params>";
        parameters += "<param><value><i4>" + answer + "</i4></value></param>";
        parameters += "</params>";

        String requestBody = "<?xml version=\"1.0\"?><methodResponse>" + parameters + "</methodResponse<";
        return requestBody;
    }

    public static String faultString (int faultCode, String string) {
        String faultString = "<methodResponse><fault><value><struct><member><name>faultCode</name><value><int>";
        faultString += faultCode + "</int></value></member><member><name>faultString</name><value><string>";
        faultString += string + "</string></value></member></struct></value></fault></methodResponse>";
        return faultString;
    }

}
