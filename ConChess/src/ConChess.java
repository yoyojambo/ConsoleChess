/*
https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
*/
public class ConChess {

	private static final String initPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	// Board begins at a8, ends at h1
	private static Character[][] board;
	// Turn true = white, false = black
	private static boolean turn;
	// Castling: [0] = White King Side, [1] White Queen Side, [2] Black King Side, [3] Black Queen Side
	private static boolean[] castling = new boolean[4];

	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to Console Chess!\n");
		Fen2Arr(initPosition);
		Draw();
		while (true) {
			int[][] move = Input.nextMove();
			Character[][] newBoard = make(move);
			if (newBoard != null) {
				board = newBoard.clone();
				turn = !turn;
				Draw();
				Help.basic();
			}
			else {
				System.out.println("Illegal move, input again. \n");
			}
		}
	}

	public static Character[][] make(int[][] move) {
        Character[][] newBoard = board.clone();
        int[] origin = move[0];
        int[] destiny = move[1];
        if (legal(origin, destiny)) {
            newBoard [destiny[0]] [destiny[1]] = newBoard [origin[0]] [origin[1]];
			newBoard [origin[0]] [origin[1]] = null;
			return newBoard;
        }
        else {
            return null;
        }
    }

    private static boolean legal( int[] origin, int[] destiny) {
        return true;
    }

	private static void Draw() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
	}

	public static void Fen2Arr(String position) {

		//TODO: Actual translation
		Character[][] nBoard = { {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'}, {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'}, {}, {}, {}, {}, {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'}, {'R','N','B','Q','K','B','N','R'} };
		turn = true;
		for (int i = 0; i < castling.length; i++) {
			castling[i] = true;
		}
		board = nBoard.clone();
	}
	
}
