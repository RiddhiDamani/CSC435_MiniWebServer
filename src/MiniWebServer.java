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
 c. MiniWebChecklist.html

6. Notes:
  a. I have referred and made use of few code snippets from HostServer Assignment to return HTML output to also include a new
     copy of the form input back to the user.
  b. Steps followed to run this code:
       - Place the WebAdd.html file and MiniWebServer.java code in one of your folder on your disk.
       - Compile and Execute this MiniWebServer.java code
       - Now, Click on the WebAdd.html page. It will connect to the localhost: 2540 and something like below url will be shown on
         the browser.
         file:///Users/riddhidamani/Desktop/Projects/Java_Projects/DePaul_Project/Distribited_System/MiniWebServerAssignment/src/WebAdd.html
       - Now, enter the person name, num1 and num2 values
       - Hit Submit button.
       - A new form - http://localhost:2540/WebAdd.fake-cgi will display on the web page along with the addition result.
       - You can fill in the form and press SUBMIT repeatedly without reloading the browser.

7. Answers to the checklist questions:
   Functionality of Client-Server model for HTTP: The general format of request-response between the HTTP client and server is:
   - First, the HTTP client goes ahead and opens up the connection.
   - Then, HTTP client sends a request message to the HTTP Server.
   - The server then, listens to the request message and provides appropriate response to it -like sending the requested web page
   - Once done successfully, the server goes ahead and closes the connection with the HTTP Client.

   a. Explained how MIME-types are used to tell the browser what data is coming.
   Ans: MIME-types are also termed as Media Types. They help to communicate how the data should be interpreted that has been sent
   by the server that is what format it should interpret too. For instance, if the server sends a plain text data, the client browser
   should understand it and simply displays the text on the browser. There is no need to interpret it in a different way. On the contrary,
   if the server sends back the HTML then the client should render all of the proper elements from HTML in its user
   interface before actually showing the data to the user. So, in both the above cases, different types of data are being sent in the message
   body. In HTTP, MIME-types helps to communicate this i.e. provides information on message body data belongs to which form of data.
   - Thus, MIME type is a identifier for a particular type of data format. Client-Server generally communicates this via passing MIME types
   with each other explaining how to interpret the bodies of the messages that they are sending.
   - There are various MIME types available like : image/jpg, image/png, text/plain, text/html.  Format of the request and response body
   is described by these MIME types. Hence, it is essential to specify correct MIME-type.
   - HTTP message headers communicate these MIME types that are connected with the bodies of requests and response. Header information
   contains the meta information and it gets communicated as the content-type header in the HTTP message. example: Content-Type = "text/html"
   - Content-type determines the body format of request or the response. This way MIME-types are used to tell the browser about
   the incoming data.

   b. Explained how you would return the contents of requested files (web pages) of type HTML (text/html)
   Ans: In order to return the contents of the requested web pages of type - text/html, I will:
   - First, open the socket connection between the client and the server at port 2540.
   - Second, hit the file at the specified URL. Send the request (GET /WebAdd.fake-cgi?person=Riddhi&num1=41&num2=5
   HTTP/1.1) of respective HTML file that you require.
   - Third, parse the file name. Read in the contents of the HTML file to local variables if needed.
   - Fourth, perform any integer calculation or any type of actions required on the input data from the client
   - Fifth, build and send the HTML response and the header details like [HTTP/1.1 200 OK Content-Length : 61 Content-type : text/html 3ms]
    along with the carriage returns and the line feeds
   - Last, close the socket connection.

   c. Explained how you would return the contents of requested files (web pages) of type TEXT (text/plain)
   Ans: In order to return the content of the requested plain text MIME type, I will:
   - First, open the socket connection between the client and the server.
   - Second, parse the file name
   - Third, open that file, read in the contents of the .txt file
   - Fourth, build the HTTP response. Make sure to have a correct charset specified for the text in the HTTP header message.
   This will help in proper encoding of the text file.
   - Fifth, send the header details along with the response and the carriage returns and the line feeds
   - Last, close the socket connection.

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
        // Declaring Server socket variable that takes in request at port 2540 with 6 incoming request for connections
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

    String response2Browser;
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
            //System.out.print("Data From Browser: " + dataFromBrowser);
            // Checking for data from browser whether it is null or not.
            if(dataFromBrowser == null) {
                System.out.println("No Data Received from Browser");
            }
            assert dataFromBrowser != null;
            // Checking the length of the data received from the browser
            if(dataFromBrowser.length() < 1) {
                System.out.println("No Data Received from Browser");
            }
            // Taking substring from the dataFromBrowser starting from index 4 and ending at total length of data - 9
            String subStringData = dataFromBrowser.substring(4, dataFromBrowser.length() - 9);
            //System.out.print("\nSubStringData : " + subStringData);
            // Checking whether the string contains cgi word in it and processes the data further
            if (subStringData.contains("cgi"))
            {
                    // Invoking the myWebAdd method - Passing subStringData as parameter
                    response2Browser = myWebAdd(subStringData);
                    //System.out.print("Result addition" + response2Browser);
            }
            // Building HTML Web Response
            StringBuilder htmlWebResponse = new StringBuilder();
            // Building Header
            htmlWebResponse.append(sendHTMLHeader(response2Browser));
            // Building Submit button
            htmlWebResponse.append(sendHTMLSubmitButton());
            // Displaying the web response data - form with the result of addition
            sendHTMLToPrintStream(htmlWebResponse.toString(), outputData);
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
    // Method to calculate the two numbers entered by the user.
    public String myWebAdd(String subStringData) {
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
        response2Browser = "Hello " + person_name + "! Hope you are doing well. The sum of " + number1
                + " and " + number2 + " is " + addition;
        // Returning back the response2Browser value
        return response2Browser;
    }

    // Method that builds the HTML Header  - It takes in response2Browser Data inorder to display the result of addition on web page
    // It loads the HTML, then the form, then the result of addition , adds the link to the localhost:2540/WebAdd.fake-cgi
    // so that the response of the form goes back to the same port and performs the necessary operation.
    static String sendHTMLHeader(String response2Browser) {
        // Declaring htmlStringData variable
        StringBuilder htmlStringData = new StringBuilder();
        // Appending the header of HTML
        htmlStringData.append("<html><head><meta charset=\"UTF-8\"><title>WebAdd</title><link rel=\"icon\" href=\"data:,\"></head><body>\n");
        htmlStringData.append("<H1> WebAdd </H1>");
        // Appending the form
        htmlStringData.append("<FORM method=\"GET\" action=\"http://localhost:2540/WebAdd.fake-cgi\">");
        // Appending Text String for display on web page
        htmlStringData.append("Enter your name and two numbers. My program will return the sum:<p>");
        // Appending Input type field for name, num1 and num2 and settings its default value
        htmlStringData.append("<INPUT TYPE=\"text\" NAME=\"person\" size=20 value=\"YourName\"><P>");
        htmlStringData.append("<INPUT TYPE=\"text\" NAME=\"num1\" size=5 value=\"4\" id=\"a\"> <br>");
        htmlStringData.append("<INPUT TYPE=\"text\" NAME=\"num2\" size=5 value=\"5\" id=\"b\"> <p>");
        // Checking to see if we receive calculation of addition, if yes it will display the response2Browser
        // message above the submit button
        if(response2Browser.length() > 0) {
            htmlStringData.append("<p><strong>Result of Addition: </strong>" + response2Browser + "</p>");
        }
        // returning the htmlStringData back
        return htmlStringData.toString();
    }

    // Method used to send the submit button on the webpage HTML form
    static String sendHTMLSubmitButton() {
        return "<input type=\"submit\" value=\"Submit Numbers\"" + "</p>\n</form></body></html>\n";
    }
    // Method used to send the HTML to the output stream
    // It mentions the content-length and content-type of the data
    static void sendHTMLToPrintStream(String html, PrintStream outStream) {
        // Sending HTTP/1.1 200 OK status line message to the web browser, notifying the browser that the request was
        // successfully processed - hence, 'OK'
        outStream.println("HTTP/1.1 200 OK");
        // Sending the content length to the web browser
        outStream.println("Content-Length: " + html.length());
        // Sending th content type along with the carriage return and line feeds to the web browser
        outStream.println("Content-Type: text/html" + "\r\n\r\n");
        // Sending the HTML
        outStream.println(html);
    }
}