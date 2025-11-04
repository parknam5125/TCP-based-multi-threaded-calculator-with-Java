import java.io.*;
import java.net.*;
import java.util.*;

public class Cal_Client_2 {
    public static void main(String[] args) {
        int port = 9999;
        String serverIP = "127.0.0.1";  //local myself
        try(BufferedReader conf = new BufferedReader(new FileReader("server_info.txt"))){ //Read server_info.txt
            serverIP = conf.readLine().trim();
            port = Integer.parseInt(conf.readLine().trim());
            System.out.println("Read from server_info.txt\nServerIP: " + serverIP + "\nPort: " + port);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        try (
            Socket socket = new Socket(serverIP, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Scanner sc = new Scanner(System.in)
        ) {
            System.out.println("Connect.\n-----------------------------------\nTyping format: num opcode num\n(Exit: end)");

            while (true) {
                System.out.print("Input: ");
                String msg = sc.nextLine();

                out.write(msg + "\n");
                out.flush();

                if (msg.equalsIgnoreCase("end")) {
                    System.out.println("Disconnect");
                    break;
                }

                String line;
                StringBuilder response = new StringBuilder();   //Take reponse from server
                while ((line = in.readLine()) != null) {
                    if (line.equals("END")) break;
                    response.append(line).append("\n");
                }

                System.out.println("Answer:\n" + response.toString());
            }

        }
        catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
