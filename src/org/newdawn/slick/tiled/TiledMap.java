package org.newdawn.slick.tiled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is intended to parse TilED maps. TilED is a generic tool for tile map editing and can
 * be found at:
 * 
 * http://mapeditor.org/
 * 
 * @author kevin
 */
public class TiledMap {
	/** The code used to decode Base64 encoding */
	private static byte[] baseCodes = new byte[256];

	/**
	 * Static initialiser for the codes created against Base64
	 */
	static {
		for (int i = 0; i < 256; i++)
			baseCodes[i] = -1;
		for (int i = 'A'; i <= 'Z'; i++)
			baseCodes[i] = (byte) (i - 'A');
		for (int i = 'a'; i <= 'z'; i++)
			baseCodes[i] = (byte) (26 + i - 'a');
		for (int i = '0'; i <= '9'; i++)
			baseCodes[i] = (byte) (52 + i - '0');
		baseCodes['+'] = 62;
		baseCodes['/'] = 63;
	}
	
	/** The width of the map */
	protected int width;
	/** The height of the map */
	protected int height;
	/** The width of the tiles used on the map */
	protected int tileWidth;
	/** The height of the tiles used on the map */
	protected int tileHeight;
	/** The location prefix where we can find tileset images */
	protected String tilesLocation;
	
	/** the properties of the map */
	protected Properties props;
	
	/** The list of tilesets defined in the map */
	protected ArrayList tileSets = new ArrayList();
	/** The list of layers defined in the map */
	protected ArrayList layers = new ArrayList();

	/**
	 * Create a new tile map based on a given TMX file
	 * 
	 * @param ref The location of the tile map to load
	 * @throws SlickException Indicates a failure to load the tilemap
	 */
	public TiledMap(String ref) throws SlickException {
		ref = ref.replace('\\','/');
		load(ResourceLoader.getResourceAsStream(ref), ref.substring(0,ref.lastIndexOf("/")));
	}

	/**
	 * Create a new tile map based on a given TMX file
	 * 
	 * @param ref The location of the tile map to load
	 * @param tileSetsLocation The location where we can find the tileset images and other resources
	 * @throws SlickException Indicates a failure to load the tilemap
	 */
	public TiledMap(String ref, String tileSetsLocation) throws SlickException {
		load(ResourceLoader.getResourceAsStream(ref), tileSetsLocation);
	}
	
	/**
	 * Load a tile map from an arbitary input stream
	 * 
	 * @param in The input stream to load from
	 * @throws SlickException Indicates a failure to load the tilemap
	 */
	public TiledMap(InputStream in) throws SlickException {
		load(in, "");
	}

	/**
	 * Load a tile map from an arbitary input stream
	 * 
	 * @param in The input stream to load from
	 * @param tileSetsLocation The location at which we can find tileset images
	 * @throws SlickException Indicates a failure to load the tilemap
	 */
	public TiledMap(InputStream in, String tileSetsLocation) throws SlickException {
		load(in, tileSetsLocation);
	}
	
	/**
     * Get the index of the layer with given name
     * 
     * @param name The name of the tile to search for
     * @return The index of the layer or -1 if there is no layer with given name
     */
   public int getLayerIndex(String name) {
      int idx = 0;
      
      for (int i=0;i<layers.size();i++) {
         Layer layer = (Layer) layers.get(i);
         
         if (layer.name.equals(name)) {
            return i;
         }
      }
      
      return -1;
   }
   
   /**
    * Gets the Image used to draw the tile at the given x and y coordinates.
    * 
    * @param x The x coordinate of the tile whose image should be retrieved
    * @param y The y coordinate of the tile whose image should be retrieved
    * @param layerIndex The index of the layer on which the tile whose image should be retrieve exists
    * @return The image used to draw the specified tile or null if there is no image for the
    * specified tile.
    */
   public Image getTileImage(int x, int y, int layerIndex) {
      Layer layer = (Layer) layers.get(layerIndex);
      
      int tileSetIndex = layer.data[x][y][0];
      if ((tileSetIndex >= 0) && (tileSetIndex < tileSets.size())) {
    	  TileSet tileSet = (TileSet) tileSets.get(tileSetIndex);
    	  
          int sheetX = tileSet.getTileX(layer.data[x][y][1]);
          int sheetY = tileSet.getTileY(layer.data[x][y][1]);
          
          return tileSet.tiles.getSprite(sheetX, sheetY);
      }
      
      return null;
   } 
   
