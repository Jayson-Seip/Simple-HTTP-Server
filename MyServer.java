import java.io.*;
import java.net.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

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
                }

                // Checks for the logo.jpg
                else if (fileRequested.equals("logo.jpg")) {
                    File file = new File(fileRequested);
                    handleHTTPRequest(socket, file, true);
                }
                // Checks for the audio.mp3 file
                else if (fileRequested.equals("audio.mp3")) {
                    File file = new File(fileRequested);
                    handleHTTPRequest(socket, file, true);
                }
                // checks for the movie.mp4 file
                else if (fileRequested.equals("movie.mp4")) {
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
     * Handles HTTP request from the Client
     * 
     * @param socket connection
     * @param writer Writes the message to be sent to the client
     * @param found  Determines if the web page or file
     * @throws IOException
     * 
     */
    public static void handleHTTPRequest(Socket socket, File file, boolean found)
            throws IOException {

        String fileName = file.getName();
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        // If a File is found
        if (found) {
            writer.println("HTTP/1.1 200 OK");
            if (fileName.endsWith(".html")) {

                BufferedReader fileReader = new BufferedReader(new FileReader(file));
                writer.println("Content-Type: text/html; charset=UTF-8");

                // Reads the contents of the HTML file
                String line;
                while ((line = fileReader.readLine()) != null) {
                    writer.println(line);
                }
                fileReader.close();
            }
            if (fileName.endsWith(".jpg")) {
                writer.println("Content-Type: image/jpeg");
                writer.println();
                BufferedImage image = ImageIO.read(file);

                ImageIO.write(image, "jpg", socket.getOutputStream());
                socket.getOutputStream().flush();
            }
            // Handles audio mp3 or video mp4 files since they are handled the same way
            if (fileName.endsWith(".mp3") || fileName.endsWith(".mp4")) {
                if (fileName.endsWith(".mp3")) {
                    writer.println("Content-Type: audio/mpeg");
                } else {
                    writer.println("Content-Type: video/mp4");
                    writer.println("Content-Length: " + file.length());
                    System.out.println("Video");
                    System.out.println(file.length() + " bytes.");
                }
                writer.println();

                byte[] buffer = new byte[16384];
                int bytesRead;
                FileInputStream inputStream = new FileInputStream(file);
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    socket.getOutputStream().write(buffer, 0, bytesRead);
                }
                socket.getOutputStream().flush();

                inputStream.close();
            }

        }
        // If webpage is not found process a 404 HTTP reply
        else {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            writer.println("HTTP/1.1 404 Not Found");
            writer.println("Content-Type: text/html; charset=UTF-8");
            String line;
            while ((line = fileReader.readLine()) != null) {
                writer.println(line);
            }
            fileReader.close();

        }
        writer.close();
    }
}