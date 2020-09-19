package new_Sudoku;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

@SuppressWarnings("serial")
public class New_SudokuFrame extends JFrame implements ItemListener {

	private Container JFramePanel;
	private Sudoku_Generator sGen;
	private New_SudokuPuzzle sPuzzle;
	JCheckBox checkButton;
	
	int[] gridButtonCoor; //Coordinates of the grid box pressed
	int[] numButtonCoor; //Coordinates of the number button pressed
	int flag = -1; //flag for knowing if a grid box has been selected
	int check = 0; //flag for knowing the state of the check box

	private JButton[] numButton = new JButton[10]; //the buttons of the number panel
	private JButton[][] square = new JButton[9][9]; //the buttons of the Sudoku Grid
	JPanel numberPanel = new JPanel();
	Stack theStack = new Stack(60000); //81x81x9
	int[] retrievedBoard = new int[81]; //a table with contents from/for stack

	///Constructor///
	
	private New_SudokuFrame() {

		UIManager.put("Button.select",new Color(255, 255, 200));

		createWindow();

		createMenuBar();

		createSudokuGrid();

		createButtonPanel();
	}

	///UI Components JFRAME///
	
	private void createWindow() {

		setTitle("Sudoku");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void createMenuBar() {

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("New Game");

		JMenuItem easyGame = new JMenuItem("Easy");
		easyGame.addActionListener(new GameModeListener());

		JMenuItem intermediateGame = new JMenuItem("Intermediate");
		intermediateGame.addActionListener(new GameModeListener());

		JMenuItem expertGame = new JMenuItem("Expert");
		expertGame.addActionListener(new GameModeListener());

		fileMenu.add(easyGame);
		fileMenu.addSeparator();
		fileMenu.add(intermediateGame);
		fileMenu.addSeparator();
		fileMenu.add(expertGame);

		menuBar.add(fileMenu);
		setJMenuBar(menuBar);    
	}

	private void createSudokuGrid() {

		JFramePanel = getContentPane();
		JFramePanel.setLayout(new BoxLayout(JFramePanel, BoxLayout.PAGE_AXIS));
		
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(0,3));
		gridPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		gridPanel.setPreferredSize(new Dimension(500,500));
		add(gridPanel, JFramePanel);
		
		//Create 9 Panels for the grid
		JPanel[][] smallPanel = new JPanel[3][3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				smallPanel[i][j] = new JPanel();
				
				smallPanel[i][j].setLayout(new GridLayout(3, 3));
				smallPanel[i][j].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				add(smallPanel[i][j],gridPanel);
			}
		}

