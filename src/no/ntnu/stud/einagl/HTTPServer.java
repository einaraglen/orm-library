package no.ntnu.stud.einagl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HTTPServer {
    //server port definition.
    static final int PORT = 8080;

    public static void main(String[] args) {
        //serversocket definition.
        ServerSocket server = null;
        try {
            // initialize server socket with PORT.
            server = new ServerSocket(PORT);
            System.out.println("Server started.\n Listening for connections on port: " + PORT + "...\n");

            while (true) {
                // we listen until user halts server execution
                Socket connection = server.accept();
                System.out.println("Connection opened. (" + new Date() + ")");
                // Start to run the server logics. connection is for the IO stream.
                runServer(connection);
            }

        } catch (IOException e) {
            System.out.println("Error while accepting request!");
        } finally {
            try {
                // server close connection.
                server.close();

            } catch (IOException e) {
                System.out.println("Unable to close the server socket!");

            }
        }

    }

    private static void runServer(Socket connection) {
        //BufferedReader is a connection for characters (text).
        BufferedReader request = null;
        //BufferedOutputStream is a byte based connection can be
        // used for all types of files (images...)
        BufferedOutputStream response = null;
        try {
            // READ CHARACTERS FROM THE CLIENT VIA INPUT STREAM ON THE SOCKET
            // Initialization for BufferedReader instance.
            request = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // WE GET character output stream to client (for headers)
            response = new BufferedOutputStream(connection.getOutputStream());

            // Read only the first line of the request, and break it where there is a space
            // character
            String[] fragments = request.readLine().split(" ");
            //method extraction
            String method = fragments[0].toUpperCase();
            //Requested service extraction
            String service = fragments[1].toLowerCase();

            // Read all headers.
            StringBuilder requestBody = new StringBuilder();
            String line;
            Map<String, String> headers = new HashMap<>();
            while (request.ready()) {
                line = request.readLine();
                if (line.trim().isBlank()) {
                    break;
                } else {
                    String[] parts = line.split(":");
                    headers.put(parts[0].trim(), parts[1].trim());
                }
            }

            try {
                //while ((line = request.readLine()) != null && line.length() != 0) {
                while(request.ready()){
                    line = request.readLine();
                    requestBody.append(line);
                    requestBody.append(System.getProperty("line.separator"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // we support only GET , HEAD and POST methods, we check
            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                // If it is not supporting method, use replyMethodNotSupported method.
            } else {
                try {
                    //WE ARE HERE IF ALL IS GOOD
                    // If it is correct method, call the replyWithRequestedFile method.
                    reply(response, service, requestBody.toString(), method);

                } catch (FileNotFoundException fnfe) {
                    System.out.println(fnfe);
                }
            }

        } catch (IOException ioe) {
            replyServerError(ioe);

        } finally {
            //close Connection
            closeConnection(connection, request, response);
        }
    }

    private static void closeConnection(Socket connect, BufferedReader in, BufferedOutputStream dataOut) {
        //close connection for socket, input reader and output stream.
        // handle exceptions.
        try {
            in.close();
            dataOut.close();
            connect.close();

        } catch (Exception e) {
            System.err.println("Error closing stream: " + e.getLocalizedMessage());

        }

        System.out.println("Connection closed.\n");
    }

    private static void replyServerError(IOException ioe) {
        //server running error exception
        System.out.println("Server error: " + ioe);
    }

    private static void reply(BufferedOutputStream response, String service, String requestBody, String method) throws IOException {

        formatHttpResponseHeader(response, "200 OK", 126, "text/plain");

        if (method.equals("POST")) {
            StringBuilder bodyFormat = new StringBuilder();
            bodyFormat.append("Requested body is: \n");
            if (service.equals("/orm-lib/")) {
                String[] body = requestBody.split("\n");

                bodyFormat.append(body[0]);
                bodyFormat.append(System.getProperty("line.separator"));

                String num = body[0].split(" ")[5].replaceAll("QUESTION", "").trim();

                switch(Integer.parseInt(num)) {
                    case 1:
                        insertBook(body, bodyFormat);
                        break;
                    case 2:
                        updateClient(body, bodyFormat);
                        break;
                    case 3:
                        deleteLoan(bodyFormat);
                        break;
                    case 4:
                        retrieveBorrowersOf(body, bodyFormat);
                        break;
                    case 5:
                        retrieveBorrowedOnDate(body, bodyFormat);
                        break;
                    case 6:
                        retrieveAllLoans(bodyFormat);
                        break;
                    default:
                        break;
                }
            }
            response.write(bodyFormat.toString().getBytes());
            response.flush();
        }
        response.flush();
    }

    //Question 1
    private static void insertBook(String[] body, StringBuilder bodyFormat) {
        Connector.insertBook(
                Integer.parseInt(body[2].replace("\r", "")),
                Integer.parseInt(body[3].replace("\r", "")),
                body[4].replace("\r", ""),
                body[5].replace("\r", "")
        );

        bodyFormat.append("Insert Completed!\n");
        bodyFormat.append(System.getProperty("line.separator"));
        bodyFormat.append("-------------------\n");
        bodyFormat.append(System.getProperty("line.separator"));

    }

    //Question 2
    private static void updateClient(String[] body, StringBuilder bodyFormat) {
        Connector.updateClientAddressOf(body[2].replace("\r", ""), body[3].replace("\r", ""));
        Connector.updateClientNumberOf(
                body[2].replace("\r", ""),
                Integer.parseInt(body[4].replace("\r", ""))
        );

        bodyFormat.append("Update Completed!\n");
        bodyFormat.append(System.getProperty("line.separator"));
        bodyFormat.append("-------------------\n");
        bodyFormat.append(System.getProperty("line.separator"));
    }

    //Question 3
    private static void deleteLoan(StringBuilder bodyFormat) {
        Connector.deleteLoanOf(4101);

        bodyFormat.append("Delete Completed!\n");
        bodyFormat.append(System.getProperty("line.separator"));
        bodyFormat.append("-------------------\n");
        bodyFormat.append(System.getProperty("line.separator"));
    }

    //Question 4
    private static void retrieveBorrowersOf(String[] body, StringBuilder bodyFormat) {
        ArrayList<String> lines = Connector.readBorrowersOf(body[2].replace("\r", ""));

        for (String line : lines) {
            bodyFormat.append(line + "\n");
        }

        bodyFormat.append(System.getProperty("line.separator"));
        bodyFormat.append("Read 1 Completed!\n");
        bodyFormat.append(System.getProperty("line.separator"));
        bodyFormat.append("-------------------\n");
        bodyFormat.append(System.getProperty("line.separator"));
    }

    //Question 5
    private static void retrieveBorrowedOnDate(String[] body, StringBuilder bodyFormat) {
        ArrayList<String> lines = Connector.readBookBasedOnDate(body[2].replace("\r", ""));

        for (String line : lines) {
            bodyFormat.append(line + "\n");
        }

        bodyFormat.append(System.getProperty("line.separator"));
        bodyFormat.append("Read 2 Completed!\n");
        bodyFormat.append(System.getProperty("line.separator"));
        bodyFormat.append("-------------------\n");
        bodyFormat.append(System.getProperty("line.separator"));
    }

    //Question 6
    private static void retrieveAllLoans(StringBuilder bodyFormat) {
        ArrayList<String> lines = Connector.readLoansPerBranch();

        for (String line : lines) {
            bodyFormat.append(line + "\n");
        }

        bodyFormat.append(System.getProperty("line.separator"));
        bodyFormat.append("Read 3 Completed!\n");
        bodyFormat.append(System.getProperty("line.separator"));
        bodyFormat.append("-------------------\n");
        bodyFormat.append(System.getProperty("line.separator"));
    }

    private static void formatHttpResponseHeader(BufferedOutputStream dataOut, String responseStatus, int fileLength,
                                                 String contentMimeType) {
        final PrintWriter out = new PrintWriter(dataOut);
        out.println("HTTP/1.1 " + responseStatus);
        out.println("Server: Java HTTP Server from einaragl 2.0");
        out.println("Date: " + new Date());
        out.println("Content-type: " + contentMimeType);
        out.println("Content-length: " + fileLength);
        out.println(); // blank line between headers and body. VERY IMPORTANT.
        out.flush();
    }

}