	/**
	 * Get the width of the map
	 * 
	 * @return The width of the map (in tiles)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the map
	 * 
	 * @return The height of the map (in tiles)
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the height of a single tile
	 * 
	 * @return The height of a single tile (in pixels)
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * Get the width of a single tile
	 * 
	 * @return The height of a single tile (in pixels)
	 */
	public int getTileWidth() {
		return tileWidth;
	}
	   
	/**
	 * Get the global ID of a tile at specified location in the map
	 * 
	 * @param x
	 *            The x location of the tile
	 * @param y
	 *            The y location of the tile
	 * @param layerIndex
	 *            The index of the layer to retireve the tile from
	 * @return The global ID of the tile
	 */
	public int getTileId(int x,int y,int layerIndex) {
		Layer layer = (Layer) layers.get(layerIndex);
		return layer.getTileID(x,y);
	}
	
	/**
	 * Set the global ID of a tile at specified location in the map
	 * @param x
	 *            The x location of the tile
	 * @param y
	 *            The y location of the tile
	 * @param layerIndex
	 *            The index of the layer to set the new tileid
	 * @param tileid
	 *            The tileid to be set
	 */
	public void setTileId(int x, int y, int layerIndex, int tileid) {
		Layer layer = (Layer) layers.get(layerIndex);
		layer.setTileID(x, y, tileid);
	}
	
	/**
	 * Get a property given to the map. Note that this method will
	 * not perform well and should not be used as part of the default code
	 * path in the game loop.
	 * 
	 * @param propertyName The name of the property of the map to retrieve
	 * @param def The default value to return
	 * @return The value assigned to the property on the map (or the default value if none is supplied)
	 */
	public String getMapProperty(String propertyName, String def) {
		if (props == null)
			return def;
		return props.getProperty(propertyName, def);
	}
	
	/**
	 * Get a property given to a particular layer. Note that this method will
	 * not perform well and should not be used as part of the default code
	 * path in the game loop.
	 * 
	 * @param layerIndex The index of the layer to retrieve
	 * @param propertyName The name of the property of this layer to retrieve
	 * @param def The default value to return
	 * @return The value assigned to the property on the layer (or the default value if none is supplied)
	 */
	public String getLayerProperty(int layerIndex, String propertyName, String def) {
		Layer layer = (Layer) layers.get(layerIndex);
		if (layer == null || layer.props == null)
			return def;
		return layer.props.getProperty(propertyName, def);
	}
	
	
	/**
	 * Get a propety given to a particular tile. Note that this method will
	 * not perform well and should not be used as part of the default code
	 * path in the game loop.
	 * 
	 * @param tileID The global ID of the tile to retrieve
	 * @param propertyName The name of the property to retireve
	 * @param def The default value to return
	 * @return The value assigned to the property on the tile (or the default value if none is supplied)
	 */
	public String getTileProperty(int tileID, String propertyName, String def) {
		if (tileID == 0) {
			return def;
		}
		
		TileSet set = findTileSet(tileID);
		
		Properties props = set.getProperties(tileID);
		if (props == null) {
			return def;
		}
		return props.getProperty(propertyName, def);
	}
	
	/**
	 * Render the whole tile map at a given location
	 * 
	 * @param x The x location to render at 
	 * @param y The y location to render at
	 */
	public void render(int x,int y) {
		render(x,y,0,0,width,height,false);
	}

	/**
	 * Render a section of the tile map
	 * 
	 * @param x The x location to render at
	 * @param y The y location to render at
	 * @param sx The x tile location to start rendering
	 * @param sy The y tile location to start rendering
	 * @param width The width of the section to render (in tiles)
	 * @param height The height of the secton to render (in tiles)
	 */
	public void render(int x,int y,int sx,int sy,int width,int height) {
		render(x,y,sx,sy,width,height,false);
	}

