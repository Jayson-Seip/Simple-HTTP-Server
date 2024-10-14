import java.io.*;
import java.net.*;

public class MyServer {
    final static int PORT = 8889;

    public static void main(String[] args) {
        try {
            System.out.println("Server listening on Port " + PORT);
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                Socket socket = server.accept();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                String requestLine = bufferedReader.readLine();

                String fileRequested = requestLine.split(" ")[1].substring(1);

                if (fileRequested.equals("index.html")) {
                    File file = new File(fileRequested);
                    handleHTTPRequest(socket, writer, file, true);
                }
                // Handles if the web page cannot be found.
                else {
                    File file = new File("404.html");
                    handleHTTPRequest(socket, writer, file, false);
                }

            }
        } catch (IOException e) {

        }

    }

    /**
     * 
     * @param server connection
     * @param writer Writes the message to be sent to the client
     * @param found  Determines if the web page is found or note
     * @throws IOException
     * 
     */
    public static void handleHTTPRequest(Socket server, PrintWriter writer, File file, boolean found)
            throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        if (found) {

            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html; charset=UTF-8");
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.println(line);
            }

        }
        // If webpage is not found process a 404 HTTP reply
        else {
            writer.println("HTTP/1.1 404 Not Found");
            writer.println("Content-Type: text/html; charset=UTF-8");
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.println(line);
            }

        }
        writer.close();
        fileReader.close();
        server.close();

    }

}