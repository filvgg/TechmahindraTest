package com.techmahindra.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

//  



class Cell {
	public int row;
	public int col;

	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
	}
	public Cell getLeft() {
		return new Cell(row, col - 1);
	}
	public Cell getRight() {
		return new Cell(row, col + 1);
	}
	public Cell getBottom() {
		return new Cell(row + 1, col);
	}
	public Cell getUp() {
		return new Cell(row - 1, col);
	}
	public String toString() {
		return "<" + row + "," + col + ">";
	}
}



public class ShortPath {
	
	private static int xS, yS;       // (x,y) for the Start point
	private static int xE, yE;       // (x,y) for the End point
	private static List<Cell> path;
	private static char matrixC[][] = null;  //  this is the chat matrix and initialized  from  file matr.txt
	private static int[][] grid = null;      //  this is itn matrix for  calculations

	/**
	   * This method Prints  char[][] on the console
	   * 
	   * @param matr
	 */
	private static void printMmatrix(char[][] matr) {
		for (int a_i = 0; a_i < matr.length; a_i++) {
			for (int a_j = 0; a_j < matr[a_i].length; a_j++) {
				System.out.print(matr[a_i][a_j] + " ");
			}
			System.out.println();
		}
	}
	/**
	   * This method Prints  int [][] matrixC on the console
	   * 
	   * @param matr
	 */
	private static void printMmatrix(int [][] matr ){
		for(int a_i=0; a_i < matr.length; a_i++){
    		for(int a_j=0; a_j < matr[a_i].length; a_j++){
    			//System.out.print(   ( "("+a_i + "," + a_j + ")=" + matr[a_i][a_j] + "          ").substring(0,10)   )   ;
    			System.out.print(   ( matr[a_i][a_j] + "          ").substring(0,6)   )   ;
    		}
    		System.out.println();
    	}
	}

	
	/**
	   * This method Detects the dimensions of the matrix and fills it
	   * 
	   * @param fName
	 */
	private static char[][] readFile(String fName) {

		BufferedReader in = null;
		int n = 0;
		int m = 0;
		try {
			in = new BufferedReader(new FileReader(fName));
			String read = null;
			read = in.readLine();
			String[] splited = read.split("\\s+");
			m = splited.length;
			while ((read = in.readLine()) != null) {
				n++;
			}
		} catch (IOException e) {
			System.out.println("There was a problem: " + e);
			e.printStackTrace();
			
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		char matr[][] = new char[n + 1][m];
		

		n = 0;
		m = 0;

		try {
			in = new BufferedReader(new FileReader(fName));
			String read = null;
			while ((read = in.readLine()) != null) {
				String[] splited = read.split("\\s+");
				m = 0;
				for (String part : splited) {
					
					matr[n][m] = part.charAt(0);
					//Only symbols '.' , 'W' , 'S' and 'E' are allowed separated by 'space'
					if( matr[n][m] == '.' || matr[n][m] == 'W' || matr[n][m] == 'S' || matr[n][m] == 'E' ) {
						m++;
					}else {
						System.err.println("The symbol '" + matr[n][m] +  "' is not allowed! ");
						System.out.println(" ");
						System.err.println("Please correct your matrix! ");
						System.exit(0);
					}
					
					
				}
				n++;
			}
		} catch (IOException e) {
			System.err.println("There was a problem: " + e);
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}

		return matr;
	}


	/**
	   * This method Creates a list of  the neighbors of  cell p  in  the  working area 
	   * 
	   * @params Cell p, int row, int col
	 */
	private static List<Cell> getNeighbours(Cell p, int row, int col) {
		List<Cell> neighours = new ArrayList<Cell>();

		Cell posRight = p.getRight();
		if (posRight.row >= 0 && posRight.row < row && posRight.col >= 0 && posRight.col < col)
			neighours.add(posRight);
		
		Cell posLeft = p.getLeft();
		if (posLeft.row >= 0 && posLeft.row < row && posLeft.col >= 0 && posLeft.col < col)
			neighours.add(posLeft);

		Cell posUp = p.getUp();
		if (posUp.row >= 0 && posUp.row < row && posUp.col >= 0 && posUp.col < col)
			neighours.add(posUp);

		Cell posDown = p.getBottom();
		if (posDown.row >= 0 && posDown.row < row && posDown.col >= 0 && posDown.col < col)
			neighours.add(posDown);
		return neighours;
	}


	/**
	   * Method to check if it is possible to go to position (row, col)
	   * from current position. The method returns false if the cell
	   * not a valid position or has value 0 or it is already visited i.e  value is >0
	   * @params int d, int row, int col
	 */
	private static boolean isValid(int d, int row, int col) {
		return (row >= 0) && (row < grid.length) && (col >= 0) && (col < grid[0].length) && grid[row][col] == d - 1;
	}
	
	/**
	   * Finds Shortest Possible Route in a matrix matrixC from source
	   * cell S(xS, yS) to destination cell E(xE, yE)
	   * 
	   * @params char[][] arr, int row, int col
	 */
	public static int getLengthOfPath(char[][] arr, int row, int col) {

		int dist = 0;
		grid = new int[row][col];

		for (int a_i = 0; a_i < arr.length; a_i++) {
			for (int a_j = 0; a_j < arr[a_i].length; a_j++) {
				if (arr[a_i][a_j] == 'W')
					grid[a_i][a_j] = -1;
			}
			// System.out.println();
		}

		//for PriorityQueue  we must implement Comparator
		PriorityQueue<Cell> queue = new PriorityQueue<Cell>(col * row, new Comparator<Cell>() {

			@Override
			public int compare(Cell o1, Cell o2) {
				if (grid[o1.row][o1.col] < grid[o2.row][o2.col])
					return -1;
				else if (grid[o1.row][o1.col] > grid[o2.row][o2.col])
					return 1;
				else
					return 0;
			}
		});

		queue.offer(new Cell(xS, yS));
		grid[xS][yS] = 1;
		
		// run till queue is not empty	
		while (!queue.isEmpty()) {

			Cell curC = queue.poll();
			List<Cell> ns = getNeighbours(curC, row, col);

			for (Cell n : ns) {

				if (!(arr[n.row][n.col] == 'W') && grid[n.row][n.col] == 0 ) {

					grid[n.row][n.col] = grid[curC.row][curC.col] + 1;
					queue.offer(n);
				}
				//for debugging
                //System.out.println("________________________________"); 
                //printMmatrix(arr);
				//printMmatrix(grid);
				//System.out.println("________________________________");
				if (arr[n.row][n.col] == 'E') {
					dist = grid[curC.row][curC.col] + 1;
					path = backToS(dist, xE, yE);
					//
					return dist;
				}

			}

		}
		return 0;
	}

	public static List backToS(int d, int row, int col) {
		//printMmatrix(grid );
		int cx = row;
		int cy = col;
		List<Cell> path = new ArrayList<Cell>();
		int c = d - 1;
		// printMmatrix(grid );

		while (c != 1) {
			Cell curr = new Cell(cx, cy);
			List<Cell> ns = getNeighbours(curr, matrixC.length, matrixC[0].length);
			for (Cell n : ns) {
				boolean b = isValid(grid[cx][cy], n.row, n.col);
				if (b) {
					cx = n.row;
					cy = n.col;
					path.add(new Cell(cx, cy));
					break;
				}
			}
			c--;
		}

		return path;
	}
	
    //We are filling in the visited 
	private static void fillInTheVisited() {
		for (int a_i = 0; a_i < grid.length; a_i++) {
			for (int a_j = 0; a_j < grid[a_i].length; a_j++) {
				if ((grid[a_i][a_j] > 0) & (matrixC[a_i][a_j] != 'W') & (matrixC[a_i][a_j] != 'E') & (matrixC[a_i][a_j] !='S'))      {
					matrixC[a_i][a_j]='"';
				} 
				//System.out.print(matr[a_i][a_j] + " ");
			}
			//System.out.println();
		}
	}

	
	//We mark the way with '*'
	private static void makePath() {
		for (Cell n : path) {
			matrixC[n.row][n.col] = '*';
		}
	}

	/**
	   * This method finds the coordinates of S and E
	   * if S or/and E do not  exist message "Please correct your input matrix! "
	   * @param matr
	 */
	private static void coordSandE(char[][] matr) {
		boolean foundS = false;
		boolean foundE = false;
		for (int a_i = 0; a_i < matr.length; a_i++) {
			for (int a_j = 0; a_j < matr[a_i].length; a_j++) {
				// System.out.print(matr[a_i][a_j] +" ");
				if (matr[a_i][a_j] == 'S') {
					xS = a_i;
					yS = a_j;
					foundS=true;
				}
				if (matr[a_i][a_j] == 'E') {
					xE = a_i;
					yE = a_j;
					foundE=true;
				}
			}
		}
		
		if (!(foundS&&foundE)) {
			System.err.println("No Starting or/and Ending points! ");
			System.err.println("Please correct your input matrix! ");
			System.exit(0);
		}
		
		
	}

	public static void main(String[] args) {
		
		//System.out.println(Paths.get("").toAbsolutePath().toString());
		
		matrixC = readFile(Paths.get("").toAbsolutePath().toString()+"\\matr.txt");
		
		//Test cases during development
		//matrixC = readFile("matr00.txt");
		//matrixC = readFile("matr01.txt");
		//matrixC = readFile("matr02.txt");
		//matrixC = readFile("matr03.txt");
		//matrixC = readFile("matr04.txt");
		//matrixC = readFile("matr05.txt");
		//matrixC = readFile("matr06.txt");
		//matrixC = readFile("matr07.txt");
		//matrixC = readFile("matr08.txt");
		//matrixC = readFile("matr09.txt");
		//matrixC = readFile("matr10.txt");
		//matrixC = readFile("matr11.txt");
		//matrixC = readFile("matr12.txt");
		//matrixC = readFile("matr13.txt");
		//matrixC = readFile("matr14.txt");
		//matrixC = readFile("matr15.txt");
		System.out.println("--------------------------------------");
		printMmatrix(matrixC);
		coordSandE(matrixC);
		
		int  LengthOfPath = getLengthOfPath(matrixC, matrixC.length, matrixC[0].length);
		if (LengthOfPath <= 0) {
			System.out.println("--------------------------------------");
			System.out.println("The length of the path in '*' is: " + (LengthOfPath-2));
			System.out.println(" ");
			System.err.println("No path from S to E ");
		} else {
			System.out.println("--------------------------------------");
			System.out.println("The length of the path in '*' is: " + (LengthOfPath-2) + "   (without  S and E)   "  );
			System.out.println("The numbers of moves from S to E are : " + (LengthOfPath-1)  );
			System.out.println("");
			System.out.print("The path is : '" );
			for (int i=0; i < LengthOfPath-2; i++) {
				System.out.print("*");
			}
			System.out.println("'");
			System.out.println("--------------------------------------");
			
			// printMmatrix(grid);
			
			fillInTheVisited();
			makePath();
			printMmatrix(matrixC);
			System.out.println("--------------------------------------");
		}
	}
}