		//Create the buttons for the grid
		for (int i = 0; i < 9; i++) {
			int panelI = i / 3;
			for (int j = 0; j < 9; j++) {
				int panelJ = j / 3;

				String text = String.format("%d%d", i, j);

				square[i][j] = new JButton("");
				square[i][j].setFont(new Font("Arial", Font.BOLD, 20));
				square[i][j].setBackground(Color.WHITE);
				square[i][j].setEnabled(false);
				square[i][j].addActionListener(new GridListener());
				square[i][j].setFocusPainted(false);
				square[i][j].setActionCommand(text);


				smallPanel[panelI][panelJ].add(square[i][j]);

				gridPanel.add(smallPanel[panelI][panelJ]);
			}
		}
	}

	private void createButtonPanel() {

		numberPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 20));
		numberPanel.setPreferredSize(new Dimension(800,100));
		add(numberPanel, JFramePanel);

		//Number Buttons
		for (int k=1; k<10; k++) {

			numButton[k] = new JButton(""+k);
			numButton[k].addActionListener(new ButtonListener());
			numButton[k].setBackground(Color.WHITE);
			numButton[k].setActionCommand(""+k);
			numButton[k].setEnabled(false);
			numberPanel.add(numButton[k]);
		}
		
		//Eraser Button
		ImageIcon eraserIcon = new ImageIcon("images/eraser.png");
		Image image1 = eraserIcon.getImage().getScaledInstance(15, 15, Image.SCALE_DEFAULT);
		eraserIcon = new ImageIcon(image1);
		JButton eraserButton = new JButton(eraserIcon);
		eraserButton.setIcon(eraserIcon);
		eraserButton.addActionListener(new ButtonListener());
		eraserButton.setBackground(Color.WHITE);
		eraserButton.setActionCommand("eraser");
		eraserButton.setEnabled(false);
		numberPanel.add(eraserButton);

		//Undo Button
		ImageIcon undoIcon = new ImageIcon("images/undo.png");
		Image image2 = undoIcon.getImage().getScaledInstance(15, 15, Image.SCALE_DEFAULT);
		undoIcon = new ImageIcon(image2);
		JButton undoButton = new JButton(undoIcon);
		undoButton.addActionListener(new ButtonListener());
		undoButton.setBackground(Color.WHITE);
		undoButton.setActionCommand("undo");
		undoButton.setEnabled(false);
		numberPanel.add(undoButton);

		//Check box
		checkButton = new JCheckBox("Verify against solution");
		checkButton.setSelected(false);
		checkButton.addItemListener(this);
		checkButton.setEnabled(false);
		numberPanel.add(checkButton);

		//Rubik Button "Solve"
		ImageIcon rubikIcon = new ImageIcon("images/rubik.png");
		Image image3 = rubikIcon.getImage().getScaledInstance(15, 15, Image.SCALE_DEFAULT);
		rubikIcon = new ImageIcon(image3);
		JButton rubikButton = new JButton(rubikIcon);
		rubikButton.addActionListener(new ButtonListener());
		rubikButton.setBackground(Color.WHITE);
		rubikButton.setActionCommand("solve");
		rubikButton.setEnabled(false);
		numberPanel.add(rubikButton);
	}


	///Functions///
	
	private void disableButtonPanel(){

		for (Component cp : numberPanel.getComponents() ){
			cp.setEnabled(false);
		}
	}

	private void enableButtonPanel(){
		for (Component cp : numberPanel.getComponents() ){
			cp.setEnabled(true);
		}
	}

	private void enableGrid(){

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				square[i][j].setEnabled(true);
			}
		}
	}

	//find which button has been pressed
	private int[] pressedButton(ActionEvent Button){

		String pressedButton = Button.getActionCommand();

		//turn string into table of integers
		int[] buttoncoor = new int[pressedButton.length()];

		for (int i = 0; i < pressedButton.length(); i++) {

			buttoncoor[i] = Character.getNumericValue(pressedButton.charAt(i));
		}
		//returns the coordinates
		return buttoncoor;
	}

	//repaint the grid
	private void repaintGrid(){

		int[][] ini = sPuzzle.initialBoard;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {

				if (ini[i][j] != 0){
					square[i][j].setBackground(Color.lightGray);
				}
				else {
					square[i][j].setBackground(Color.WHITE); 
				}
			}
		}
	}

	//paints yellow the grid boxes that are equal with the selected one
	private void selectButtons(ActionEvent gridButton, int[] buttoncoor){

		//take the coordinates
		int row = buttoncoor[0];
		int col = buttoncoor[1];

		//now that you know which button has been pressed find other buttons with the same value
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {

				//if the pressed button is empty, keep white the same ones
				if (square[i][j].getText().equals(square[row][col].getText()) && square[i][j].getText().equals("")){

					square[i][j].setBackground(Color.white);
				}
				//if the pressed button has a number, change to yellow the same ones
				else if (square[i][j].getText().equals(square[row][col].getText())){

					square[i][j].setBackground(new Color(255, 255, 200));
				}
			}
		}
		//if the pressed is not blue, then change it to yellow
		if (!(square[row][col].getBackground() == new Color(66, 134, 244))){ //(66, 134, 244)
			square[row][col].setBackground(new Color(255, 255, 200));
		}
	}

	//deletes a value of the grid
	private void deleteValue(ActionEvent pressedNum){

		//don't do anything if it hasn't been selected a grid box
		if (flag == 0){

			//coordinates of the pressed button
			int gridx = gridButtonCoor[0];
			int gridy = gridButtonCoor[1];

			//don't delete the value if it's an initial one
			if (!(sPuzzle.board[gridx][gridy] == sPuzzle.initialBoard[gridx][gridy])){

				square[gridx][gridy].setText("");

				//update accordingly the value of the board
				sPuzzle.board[gridx][gridy] = 0;

				//repaint the grid if you deleted a value
				repaintGrid();
			}
			//if selected is not blue, then change it to yellow
			if (!(square[gridx][gridy].getBackground() == new Color(66, 134, 244))){
				square[gridx][gridy].setBackground(new Color(255, 255, 200));
			}
		}
	}

	//changes a value of the grid
	private void changeValue(ActionEvent pressedNum){

		//redflag indicates if a collision has been made
		int redflag = -1;

		//don't do anything if it hasn't selected a grid box
		if (flag == 0){

			//coordinates of the pressed button
			int gridx = gridButtonCoor[0];
			int gridy = gridButtonCoor[1];

			//find the number of the panel that has been pressed
			numButtonCoor = pressedButton(pressedNum);
			int numPanel = numButtonCoor[0];

			//change the value if grid box is empty
			if (square[gridx][gridy].getText().equals("")){

				//check row and column for collision
				for (int i = 0; i < 9; i++){
					if (sPuzzle.board[i][gridy] == numPanel){
						square[i][gridy].setBackground(Color.RED);
						redflag = 0; //collision is made
					}
					if (sPuzzle.board[gridx][i] == numPanel){
						square[gridx][i].setBackground(Color.RED);
						redflag = 0; //collision is made
					}
				}
				//check box (3x3) for collision
				for (int i = (gridx / 3) * 3; i < (gridx / 3) * 3 + 3; i++) {
					for (int j = (gridy / 3) * 3; j < (gridy / 3) * 3 + 3; j++) {
						if (sPuzzle.board[i][j] == numPanel){
							square[i][j].setBackground(Color.RED);
							redflag = 0; //collision is made
						}
					}
				}
				// add the number pressed to the grid if there is no collision
				if (!(redflag == 0)){

					//if you try to add a number to the grid,
					//push the whole table to the stack before
					int k=0;
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 9; j++) {
							retrievedBoard[k] = sPuzzle.board[i][j];
							k++;
						}
					}
					//push to stack
					for (int i = 0; i < 81; i++){
						theStack.push(retrievedBoard[i]);
					}
					//change the value of the grid box
					square[gridx][gridy].setText(""+numPanel);

					//update accordingly the value of the board
					sPuzzle.board[gridx][gridy] = numPanel;
				}
			}
			//make yellow the grid boxes after you change or add a number, not only after selection
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {

					//if the pressed button is empty, keep white the same ones
					if (square[i][j].getText().equals(square[gridx][gridy].getText()) 
							&& square[i][j].getText().equals("")){

						square[i][j].setBackground(Color.white);
					}
					//if the pressed button has a number, change to yellow the same ones
					else if (square[i][j].getText().equals(square[gridx][gridy].getText())){

						square[i][j].setBackground(new Color(255, 255, 200));
					}
				}
			}
			//if the pressed is not blue, then change it to yellow
			if (!(square[gridx][gridy].getBackground() == new Color(66, 134, 244))){
				square[gridx][gridy].setBackground(new Color(255, 255, 200));
			}
		}
	}

	//paints blue the wrong boxes
	private void paintBlue(int check){

		//if the check box has been selected
		if (check == 1){
			for (int i = 0; i < 9; i++){
				for (int j = 0; j < 9; j++){
					if (!(sPuzzle.board[i][j] == sPuzzle.BOARD[i][j]) 
							&& !(square[i][j].getText().equals(""))){
						square[i][j].setBackground(new Color(66, 134, 244));
					}
				}
			}	
		}
	}

	//checks if the Sudoku is done
	private void isFull(){

		//flag for knowing that the board is right
		int notEqual = -1;

		for (int j = 0; j < 9; j++){
			for (int k = 0; k < 9; k++){
				if (!(sPuzzle.board[j][k] == sPuzzle.BOARD[j][k])){ 
					notEqual = 0;
				}
			}
		}
		//if it is right
		if (notEqual == -1){

			disableButtonPanel();

			check = 0;

			JOptionPane.showMessageDialog(JFramePanel, "SUCCESS !!!");
		}
	}

	//Stack
	private class Stack{

		private int maxSize; //size of the stack
		private int[] stackArray;
		private int top;

		private Stack(int s){
			maxSize = s;
			stackArray = new int[maxSize];
			top = -1;
		}

		private void push(int j){
			stackArray[++top] = j;
		}

		private int pop(){
			return stackArray[top--];
		}
	}


	///Listeners///

	//Menu Listener
	private class GameModeListener implements ActionListener {

		//reaction to the chosen Game level  
		public void actionPerformed(ActionEvent e) {
			
			int mode=0;
			
			enableGrid();

			enableButtonPanel();

			checkButton.setSelected(false); //initial state of the check box

			if (e.getActionCommand() == "Easy"){
				
				mode=1;
				sGen = new Sudoku_Generator(mode);
				String gameUrl = sGen.Gen_url;
				sPuzzle = new New_SudokuPuzzle(gameUrl, square);
			}
			else if (e.getActionCommand() == "Intermediate"){
				
				mode=2;
				sGen = new Sudoku_Generator(mode);
				String gameUrl = sGen.Gen_url;
				sPuzzle = new New_SudokuPuzzle(gameUrl, square);
			}
			else if (e.getActionCommand() == "Expert") {
				
				mode=3;
				sGen = new Sudoku_Generator(mode);
				String gameUrl = sGen.Gen_url;
				sPuzzle = new New_SudokuPuzzle(gameUrl, square);
			}
		}
	}

	//Grid Buttons Listener
	private class GridListener implements ActionListener {

		public void actionPerformed(ActionEvent pressedGrid) {

			repaintGrid(); //paints appropriately the grid

			paintBlue(check); //if check box is selected

			gridButtonCoor = pressedButton(pressedGrid); 

			selectButtons(pressedGrid, gridButtonCoor); //finds the pressed button

			flag = 0; //grid box has been selected
		}
	}

	//Button Panel Listener
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent pressedNum) {

			if ("eraser".equals(pressedNum.getActionCommand())) {

				deleteValue(pressedNum);

				paintBlue(check);
			} 

			else if ("undo".equals(pressedNum.getActionCommand())) {

				//if stack is empty stop don't undo
				if (!(theStack.top == -1)){

					//full retrievedBoard with integers from the stack
					for (int i = 80; i > -1; i--){
						retrievedBoard[i] = theStack.pop();
					}
					//store the retrievedBoard to the board
					int k=0;
					for (int i = 0; i < 9; i++){
						for (int j = 0; j < 9; j++){
							sPuzzle.board[i][j] = retrievedBoard[k];
							k++;
						}
					}

					sPuzzle.setGrid(square); 

					repaintGrid();

					paintBlue(check);
				}
			}

			else if ("solve".equals(pressedNum.getActionCommand())) {

				sPuzzle.setSolution();

				repaintGrid();

				disableButtonPanel();

				check = 0; //prevent painting blue the grid
			}
			else { //pressed a number

				repaintGrid();

				paintBlue(check);

				changeValue(pressedNum);

				paintBlue(check);

				isFull();
			}
		}
	}

	//CheckBox Listener
	@Override
	public void itemStateChanged(ItemEvent pressedCheckBox) {

		//if check box is selected
		if (pressedCheckBox.getStateChange() == 1){

			check = 1;
		}
		//if check box is deselected
		else {

			check = 0;
		}

		repaintGrid();

		paintBlue(check);
	}


	///Main///
	public static void main(String[] args){

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				New_SudokuFrame frame = new New_SudokuFrame();
				frame.setVisible(true);	
				frame.pack();
			}
		});
	}
}