	/**
	 * Render a section of the tile map
	 * 
	 * @param x The x location to render at
	 * @param y The y location to render at
	 * @param sx The x tile location to start rendering
	 * @param sy The y tile location to start rendering
	 * @param width The width of the section to render (in tiles)
	 * @param height The height of the secton to render (in tiles)
	 * @param l The index of the layer to render
	 * @param lineByLine True if we should render line by line, i.e. giving us a chance
	 * to render something else between lines (@see {@link #renderedLine(int, int, int)}
	 */
	public void render(int x,int y,int sx,int sy,int width,int height,int l,boolean lineByLine) {
		Layer layer = (Layer) layers.get(l);
		for (int ty=0;ty<height;ty++) {
			layer.render(x,y,sx,sy,width,ty,lineByLine, tileWidth, tileHeight);
		}
	}
	
	/**
	 * Render a section of the tile map
	 * 
	 * @param x The x location to render at
	 * @param y The y location to render at
	 * @param sx The x tile location to start rendering
	 * @param sy The y tile location to start rendering
	 * @param width The width of the section to render (in tiles)
	 * @param height The height of the secton to render (in tiles)
	 * @param lineByLine True if we should render line by line, i.e. giving us a chance
	 * to render something else between lines (@see {@link #renderedLine(int, int, int)}
	 */
	public void render(int x,int y,int sx,int sy,int width,int height, boolean lineByLine) {
		for (int ty=0;ty<height;ty++) {
			for (int i=0;i<layers.size();i++) {
				Layer layer = (Layer) layers.get(i);
				layer.render(x,y,sx,sy,width, ty,lineByLine, tileWidth, tileHeight);
			}
		}
	}
	
	/**
	 * Retrieve a count of the number of layers available
	 * 
	 * @return The number of layers available in this map
	 */
	public int getLayerCount() {
		return layers.size();
	}
	
