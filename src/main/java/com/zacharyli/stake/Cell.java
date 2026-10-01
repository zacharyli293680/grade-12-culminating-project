package com.zacharyli.stake;

// blueprint for cell object

// imports
import java.util.*;

// cell class
public class Cell {
	
	// fields
	private int cellNum;
	private boolean diamond;
	private boolean checked;
	private int cellCol;
	private int cellRow;
	private boolean egg;
	
	// constructor (mines)
	public Cell(int cellNum, boolean diamond) {
		this.checked = false;
		this.cellNum = cellNum;
		this.diamond = diamond;
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
	@Override
	public int hashCode() {
		return Objects.hash(cellNum, cellCol, cellRow);
	}
	
	// equals
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Cell)) {
			return false;
		}
		Cell c = (Cell) o;
		return this.cellNum == c.cellNum && this.cellCol == c.cellCol && this.cellRow == c.cellRow;
	}
	
	// toString
	public String toString() {
		if (cellNum > 0) {
			return diamond ? "diamond" : "mine";
		}
		return egg ? "egg" : "empty";
	}
}
