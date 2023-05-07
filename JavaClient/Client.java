import java.io.*;
import java.net.*;
import java.net.http.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;

import org.w3c.dom.Node;

/**
 * This approach uses the java.net.http.HttpClient classes, which
 * were introduced in Java11.
 */
public class Client {
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    static String host = "";
    static int port = 0;

    public static void main(String... args) throws Exception{
        host = args[0];
        port = Integer.valueOf(args[1]);

        try{
            add(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }catch(Exception e) {
            throw new ArithmeticException("Overflow!");
        }
        try {
            multiply(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }catch(Exception e) {
            throw new ArithmeticException("Overflow!");
        }
        // try {
        //     subtract("hi", "bye"); //won't even compile
        // }catch(Exception e) {
        //     throw new IllegalArgumentException("Can't subtract Strings");
        // }
        try {
            divide(1, 0);
        }catch(Exception e) {
            throw new ArithmeticException("Divide by zero");
        }

        System.out.println(add() == 0);
        System.out.println(add(1, 2, 3, 4, 5) == 15);
        System.out.println(add(2, 4) == 6);
        System.out.println(subtract(12, 6) == 6);
        System.out.println(multiply(3, 4) == 12);
        System.out.println(multiply(1, 2, 3, 4, 5) == 120);
        System.out.println(divide(10, 5) == 2);
        System.out.println(modulo(10, 5) == 0);
    }
    public static int add(int lhs, int rhs) throws Exception {
        return sendRequest("add", lhs, rhs);
    }
    public static int add(Integer... params) throws Exception {
        return sendRequest("add", (Object[])params);
    }
    public static int subtract(int lhs, int rhs) throws Exception {
        return sendRequest("subtract",  lhs, rhs);
    }
    public static int multiply(int lhs, int rhs) throws Exception {
        return sendRequest("multiply", lhs, rhs);
    }
    public static int multiply(Integer... params) throws Exception {
        return sendRequest("multiply", (Object[])params);
    }
    public static int divide(int lhs, int rhs) throws Exception {
        return sendRequest("divide",  lhs, rhs);
    }
    public static int modulo(int lhs, int rhs) throws Exception {
        return sendRequest("modulo",  lhs, rhs);
    }

    public static int sendRequest(String methodName, Object... arguments) throws Exception{
        //CREATE INSTANCE OF CLIENT
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

        //CREATE REQUEST BODY
        String parameters = "<params>";
        for (Object param: arguments) {
            parameters += "<param><value><i4>" + param + "</i4></value></param>";
        }
        parameters += "</params>";

        String requestBody = "<?xml version=\"1.0\"?><methodCall><methodName>" + methodName + parameters + "</methodName></methodCall>";

        //SEND REQUEST
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://" + host + ":" + port))
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .header("Content-Type", "text/xml")
        .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        String answer = Integer.MIN_VALUE + "";
        try{
            answer = responseBody.substring(responseBody.indexOf("<i4>") + 4, responseBody.indexOf("</i4>"));
        }catch (Exception e) {
            System.out.println(response.statusCode());
            System.out.println(response.body());
        }

        return Integer.parseInt(answer);
    }
}
