import java.util.Scanner;

public class ConChess {

	// Board begins at a8, ends at h1
	// private static final String initPosition =
	// "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	// Board is layed out top to bottom, left to right, [x][y]
	private char[][] board;
	// Turn true = white, false = black
	private boolean turn;
	private boolean Check = false;
	// Kings Location: [0] white, [1] black; x,y
	private int[][] kLocation = { { 4, 7 }, { 4, 0 } };
	// Castling:[0] [0] = White Short, [1] White Long,
	// [1] [0] Black Short, [1] Black Long
	private boolean[][] castling = { { true, true }, { true, true } };
	// CastleArray is just to specify it is the castling action
	private static int[] castleArray = { 213453, 592837 };
	final private static String logo = " ____ ____ ____ ____ ____ ____ ____\n||C |||O |||N |||S |||O |||L |||E ||\n||__|||__|||__|||__|||__|||__|||__||\n|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|\n ____ ____ ____ ____ ____\n||C |||H |||E |||S |||S ||\n||__|||__|||__|||__|||__||\n|/__\\|/__\\|/__\\|/__\\|/__\\|";

	public ConChess() {
		board = cloneArr(initPositionArr());
		turn = true;
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String[] formattedLogo = logo.split("\n");
		System.out.println("\tWelcome to:");
		for (String string : formattedLogo) {
			System.out.println('\t' + string);
		}
		System.out.println();
		Help();
		ConChess game = new ConChess();
		game.board = cloneArr(initPositionArr());
		game.Draw();
		while (true) {
			int[][] move = new int[2][2];
			try {
				move = game.nextMove(input);
			} catch (Exception e) {
				System.out.println("Input not formated correctly, try again.");
				continue;
			}
			if (move == null) {
				break;
			}
			int[] origin = move[0];
			int[] destiny = move[1];
			if (legal(origin, destiny, game)) {

				// The Castling "Exception" needs to be implemented for here too
				// The castling has been validated at this point supposedly
				int castleSideIndex = game.turn ? 0 : 1;
				if (origin == castleArray) {
					boolean longCastle = destiny[0] == 1 ? true : false;

					// The y ordinate of the movement
					int y = game.turn ? 7 : 0;

					// Rook's movement
					origin[0] = (longCastle ? 0 : 7);
					origin[1] = y;
					destiny[0] = (longCastle ? 3 : 5);
					destiny[1] = y;
					game.board = makeMove(origin, destiny, game.board);

					// King's movement
					origin[0] = 4;
					origin[1] = y;
					destiny[0] = (longCastle ? 2 : 6);
					destiny[1] = y;
					game.board = makeMove(origin, destiny, game.board);
					
					// Cant castle anymore
					game.castling[castleSideIndex][0] = false;
					game.castling[castleSideIndex][1] = false;
				} else {
					game.board = makeMove(origin, destiny, game.board);
					char originChar = Character.toLowerCase(game.board[origin[0]][origin[1]]);

					// Makes the appropiate changes to the castling array so that it remembers that it has been moved
					if (originChar == 'k') {
						game.castling[castleSideIndex][0] = false;
						game.castling[castleSideIndex][1] = false;
					}
					else if (originChar == 'r') {
						if (origin[1] == (game.turn ? 7 : 0) && (origin[0] == 0 || origin[0] == 7) ) {
							// if x is 7 the castle is short ;), which is the first index of the castling[] array
							game.castling[castleSideIndex][origin[0] == 7 ? 0 : 1] = false;
						}
					}
				}
				game.turn = !game.turn;
				game.Draw();
				if (game.Check) {
					System.out.println("Check!");
				}
				game.Check = false;
			} else {
				System.out.println("Illegal move, input again.\n");
			}
		}
		input.close();
	}

