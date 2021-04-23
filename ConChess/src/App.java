
/*
https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
*/
public class App {

    public char[][] board= new char[8][8];
    public boolean activeSide = true; //White true; Black false;

    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to Console Chess!\n");
        Help.basic();

        while (true) {
            type move = input();
            logic(move);
            draw();
        }
    }
}
