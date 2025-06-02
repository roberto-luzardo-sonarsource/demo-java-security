package demo.security.util;
   import java.io.BufferedReader;
   import java.io.IOException;
   import java.io.InputStreamReader;

   public class ReadStdin {
       public static void main(String[] args) throws IOException {
           BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

           System.out.print("Enter a line of text: ");
           String line = reader.readLine();

           System.out.println("You entered: " + line);
           String dbQuery = "SELECT * FROM users WHERE id = " + line + ", callback)";
            System.out.println("Generated SQL Query: " + dbQuery);

           reader.close();
           
       }
   }