	private int[][] nextMove(Scanner input) throws Exception {
		System.out.print("\n \t" + ((turn) ? "White" : "Black") + " Move: ");
		String unParsed = input.nextLine().toLowerCase().trim();
		if (unParsed.equals("exit")) {
			return null;
		} else if (unParsed.equals("resign")) {
			String loser = turn ? "White" : "Black";
			String winner = !turn ? "White" : "Black";
			System.out.println(loser + " resigns\n" + winner + " wins!");
			return null;
		} else if (unParsed.equals("draw")) {
			String offer = turn ? "White" : "Black";
			System.out.println(offer + " offers draw.\nDo you accept? (y/n)");
			String drawStr = input.nextLine().toLowerCase();
			if (drawStr.equals("y")) {
				System.out.println("Draw accepted.");
				return null;
			} else if (drawStr.equals("n")) {
				System.out.println("Draw denied.");
				nextMove(input);
			} else {
				System.out.println("Not an answer, draw aborted.");
				nextMove(input);
			}
			// Castling requires coordination with a specific coordinate hardcoded to the
			// legal function too
			// castleArray is the number array for the first coordinates x value, which will
			// indicate it is a castle move
			// After castleArray, the side is given by the first number of the first array,
			// 0 = short castle and 1 = long castle
		} else if (unParsed.equals("ooo")) {
			return new int[][] { castleArray, { 1, 0 } };
		} else if (unParsed.equals("oo")) {
			return new int[][] { castleArray, { 0, 0 } };
		}
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

		return move;
	}

	private static void Help() {
		// TODO: Make some instructions
		String instructions = "\tTo move a piece, enter the coordinates of the piece,\n\tand where you would want to move it, in the following\n\tformat:\"xy xy\", for example \"d2 d4\".";
		System.out.println(instructions);
	}

	public static char[][] makeMove(int[] origin, int[] destiny, char[][] board) {
		int originX = origin[0];
		int originY = origin[1];
		int destinyX = destiny[0];
		int destinyY = destiny[1];
		char[][] newBoard = cloneArr(board);
		char temp = newBoard[originX][originY];
		newBoard[originX][originY] = ' ';
		newBoard[destinyX][destinyY] = temp;
		return newBoard;
	}

	enum Directions {
		UP, DOWN, LEFT, RIGHT
	}

	public static boolean legal(int[] origin, int[] destiny, ConChess instance) {
		char[][] board = instance.board;
		boolean turn = instance.turn;

		// Castling needs to be checked before anything else, it is an exception that
		// will trigger the other fail conditions
		if (origin == castleArray) {
			// In makeMove the castle instruction leaves a 1 if the castle is long (OOO) and
			// 0 if it is short (OO)
			boolean longCastle = destiny[0] == 1 ? true : false;
			if (instance.castling[turn ? 0 : 1][destiny[0]]) {
				int[] castleOrigin = new int[2];
				int[] castleDestiny = new int[2];

				castleOrigin[0] = longCastle ? 0 : 7;
				castleOrigin[1] = turn ? 7 : 0;
				castleDestiny[1] = turn ? 7 : 0;
				castleDestiny[0] = 4;

				// I'm dumb and made everything a goddamn parameter so here they are
				int originX = castleOrigin[0];
				int originY = castleOrigin[1];
				int destinyX = castleDestiny[0];
				int destinyY = castleDestiny[1];
				int differenceX = destinyX - originX;
				int differenceY = destinyY - originY;
				int absDiffX = Math.abs(differenceX);
				int absDiffY = Math.abs(differenceY);

				// RookValidation so that there is no pieces in between
				return rookValidation(castleOrigin, castleDestiny, differenceX, differenceY, absDiffX, absDiffY, board);
			}
		}
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
		int differenceX = destinyX - originX;
		int differenceY = destinyY - originY;

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
				if (!knightValidation(absDiffX, absDiffY)) {
					return false;
				}
				break;
			// Rook
			case 'r':
				if (!rookValidation(origin, destiny, differenceX, differenceY, absDiffX, absDiffY, board)) {
					return false;
				}

				break;
			// Bishop
			case 'b':

				if (!bishopValidation(origin, destiny, differenceX, differenceY, absDiffX, absDiffY, board)) {
					return false;
				}
				break;

			// Queen uses both the bishop and the rook va
			case 'q':
				if (!bishopValidation(origin, destiny, differenceX, differenceY, absDiffX, absDiffY, board)
						&& !rookValidation(origin, destiny, differenceX, differenceY, absDiffX, absDiffY, board)) {
					return false;
				}
				break;

			// Pawn
			case 'p':
				if (!pawnValidation(destiny, originSide, differenceY, absDiffX, absDiffY, board)) {
					return false;
				}
				break;

			// king
			case 'k':
				if (absDiffX > 1 || absDiffY > 1) {
					return false;
				}

				break;
			default:
				break;
		}
		// Self Check prevention
		char[][] potentialBoard = cloneArr(makeMove(origin, destiny, board));

