import java.util.HashMap;
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
	private int[][] kingLocation;
	// Castling:[0] [0] = White Short, [1] White Long,
	// [1] [0] Black Short, [1] Black Long
	private boolean[][] castling;
	private int fullMoves;
	// CastleCode is just to specify it is the castling action
	private static final int[] castleCode = { 213453, 592837 };
	private int halfMoves;
	final private static String logo = " ____ ____ ____ ____ ____ ____ ____\n||C |||O |||N |||S |||O |||L |||E ||\n||__|||__|||__|||__|||__|||__|||__||\n|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|/__\\|\n ____ ____ ____ ____ ____\n||C |||H |||E |||S |||S ||\n||__|||__|||__|||__|||__||\n|/__\\|/__\\|/__\\|/__\\|/__\\|";
	private static String[] piecesEaten = {"", ""};
	private static HashMap <Character, Integer> piecesValue = new HashMap<>();
	private static char[] piecesValueCharArray = {'p', 'k', 'b', 'r', 'q'};
	private static int[] piecesValueIntArray = {1, 3, 3, 5, 9};
	
	public ConChess() {
		setBoard(cloneArr(initPositionArr()));
		setTurn(true);

		boolean[][] castling = { { true, true }, { true, true } };
		setCastling(castling);

		int[][] kingLocation = { { 4, 7 }, { 4, 0 } };
		setKingLocation(kingLocation);

		setFullMoves(0);

		piecesValueArrayCheck();
	}

	private static void piecesValueArrayCheck() {
		if (piecesValue.isEmpty()) {
			for (int i = 0; i < 5; i++) {
				char charEntry = piecesValueCharArray[i];
				int intEntry = piecesValueIntArray[i]; 

				piecesValue.put(charEntry, intEntry);
			}
		}
	}

	public ConChess(char[][] board, boolean turn, boolean[][] castling, int[][] kingLocation, int fullMoves) {
		setBoard(board);
		setTurn(turn);
		setCastling(castling);
		setKingLocation(kingLocation);
		setFullMoves(fullMoves);
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String[] formattedLogo = logo.split("\n");
		System.out.println("\tWelcome to:");
		for (String string : formattedLogo) {
			System.out.println('\t' + string);
		}
		System.out.println();
		Instructions();
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

				// The Castling "Exception" implemented
				// The castling has been validated at this point supposedly
				int turnIndex = game.turn ? 0 : 1;
				if (origin == castleCode) {
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
					game.castling[turnIndex][0] = false;
					game.castling[turnIndex][1] = false;
				} else {

					// Pieces eaten update
					char destinyChar = game.board[destiny[0]][destiny[1]];
					if (destinyChar != ' ') {
						piecesEaten[turnIndex] = insertEatenPiece(piecesEaten[turnIndex], destinyChar);
						game.setHalfMoves(0);
					}
					else game.increaseHalfMoves();

					game.board = makeMove(origin, destiny, game.board);
					char originChar = Character.toLowerCase(game.board[origin[0]][origin[1]]);

					// Makes the appropiate changes to the castling array so that it remembers that
					// it has been moved
					if (originChar == 'k') {
						game.castling[turnIndex][0] = false;
						game.castling[turnIndex][1] = false;
					} else if (originChar == 'r') {
						if (origin[1] == (game.turn ? 7 : 0) && (origin[0] == 0 || origin[0] == 7)) {
							// if x is 7 the castle is short ;), which is the first index of the castling[]
							// array
							game.castling[turnIndex][origin[0] == 7 ? 0 : 1] = false;
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

	public int[][] nextMove(Scanner input) throws Exception {
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
			return new int[][] { castleCode, { 1, 0 } };
		} else if (unParsed.equals("oo")) {
			return new int[][] { castleCode, { 0, 0 } };
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
	
	public static void Instructions() {
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
	
	public static ConChess FEN2Chess(String unparsedFENString) {
		// TODO: Maybe make errors to be able to display a more specific message if anything fails
		String[] parsedFEString = unparsedFENString.split(" ");
		if (parsedFEString.length != 6) {
			return null;
		}
		// First field is piece position
		char[][] board = FenString2Position(parsedFEString[0]);
		if (board == null)
		return null;
		int[][] kingLocation = searchKingLocation(board);
		// Second field is Side to move
		boolean turn;
		if (parsedFEString[1].equals("w")) {
			turn = true;
		} else if (parsedFEString[1].equals("b")) {
			turn = false;
		} else
		return null;
		// Third field is Castling
		boolean[][] castling = new boolean[2][2];
		castling[0][0] = (parsedFEString[2].indexOf('K') != -1) ? true : false;
		castling[0][1] = (parsedFEString[2].indexOf('Q') != -1) ? true : false;
		castling[1][0] = (parsedFEString[2].indexOf('k') != -1) ? true : false;
		castling[1][1] = (parsedFEString[2].indexOf('q') != -1) ? true : false;
		// Fourth fiel is en passant
		// Fifth field is the Halfmove Clock
		// Sixt field is Fullmove counter
		int fullMoves;
		try {
			fullMoves = Integer.parseInt(parsedFEString[5]);
		} catch (Exception e) {
			return null;
		}
		return new ConChess(board, turn, castling, kingLocation, fullMoves);
	}

	private static char[][] FenString2Position(String string) {
		String legalPieces = "pnbrqk";
		char[][] board = new char[8][8];
		int strIndex = 0;
		int file = 0;
		int rank = 0;
		while (strIndex < string.length()) {
			char indexChar = string.charAt(strIndex);
			int num = Character.getNumericValue(indexChar);
			if (indexChar == '/') {
				file = 0;
				rank++;
			}
			// If it is a piece
			else if (legalPieces.indexOf(indexChar) != -1) {
				try {
					board[file][rank] = indexChar;
				} catch (IndexOutOfBoundsException e) {
					return null;
				}
				file++;
			} else if (num > 0) {
				file = +num;
			}
			// This should occur only if there was a misinput
			else
				return null;
			strIndex++;
			continue;
		}
		return board;
	}

	private static int[][] searchKingLocation(char[][] board) {
		int[][] kingLocation = new int[2][2];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (board[x][y] == 'k') {
					int[] location = { x, y };
					kingLocation[1] = location;
				} else if (board[x][y] == 'K') {
					int[] location = { x, y };
					kingLocation[0] = location;
				}
			}
		}
		return kingLocation;
	}

	enum Directions {
		UP, DOWN, LEFT, RIGHT
	}

	public static boolean legal(int[] origin, int[] destiny, ConChess instance) {
		char[][] board = instance.board;
		boolean turn = instance.turn;

		// Castling needs to be checked before anything else, it is an exception that
		// will trigger the other fail conditions
		if (origin == castleCode) {
			// Cant castle while in check
			if (instance.Check)
				return false;
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
		if (!bound(origin) || !bound(destiny))
			return false;
		// Move to the same place
		else if (origin.equals(destiny))
			return false;

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
		if (board[originX][originY] == ' ')
			return false;
		// Validating turn is right
		if (turn != originSide)
			return false;
		// Validating move is attacking the opponent, not itself
		else if (originSide == destinySide && destinyChar != ' ')
			return false;

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
			int[][] potentialKingPos = cloneArr(instance.kingLocation);
			potentialKingPos[turn ? 0 : 1] = cloneArr(destiny);

			if (kingCheck(potentialBoard, turn, potentialKingPos)) {
				return false;
			} else {
				instance.kingLocation[turn ? 0 : 1] = cloneArr(destiny);
			}
		} else {
			if (kingCheck(potentialBoard, turn, instance.kingLocation)) {
				return false;
			}
		}
		if (kingCheck(potentialBoard, !turn, instance.kingLocation)) {
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

		// Possibly, this can be implemented in a more elegant way, like the bishop
		// validation
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
	private static boolean kingCheck(char[][] board, boolean wKing, int[][] kingLocation) {
		// wKing = Which king, and also White King (true=white, false=black)
		int wKingIndex = wKing ? 0 : 1;
		int[] destiny = cloneArr(kingLocation)[wKingIndex];
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

	public void Draw() {
		String infoBoxIndent = "   ";
		int infoBoxHeight = 7;
		System.out.print("\n\n");
		PatternPrinter("\u250c", "\u2510", 33, 4, "\u252c", "\u2500", true, " \t");
		for (int i = 0; i < this.board.length; i++) {
			int row = (-1 * i) + 8;
			System.out.print("      " + row + " \u2502");
			for (int j = 0; j < this.board[i].length; j++) {
				char piece = this.board[j][i];
				System.out.print(" " + piece + " \u2502");
			}
			int boxWidth = 16;
			if (row == infoBoxHeight) {
				System.out.println();
				PatternPrinter("\u251c", "\u2524", 33, 4, "\u253c", "\u2500", false, "\t");
				PatternPrinter("\u250c", "\u2510", boxWidth, 0, "\u252c", "\u2500", true, infoBoxIndent);
			}
			else if (row == infoBoxHeight - 1) {
				String textPiecesEaten = "Pieces eaten:";
				System.out.println(infoBoxIndent + "\u2502"+ textPiecesEaten + boxFitter(textPiecesEaten.length(), boxWidth) + "\u2502");
				PatternPrinter("\u251c", "\u2524", 33, 4, "\u253c", "\u2500", false, "\t");
				System.out.println(infoBoxIndent + "\u2502" + piecesEaten[1] + boxFitter(piecesEaten[1].length(), boxWidth) + "\u2502");
			}
			else if (row == infoBoxHeight - 2) {
				System.out.println(infoBoxIndent + "\u2502" + piecesEaten[0] + boxFitter(piecesEaten[0].length(), boxWidth) + "\u2502");
				PatternPrinter("\u251c", "\u2524", 33, 4, "\u253c", "\u2500", false, "\t");
				String textMoves = "Half Move: " + halfMoves;
				System.out.println(infoBoxIndent + "\u2502"+ textMoves + boxFitter(textMoves.length(), boxWidth) + "\u2502");
			}
			else if (row == infoBoxHeight - 3) {
				String textMoves = "Move: " + fullMoves;
				System.out.println(infoBoxIndent + "\u2502"+ textMoves + boxFitter(textMoves.length(), boxWidth) + "\u2502");
				PatternPrinter("\u251c", "\u2524", 33, 4, "\u253c", "\u2500", false, "\t");
				PatternPrinter("\u2514", "\u2518", boxWidth, 0, "\u2534", "\u2500", true, infoBoxIndent);
			}
			else if (i != this.board.length - 1) {
				System.out.println();
				PatternPrinter("\u251c", "\u2524", 33, 4, "\u253c", "\u2500", true, "\t");
			}
		}
		PatternPrinter("\u2514", "\u2518", 33, 4, "\u2534", "\u2500", true, "\n\t");
		System.out.println("\t  A   B   C   D   E   F   G   H");
	}

	private String boxFitter(int length, int boxWidth) {
		String spaces = "";
		while (spaces.length() + length < boxWidth - 2) {
			spaces = spaces + " ";
		}
		return spaces;
	}

	private String conditionalENDL(boolean b) {
		return b ? "\n":"";
	}

	// Makes printing the lines between the pieces easier, making boxes less painful
	public void PatternPrinter(String firstString, String lastString, int length, int cycle, String cycleString,
			String commonString, boolean endl, String indentString) {
		System.out.print(indentString);
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				System.out.print(firstString);
			} else if (i == length - 1) {
				System.out.print(lastString);
			} else if (cycle != 0 && i % cycle == 0) {
				System.out.print(cycleString);
			} else {
				System.out.print(commonString);
			}
		}
		System.out.print(conditionalENDL(endl));
	}

	public static char[][] cloneArr(char[][] oldArray) {
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

	private boolean[][] cloneArr(boolean[][] oldArray) {
		boolean[][] newArray = new boolean[oldArray.length][oldArray[0].length];
		for (int i = 0; i < oldArray.length; i++) {
			for (int j = 0; j < newArray[0].length; j++) {
				newArray[i][j] = oldArray[i][j];
			}
		}
		return newArray;
	}

	public static int[][] cloneArr(int[][] oldArray) {
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

	public static int[] cloneArr(int[] oldArray) {
		int length = oldArray.length;
		int[] newArray = new int[length];
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = oldArray[i];
		}
		return newArray;
	}

	public static char[][] initPositionArr() {
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

	private static String insertEatenPiece(String string, char destinyChar) {
		if (string.length() == 0) return "" + destinyChar;
		
		char[] oldCharArray = string.toCharArray();
		int insertIndex = 0;
		insertIndex = findInsertIndex(destinyChar, oldCharArray);

		char[] newCharArray = new char[oldCharArray.length + 1];
		// Inserts the char to the right position
		for (int i = 0; i < newCharArray.length; i++) {
			if (i < insertIndex) newCharArray[i] = oldCharArray[i];
			else if (i == insertIndex) newCharArray[i] = destinyChar;
			else newCharArray[i] = oldCharArray[i - 1];
		}
		return new String(newCharArray);
	}

	private static int findInsertIndex(char destinyChar, char[] oldCharArray) {
		int f = 0;
		int u = oldCharArray.length;
		while (true) {
			int subArrLenght = u - f;
			int m = (f + u) / 2;
			int fValue = piecesValue.get(oldCharArray[f]);
			int mValue = piecesValue.get(oldCharArray[m]);
			int rValue = piecesValue.get(destinyChar);

			if ( subArrLenght == 1) return (rValue > fValue) ? u : f;

			if ((subArrLenght == 2)) {
				if (rValue < fValue) return f;
				else if (rValue < mValue) return m;
				else return u;
			}

			if (rValue == mValue) return m;
			else if (rValue < mValue) u = m + 1;
			else f = m + 1;
		}
	}
	
	public static int[] getCastleCode() {
		return cloneArr(castleCode);
	}

	public boolean[][] getCastling() {
		return cloneArr(castling);
	}

	public int[][] getKingLocation() {
		return cloneArr(kingLocation);
	}

	public char[][] getBoard() {
		return cloneArr(board);
	}

	public boolean getCheck() {
		return this.Check;
	}

	public int getFullMoves() {
		return fullMoves;
	}

	public int getHalfMoves() {
		return halfMoves;
	}

	public void setBoard(char[][] board) {
		this.board = board;
	}

	public void setKingLocation(int[][] kingLocation) {
		this.kingLocation = kingLocation;
	}

	public void setCastling(boolean[][] castling) {
		this.castling = castling;
	}

	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	public void setCheck(boolean check) {
		this.Check = check;
	}

	public void setFullMoves(int fullMoves) {
		this.fullMoves = fullMoves;
	}

	public void setHalfMoves(int halfMoves) {
		this.halfMoves = halfMoves;
	}
	
	private void increaseHalfMoves() {
		halfMoves++;
	}
}
