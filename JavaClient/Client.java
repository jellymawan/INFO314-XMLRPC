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

    public static void main(String... args) throws Exception {
        host = args[0];
        port = Integer.valueOf(args[1]);

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
        return sendRequest("add", params);
    }
    public static int subtract(int lhs, int rhs) throws Exception {
        return sendRequest("subtract",  lhs, rhs);
    }
    public static int multiply(int lhs, int rhs) throws Exception {
        return sendRequest("multiply", lhs, rhs);
    }
    public static int multiply(Integer... params) throws Exception {
        return sendRequest("multiply", params);
    }
    public static int divide(int lhs, int rhs) throws Exception {
        return sendRequest("divide",  lhs, rhs);
    }
    public static int modulo(int lhs, int rhs) throws Exception {
        return sendRequest("modulo",  lhs, rhs);
    }


    public static int sendRequest(String methodName, Object... arguments) throws Exception{
        //Create instance of client
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

        //Create request body
        String parameters = "<params>";
        for (Object param: arguments) {
            parameters += "<param><value><id>" + param + "</i4></value></param>";
        }
        parameters += "</params>";

        String requestBody = "<?xml version='1.-'?><methodCall><methodName>" + methodName + parameters + "</methodName></methodCall>";

        //send request
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://" + host + ":" + port))
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .header("Content-Type", "text/xml")
        .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return Integer.parseInt(response.body());
    }
}