		if (Character.toLowerCase(originChar) == 'k') {
			int[][] potentialKingPos = cloneArr(instance.kLocation);
			if (turn) {
				potentialKingPos[0] = cloneArr(destiny);
			} else {
				potentialKingPos[1] = cloneArr(destiny);
			}
			if (kingCheck(potentialBoard, turn, potentialKingPos)) {
				return false;
			} else {
				instance.kLocation[turn ? 0 : 1] = cloneArr(destiny);
			}
		} else {
			if (kingCheck(potentialBoard, turn, instance.kLocation)) {
				return false;
			}
		}
		if (kingCheck(potentialBoard, !turn, instance.kLocation)) {
			instance.Check = true;
		}

		return true;
	}

	private static boolean pawnValidation(int[] destiny, boolean originSide, int differenceY, int absDiffX,
			int absDiffY, char[][] board) {
		int destinyX = destiny[0];
		int destinyY = destiny[1];
		// Exception for the first move
		if (((originSide && differenceY == -2) || (!originSide && differenceY == 2)) && absDiffX == 0
				&& (destinyY == 3 || destinyY == 4)) {
			return true;
		}
		// Moving more than you should
		if ((absDiffX > 1 || absDiffY > 1)) {
			return false;
		}
		// Going backwards
		if ((originSide && differenceY != -1) || (!originSide && differenceY != 1)) {
			return false;
		}
		// Diagonal and straight validation
		else if ((board[destinyX][destinyY] == ' ' && absDiffX != 0)
				|| (board[destinyX][destinyY] != ' ' && absDiffX != 1)) {
			return false;
		}
		return true;
	}

	private static boolean bishopValidation(int[] origin, int[] destiny, int differenceX, int differenceY, int absDiffX,
			int absDiffY, char[][] board) {
		Directions vDirection;
		Directions hDirection;
		if (absDiffX != absDiffY) {
			return false;
		}
		vDirection = (differenceY > 0) ? Directions.UP : Directions.DOWN;
		hDirection = (differenceX > 0) ? Directions.RIGHT : Directions.LEFT;
		int travel = absDiffX;
		int originX = origin[0];
		int originY = origin[1];
		int yCheck, xCheck;
		for (int i = 1; i <= travel - 1; i++) {
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

	private static boolean rookValidation(int[] origin, int[] destiny, int differenceX, int differenceY, int absDiffX,
			int absDiffY, char[][] board) {
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

		// Possibly, this can be implemented in a more elegant way, like the bishop validation
		switch (direction) {
			case RIGHT:
				for (int i = originX + 1; i < destinyX; i++) {
					if (board[i][originY] != ' ') {
						return false;
					}
				}
				break;

			case LEFT:
				for (int i = originX - 1; i > destinyX; i--) {
					if (board[i][originY] != ' ') {
						return false;
					}
				}
				break;

			case UP:
				for (int i = originY + 1; i < destinyY; i++) {
					if (board[originX][i] != ' ') {
						return false;
					}
				}
				break;

			case DOWN:
				for (int i = originY - 1; i > destinyY; i--) {
					if (board[originX][i] != ' ') {
						return false;
					}
				}
				break;
			default:
				break;
		}
		return true;
	}

	private static boolean knightValidation(int absDiffX, int absDiffY) {
		if ((absDiffX == 2 && absDiffY == 1) || (absDiffX == 1 && absDiffY == 2)) {
			return true;
		} else {
			return false;
		}
	}

	// KingCheck validates for checks on said king
	// Returns true if the king IS checked
	private static boolean kingCheck(char[][] board, boolean wKing, int[][] kLocation) {
		// wKing = Which king, and also White King (true=white, false=black)
		int wKingIndex = wKing ? 0 : 1;
		int[] destiny = cloneArr(kLocation)[wKingIndex];
		int destinyX = destiny[0];
		int destinyY = destiny[1];

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (i == destinyX && j == destinyY) {
					continue;
				}
				char piece = board[i][j];
				if (wKing != Character.isUpperCase(piece) && piece != ' ') {
					int[] origin = { i, j };
					int originX = origin[0];
					int originY = origin[1];

					int differenceX = destinyX - originX;
					int differenceY = destinyY - originY;
					int absDiffX = Math.abs(differenceX);
					int absDiffY = Math.abs(differenceY);
					switch (Character.toLowerCase(piece)) {
						case 'n':
							if (knightValidation(absDiffX, absDiffY)) {
								return true;
							}
							break;

						case 'b':
							if (bishopValidation(origin, destiny, differenceX, differenceY, absDiffX, absDiffY,
									board)) {
								return true;
							}
							break;

						case 'r':
							if (rookValidation(origin, destiny, differenceX, differenceY, absDiffX, absDiffY, board)) {
								return true;
							}
							break;

						case 'q':
							if (bishopValidation(origin, destiny, differenceX, differenceY, absDiffX, absDiffY, board)
									|| rookValidation(origin, destiny, differenceX, differenceY, absDiffX, absDiffY,
											board)) {
								return true;
							}
							break;

						case 'p':
							if (pawnValidation(destiny, !wKing, differenceY, absDiffX, absDiffY, board)) {
								return true;
							}
							break;

						case 'k':
							if (absDiffX == 1 && absDiffY == 1) {
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

	private void Draw() {
		System.out.println(" \t_________________________________");
		for (int i = 0; i < this.board.length; i++) {
			int row = (-1 * i) + 8;
			System.out.print("      " + row + " |");
			for (int j = 0; j < this.board[i].length; j++) {
				char piece = this.board[j][i];
				System.out.print(" " + piece + " |");
			}
			System.out.println("\n\t---------------------------------");
		}

		System.out.println("\t  A   B   C   D   E   F   G   H");

	}

	/*
	 * https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
	 * 
	 * public void Fen2Arr(String position) { // TODO: Actual translation char[][]
	 * nBoard = initPositionArr(); this.turn = true; for (int i = 0; i <
	 * this.castling.length; i++) { this.castling[i] = true; } this.board =
	 * cloneArr(nBoard); }
	 */
	private static char[][] cloneArr(char[][] oldArray) {
		int width = oldArray.length;
		int height = oldArray[0].length;
		char[][] newArray = new char[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newArray[i][j] = oldArray[i][j];
			}
		}
		return newArray;
	}

	private static int[][] cloneArr(int[][] oldArray) {
		int width = oldArray.length;
		int height = oldArray[0].length;
		int[][] newArray = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newArray[i][j] = oldArray[i][j];
			}
		}
		return newArray;
	}

	private static int[] cloneArr(int[] oldArray) {
		int length = oldArray.length;
		int[] newArray = new int[length];
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = oldArray[i];
		}
		return newArray;
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
