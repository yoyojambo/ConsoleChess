import java.util.Scanner;

public class ConChess {

	// Board begins at a8, ends at h1
	// private static final String initPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	// Board is layed out top to bottom, left to right, [x][y]
	private static char[][] board;
	// Turn true = white, false = black
	private static boolean turn = true;
	private static boolean Check;
	// Kings Location: [0] white, [1] black; x,y
	private static int[][] kLocation = {{4, 7},{4, 0}};
	// Castling: [0] = White King Side, [1] White Queen Side,
	// [2] Black King Side, [3] Black Queen Side
	private static boolean[] castling = new boolean[4];

	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to Console Chess!\n");
		Help();
		board = initPositionArr();
		Draw();
		while (true) {
			int[][] move = nextMove();
			int[] origin = move[0];
			int[] destiny = move[1];
			if (legal(origin, destiny, board)) {
				char[][] newBoard = makeMove(origin, destiny);
				board = newBoard.clone();
				turn = !turn;
				Draw();
				if (Check) {
					System.out.println("Check!");
				}
				Check = false;
			} else {
				System.out.println("Illegal move, input again.\n");
			}
		}
	}

	private static int[][] nextMove() {
		Scanner input = new Scanner(System.in);
		System.out.print("\nMove: ");
		String unParsed = input.nextLine().toLowerCase();
		String[] parsed = unParsed.split(" ");

		char originXChar = parsed[0].charAt(0);
		char originYChar = parsed[0].charAt(1);
		char destinyXChar = parsed[1].charAt(0);
		char destinyYChar = parsed[1].charAt(1);

		int originX = (int) originXChar - 97;
		int originY = -1 * ((int) originYChar - 49) + 7;
		int destinyX = (int) destinyXChar - 97;
		int destinyY = -1 * ((int) destinyYChar - 49) + 7;

		int[][] move = { { originX, originY }, { destinyX, destinyY } };
		input.close();
		return move;
	}

	private static void Help() {
		// TODO: Make some instructions
	}

	public static char[][] makeMove(int[] origin, int[] destiny) {
		char[][] newBoard = board.clone();
		newBoard[destiny[0]][destiny[1]] = newBoard[origin[0]][origin[1]];
		newBoard[origin[0]][origin[1]] = ' ';
		return newBoard;
	}

	enum Directions {
		UP, DOWN, LEFT, RIGHT
	}

	private static boolean legal(int[] origin, int[] destiny, char[][] board) {
		// Bounds
		if (!bound(origin) || !bound(destiny)) {
			return false;
		} else if (origin.equals(destiny)) {
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

		// Piece validation
		if (board[originX][originY] == ' ') {
			return false;
		}
		// Validating turn is right
		if (turn != originSide) {
			return false;
		}
		// Validating move is attacking the opponent, not itself
		else if (originSide == destinySide && destinyChar != ' ') {
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
				if (!bishopCheck(origin, destiny, differenceX, differenceY, absDiffX, absDiffY)
						&& !rookCheck(origin, destiny, differenceX, differenceY, absDiffX, absDiffY)) {
					return false;
				}
				break;

			case 'p':
				if (!pawnCheck(destiny, originSide, differenceY, absDiffX, absDiffY)) {
					return false;
				}
				break;

			case 'k':
				if (absDiffX > 1 || absDiffY > 1) {
					return false;
				}
				break;
			default:
				break;
		}
		//Self Check prevention
		char[][] potentialBoard = makeMove(origin, destiny);
		if (turn == kingCheck(potentialBoard, turn)) {
			return false;
		}
		if (kingCheck(potentialBoard, !turn)) {
			Check = true;
		}

		return true;
	}

	private static boolean pawnCheck(int[] destiny, boolean originSide, int differenceY, int absDiffX, int absDiffY) {
		int destinyX = destiny[0];
		int destinyY = destiny[1];
		// Moving more than you should
		if (absDiffX > 1 || absDiffY > 1) {
			return false;
		}
		// Going backwards
		else if ((originSide && differenceY != -1) || (!originSide && differenceY != 1)) {
			return false;
		}
		// Diagonal and straight validation
		else if ((board[destinyX][destinyY] == ' ' && absDiffX != 0)
				|| (board[destinyX][destinyY] != ' ' && absDiffX != 1)) {
			return false;
		}
		return true;
	}

	private static boolean bishopCheck(int[] origin, int[] destiny, int differenceX, int differenceY, int absDiffX,
			int absDiffY) {
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

	private static boolean rookCheck(int[] origin, int[] destiny, int differenceX, int differenceY, int absDiffX,
			int absDiffY) {
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

	// KingCheck validates for checks on said king
	// Returns true if the king IS checked
	private static boolean kingCheck(char[][] board, boolean wKing) {
		// wKing = Which king, and also White King (true=white, false=black)
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				int whichKing = wKing ? 0 : 1;
				int[] destiny = kLocation[whichKing];

				char piece = board[i][j];
				if (wKing != Character.isUpperCase(piece) && piece != ' ') {
					int[] origin = { i, j };
					int originX = origin[0];
					int originY = origin[1];
					int destinyX = destiny[0];
					int destinyY = destiny[1];
					int differenceX = originX - destinyX;
					int differenceY = originY - destinyY;
					int absDiffX = Math.abs(differenceX);
					int absDiffY = Math.abs(differenceY);
					switch (Character.toLowerCase(piece)) {
						case 'n':
							if (knightCheck(absDiffX, absDiffY)) {
								return true;
							}
							break;
						case 'b':
							if (bishopCheck(origin, destiny, differenceX, differenceY, absDiffX, absDiffY)) {
								return true;
							}
							break;

						case 'r':
							if (rookCheck(origin, destiny, differenceX, differenceY, absDiffX, absDiffY)) {
								return true;
							}
							break;
						case 'q':
							if (bishopCheck(origin, destiny, differenceX, differenceY, absDiffX, absDiffY)
									|| rookCheck(origin, destiny, differenceX, differenceY, absDiffX, absDiffY)) {
								return true;
							}
							break;

						case 'p':
							if (pawnCheck(destiny, wKing, differenceY, absDiffX, absDiffY)) {
								return true;
							}
							break;
						case 'k':
							if (absDiffX == 1 || absDiffY == 1) {
								return true;
							}
							break;
						default:
							break;
					}
				}
			}
		}
		return false;
	}

	private static boolean bound(int[] coordinates) {
		return (coordinates[0] >= 0 && coordinates[0] < 8) && (coordinates[1] >= 0 && coordinates[1] < 8);
	}

	private static void Draw() {
		for (int i = 0; i < board.length; i++) {
			int row = (-1 * i) + 8;
			System.out.print(row + "\t|");
			for (int j = 0; j < board[i].length; j++) {
				char piece = board[j][i];
				System.out.print(" " + piece + " |");
			}
			System.out.println("\n\t---------------------------------");
		}
		/*
		String columns = "ABCDEFGH";
		for (int i = 0; i < columns.length(); i++) {
			
		}
		*/
		System.out.println("\t  A   B   C   D   E   F   G   H");

	}

	/*
	 * https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
	 */
	public static void Fen2Arr(String position) {

		// TODO: Actual translation
		// TODO: Set King's Positions
		char[][] nBoard = initPositionArr();
		turn = true;
		for (int i = 0; i < castling.length; i++) {
			castling[i] = true;
		}
		board = nBoard.clone();
	}

	private static char[][] initPositionArr() {
		char[][] nArray = new char[8][8];
		String pieces = "rnbqkbnr";
		// Piece laying
		for (int i = 0; i < nArray.length; i++) {
			char xDependantPiece = pieces.charAt(i);
			nArray[i][1] = 'p';
			nArray[i][6] = 'P';
			nArray[i][0] = xDependantPiece;
			nArray[i][7] = Character.toUpperCase(xDependantPiece);
			for (int j = 2; j < 6; j++) {
				nArray[i][j] = ' ';
			}
			
		}
		return nArray;
	}

}
