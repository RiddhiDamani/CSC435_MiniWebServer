/*------------------------------------------------------------------------------------------------------------------------

1. Name / Date: Riddhi Damani / 10-11-2020

2. Java version used, if not the official version for the class:
java version "9.0.4"
Java(TM) SE Runtime Environment (build 9.0.4+11)
Java HotSpot(TM) 64-Bit Server VM (build 9.0.4+11, mixed mode)

3. Precise command-line compilation examples / instructions:
In separate terminal windows:
Compilation Steps:
> javac MiniWebServer.java

4. Precise examples / instructions to run this program:
In separate terminal windows:
Execution Steps:
> java MiniWebServer

5. List of files needed for running the program.
 a. MiniWebServer.java
 b. WebAdd.html
 c. Favicon.ico
 d. MiniWebChecklist.html

6. Notes:

7. Answers to the checklist questions:
   Functionality of Client-Server model for HTTP: The general format of request-response between the HTTP client and server is:
   - First, the HTTP client goes ahead and opens up the connection.
   - Then, HTTP client sends a request message to the HTTP Server.
   - The server then, listens to the request message and provides appropriate response to it -like sending the requested web page
   - Once done successfully, the server goes ahead and closes the connection with the HTTP Client.


   a. Explained how MIME-types are used to tell the browser what data is coming.
   Ans: MIME-types (Multipurpose Internet Mail Extensions) also known as Media Types. They are utilized by the web browsers to
   correctly process the URL requested by the client. Hence, it becomes crucial for web servers to send appropriate MIME type
   in its response i.e. in the Content-type header option. For instance, in my MiniWebServer code, I have mentioned to use
   Content-type as "text/html" for web browser to process my request. By mentioning this, the browser detects that the incoming
   request message body is an HTML format. If we mention the Content-type as "text/plain" - the browser detects it as a
   plain text format.

   b. Explained how you would return the contents of requested files (web pages) of type HTML (text/html)
   Ans: In order to return the contents of the requested web pages of type - text/html, I will:
   - First, hit the file at the specified URL. Then, parse this file.
   - Second, open the socket connection at port 2540. Then, send the request (GET /WebAdd.fake-cgi?person=Riddhi&num1=41&num2=5
   HTTP/1.1) of respective HTML file that you require, read in the contents of the HTML file to local variables if needed.
   - Fourth, perform any integer calculation or any type of actions required on the input data from the client
   - Fifth, send the header details like [HTTP/1.1 200 OK Content-Length : 61 Content-type : text/html 3ms] along with
     the carriage returns and the line feeds
   - Sixth, send the actual data to the web browser i.e. send the response body
   - Last, make the server close the socket connection.

   c. Explained how you would return the contents of requested files (web pages) of type TEXT (text/plain)
   Ans: In order to return the content of the requested plain text MIME type, I will:
   - First, parse the plain .txt file
   - Second, open that file, read in the contents from that file to local variables
   - Third, perform any type of actions required on the input data from the client
   - Fourth, send the header details like [HTTP/1.1 200 OK Content-Length : 61 Content-type : text/plain 9ms] along with
     the carriage returns and the line feeds

--------------------------------------------------------------------------------------------------------------------------------*/

// Importing Java input-output libraries
import java.io.*;
// Importing Java socket/networking libraries
import java.net.*;