	/**
	 * Load a TilED map
	 * 
	 * @param in The input stream from which to load the map
	 * @param tileSetsLocation The location from which we can retrieve tileset images
	 * @throws SlickException Indicates a failure to parse the map or find a tileset
	 */
	private void load(InputStream in, String tileSetsLocation) throws SlickException {
		tilesLocation = tileSetsLocation;
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(in);
			Element docElement = doc.getDocumentElement();
			
			String orient = docElement.getAttribute("orientation");
			if (!orient.equals("orthogonal")) {
				throw new SlickException("Only orthogonal maps supported, found: "+orient);
			}
			
			width = Integer.parseInt(docElement.getAttribute("width"));
			height = Integer.parseInt(docElement.getAttribute("height"));
			tileWidth = Integer.parseInt(docElement.getAttribute("tilewidth"));
			tileHeight = Integer.parseInt(docElement.getAttribute("tileheight"));
			
			// now read the map properties
			Element propsElement = (Element) docElement.getElementsByTagName("properties").item(0);
			if (propsElement != null) {
				NodeList properties = propsElement.getElementsByTagName("property");
				if (properties != null) {
					props = new Properties();
					for (int p = 0; p < properties.getLength();p++) {
						Element propElement = (Element) properties.item(p);
						
						String name = propElement.getAttribute("name");
						String value = propElement.getAttribute("value");		
						props.setProperty(name, value);
					}
				}
			}
			
			TileSet tileSet = null;
			TileSet lastSet = null;
			
			NodeList setNodes = docElement.getElementsByTagName("tileset");
			for (int i=0;i<setNodes.getLength();i++) {
				Element current = (Element) setNodes.item(i);
				
				tileSet = new TileSet(current);
				tileSet.index = i;
				
				if (lastSet != null) {
					lastSet.setLimit(tileSet.firstGID-1);
				}
				lastSet = tileSet;
				
				tileSets.add(tileSet);
			}
			
			NodeList layerNodes = docElement.getElementsByTagName("layer");
			for (int i=0;i<layerNodes.getLength();i++) {
				Element current = (Element) layerNodes.item(i);
				Layer layer = new Layer(current);
				layer.index = i;
				
				layers.add(layer);
			}
		} catch (Exception e) {
			Log.error(e);
			throw new SlickException("Failed to parse tilemap", e);
		}
	}
	
	/**
	 * Find a tile for a given global tile id
	 * 
	 * @param gid The global tile id we're looking for
	 * @return The tileset in which that tile lives
	 */
	private TileSet findTileSet(int gid) {
		for (int i=0;i<tileSets.size();i++) {
			TileSet set = (TileSet) tileSets.get(i);
			
			if (set.contains(gid)) {
				return set;
			}
		}
		
		throw new RuntimeException("Global tile id "+gid+" not found");
	}
	
	/**
	 * A holder for tileset information
	 *
	 * @author kevin
	 */
	protected class TileSet {
		/** The index of the tile set */
		public int index;
		/** The name of the tile set */
		public String name;
		/** The first global tile id in the set */
		public int firstGID;
		/** The local global tile id in the set */
		public int lastGID = Integer.MAX_VALUE;
		/** The width of the tiles */
		public int tileWidth;
		/** The height of the tiles */
		public int tileHeight;
		/** The image containing the tiles */
		public SpriteSheet tiles;
		
		/** The number of tiles across the sprite sheet */
		public int tilesAcross;
		/** The number of tiles down the sprite sheet */
		public int tilesDown;
		
		/** The properties for each tile */
		private HashMap props = new HashMap();
		
		/**
		 * Create a tile set based on an XML definition
		 * 
		 * @param element The XML describing the tileset
		 * @throws SlickException Indicates a failure to parse the tileset
		 */
		public TileSet(Element element) throws SlickException {
			name = element.getAttribute("name");
			firstGID = Integer.parseInt(element.getAttribute("firstgid"));
			String source = element.getAttribute("source");
			
			if ((source != null) && (!source.equals(""))) {
				try {
					InputStream in = ResourceLoader.getResourceAsStream(tilesLocation + "/" + source);
					DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document doc = builder.parse(in);
					Element docElement = doc.getDocumentElement();
					element = docElement; //(Element) docElement.getElementsByTagName("tileset").item(0);
				} catch (Exception e) {
					Log.error(e);
					throw new SlickException("Unable to load or parse sourced tileset: "+tilesLocation+"/"+source);
				}
			}
            String tileWidthString = element.getAttribute("tilewidth");
            String tileHeightString = element.getAttribute("tileheight");
            if(tileWidthString.length() == 0 || tileHeightString.length() == 0) {
                throw new SlickException("TiledMap requires that the map be created with tilesets that use a " +
                        "single image.  Check the WiKi for more complete information.");
            }
			tileWidth = Integer.parseInt(tileWidthString);
			tileHeight = Integer.parseInt(tileHeightString);
			
			int spacing = 0;
			String sv = element.getAttribute("spacing");
			if ((sv != null) && (!sv.equals(""))) {
				spacing = Integer.parseInt(sv);
			}
			NodeList list = element.getElementsByTagName("image");
			Element imageNode = (Element) list.item(0);
			String ref = imageNode.getAttribute("source");
			
			Color trans = null;
			String t = imageNode.getAttribute("trans");
			if ((t != null) && (t.length() > 0)) {
				int c = Integer.parseInt(t, 16);
				
				trans = new Color(c);
			}

			Image image = new Image(tilesLocation+"/"+ref,false,Image.FILTER_NEAREST,trans);
	        
			tiles = new SpriteSheet(image , tileWidth, tileHeight, spacing);
			tilesAcross = tiles.getHorizontalCount();
			tilesDown = tiles.getVerticalCount();

			if (tilesAcross <= 0) {
				tilesAcross = 1;
			}
			if (tilesDown <= 0) {
				tilesDown = 1;
			}

			lastGID = (tilesAcross * tilesDown) + firstGID - 1;
			NodeList pElements = element.getElementsByTagName("tile");
			for (int i=0;i<pElements.getLength();i++) {
				Element tileElement = (Element) pElements.item(i);
				
				int id = Integer.parseInt(tileElement.getAttribute("id"));
				id += firstGID;
				Properties tileProps = new Properties();
				
				Element propsElement = (Element) tileElement.getElementsByTagName("properties").item(0);
				NodeList properties = propsElement.getElementsByTagName("property");
				for (int p=0;p<properties.getLength();p++) {
					Element propElement = (Element) properties.item(p);
					
					String name = propElement.getAttribute("name");
					String value = propElement.getAttribute("value");
					
					tileProps.setProperty(name, value);
				}
				
				props.put(new Integer(id), tileProps);
			}
		}
		
		/**
		 * Get the properties for a specific tile in this tileset
		 * 
		 * @param globalID The global ID of the tile whose properties should be retrieved
		 * @return The properties for the specified tile, or null if no properties are defined
		 */
		public Properties getProperties(int globalID) {
			return (Properties) props.get(new Integer(globalID));
		}
		
		/**
		 * Get the x position of a tile on this sheet
		 * 
	     * @param id The tileset specific ID (i.e. not the global one)
		 * @return The index of the tile on the x-axis
		 */
		public int getTileX(int id) {
			return id % tilesAcross;
		}

		/**
		 * Get the y position of a tile on this sheet
		 * 
	     * @param id The tileset specific ID (i.e. not the global one)
		 * @return The index of the tile on the y-axis
		 */
		public int getTileY(int id) {	
			return id / tilesAcross;
		}

		/**
		 * Set the limit of the tiles in this set
		 * 
		 * @param limit The limit of the tiles in this set
		 */
		public void setLimit(int limit) {
			lastGID = limit;
		}
		
		/**
		 * Check if this tileset contains a particular tile
		 * 
		 * @param gid The global id to seach for
		 * @return True if the ID is contained in this tileset
		 */
		public boolean contains(int gid) {
			return (gid >= firstGID) && (gid <= lastGID);
		}
	}
	
	/**
	 * A layer of tiles on the map
	 *
	 * @author kevin
	 */
	protected class Layer {
		/** The index of this layer */
		public int index;
		/** The name of this layer - read from the XML */
		public String name;
		/** The tile data representing this data, index 0 = tileset, index 1 = tile id */
		public int[][][] data;
		/** The width of this layer */
		public int width;
		/** The height of this layer */
		public int height;
		
		/** the properties of this layer */
		
		public Properties props;
		/**
		 * Create a new layer based on the XML definition
		 * 
		 * @param element The XML element describing the layer
		 * @throws SlickException Indicates a failure to parse the XML layer
		 */
		public Layer(Element element) throws SlickException {
			name = element.getAttribute("name");
			width = Integer.parseInt(element.getAttribute("width"));
			height = Integer.parseInt(element.getAttribute("height"));
			data = new int[width][height][3];

			// now read the layer properties
			Element propsElement = (Element) element.getElementsByTagName("properties").item(0);
			if (propsElement != null) {
				NodeList properties = propsElement.getElementsByTagName("property");
				if (properties != null) {
					props = new Properties();
					for (int p = 0; p < properties.getLength();p++) {
						Element propElement = (Element) properties.item(p);
						
						String name = propElement.getAttribute("name");
						String value = propElement.getAttribute("value");		
						props.setProperty(name, value);
					}
				}
			}

			Element dataNode = (Element) element.getElementsByTagName("data").item(0);
			String encoding = dataNode.getAttribute("encoding");
			String compression = dataNode.getAttribute("compression");
			
			if (encoding.equals("base64") && compression.equals("gzip")) {
				try {
	                Node cdata = dataNode.getFirstChild();
	                char[] enc = cdata.getNodeValue().trim().toCharArray();
	                byte[] dec = decodeBase64(enc);
	                GZIPInputStream is = new GZIPInputStream(new ByteArrayInputStream(dec));
	                
	                for (int y = 0; y < height; y++) {
	                    for (int x = 0; x < width; x++) {
	                        int tileId = 0;
	                        tileId |= is.read();
	                        tileId |= is.read() <<  8;
	                        tileId |= is.read() << 16;
	                        tileId |= is.read() << 24;

	                        if (tileId == 0) {
		                        data[x][y][0] = -1;
		                        data[x][y][1] = 0;
		                        data[x][y][2] = 0;
	                        } else {
		                        TileSet set = findTileSet(tileId);
	
		                        data[x][y][0] = set.index;
		                        data[x][y][1] = tileId - set.firstGID;
		                        data[x][y][2] = tileId;
	                        }
	                    }
	                }
				} catch (IOException e) {
					Log.error(e);
					throw new SlickException("Unable to decode base 64 block");
				}
			} else {
				throw new SlickException("Unsupport tiled map type: "+encoding+","+compression+" (only gzip base64 supported)");
			}
		}
		
		/**
		 * Get the gloal ID of the tile at the specified location in
		 * this layer
		 * 
		 * @param x The x coorindate of the tile
		 * @param y The y coorindate of the tile
		 * @return The global ID of the tile
		 */
		public int getTileID(int x, int y) {
			return data[x][y][2];
		}
		
		/**
		 * Set the global tile ID at a specified location
		 * 
		 * @param x The x location to set
		 * @param y The y location to set
		 * @param tile The tile value to set
		 */
		public void setTileID(int x, int y, int tile) {
            if (tile == 0) {
                data[x][y][0] = -1;
                data[x][y][1] = 0;
                data[x][y][2] = 0;
            } else {
	            TileSet set = findTileSet(tile);
	            
	            data[x][y][0] = set.index;
	            data[x][y][1] = tile - set.firstGID;
	            data[x][y][2] = tile;
            }
		}
		
		/**
		 * Render a section of this layer
		 * 
		 * @param x
		 *            The x location to render at
		 * @param y
		 *            The y location to render at
		 * @param sx
		 *            The x tile location to start rendering
		 * @param sy
		 *            The y tile location to start rendering
		 * @param width The number of tiles across to render
		 * @param ty The line of tiles to render
		 * @param lineByLine
		 *            True if we should render line by line, i.e. giving us a
		 *            chance to render something else between lines
		 * @param mapTileWidth the tile width specified in the map file
		 * @param mapTileHeight the tile height specified in the map file
		 */
		public void render(int x,int y,int sx,int sy,int width, int ty,boolean lineByLine, int mapTileWidth, int mapTileHeight) {
			for (int tileset=0;tileset<tileSets.size();tileset++) {
				TileSet set = null;
				
				for (int tx=0;tx<width;tx++) {
					if ((sx+tx < 0) || (sy+ty < 0)) {
						continue;
					}
					if ((sx+tx >= this.width) || (sy+ty >= this.height)) {
						continue;
					}
					
					if (data[sx+tx][sy+ty][0] == tileset) {
						if (set == null) {
							set = (TileSet) tileSets.get(tileset);
							set.tiles.startUse();
						}
						
						int sheetX = set.getTileX(data[sx+tx][sy+ty][1]);
						int sheetY = set.getTileY(data[sx+tx][sy+ty][1]);
						
						int tileOffsetY = set.tileHeight - mapTileHeight;
						
//						set.tiles.renderInUse(x+(tx*set.tileWidth), y+(ty*set.tileHeight), sheetX, sheetY);
						set.tiles.renderInUse(x+(tx*mapTileWidth), y+(ty*mapTileHeight)-tileOffsetY, sheetX, sheetY);
					}
				}
				
				if (lineByLine) {
					if (set != null) {
						set.tiles.endUse();
						set = null;
					}
					renderedLine(ty, ty+sy, index);
				}
				
				if (set != null) {
					set.tiles.endUse();
				}
			}
			
		}
	}
	
	/**
	 * Overrideable to allow other sprites to be rendered between lines of the
	 * map
	 * 
	 * @param visualY The visual Y coordinate, i.e. 0->height
	 * @param mapY The map Y coordinate, i.e. y->y+height
	 * @param layer The layer being rendered
	 */
	protected void renderedLine(int visualY, int mapY,int layer) {
	}
	
	/**
	 * Decode a Base64 string as encoded by TilED
	 * 
	 * @param data The string of character to decode
	 * @return The byte array represented by character encoding
	 */
    private byte[] decodeBase64(char[] data) {
		int temp = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || baseCodes[data[ix]] < 0) {
				--temp; 
			}
		}

		int len = (temp / 4) * 3;
		if ((temp % 4) == 3)
			len += 2;
		if ((temp % 4) == 2)
			len += 1;

		byte[] out = new byte[len];

		int shift = 0;
		int accum = 0;
		int index = 0;

		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : baseCodes[data[ix]];

			if (value >= 0) {
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] = (byte) ((accum >> shift) & 0xff);
				}
			}
		}

		if (index != out.length) {
			throw new RuntimeException(
					"Data length appears to be wrong (wrote " + index
							+ " should be " + out.length + ")");
		}

		return out;
	}
}
