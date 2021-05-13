import java.util.Scanner;

public class Input {
    // TODO: Fix this crap
    private static Scanner input = new Scanner(System.in);

    public static int[][] nextMove() throws Exception {
        String line = input.nextLine().toLowerCase().trim();
        if (line.equals("")) {
            int[][] debug = { {4,6} , {4,5} };
            return debug;
        }
        int length = line.length();
        int[][] indexArr;

        if (formatcheck(line)) {
            indexArr = str2index(line);
        }
        else {
            System.out.println("Incorrect Format, try again \n");
            indexArr = nextMove();
        }

        return indexArr;
    }

    private static int[][] str2index(String line) {
        return null;
    }

    private static boolean formatcheck(String line) {
        return true;
    }
}
