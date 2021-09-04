public class Main{
    public static void main(String [] args){
        User userInterface = new User("LSH.db");
        userInterface.printTables();
        userInterface.printInserts();
        userInterface.printIndex();
        userInterface.close();
    }
}