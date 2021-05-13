public class ConChess {

	private static final String initPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	// Board begins at a1, ends at h8
	private static char[][] board;
	// Turn true = white, false = black
	private static boolean turn;
	// Castling: [0] = White King Side, [1] White Queen Side,
	// [2] Black King Side, [3] Black Queen Side
	private static boolean[] castling = new boolean[4];

	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to Console Chess!\n");
		Help();
		Fen2Arr(initPosition);
		Draw();
		while (true) {
			int[][] move = Input.nextMove();
			char[][] newBoard = make(move);

			if (newBoard != null) {
				board = newBoard.clone();
				turn = !turn;
				Draw();
			} else {
				System.out.println("Illegal move, input again. \n");
			}
		}
	}

	private static void Help() {
		// TODO: Make some instructions
	}

	public static char[][] make(int[][] move) {
		char[][] newBoard = board.clone();
		int[] origin = move[0];
		int[] destiny = move[1];
		if (legal(origin, destiny)) {
			newBoard[destiny[0]][destiny[1]] = newBoard[origin[0]][origin[1]];
			newBoard[origin[0]][origin[1]] = ' ';
			return newBoard;
		} else {
			return null;
		}
	}

	enum Directions {
		UP, DOWN, LEFT, RIGHT
	}

	private static boolean legal(int[] origin, int[] destiny) {
		// Bounds
		if (!bound(origin) || !bound(destiny)) {
			return false;
		}
		char originChar = board[origin[0]][origin[1]];
		int originX = origin[0];
		int originY = origin[1];
		boolean originSide = Character.isUpperCase(originChar);
		char destinyChar = board[destiny[0]][destiny[1]];
		int destinyX = destiny[0];
		int destinyY = destiny[1];
		boolean destinySide = Character.isUpperCase(destinyChar);
		int differenceX = originX - destinyX;
		int differenceY = originY - destinyY;

		if (board[originX][originY] == ' ') {
			return false;
		}
		// Checking turn is right
		if (turn != originSide) {
			return false;
		}
		// Checking its attacking the opponent
		else if (originSide == destinySide) {
			return false;
		}

		int absDiffX = Math.abs(differenceX);
		int absDiffY = Math.abs(differenceY);
		switch (Character.toLowerCase(originChar)) {
			// Knight
			case 'n':
				if (!knightCheck(absDiffX, absDiffY)) {
					return false;
				}
				break;
			// Rook
			case 'r':
				if (!rookCheck(origin, destiny, differenceX, differenceY, absDiffY, absDiffY)) {
					return false;
				}

				break;
			// Bishop
			case 'b':

				if (!bishopCheck(origin, destiny, differenceX, differenceY, absDiffY, absDiffY)) {
					return false;
				}

				break;

			// Queen
			case 'q':
				if (!bishopCheck(origin, destiny, differenceX, differenceY, absDiffX, absDiffY) && !rookCheck(origin, destiny, differenceX, differenceY, absDiffX, absDiffY)) {
					return false;
				}
				break;
			default:
				break;
		}
		return true;
	}

	private static boolean bishopCheck(int[] origin, int[] destiny, int differenceX, int differenceY, int absDiffX, int absDiffY) {
		Directions vDirection;
		Directions hDirection;
		if (absDiffX != absDiffY) {
			return false;
		}
		vDirection = (differenceX > 0) ? Directions.UP : Directions.DOWN;
		hDirection = (differenceY > 0) ? Directions.RIGHT : Directions.LEFT;
		int travel = absDiffX;
		int originX = origin[0];
		int originY = origin[1];
		int yCheck, xCheck;
		for (int i = 0; i <= travel; ++i) {
			if (vDirection == Directions.UP) {
				yCheck = originY + i;
			} else {
				yCheck = originY - i;
			}
			if (hDirection == Directions.RIGHT) {
				xCheck = originX + i;
			} else {
				xCheck = originX - i;
			}
			if (board[xCheck][yCheck] != ' ') {
				return false;
			}
		}
		return true;
	}

	private static boolean rookCheck(int[] origin, int[] destiny, int differenceX, int differenceY, int absDiffX, int absDiffY) {
		if (absDiffX > 0 && absDiffY > 0) {
			return false;
		}
		Directions direction;
		if (differenceX > 0) {
			direction = Directions.RIGHT;
		} else if (differenceX < 0) {
			direction = Directions.LEFT;
		} else if (differenceY > 0) {
			direction = Directions.UP;
		} else {
			direction = Directions.DOWN;
		}
		int originX = origin[0];
		int originY = origin[1];
		int destinyX = destiny[0];
		int destinyY = destiny[1];
		switch (direction) {
			case RIGHT:
				for (int i = originX; i < destinyX; ++i) {
					if (board[originX][originY] != ' ') {
						return false;
					}
				}
				break;

			case LEFT:
				for (int i = originX; i < destinyX; --i) {
					if (board[originX][originY] != ' ') {
						return false;
					}
				}
				break;

			case UP:
				for (int i = originY; i < destinyY; ++i) {
					if (board[originX][originY] != ' ') {
						return false;
					}
				}
				break;

			case DOWN:
				for (int i = originY; i < destinyY; --i) {
					if (board[originX][originY] != ' ') {
						return false;
					}
				}
				break;
			default:
				break;
		}
		return true;
	}

	private static boolean knightCheck(int absDiffX, int absDiffY) {
		if (!((absDiffX == 2 && absDiffY == 1) && !(absDiffX == 1 && absDiffY == 2))) {
			return false;
		} else {
			return true;
		}
	}

	private static boolean bound(int[] coordinates) {
		return (coordinates[0] >= 0 && coordinates[0] <= 9) && (coordinates[1] >= 0 && coordinates[1] <= 9);
	}

	private static void Draw() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				Character piece = board[i][j];
				System.out.print(piece);
			}
			System.out.println();
		}
	}

	/*
	 * https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
	 */
	public static void Fen2Arr(String position) {

		// TODO: Actual translation
		char[][] nBoard = { { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' }, { 'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R' },
				new char[8], new char[8], new char[8], new char[8], { 'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r' },
				{ 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p' } };
		turn = true;
		for (int i = 0; i < castling.length; i++) {
			castling[i] = true;
		}
		board = nBoard.clone();
	}

}
