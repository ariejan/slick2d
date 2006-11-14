Yasl - Yet Another Sprite Library (Name to be changed soon)
-----------------------------------------------------------

This is a very early version. The tests work but no demo has been written yet. Yes the windows natives
are included in the main distribution in the right place. This is just meant to make it easier for quick
start windows users. All the natives required can be found in jars in the 'lib' directory.

Tests/Examples
~~~~~~~~~~~~~~

org.newdawn.yasl.tests.ImageTest - Rendering of images from GIF,PNG,TGA (use TGA for performance)
org.newdawn.yasl.tests.FontTest - Rendering of AngelCode based fonts (bitmap fonts)
org.newdawn.yasl.tests.InputTest - Tests for mouse and keyboard notification
org.newdawn.yasl.tests.GraphicsTest - Graphics context rendering, lines, boxes, strings, antialiasing etc
org.newdawn.yasl.tests.SoundTest - Test for the sound system, OGG and XM playback.
org.newdawn.yasl.tests.TileMapTest - Support for TilED maps (a generic tilemap editor in Java)

Tools
~~~~~

AngelCode bitmap font tool - seems to be the best around at the moment and provided clean kerning 
                             information. Available from http://www.angelcode.com/products/bmfont/.
TilED tile map tool        - Java alternative to Mappy (which just seems borked to me). Available
	  						 from http://www.mapeditor.org.