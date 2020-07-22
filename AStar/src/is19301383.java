/*****************************************************************************
Program made by Damon Holland / ID: 19301383
CS4006 University of Limerick
April 20, 2020
//Apologies if syntax is a bit rough, this is my first program made in java.
*****************************************************************************/

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


//A Cell Class which holds information about its location and status
class Cell
{
    public int x, y;
    public boolean bIsBlocked, bIsStart, bIsGoal;
    
    //Constructor
    public Cell(int x, int y)
    {
    	//Initialize variables, boolean flags are set false on creation.
        this.x = x;
        this.y = y;
        this.bIsBlocked = false;
        this.bIsStart = false;
        this.bIsGoal = false;
    }
}

//A Grid class in which the program operates upon
class Grid
{
    public int numCols, numRows, cellSize;
    public boolean bHasLocations;
    public Cell[][] cellArray;
    public Cell startCell, goalCell;
    
    //Constructor
    public Grid(int numCols, int numRows, int cellSize)
    {
    	//Initialize variables, set boolean flag to false on creation
        this.numCols = numCols;
        this.numRows = numRows;
        this.cellSize = cellSize;
        this.bHasLocations = false;
        
        //Create and fill a 2D array of cells with the specified size
        cellArray = new Cell[numCols][numRows];
        
        for(int col = 0; col < numCols; col++)
        {
        	for(int row = 0; row < numRows; row++)
        	{
            	cellArray[col][row] = new Cell(col, row);
        	}
        }
    }
    
    //Places a random obstacle on the grid
    public void createRandomObstacle()
    {
		Random rand = new Random();
		int letterWidth, letterHeight, obstacleX, obstacleY;
		
    	//First, decide which letter the obstacle will be shaped (I, T, L, U, C)
		int obstacleType = rand.nextInt(5);
		
		//Create the obstacle based on the random letter chosen
		switch(obstacleType) 
		{
		case 0:
			//Create an I ***********************************
			letterWidth = 1;
			letterHeight = 3 + rand.nextInt(3);
			obstacleX = rand.nextInt(9 - letterWidth);
			obstacleY = rand.nextInt(9 - letterHeight);
			for(int col = obstacleX; col < obstacleX + letterWidth; col++)
			{
				for(int row = obstacleY; row < obstacleY + letterHeight; row++) 
				{
					cellArray[col][row].bIsBlocked = true;
				}
			}
			//***********************************************
			break;
		case 1:
			//Create a T ***********************************
			letterWidth = 3;
			letterHeight = 2 + rand.nextInt(2);
			obstacleX = rand.nextInt(9 - letterWidth);
			obstacleY = rand.nextInt(9 - letterHeight);
			for(int col = obstacleX; col < obstacleX + letterWidth; col++)
			{
				cellArray[col][obstacleY].bIsBlocked = true;
			}
			for(int row = obstacleY; row < obstacleY + letterHeight; row++)
			{
				cellArray[obstacleX + 1][row].bIsBlocked = true;
			}
			//***********************************************
			break;
		case 2:
			//Create an L ***********************************
			letterWidth = 2 + rand.nextInt(2);
			letterHeight = 2 + rand.nextInt(5 - letterWidth);
			if(letterWidth > letterHeight)
			{
				letterWidth = letterHeight;
			}
			obstacleX = rand.nextInt(9 - letterWidth);
			obstacleY = rand.nextInt(9 - letterHeight);
			for(int col = obstacleX; col < obstacleX + letterWidth; col++)
			{
				cellArray[col][obstacleY + letterHeight - 1].bIsBlocked = true;
			}
			for(int row = obstacleY; row < obstacleY + letterHeight; row++)
			{
				cellArray[obstacleX][row].bIsBlocked = true;
			}
			//***********************************************
			break;
		case 3:
			//Create a U ***********************************
			letterWidth = 3;
			letterHeight = 2;
			obstacleX = rand.nextInt(9 - letterWidth);
			obstacleY = rand.nextInt(9 - letterHeight);
			for(int col = obstacleX; col < obstacleX + letterWidth; col++)
			{
				for(int row = obstacleY; row < obstacleY + letterHeight; row++) 
				{
					cellArray[col][row].bIsBlocked = true;
				}
			}
			cellArray[obstacleX + 1][obstacleY].bIsBlocked = false;
			//***********************************************
			break;
		case 4:
			//Create a C ***********************************
			letterWidth = 2;
			letterHeight = 3;
			obstacleX = rand.nextInt(9 - letterWidth);
			obstacleY = rand.nextInt(9 - letterHeight);
			for(int col = obstacleX; col < obstacleX + letterWidth; col++)
			{
				for(int row = obstacleY; row < obstacleY + letterHeight; row++) 
				{
					cellArray[col][row].bIsBlocked = true;
				}
			}
			cellArray[obstacleX + 1][obstacleY + 1].bIsBlocked = false;
			//***********************************************
			break;
		}
    	return;
    }
    
