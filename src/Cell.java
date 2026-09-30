// blueprint for cell object

// imports
import java.util.*;
import java.io.*;

// cell class
public class Cell {
	
	// fields
	private int cellNum;
	private boolean diamond;
	private boolean checked;
	private static int numDiamonds = 0;
	private static int numMines = 0;
	public static int checkedCells = 0;
	private int cellCol;
	private int cellRow;
	private boolean egg;
	
	// constructor (mines)
	public Cell(int cellNum, boolean diamond) {
		this.checked = false;
		this.cellNum = cellNum;
		this.diamond = diamond;
		if (diamond) {
			numDiamonds++;
		} else {
			numMines++;
		}
	}
	
	// constructor (dragon tower)
	public Cell(int cellCol, int cellRow, boolean egg) {
		this.cellCol = cellCol;
		this.cellRow = cellRow;
		this.egg = egg;
		this.checked = false;
	}
	
	// getters and setters
	public boolean getDiamond() {
		return diamond;
	}
	
	public boolean getChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public int getCellNum() {
		return cellNum;
	}
	
	public boolean getEgg() {
		return egg;
	}
	
	// hashcode
	public int hashCode(Cell c) {
		return Objects.hash(c);
	}
	
	// equals
	public boolean equals(Cell c) {
		if (this.cellNum == c.cellNum) {
			return true;
		}
		return false;
	}
	
	// toString
	public String toString() {
		if (egg) {
			return "egg";
		} else {
			return "empty";
		}
	}
	/*
	public String toString() {
		if (diamond) {
			return "diamond";
		}
		return "mine";
	}
	*/
}
