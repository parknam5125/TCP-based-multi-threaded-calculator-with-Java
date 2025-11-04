import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Cal_Server {
    //Calculator Function
    public static String calc(String exp) {
        try {
            StringTokenizer st = new StringTokenizer(exp, " "); //Tokenize by space(" ") ex) 5 + 10 -> (5), (+), (10)
            if (st.countTokens() != 3)
                return "Error\n" + "Error type: Invalid format\n" + "Desc: Valid format is 'NUM OP NUM'\nEND\n";

            int op1 = Integer.parseInt(st.nextToken());
            String opcode = st.nextToken().trim();
            int op2 = Integer.parseInt(st.nextToken());
            double answer;
            //Perform arithmetic operation
            switch (opcode) {
                case "+":
                    answer = op1 + op2;
                    break;
                case "-":
                    answer = op1 - op2;
                    break;
                case "*":
                    answer = op1 * op2;
                    break;
                case "/":
                    if (op2 == 0)
                        return "Error\n" + "Error type: Divide by zero\n" + "Desc: Can't divide by zero\nEND\n";
                    answer = (double) op1 / op2;
                    break;
                default:
                    //Invalid operator entered
                    return "Error\n" + "Error type: Invalid opcode\n" + "Desc: Valid opcode is only one of (+, -, *, /)\nEND\n";
            }
            //Normal result message (terminated with "END")
            return answer + "\nEND\n";
        }
        catch (NumberFormatException e) {   //Input cannot be parsed into numbers
            return "Error\n" + "Error type: Not number\n" + "Desc: Non-numeric input\nEND\n";
        }
        catch (Exception e) {   //Catch any other exceptions
            return "Error\n" + "Error type: Unknown\n" + "Desc: " + e.getMessage() + "\nEND\n";
        }
    }

    public static void main(String[] args) {    //Main Function
        int port = 9999;
        ExecutorService max = Executors.newFixedThreadPool(5); //Maximum concurrent clients = 5
        System.out.println("Server on, port: " + port);

        try(ServerSocket listener = new ServerSocket(port)){
            while (true) {
                Socket socket = listener.accept();  //Waiting for connection
                max.execute(new CalcTask(socket));  //Assign to thread pool
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    static class CalcTask implements Runnable { //Worker Thread (CalcTask) with Runnable
        private Socket socket;
        public CalcTask(Socket socket) {
            this.socket = socket;
        }
        public void run() { //Override
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
            ) {
                System.out.println("Client connected: " + socket.getInetAddress());
                String input;
                //Keep reading requests until "end" is received
                while ((input = in.readLine()) != null) {
                    if (input.equalsIgnoreCase("end")) {
                        out.write("Connection closed\n");
                        out.flush();
                        break;
                    }
                    System.out.println("input: " + input);
                    String result = calc(input);    //Perform calculation
                    out.write(result);              //Send result back to client
                    out.flush();
                }

            }
            catch (IOException e) {
                System.out.println("I/O error: " + e.getMessage() + "\nEND\n");
            }
            finally {   //Ensure the socket is closed properly
                try {
                    socket.close();
                    System.out.println("Disconnected.");
                } catch (IOException e) {
                    System.out.println("Close error.");
                }
            }
        }
    }
}