    //Clears the grid of obstacles and locations
    public void clear()
    {
    	for(int col = 0; col < numCols; col++)
        {
        	for(int row = 0; row < numRows; row++)
        	{
            	cellArray[col][row].bIsBlocked = false;
        	}
        }
    	clearLocations();
    	return;
    }
    
    //Clears the grid of locations
    public void clearLocations()
    {
    	for(int col = 0; col < numCols; col++)
        {
        	for(int row = 0; row < numRows; row++)
        	{
            	cellArray[col][row].bIsStart = false;
            	cellArray[col][row].bIsGoal = false;
        	}
        }
    	bHasLocations = false;
    }
    
    //Sets the locations (Start and Goal) to user specified Cells
    public void setLocations()
    {
    	String input;
    	boolean bIsValidInput = false;
    	int startCol, startRow, goalCol, goalRow;
    	startCol = startRow = goalCol = goalRow = 0;
    	
    	//Clear the old locations
    	clearLocations();
    	
    	//Ask the user for input until valid
    	while(!bIsValidInput)
    	{
    		input = JOptionPane.showInputDialog("Enter the start position coordinate(ex: A1): ");
    		if(input!= null && input.length() == 2)
    		{
    			startCol = Character.getNumericValue(Character.toLowerCase(input.charAt(0))) - 10;
    			startRow = Character.getNumericValue(input.charAt(1)) - 1;
    		
    			if (startCol >= 0 && startCol < 8 && startRow >=0 && startRow < 8 &&
    				!cellArray[startCol][startRow].bIsBlocked)
    			{
    				bIsValidInput = true;
    				cellArray[startCol][startRow].bIsStart = true;
    			}
    		}
    		else
    		{
    			//If the user cancels, clear the locations and return
    			clearLocations();
    			return;
    		}
    	}
    	
    	//Ask the user for input until valid
    	bIsValidInput = false;
    	while(!bIsValidInput)
    	{
    		input = JOptionPane.showInputDialog("Enter the goal position coordinate(ex: B2): ");
    		if(input!= null && input.length() == 2)
    		{
    			goalCol = Character.getNumericValue(Character.toLowerCase(input.charAt(0))) - 10;
    			goalRow = Character.getNumericValue(input.charAt(1)) - 1;
    		
    			if (goalCol >= 0 && goalCol < 8 && goalRow >=0 && goalRow < 8 &&
    				!cellArray[goalCol][goalRow].bIsBlocked && !cellArray[goalCol][goalRow].bIsStart)
    			{
    				bIsValidInput = true;
    				cellArray[goalCol][goalRow].bIsGoal = true;
    			}
    		}
    		else
    		{
    			//If the user cancels, clear the locations and return
    			clearLocations();
    			return;
    		}
    	}
    	
    	//Flag the cells as locations user given coordinates
    	startCell = cellArray[startCol][startRow];
    	goalCell = cellArray[goalCol][goalRow];
    	
    	bHasLocations = true;
    	return;
    }
}

//A State class to represent a node in the path
class State
{
	//Previous state used to backtrack when the solution is found
	State previousState;
	public Cell currentCell;
	public Cell goalCell;
	public int cost, score;

	//The Constructor
	public State(State previousState, Cell currentCell, Cell goalCell, int cost)
	{
		this.currentCell = currentCell;
		this.goalCell = goalCell;
		this.cost = cost;
		this.previousState = previousState;
		
		//Calculate the score for the state based on Manhattan distance to the goal cell and the cost to reach the state
		if(this.isSolved())
		{
			//If this state has reached the goal, give very good score to prioritize this state over any other
			this.score = -1000;
		}
		else
		{
			this.score = Math.abs(currentCell.x - goalCell.x) +
						 Math.abs(currentCell.y - goalCell.y) + cost;	
		}
		
	}

	//Returns true if this state has reached the goal cell
	public boolean isSolved()
	{
		boolean bIsSolved = false;
		
		if (currentCell.x == goalCell.x && currentCell.y == goalCell.y)
		{
			bIsSolved = true;
		}
		
		return bIsSolved;
	}
	
}

//The main Class to manage the program
public class is19301383 extends Canvas
{
	private static final long serialVersionUID = 1L;
	Vector<State> solvedPath = null;
	