// Class for MiniWebServer
public class MiniWebServer {
    // Main Class
    public static void main(String[] a) throws Exception {
        // Queue length - for number of request to be queued by OS
        int queue_length = 6;
        // Using port number 2540 for HTTP Connection
        int portNum = 2540;
        // Declaring socket variable for local use
        Socket socket;
        // Declaring Server socket variable that takes in request at port 2540 with 6 incoming request for
        // connections
        ServerSocket serverSocket = new ServerSocket(portNum, queue_length);
        // Displaying message on the terminal of launching up server
        System.out.println("Riddhi Damani's Mini Web Server launching up! Running at port 2540.");
        // try - catch block
        try {
            while(true) {
                // Accepting incoming connection request and assigning it to server object which in turn
                // is used to start the MWS worker threads
                socket = serverSocket.accept();
                // Launching a new MWS Worker object and invoking the start() method
                new MWS_Worker(socket).start();
            }
        }
        catch (IOException ioException) {
            // Handling Input-Output exceptions
            ioException.printStackTrace();
        }
    }
}
// Mini Web Server Worker Class that extends the java Thread class
class MWS_Worker extends Thread {
    // Declaring socket variable for worker class
    Socket socket;
    // Defining the MWS_Worker Constructor
    MWS_Worker (Socket s)
    {
        // Assigning the incoming socket connection to local worker socket variable
        socket = s;
    }
    // Run method - start()
    public void run() {
        // try - catch block
        try {
            // Declaring variable for taking data from browser
            String dataFromBrowser;
            // Declaring inputData variable for reading in data
            BufferedReader inputData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Declaring outputData variable for writing the data
            PrintStream outputData = new PrintStream(socket.getOutputStream());
            // Reading in line data to our dataFromBrowser variable
            dataFromBrowser = inputData.readLine();
            // Checking for data from browser whether it is null or not.
            if(dataFromBrowser == null) {
                System.out.println("No Data Received from Browser");
            }
            assert dataFromBrowser != null;
            // Checking the length of the data received from the browser
            if(dataFromBrowser.length() == 0) {
                System.out.println("No Data Received from Browser");
            }
            // Taking substring from the dataFromBrowser starting from index 4 and ending at total length of data - 9
            //System.out.println("Data from browser: " + dataFromBrowser);
            String subStringData = dataFromBrowser.substring(4, dataFromBrowser.length() - 9);
            //System.out.println("FileName: " + subStringData);

            if (subStringData.equals("/favicon.ico")) {
                //System.out.println("Favicon should not be processed!!!");
            }
            // Checking whether the string contains cgi word in it and processes the data further
            if (subStringData.contains("cgi"))
            {
                    // Setting the contentType to "Text/Html"
                    String contentType = "text/html";
                    // Invoking the myWebAdd method - Sending outputData, subStringData and contentType as parameter
                    myWebAdd(outputData, subStringData, contentType);
            }
            // Clearing out of the buffer
            System.out.flush();
            // Closing the socket connection
            socket.close();
        }
        // Handling input-output errors/exceptions
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    // Method - myWebAdd that performs addition of the 2 numbers entered by the user
    // on the web browser form
    public void myWebAdd( PrintStream outputData, String subStringData, String contentType) {
        // Parsing the first 21 characters from the subStringData string
        String trimName = subStringData.substring(22);
        // Splitting the person keyword, yourName value, num1 keyword, value of num1, num2 keyword,
        // value of num2 into a String array. Keeping the regex as '=' and '&'
        String [] data = trimName.split("[=&]");
        // Defining person_name variable and assigning it value from String's array's 1st location
        String person_name = data[1];
        // Defining number1 variable and assigning it value from String's array's 3rd location
        String number1 = data[3];
        // Defining number2 variable and assigning it value from String's array's 5th location
        String number2 = data[5];
        // Performing addition on number1 and number2 and parsing it as Integer
        int addition = Integer.parseInt(number1) + Integer.parseInt(number2);
        // Creating output string for the display to the user! Sharing the result of addition of the 2 input numbers
        // provided by him/her
        String response2Browser = "Hello " + person_name + "! Hope you are doing well. The sum of " + number1
                + " and " + number2 + " is " + addition;
        // Checking for the response length that needs to be send to the web browser. Assigning it to responseLength variable
        int responseLength = response2Browser.length();
        // Sending HTTP/1.1 200 OK status line message to the web browser, notifying the browser that the request was
        // successfully processed - hence, 'OK'
        outputData.print("HTTP/1.1 200 OK");
        // Sending the content length to the web browser
        outputData.print("Content-Length : " + responseLength);
        // Sending th content type along with the carriage return and line feeds to the web browser
        outputData.print("Content-type : " + contentType + "\r\n\r\n");
        // Returning a new copy of the WebAdd form back to the user for taking input back without the trouble of reloading the
        // page. That is, user can repeatedly press SUBMIT button without the need of manually reloading the browser's page
        outputData.print("<html lang=\"en\">");
        outputData.print("<head>");
        outputData.print("<meta charset=\"UTF-8\">");
        outputData.print("<title>WebAdd</title>");
        outputData.print("<link rel=\"icon\" href=\"data:,\">");
        outputData.print("</head>");
        outputData.print("<body>");
        outputData.print("<H1> WebAdd </H1>");
        // Performing the submit action again and again - by invoking the WebAdd.fake-cgi link
        outputData.print("<FORM method=\"GET\" action=\"http://localhost:2540/WebAdd.fake-cgi\">");
        outputData.print("Enter your name and two numbers. My program will return the sum:<p>");
        outputData.print("<INPUT TYPE=\"text\" NAME=\"person\" size=20 value=\"YourName\"><P>");
        outputData.print("<INPUT TYPE=\"text\" NAME=\"num1\" size=5 value=\"4\" id=\"a\"> <br>");
        outputData.print("<INPUT TYPE=\"text\" NAME=\"num2\" size=5 value=\"5\" id=\"b\"> <p>");
        outputData.print("<INPUT TYPE=\"submit\" VALUE=\"Submit Numbers\">");
        // print the replyToBrowser to the browser as formatted HTML
        outputData.print("<p>" + "<strong>Result of Addition: </strong>" + response2Browser + "</p>");
        // Closing of the Form
        outputData.print("</FORM>");
        // Closing of the HTML body section
        outputData.print("</body>");
        // Closing of the HTML
        outputData.print("</html>");
    }
}