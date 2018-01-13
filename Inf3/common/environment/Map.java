package environment;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.annotation.JsonIgnore;

import exception.MapException;

/**
 * 2D representation of the game world
 * 
 * @author Daniel
 */
public class Map {
	protected static HashMap<Color, Property[]> colormap;
	static {
		colormap = new HashMap<>();
		colormap.put(Color.WHITE, new Property[] { Property.WALKABLE });
		colormap.put(Color.BLACK, new Property[] { Property.WALL });
		colormap.put(Color.BLUE, new Property[] { Property.WATER });
		colormap.put(Color.GREEN, new Property[] { Property.FOREST, Property.WALKABLE });
	}

	private int width;
	private int height;
	// outer: columns, inner: rows
	protected List<List<MapCell>> _cells;
	@JsonIgnore
	protected ArrayList<MapCell> _huntables;

	/**
	 * Gets the whole grid
	 * 
	 * @return the grid of {@link MapCell}s
	 */
	// public List<List<MapCell>> getCells() {
	public List<MapCell> getCells() {
		final List<MapCell> flatCells = new ArrayList<>();
		_cells.forEach(flatCells::addAll);
		return flatCells;
	}

	/**
	 * Returns a list of huntable {@link MapCell}s. This list is a reference to the
	 * inner list of huntable cells. So modifying this list is highly discouraged.
	 * 
	 * @return a list of huntable {@link MapCell}s
	 */
	@JsonIgnore
	public ArrayList<MapCell> getHuntableCells() {
		return this._huntables;
	}

	/**
	 * @return the number of {@link MapCell}s that are currently huntable
	 */
	@JsonIgnore
	public int getHuntableCellCount() {
		return this._huntables.size();
	}

	/**
	 * Width of the {@link Map} in cells
	 * 
	 * @return {@link MapCell}s per row
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Height of the {@link Map} in cells
	 * 
	 * @return {@link MapCell}s per column
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns a cell at a given position.
	 * 
	 * @param x
	 *            x-index
	 * @param y
	 *            y-index
	 * @return {@link Map} at that position or NULL if the indices are invalid
	 */
	public MapCell getCellAt(int x, int y) {
		return x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight() ? null : _cells.get(x).get(y);
	}

	/**
	 * Replaces a {@link MapCell} at a given position.<br>
	 * It also updates the list of huntable cells if necessary.
	 * 
	 * @param mc
	 *            new {@link MapCell}
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */
	public void setCellAt(MapCell mc, int x, int y) {
		mc.setMap(this);
		this._cells.get(x).set(y, mc);
		if (mc.hasProperty(Property.HUNTABLE)) {
			this._huntables.add(mc);
		}
	}

	/**
	 * Sets a {@link MapCell} huntable or not
	 * 
	 * @param x
	 *            x-coordinate of cell
	 * @param y
	 *            y-coordinate of cell
	 * @param huntable
	 *            whether it should become huntable or not
	 */
	public void setHuntableAt(int x, int y, boolean huntable) {
		if (huntable) {
			if (getCellAt(x, y).addProperty(Property.HUNTABLE)) {
				this._huntables.add(getCellAt(x, y));
			}
		} else {
			if (getCellAt(x, y).removeProperty(Property.HUNTABLE)) {
				this._huntables.remove(getCellAt(x, y));
			}
		}
	}

	/**
	 * Selects a random {@link MapCell} with the attribute WALKABLE. This might run
	 * into an infinite loop if the {@link Map} contains no such cells at all
	 * 
	 * @return a walkable cell at a random position
	 */
	@JsonIgnore
	public MapCell getRandomWalkableCell() {
		final Random rand = new Random();
		int x, y;
		do {
			x = rand.nextInt(this.getWidth());
			y = rand.nextInt(this.getHeight());
		} while (!this.getCellAt(x, y).hasProperty(Property.WALKABLE));
		return this.getCellAt(x, y);
	}

	protected Map() {
	}

	public Map(int width, int height) {
		this.init(width, height);
	}

	/**
	 * Constructor creates the {@link Map} from a bitmap file. See colormap for
	 * correspondence between color and cellproperty
	 * 
	 * @param _bitmapFile
	 *            path to the bitmap file
	 * @throws MapException
	 *             if the file doesn't exist, or is not a valid .bmp-file
	 */
	public Map(File _bitmapFile) throws MapException {
		if (!_bitmapFile.exists()) {
			throw new MapException(String.format("Could not find Mapfile '%s'", _bitmapFile.getAbsolutePath()));
		}
		if (!_bitmapFile.isFile()) {
			throw new MapException(
					String.format("Could not create map from directory '%s'", _bitmapFile.getAbsolutePath()));
		}
		final int index = _bitmapFile.getName().lastIndexOf('.');
		if (index <= 0 || !_bitmapFile.getName().substring(index + 1).equals("bmp")) {
			throw new MapException(
					String.format("Could not create map from non-bmp '%s'", _bitmapFile.getAbsolutePath()));
		}
		try {
			final BufferedImage bitmap = ImageIO.read(_bitmapFile);
			final int width = bitmap.getData().getWidth();
			final int height = bitmap.getData().getHeight();
			this.init(width, height);
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					getCellAt(i, j).addProperties(colormap.get(new Color(bitmap.getRGB(i, j))));
				}
			}
			bitmap.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes an array of {@link MapCell}s which don't have {@link Property}s
	 * yet
	 * 
	 * @param width
	 *            width of the array
	 * @param height
	 *            height of the array
	 */
	private void init(int width, int height) {
		this._huntables = new ArrayList<>();
		this.width = width;
		this.height = height;
		this._cells = new ArrayList<>(width);
		for (int i = 0; i < width; i++) {
			final ArrayList<MapCell> row = new ArrayList<>(height);
			for (int j = 0; j < height; j++) {
				row.add(new MapCell(this, i, j));
			}
			this._cells.add(row);
		}
	}

	/**
	 * Pretty print for debugging
	 */
	@Override
	public String toString() {
		String ret = "Width: " + this.getWidth() + " Height: " + this.getHeight() + "\r\n";
		for (int i = 0; i < this._cells.size(); i++) {
			for (int j = 0; j < this._cells.get(i).size(); j++) {
				ret += this._cells.get(i).get(j).hasProperty(Property.WALKABLE) ? " " : "1";
			}
			ret += "\r\n";
		}
		return ret;
	}
}
