package environment;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import exception.MapException;

/**
 * 2D representation of the game world
 * @author Daniel
 */
public class Map {
	protected static HashMap<Color, Property[]> colormap;
	// outer: columns, inner: rows
	protected MapCell[][] cells;
	protected ArrayList<MapCell> huntables; 
	static {
		colormap = new HashMap<Color, Property[]>();
		colormap.put(Color.WHITE, new Property[]{Property.WALKABLE});
		colormap.put(Color.BLACK, new Property[]{Property.WALL});
		colormap.put(Color.BLUE, new Property[]{Property.WATER});
		colormap.put(Color.GREEN, new Property[]{Property.FOREST, Property.WALKABLE});
	}
	
	/**
	 * @return the number of {@link MapCell}s that are currently huntable
	 */
	public int getHuntableCellCount() {
		return this.huntables.size();
	}
	
	/**
	 * Returns a list of huntable {@link MapCell}s. This list is a reference to the inner list of huntable cells.
	 * So modifying this list is highly discouraged.
	 * @return a list of huntable {@link MapCell}s
	 */
	public ArrayList<MapCell> getHuntableCells() {
		return this.huntables;
	}
	
	protected Map() {}
	
	/**
	 * Constructor
	 * creates the {@link Map} from a bitmap file. See colormap for correspondence between color and cellproperty
	 * @param _bitmapFile path to the bitmap file
	 * @throws MapException if the file doesn't exist, or is not a valid .bmp-file 
	 */
	public Map(File _bitmapFile) throws MapException {
		if(!_bitmapFile.exists()) {
			throw new MapException(String.format("Could not find Mapfile '%s'",_bitmapFile.getAbsolutePath()));
		}
		if(!_bitmapFile.isFile()) {
			throw new MapException(String.format("Could not create map from directory '%s'",_bitmapFile.getAbsolutePath()));
		}
		int index = _bitmapFile.getName().lastIndexOf('.');
		if(index <= 0 || !_bitmapFile.getName().substring(index+1).equals("bmp") ) {
			throw new MapException(String.format("Could not create map from non-bmp '%s'",_bitmapFile.getAbsolutePath()));
		}
		try {
			BufferedImage bitmap = ImageIO.read(_bitmapFile);
			int width = bitmap.getData().getWidth();
			int height = bitmap.getData().getHeight();
			this.init(width, height);
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < height; j++) {
					this.cells[i][j].addProperties(colormap.get(new Color(bitmap.getRGB(i, j))));
				}
			}
			bitmap.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a cell at a given position.
	 * @param x x-index
	 * @param y y-index
	 * @return {@link Map} at that position or NULL if the indices are invalid
	 */
	public MapCell getCellAt(int x, int y) {
		return x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight() ? null : cells[x][y];
	}
	
	/**
	 * Replaces a {@link MapCell} at a given position.<br>
	 * It also updates the list of huntable cells if necessary.
	 * @param mc new {@link MapCell}
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void setCellAt(MapCell mc, int x, int y) {
		mc.setMap(this);
		this.cells[x][y] = mc;
		if(mc.hasProperty(Property.HUNTABLE)) {
			this.huntables.add(mc);
		}
	}
	
	/**
	 * Sets a {@link MapCell} huntable or not
	 * @param x x-coordinate of cell
	 * @param y y-coordinate of cell
	 * @param huntable whether it should become huntable or not
	 */
	public void setHuntableAt(int x, int y, boolean huntable) {
		if(huntable) {
			if(cells[x][y].addProperty(Property.HUNTABLE)) {
				this.huntables.add(cells[x][y]);
			}
		} else {
			if(cells[x][y].removeProperty(Property.HUNTABLE)) {
				this.huntables.remove(cells[x][y]);
			}
		}
	}
	
	/**
	 * Gets the whole grid
	 * @return the grid of {@link MapCell}s
	 */
	public MapCell[][] getCells() {
		return this.cells;
	}
	
	/**
	 * Width of the {@link Map} in cells
	 * @return {@link MapCell}s per row
	 */
	public int getWidth() {
		return cells.length;
	}
	
	/**
	 * Height of the {@link Map} in cells
	 * @return {@link MapCell}s per column
	 */
	public int getHeight() {
		return cells.length > 0 ? cells[0].length : 0;
	}
	
	/**
	 * Selects a random {@link MapCell} with the attribute WALKABLE.
	 * This might run into an infinite loop if the {@link Map} contains no such cells at all
	 * @return a walkable cell at a random position
	 */
	public MapCell getRandomWalkableCell() {
		Random rand = new Random();
		int x,y;
		do {
			x = rand.nextInt(this.getWidth());
			y = rand.nextInt(this.getHeight());
		} while(!this.cells[x][y].hasProperty(Property.WALKABLE));
		return this.cells[x][y];
	}
	
	public Map(int width, int height) {
		this.init(width, height);
	}
	
	/**
	 * Initializes an array of {@link MapCell}s which don't have {@link Property}s yet
	 * @param width width of the array 
	 * @param height height of the array
	 */
	private void init(int width, int height) {
		this.huntables = new ArrayList<MapCell>();
		this.cells = new MapCell[width][height];
		for(int i = 0; i < width; i++) {
			for( int j = 0; j < height; j++) {
				this.cells[i][j] = new MapCell(this, i,j);
			}
		}		
	}
	
	/**
	 * Gets one single row from the {@link MapCell} data, prepended with the index of the column
	 * @param index the index of the row
	 * @return row as string where walkable cells are marked as 0 and blocked cells as 1. The String is prepended by <index of column>:
	 */
	public String rowAsString(int index) {
		String col = index+":";
		for(int i = 0; i < this.cells.length; i++) {
			col += this.cells[i][index].hasProperty(Property.WALKABLE) ? "0" : "1";
		}
		return col;
	}
	
	/**
	 * Pretty print for debugging
	 */
	@Override
	public String toString() {
		String ret = "Width: "+this.getWidth()+" Height: "+this.getHeight()+"\r\n";
		for(int i = 0; i < this.cells.length; i++) {
			for( int j = 0; j < this.cells[i].length; j++) {
				ret += this.cells[i][j].hasProperty(Property.WALKABLE) ? " " : "1";
			}
			ret += "\r\n";
		}
		return ret;
	}
}