	//main function handles graphics, creating the grid, buttons, and rendering all parts of the program
	public static void main(String[] args)
	{
		final int GRID_COLS = 8, GRID_ROWS = 8, CELL_SIZE = 50;
		is19301383 program = new is19301383();
		JFrame frame = new JFrame("A-Star");
        Grid grid = new Grid(GRID_COLS, GRID_ROWS, CELL_SIZE);
        JButton resetButton = new JButton("Reset");
        JButton setButton = new JButton("Set Target");
        JButton solveButton = new JButton("Solve");

        //Set position and size of buttons
          resetButton.setBounds(50,460,100,50);
          setButton.setBounds(200,460,100,50);
          solveButton.setBounds(350,460,100,50);
        
        //Initialize the frame and add needed components
        frame.add(resetButton);
        frame.add(setButton);
        frame.add(solveButton);
        frame.add(program);
        frame.pack();
        frame.setSize((GRID_COLS + 2) * CELL_SIZE, (GRID_ROWS + 3) * CELL_SIZE);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        grid.createRandomObstacle();
        
        //Add the functionality for the buttons
        resetButton.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent event)
        	{
        		program.solvedPath = null;
        		grid.clear();
        		grid.createRandomObstacle();
        	 }  
        });  
        
        setButton.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent event)
        	{
        		program.solvedPath = null;
        		grid.setLocations();
        	}
        });
        
        solveButton.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent event)
        	{
        		if(!grid.bHasLocations)
        		{
        			JOptionPane.showMessageDialog(frame, "You need to set a start and goal first.");
        		}
        		else
        		{
        			program.solvedPath = program.solve(grid, GRID_COLS, GRID_ROWS);
        			if(null == program.solvedPath)
        			{
        				JOptionPane.showMessageDialog(frame, "This configuration is not solvable.");
        			}
        			else
        			{
        				JOptionPane.showMessageDialog(frame, "Solved in " + (program.solvedPath.size() - 1) + " Moves.");
        			}
        		}
        	}
        }); 
        
        //Infinite loop to keep rendering the program until the window is closed
        while(true)
        {
            program.render(grid, program.solvedPath);	
        }
        
	}

	
	//Render function, draws components on the screen
	private void render(Grid currentGrid, Vector<State> solvedPath)
	{
		char colLabel = 'A';
		int rowLabel = 1;
		
		//Create buffer strategy on first render attempt
		BufferStrategy bs = this.getBufferStrategy();
		if(null == bs)
		{
			this.createBufferStrategy(2);
			return;
		}
		
		//Initialize graphics
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, (currentGrid.numCols + 2) * currentGrid.cellSize,
				  (currentGrid.numRows + 3) * currentGrid.cellSize);
		g.setColor(Color.black);
		
		//DRAW***************************************************************************************************
		//Draw Labels
		for(int col = 1; col <= currentGrid.numCols; col++)
		{
			g.drawString(String.valueOf(colLabel++), col*currentGrid.cellSize + currentGrid.cellSize / 2, currentGrid.cellSize / 2);
		}
		for(int row = 1; row <= currentGrid.numRows; row++)
		{
			g.drawString(String.valueOf(rowLabel++), currentGrid.cellSize / 2, row*currentGrid.cellSize + currentGrid.cellSize / 2);
		}
		
		//Draw Cells
		for (int col = 0; col < currentGrid.numCols; col++)
		{
			for(int row = 0; row < currentGrid.numRows; row++)
			{
				Cell currentCell = currentGrid.cellArray[col][row];
				//Draw Cell
				if(currentCell.bIsBlocked)
				{
					g.fillRect((1 + currentCell.x) * currentGrid.cellSize, (1 + currentCell.y) * currentGrid.cellSize,
							   currentGrid.cellSize, currentGrid.cellSize);
				}
				else
				{
					g.drawRect((1 + currentCell.x) * currentGrid.cellSize, (1 + currentCell.y) * currentGrid.cellSize,
							   currentGrid.cellSize, currentGrid.cellSize);
					if(currentCell.bIsStart)
					{
						g.drawString("Start", (1 + currentCell.x) * currentGrid.cellSize + currentGrid.cellSize / 4,
											  (1 + currentCell.y) * currentGrid.cellSize + currentGrid.cellSize / 2);
					}
					else if(currentCell.bIsGoal)
					{
						g.drawString("Goal", (1 + currentCell.x) * currentGrid.cellSize + currentGrid.cellSize / 4,
											  (1 + currentCell.y) * currentGrid.cellSize + currentGrid.cellSize / 2);
					}
				}
			}
		}
		
		//Draw solved Path, if it exists
		g.setColor(Color.blue);
		if (null != solvedPath)
		{
			for(int i = 0; i < solvedPath.size() - 1; i++)
			{
				Cell currentCell = solvedPath.elementAt(i).currentCell;
				Cell nextCell = solvedPath.elementAt(i + 1).currentCell;
				g.drawLine(((1 + currentCell.x) * currentGrid.cellSize) + currentGrid.cellSize / 2,
						   ((1 + currentCell.y) * currentGrid.cellSize) + currentGrid.cellSize / 2,
						   ((1 + nextCell.x) * currentGrid.cellSize) + currentGrid.cellSize / 2,
						   ((1 + nextCell.y) * currentGrid.cellSize) + currentGrid.cellSize / 2);
			}
		}
		
		//********************************************************************************************************
		
		g.dispose();
		bs.show();
	}
	
	//solves the given configuration, if possible. Returns the pathway used so it can be rendered on the screen, or null if unsolvable
	private Vector<State> solve(Grid currentGrid, final int GRID_COLS,final int GRID_ROWS)
	{	
		int totalMoves;
		Vector<State> openStates = new Vector<State>();
		Vector<State> closedStates = new Vector<State>();
		Vector<State> finalPath = new Vector<State>();
		
		//Set current state to the Start
		State currentState = new State(null, currentGrid.startCell, currentGrid.goalCell, 0);
		openStates.add(currentState);
		
		while(!currentState.isSolved())
		{
			Vector<State> possibleMoves = new Vector<State>();
			State newState;
			boolean bAlreadyAdded;
			
			//Add all possible moves to a vector
			if(currentState.currentCell.x + 1 < GRID_COLS)
			{
				newState = new State(currentState, currentGrid.cellArray[currentState.currentCell.x + 1][currentState.currentCell.y],
									 currentGrid.goalCell, currentState.cost + 1);
				if(!newState.currentCell.bIsBlocked)
				{
					possibleMoves.add(newState);
				}
			}
			
			if(currentState.currentCell.x - 1 >= 0)
			{
				newState = new State(currentState, currentGrid.cellArray[currentState.currentCell.x - 1][currentState.currentCell.y],
									 currentGrid.goalCell, currentState.cost + 1);
				if(!newState.currentCell.bIsBlocked)
				{
					possibleMoves.add(newState);
				}
			}
			
			if(currentState.currentCell.y + 1 < GRID_COLS)
			{
				newState = new State(currentState, currentGrid.cellArray[currentState.currentCell.x][currentState.currentCell.y + 1],
									 currentGrid.goalCell, currentState.cost + 1);
				if(!newState.currentCell.bIsBlocked)
				{
					possibleMoves.add(newState);
				}
			}
			
			if(currentState.currentCell.y - 1 >= 0)
			{
				newState = new State(currentState, currentGrid.cellArray[currentState.currentCell.x][currentState.currentCell.y - 1],
									 currentGrid.goalCell, currentState.cost + 1);
				if(!newState.currentCell.bIsBlocked)
				{
					possibleMoves.add(newState);
				}
			}
			
			
			//Check if each possible move is not already closed or open. If not, then open it
			for(int i = 0; i < possibleMoves.size(); i++)
			{
				bAlreadyAdded = false;
				for(int j = 0; j < closedStates.size() && !bAlreadyAdded; j++)
				{
					if(possibleMoves.elementAt(i).currentCell.x == closedStates.elementAt(j).currentCell.x &&
					   possibleMoves.elementAt(i).currentCell.y == closedStates.elementAt(j).currentCell.y)
					{
						bAlreadyAdded = true;
					}	
				}
				
				for(int j = 0; j < openStates.size() && !bAlreadyAdded; j++)
				{
					if(possibleMoves.elementAt(i).currentCell.x == openStates.elementAt(j).currentCell.x &&
					   possibleMoves.elementAt(i).currentCell.y == openStates.elementAt(j).currentCell.y)
					{
						bAlreadyAdded = true;
					}
					
				}
				
				if(!bAlreadyAdded)
				{
					openStates.add(possibleMoves.elementAt(i));
				}
				
			}
			
			//Close the old state
			closedStates.add(currentState);
			openStates.remove(0);
			
			
			//Reorder the open states based on their score
			Collections.sort(openStates, new Comparator<State>() {
				@Override
		        public int compare(State state1, State state2)
		        {
		                return state1.score - state2.score;
		        }
			});
			
			
			//Move to best current state
			if (openStates.size() != 0)
			{
				currentState = openStates.elementAt(0);	
			}
			else
			{
				//If there are no more states, the configuration is unsolvable, so return
				return null;
			}
		}
		
		//Trace back from the solved state to the beginning to create a vector with the solution path.
		totalMoves = currentState.cost;
		for(int i = 0; i <= totalMoves; i++)
		{
			finalPath.add(0, currentState);
			currentState = currentState.previousState;
		}

		return finalPath;
	}

}
