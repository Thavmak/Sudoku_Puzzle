package new_Sudoku;

import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class New_SudokuPuzzle extends JFrame {
	
	private JButton[][] SQUARE; // has the buttons of the Sudoku Grid 
	public SudokuSolve sSolver;

	public int[][] board = new int[9][9]; // has the current state of the board
	public int[][] initialBoard = new int[9][9]; //has the initial values of the given puzzle
	public int[][] BOARD = new int[9][9]; //has the solution of the puzzle
	
	///Constructor///
	
	public New_SudokuPuzzle(String url, JButton[][] square) {
				
		convertToTable(url);
		
		initializeGrid(square);
		
		sSolver = new SudokuSolve(initialBoard);
	}

	//makes a 2d table (initialBoard[9][9]) with the values taken from the String
	private void convertToTable(String url) {

		//extracts the numbers from the String and adds them to a table
		int[] nums = new int[url.length()];

		for (int i = 0; i < url.length(); i++) {

			nums[i] = Character.getNumericValue(url.charAt(i));

			//format table appropriately
			if ( nums[i] == -1) {
				nums[i] = 0;
			}
			if (i == 9 || i == 19 || i == 29 || i == 39 || i == 49 || i == 59 || i == 69 || i == 79 || i == 89 || i == 90 ) {
				nums[i] = -1;
			}
		}

		//initializes the board with the given puzzle
		int i=0;
		for (int j = 0; j < 9; j++) {
			for (int k = 0; k < 9; k++) {
				if ( nums[i] == -1){
					i++;
				}
				initialBoard[j][k] = nums[i];
				board[j][k] = initialBoard[j][k];
				i++;
			}
		}
	}

	//adds the table (initialBoard[9][9]) to the Sudoku grid
	private void initializeGrid(JButton[][] square) {

		this.SQUARE = square;

		//repaints the grid for the next game
		for (int j=0; j<9; j++) {
			for (int k=0; k<9; k++) {
				this.SQUARE[j][k].setBackground(Color.WHITE); 
			}
		}
		//adds puzzle to the grid
		for (int j=0; j<9; j++) {
			for (int k=0; k<9; k++) {
				if (initialBoard[j][k] == 0) {
					this.SQUARE[j][k].setText("");
				}
				else{
					this.SQUARE[j][k].setText(""+initialBoard[j][k]);
					this.SQUARE[j][k].setBackground(Color.lightGray);
				}
			}
		} 
	}

	//adds the solution of the puzzle to the Sudoku grid
	public void setSolution() {
		for (int j=0; j<9; j++) {
			for (int k=0; k<9; k++) {

				SQUARE[j][k].setText(""+BOARD[j][k]);
			}
		}
	}

	//adds the current table to the Sudoku grid
	public void setGrid(JButton[][] square) {

		this.SQUARE = square;

		for (int j=0; j<9; j++) {
			for (int k=0; k<9; k++) {
				if (board[j][k] == 0) {
					this.SQUARE[j][k].setText("");
				}
				else{
					this.SQUARE[j][k].setText(""+board[j][k]);
				}
			}
		}
	}

	//Gives the solution of the given Sudoku
	private class SudokuSolve {

		public SudokuSolve(int[][] board) {

			for (int r = 0; r < 9; r++) {
				for (int c = 0; c < 9; c++) {
					BOARD[r][c] = board[r][c];
				}
			}
			solver(BOARD);
		}

		public boolean solver(int[][] BOARD) {

			for (int r = 0; r < BOARD.length; r++) {
				for (int c = 0; c < BOARD[0].length; c++) {
					if (BOARD[r][c] == 0) {
						for (int k = 1; k <= 9; k++) {

							BOARD[r][c] = (0 + k);

							if (isValid(BOARD, r, c) && solver(BOARD)) {

								return true;
							} 
							else {

								BOARD[r][c] = 0;
							}
						}
						return false;
					}
				}
			}
			return true;
		}

		public boolean isValid(int[][] BOARD, int r, int c) {

			//check row
			boolean[] row = new boolean[9];

			for (int i = 0; i < 9; i++) {
				if (BOARD[r][i] >= 1 && BOARD[r][i] <= 9) {
					if (row[BOARD[r][i] - 1] == false) {

						row[BOARD[r][i] - 1] = true;
					} 
					else {
						return false;
					}
				}
			}

			//check column
			boolean[] col = new boolean[9];

			for (int i = 0; i < 9; i++) {
				if (BOARD[i][c] >= 1 && BOARD[i][c] <= 9) {
					if (col[BOARD[i][c] - 1] == false) {

						col[BOARD[i][c] - 1] = true;
					} 
					else {
						return false;
					}
				}
			}

			//check the (3x3) grid
			boolean[] grid = new boolean[9];

			for (int i = (r / 3) * 3; i < (r / 3) * 3 + 3; i++) {
				for (int j = (c / 3) * 3; j < (c / 3) * 3 + 3; j++) {
					if (BOARD[i][j] >= 1 && BOARD[i][j] <= 9) {
						if (grid[BOARD[i][j] - 1] == false) {

							grid[BOARD[i][j] - 1] = true;
						} 
						else {
							return false;
						}
					}
				}
			}
			return true;
		}
	}
}