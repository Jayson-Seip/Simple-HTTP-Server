import java.io.*;
import java.net.*;

public class MyServer {
    final static int PORT = 8889;

    public static void main(String[] args) {
        try {
            System.out.println("Server listening on Port " + PORT);
            @SuppressWarnings("resource")
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                Socket socket = server.accept();

                // Reads message from the client
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Used to send message back to the client in response to the HTTP request

                String requestLine = bufferedReader.readLine();

                String fileRequested = requestLine.split(" ")[1].substring(1);

                // Checks for the index.html page

                if (fileRequested.equals("index.html")) {
                    File file = new File(fileRequested);
                    handleHTTPRequest(socket, file, true);
                } else if (fileRequested.equals("logo.jpg")) {
                    File file = new File(fileRequested);
                    handleHTTPRequest(socket, file, true);
                }
                // Handles if the web page cannot be found.
                else {
                    File file = new File("404.html");
                    handleHTTPRequest(socket, file, false);
                }

            }
        } catch (IOException e) {
            System.out.println("Error Connecting to Server");
        }

    }

    /**
     * 
     * @param scoket connection
     * @param writer Writes the message to be sent to the client
     * @param found  Determines if the web page is found or note
     * @throws IOException
     * 
     */
    public static void handleHTTPRequest(Socket socket, File file, boolean found)
            throws IOException {

        String fileName = file.getName();
        // If a File is found
        if (found) {
            if (fileName.endsWith(".html")) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fileReader = new BufferedReader(new FileReader(file));
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: text/html; charset=UTF-8");

                // Reads the contents of the HTML file
                String line;
                while ((line = fileReader.readLine()) != null) {
                    writer.println(line);
                }
                fileReader.close();
                writer.close();
            }
            if (fileName.endsWith(".jpg")) {

            }

        }
        // If webpage is not found process a 404 HTTP reply
        else {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("HTTP/1.1 404 Not Found");
            writer.println("Content-Type: text/html; charset=UTF-8");
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.println(line);
            }
            fileReader.close();
            writer.close();
        }
    }
}