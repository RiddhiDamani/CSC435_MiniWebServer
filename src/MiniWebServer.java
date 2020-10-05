import java.io.*;
import java.net.*;

public class MiniWebServer {

    public static void main(String[] a) throws Exception {
        int queue_length = 6;
        int portNum = 2540;
        Socket socket;

        ServerSocket serverSocket = new ServerSocket(portNum, queue_length);

        System.out.println("Riddhi Damani's Mini Web Server launching up! Running at port 2540.");
        try {
            while(true) {
                socket = serverSocket.accept();
                new MWS_Worker(socket).start();
            }
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }
}

class MWS_Worker extends Thread {
    Socket socket;
    MWS_Worker (Socket s) {socket = s;}

    public void run() {
        try {
            String dataFromBrowser;
            BufferedReader inputData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream outputData = new PrintStream(socket.getOutputStream());

            dataFromBrowser = inputData.readLine();
            //System.out.println("fromBrowserString: " + dataFromBrowser);

            if(dataFromBrowser == null) {
                System.out.println("No Data Received from Browser");
            }
            assert dataFromBrowser != null;
            if(dataFromBrowser.length() == 0) {
                System.out.println("No Data Received from Browser");
            }

            System.out.println("Data from browser: " + dataFromBrowser);
            String subStringData = dataFromBrowser.substring(4, dataFromBrowser.length() - 9);
            System.out.println("FileName: " + subStringData);

            if (subStringData.equals("/favicon.ico")) {
                //System.out.println("favicon detected and ignored");
            }
            if (subStringData.contains("cgi"))
            {
                    System.out.println(subStringData);
                    String contentType = "text/html";
                    myWebAdd(subStringData, contentType, outputData);
            }
            System.out.flush();
            socket.close();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void myWebAdd(String subStringData, String contentType, PrintStream outputData){

        String trimName = subStringData.substring(22);
        String [] data = trimName.split("[=&]");

        String person_name = data[1];
        String number1 = data[3];
        String number2 = data[5];
        int addition = Integer.parseInt(number1) + Integer.parseInt(number2);

        String response2Browser = "Hello " + person_name + "! Hope you are doing well. The sum of " + number1
                + " and " + number2 + " is " + addition;
        int responseLength = response2Browser.length();

        outputData.print("HTTP/1.1 200 OK");
        outputData.print("Content-Length : " + responseLength);
        outputData.print("Content-type : " + contentType + "\r\n\r\n");
        outputData.print("<p>" + response2Browser + "</p>");
    }
}