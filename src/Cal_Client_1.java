import java.io.*;
import java.net.*;
import java.util.*;

public class Cal_Client_1 {
    public static void main(String[] args) {
        int port = 9999;                //Default port number
        String serverIP = "127.0.0.1";  //Default IP(local myself)

         //Read server_info.txt
        try(BufferedReader conf = new BufferedReader(new FileReader("server_info.txt"))){
            serverIP = conf.readLine().trim();  //Read server IP
            port = Integer.parseInt(conf.readLine().trim());    // Read server port
            System.out.println("Read from server_info.txt\nServerIP: " + serverIP + "\nPort: " + port);
        }
        catch(IOException e){
            // If file does not exist, use default 127.0.0.1:9999, and print error message
            System.out.println(e.getMessage());
        }
        try (
            Socket socket = new Socket(serverIP, port); //TCP Connect
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Input stream from server
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));  //Output stream to server
            Scanner sc = new Scanner(System.in) //Read input from keyboard
        ) {
            System.out.println("Connect.\n-----------------------------------\nTyping format: num opcode num\n(Exit: end)");
            //Communication loop
            while (true) {
                System.out.print("Input: ");
                String msg = sc.nextLine(); //Read a line from user

                out.write(msg + "\n");  //Send to server
                out.flush();            //Immediately transmit

                if (msg.equalsIgnoreCase("end")) {  //If user enter 'end', then exit
                    System.out.println("Disconnect");
                    break;
                }

                String line;
                StringBuilder response = new StringBuilder();   //Take reponse from server
                while ((line = in.readLine()) != null) {
                    if (line.equals("END")) break;  //End of server message
                    response.append(line).append("\n");
                }
                //Print response
                System.out.println("Answer:\n" + response.toString());
            }

        }
        catch (IOException e) {
            //Handle connection failure
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
