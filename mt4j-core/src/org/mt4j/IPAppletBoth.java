package org.mt4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.mt4j.util.opengl.GLCommon;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PStyle;

public interface IPAppletBoth {
	
	
	/////////////////////////////Android //TODO
	
//
////  /** Called with the activity is first created. */
////  public void onCreate(Bundle savedInstanceState);
////
////  public void onConfigurationChanged(Configuration newConfig);
////
////  /**
////   * Developers can override here to save state. The 'paused' variable will be
////   * set before this function is called.
////   */
////  public void pause();
////
////  /**
////   * Developers can override here to restore state. The 'paused' variable
////   * will be cleared before this function is called.
////   */
////  public void resume();
////
////  public void onDestroy();
//
////  // TODO this is only used by A2D, when finishing up a draw. but if the
////  // surfaceview has changed, then it might belong to an a3d surfaceview. hrm.
////  public SurfaceHolder getSurfaceHolder();
////
////  /**
////   * Called by the sketch surface view, thought it could conceivably be called
////   * by Android as well.
////   */
////  public void surfaceWindowFocusChanged(boolean hasFocus);
////
////  /**
////   * If you override this function without calling super.onTouchEvent(),
////   * then motionX, motionY, motionPressed, and motionEvent will not be set.
////   */
////  public boolean surfaceTouchEvent(MotionEvent event);
////
////  public boolean surfaceKeyDown(int code, KeyEvent event);
////
////  public boolean surfaceKeyUp(int code, KeyEvent event);
//
//  public int sketchWidth();
//
//  public int sketchHeight();
//
//  public String sketchRenderer();
//
////  public boolean sketchTranslucency();
////
////  public String sketchColordepth();
////
////  public void orientation(int which);
//
//  /**
//   * Called by the browser or applet viewer to inform this applet that it
//   * should start its execution. It is called after the init method and
//   * each time the applet is revisited in a Web page.
//   * <p/>
//   * Called explicitly via the first call to PApplet.paint(), because
//   * PAppletGL needs to have a usable screen before getting things rolling.
//   */
//  public void start();
//
//  /**
//   * Called by the browser or applet viewer to inform
//   * this applet that it should stop its execution.
//   * <p/>
//   * Unfortunately, there are no guarantees from the Java spec
//   * when or if stop() will be called (i.e. on browser quit,
//   * or when moving between web pages), and it's not always called.
//   */
//  public void stop();
//
//  /**
//   * Called by the browser or applet viewer to inform this applet
//   * that it is being reclaimed and that it should destroy
//   * any resources that it has allocated.
//   * <p/>
//   * This also attempts to call PApplet.stop(), in case there
//   * was an inadvertent override of the stop() function by a user.
//   * <p/>
//   * destroy() supposedly gets called as the applet viewer
//   * is shutting down the applet. stop() is called
//   * first, and then destroy() to really get rid of things.
//   * no guarantees on when they're run (on browser quit, or
//   * when moving between pages), though.
//   */
//  public void destroy();
//
//  public void setup();
//
//  public void draw();
//
//  /**
//   * Starts up and creates a two-dimensional drawing surface, or resizes the
//   * current drawing surface.
//   * <P>
//   * This should be the first thing called inside of setup().
//   * <P>
//   * If called once a renderer has already been set, this will use the
//   * previous renderer and simply resize it.
//   */
//  public void size(int iwidth, int iheight);
//
//  public void size(int iwidth, int iheight, String irenderer);
//
//  /**
//   * Creates a new PGraphics object and sets it to the specified size.
//   *
//   * Note that you cannot change the renderer once outside of setup().
//   * In most cases, you can call size() to give it a new size,
//   * but you need to always ask for the same renderer, otherwise
//   * you're gonna run into trouble.
//   *
//   * The size() method should *only* be called from inside the setup() or
//   * draw() methods, so that it is properly run on the main animation thread.
//   * To change the size of a PApplet externally, use setSize(), which will
//   * update the component size, and queue a resize of the renderer as well.
//   */
//  public void size(final int iwidth, final int iheight, final String irenderer,
//                   final String ipath);
//
//  /**
//   * Create an offscreen PGraphics object for drawing. This can be used
//   * for bitmap or vector images drawing or rendering.
//   * <UL>
//   * <LI>Do not use "new PGraphicsXxxx()", use this method. This method
//   * ensures that internal variables are set up properly that tie the
//   * new graphics context back to its parent PApplet.
//   * <LI>The basic way to create bitmap images is to use the <A
//   * HREF="http://processing.org/reference/saveFrame_.html">saveFrame()</A>
//   * function.
//   * <LI>If you want to create a really large scene and write that,
//   * first make sure that you've allocated a lot of memory in the Preferences.
//   * <LI>If you want to create images that are larger than the screen,
//   * you should create your own PGraphics object, draw to that, and use
//   * <A HREF="http://processing.org/reference/save_.html">save()</A>.
//   * For now, it's best to use <A HREF="http://dev.processing.org/reference/everything/javadoc/processing/core/PGraphics3D.html">P3D</A> in this scenario.
//   * P2D is currently disabled, and the JAVA2D default will give mixed
//   * results. An example of using P3D:
//   * <PRE>
//   *
//   * PGraphics big;
//   *
//   * void setup() {
//   *   big = createGraphics(3000, 3000, P3D);
//   *
//   *   big.beginDraw();
//   *   big.background(128);
//   *   big.line(20, 1800, 1800, 900);
//   *   // etc..
//   *   big.endDraw();
//   *
//   *   // make sure the file is written to the sketch folder
//   *   big.save("big.tif");
//   * }
//   *
//   * </PRE>
//   * <LI>It's important to always wrap drawing to createGraphics() with
//   * beginDraw() and endDraw() (beginFrame() and endFrame() prior to
//   * revision 0115). The reason is that the renderer needs to know when
//   * drawing has stopped, so that it can update itself internally.
//   * This also handles calling the defaults() method, for people familiar
//   * with that.
//   * <LI>It's not possible to use createGraphics() with the OPENGL renderer,
//   * because it doesn't allow offscreen use.
//   * <LI>With Processing 0115 and later, it's possible to write images in
//   * formats other than the default .tga and .tiff. The exact formats and
//   * background information can be found in the developer's reference for
//   * <A HREF="http://dev.processing.org/reference/core/javadoc/processing/core/PImage.html#save(java.lang.String)">PImage.save()</A>.
//   * </UL>
//   */
//  public PGraphics createGraphics(int iwidth, int iheight, String irenderer);
//
//  /**
//   * Preferred method of creating new PImage objects, ensures that a
//   * reference to the parent PApplet is included, which makes save() work
//   * without needing an absolute path.
//   */
//  public PImage createImage(int wide, int high, int format);
//
////  public PImage createImage(int wide, int high, int format, int sampling);
//
////  public PImage createImage(int wide, int high, PTexture.Parameters params);
//
//  /**
//   * Main method for the primary animation thread.
//   */
//  public void run();
//
//  public void handleDraw();
//
//  public void redraw();
//
//  public void loop();
//
//  public void noLoop();
//
//  public void mousePressed();
//
//  public void mouseReleased();
//
//  public void mouseDragged();
//
//  public void mouseMoved();
//
//  /**
//   * Called each time a single key on the keyboard is pressed.
//   * Because of how operating systems handle key repeats, holding
//   * down a key will cause multiple calls to keyPressed(), because
//   * the OS repeat takes over.
//   * <P>
//   * Examples for key handling:
//   * (Tested on Windows XP, please notify if different on other
//   * platforms, I have a feeling Mac OS and Linux may do otherwise)
//   * <PRE>
//   * 1. Pressing 'a' on the keyboard:
//   *    keyPressed  with key == 'a' and keyCode == 'A'
//   *    keyTyped    with key == 'a' and keyCode ==  0
//   *    keyReleased with key == 'a' and keyCode == 'A'
//   *
//   * 2. Pressing 'A' on the keyboard:
//   *    keyPressed  with key == 'A' and keyCode == 'A'
//   *    keyTyped    with key == 'A' and keyCode ==  0
//   *    keyReleased with key == 'A' and keyCode == 'A'
//   *
//   * 3. Pressing 'shift', then 'a' on the keyboard (caps lock is off):
//   *    keyPressed  with key == CODED and keyCode == SHIFT
//   *    keyPressed  with key == 'A'   and keyCode == 'A'
//   *    keyTyped    with key == 'A'   and keyCode == 0
//   *    keyReleased with key == 'A'   and keyCode == 'A'
//   *    keyReleased with key == CODED and keyCode == SHIFT
//   *
//   * 4. Holding down the 'a' key.
//   *    The following will happen several times,
//   *    depending on your machine's "key repeat rate" settings:
//   *    keyPressed  with key == 'a' and keyCode == 'A'
//   *    keyTyped    with key == 'a' and keyCode ==  0
//   *    When you finally let go, you'll get:
//   *    keyReleased with key == 'a' and keyCode == 'A'
//   *
//   * 5. Pressing and releasing the 'shift' key
//   *    keyPressed  with key == CODED and keyCode == SHIFT
//   *    keyReleased with key == CODED and keyCode == SHIFT
//   *    (note there is no keyTyped)
//   *
//   * 6. Pressing the tab key in an applet with Java 1.4 will
//   *    normally do nothing, but PApplet dynamically shuts
//   *    this behavior off if Java 1.4 is in use (tested 1.4.2_05 Windows).
//   *    Java 1.1 (Microsoft VM) passes the TAB key through normally.
//   *    Not tested on other platforms or for 1.3.
//   * </PRE>
//   */
//  public void keyPressed();
//
//  /**
//   * See keyPressed().
//   */
//  public void keyReleased();
//
//  /**
//   * Only called for "regular" keys like letters,
//   * see keyPressed() for full documentation.
//   */
//  public void keyTyped();
//
//  public void focusGained();
//
//  public void focusLost();
//
//  /**
//   * Get the number of milliseconds since the applet started.
//   * <P>
//   * This is a function, rather than a variable, because it may
//   * change multiple times per frame.
//   */
//  public int millis();
//
//  /**
//   * The delay() function causes the program to halt for a specified time.
//   * Delay times are specified in thousandths of a second. For example,
//   * running delay(3000) will stop the program for three seconds and
//   * delay(500) will stop the program for a half-second. Remember: the
//   * display window is updated only at the end of draw(), so putting more
//   * than one delay() inside draw() will simply add them together and the new
//   * frame will be drawn when the total delay is over.
//   * <br/> <br/>
//   * I'm not sure if this is even helpful anymore, as the screen isn't
//   * updated before or after the delay, meaning which means it just
//   * makes the app lock up temporarily.
//   */
//  public void delay(int napTime);
//
//  /**
//   * Set a target frameRate. This will cause delay() to be called
//   * after each frame so that the sketch synchronizes to a particular speed.
//   * Note that this only sets the maximum frame rate, it cannot be used to
//   * make a slow sketch go faster. Sketches have no default frame rate
//   * setting, and will attempt to use maximum processor power to achieve
//   * maximum speed.
//   */
//  public void frameRate(float newRateTarget);
//
//  /**
//     * Show status in the status bar of a web browser, or in the
//     * System.out console. Eventually this might show status in the
//     * p5 environment itself, rather than relying on the console.
//     */
//  //  public void status(String what) {
//  //    if (online) {
//  //      showStatus(what);
//  //
//  //    } else {
//  //      System.out.println(what);  // something more interesting?
//  //    }
//  //  }
//
//  public void link(String here);
//
//  /**
//   * Link to an external page without all the muss.
//   * <P>
//   * When run with an applet, uses the browser to open the url,
//   * for applications, attempts to launch a browser with the url.
//   * <P>
//   * Works on Mac OS X and Windows. For Linux, use:
//   * <PRE>open(new String[] { "firefox", url });</PRE>
//   * or whatever you want as your browser, since Linux doesn't
//   * yet have a standard method for launching URLs.
//   */
//  public void link(String url, String frameTitle);
//
//  /**
//   * Function for an applet/application to kill itself and
//   * display an error. Mostly this is here to be improved later.
//   */
//  public void die(String what);
//
//  /**
//   * Same as above but with an exception. Also needs work.
//   */
//  public void die(String what, Exception e);
//
//  /**
//   * Call to safely exit the sketch when finished. For instance,
//   * to render a single frame, save it, and quit.
//   */
//  public void exit();
//
//  /**
//   * Called to dispose of resources and shut down the sketch.
//   * Destroys the thread, dispose the renderer,and notify listeners.
//   * <p>
//   * Not to be called or overriden by users. If called multiple times,
//   * will only notify listeners once. Register a dispose listener instead.
//   */
//  public void dispose();
//
//  public void method(String name);
//
//  public void thread(final String name);
//
//  /**
//   * Intercepts any relative paths to make them absolute (relative
//   * to the sketch folder) before passing to save() in PImage.
//   * (Changed in 0100)
//   */
//  public void save(String filename);
//
//  /**
//   * Grab an image of what's currently in the drawing area and save it
//   * as a .tif or .tga file.
//   * <P>
//   * Best used just before endDraw() at the end of your draw().
//   * This can only create .tif or .tga images, so if neither extension
//   * is specified it defaults to writing a tiff and adds a .tif suffix.
//   */
//  public void saveFrame();
//
//  /**
//   * Save the current frame as a .tif or .tga image.
//   * <P>
//   * The String passed in can contain a series of # signs
//   * that will be replaced with the screengrab number.
//   * <PRE>
//   * i.e. saveFrame("blah-####.tif");
//   *      // saves a numbered tiff image, replacing the
//   *      // #### signs with zeros and the frame number </PRE>
//   */
//  public void saveFrame(String what);
//
//  /**
//   * Return a random number in the range [0, howbig).
//   * <P>
//   * The number returned will range from zero up to
//   * (but not including) 'howbig'.
//   */
//  public float random(float howbig);
//
//  /**
//   * Return a random number in the range [howsmall, howbig).
//   * <P>
//   * The number returned will range from 'howsmall' up to
//   * (but not including 'howbig'.
//   * <P>
//   * If howsmall is >= howbig, howsmall will be returned,
//   * meaning that random(5, 5) will return 5 (useful)
//   * and random(7, 4) will return 7 (not useful.. better idea?)
//   */
//  public float random(float howsmall, float howbig);
//
//  public void randomSeed(long what);
//
//  /**
//   * Computes the Perlin noise function value at point x.
//   */
//  public float noise(float x);
//
//  /**
//   * Computes the Perlin noise function value at the point x, y.
//   */
//  public float noise(float x, float y);
//
//  /**
//   * Computes the Perlin noise function value at x, y, z.
//   */
//  public float noise(float x, float y, float z);
//
//  public void noiseDetail(int lod);
//
//  public void noiseDetail(int lod, float falloff);
//
//  public void noiseSeed(long what);
//
//  public PImage loadImage(String filename);
//
////  public PImage loadImage(String filename, int sampling);
////
////  public PImage loadImage(String filename, PTexture.Parameters params);
//
//  public PImage requestImage(String filename);
//
//  /**
//   * Load a geometry from a file as a PShape (either an SVG or OBJ file).
//   */
//  public PShape loadShape(String filename);
//
////  /**
////   * Load a geometry from a file as a PShape (either an SVG or OBJ file).
////   */
////  public PShape loadShape(String filename, int mode);
//
////  /**
////   * Creates an empty, static 3D shape, with space for nvert vertices.
////   */
////  public PShape3D createShape(int nvert, int kind);
////
////  /**
////   * Creates an empty 3D shape, with space for nvert vertices.
////   */
////  public PShape3D createShape(int nvert, int kind, int mode);
////
////  /**
////   * Tesselates a PShape into a static PShape3D (it cannot be modified during the drawing loop).
////   */
////  public PShape3D createShape(PShape shape);
////
////  /**
////   * Tesselates a PShape into a PShape3D with the desired drawing mode (STATID or DYNAMIC)..
////   */
////  public PShape3D createShape(PShape shape, int mode);
//
//  public PFont loadFont(String filename);
//
//  public PFont createFont(String name, float size);
//
//  public PFont createFont(String name, float size, boolean smooth);
//
//  /**
//   * Create a bitmap font on the fly from either a font name that's
//   * installed on the system, or from a .ttf or .otf that's inside
//   * the data folder of this sketch.
//   * <P/>
//   * Use 'null' for the charset if you want to dynamically create
//   * character bitmaps only as they're needed.
//   */
//  public PFont createFont(String name, float size, boolean smooth,
//                          char[] charset);
//
//  /**
//   * I want to read lines from a file. I have RSI from typing these
//   * eight lines of code so many times.
//   */
//  public BufferedReader createReader(String filename);
//
//  /**
//   * I want to print lines to a file. Why can't I?
//   */
//  public PrintWriter createWriter(String filename);
//
//  /**
//   * Simplified method to open a Java InputStream.
//   * <P>
//   * This method is useful if you want to use the facilities provided
//   * by PApplet to easily open things from the data folder or from a URL,
//   * but want an InputStream object so that you can use other Java
//   * methods to take more control of how the stream is read.
//   * <P>
//   * If the requested item doesn't exist, null is returned.
//   * (Prior to 0096, die() would be called, killing the applet)
//   * <P>
//   * For 0096+, the "data" folder is exported intact with subfolders,
//   * and openStream() properly handles subdirectories from the data folder
//   * <P>
//   * If not online, this will also check to see if the user is asking
//   * for a file whose name isn't properly capitalized. This helps prevent
//   * issues when a sketch is exported to the web, where case sensitivity
//   * matters, as opposed to Windows and the Mac OS default where
//   * case sensitivity is preserved but ignored.
//   * <P>
//   * It is strongly recommended that libraries use this method to open
//   * data files, so that the loading sequence is handled in the same way
//   * as functions like loadBytes(), loadImage(), etc.
//   * <P>
//   * The filename passed in can be:
//   * <UL>
//   * <LI>A URL, for instance openStream("http://processing.org/");
//   * <LI>A file in the sketch's data folder
//   * <LI>Another file to be opened locally (when running as an application)
//   * </UL>
//   */
//  public InputStream createInput(String filename);
//
//  /**
//   * Call createInput() without automatic gzip decompression.
//   */
//  public InputStream createInputRaw(String filename);
//
//  public byte[] loadBytes(String filename);
//
//  /**
//   * Load data from a file and shove it into a String array.
//   * <P>
//   * Exceptions are handled internally, when an error, occurs, an
//   * exception is printed to the console and 'null' is returned,
//   * but the program continues running. This is a tradeoff between
//   * 1) showing the user that there was a problem but 2) not requiring
//   * that all i/o code is contained in try/catch blocks, for the sake
//   * of new users (or people who are just trying to get things done
//   * in a "scripting" fashion. If you want to handle exceptions,
//   * use Java methods for I/O.
//   */
//  public String[] loadStrings(String filename);
//
//  /**
//   * Similar to createInput() (formerly openStream), this creates a Java
//   * OutputStream for a given filename or path. The file will be created in
//   * the sketch folder, or in the same folder as an exported application.
//   * <p/>
//   * If the path does not exist, intermediate folders will be created. If an
//   * exception occurs, it will be printed to the console, and null will be
//   * returned.
//   * <p/>
//   * Future releases may also add support for handling HTTP POST via this
//   * method (for better symmetry with createInput), however that's maybe a
//   * little too clever (and then we'd have to add the same features to the
//   * other file functions like createWriter). Who you callin' bloated?
//   */
//  public OutputStream createOutput(String filename);
//
//  /**
//   * Save the contents of a stream to a file in the sketch folder.
//   * This is basically saveBytes(blah, loadBytes()), but done
//   * more efficiently (and with less confusing syntax).
//   */
//  public boolean saveStream(String targetFilename, String sourceLocation);
//
//  /**
//   * Identical to the other saveStream(), but writes to a File
//   * object, for greater control over the file location.
//   * Note that unlike other api methods, this will not automatically
//   * compress or uncompress gzip files.
//   */
//  public boolean saveStream(File targetFile, String sourceLocation);
//
//  public boolean saveStream(String targetFilename, InputStream sourceStream);
//
//  /**
//   * Saves bytes to a file to inside the sketch folder.
//   * The filename can be a relative path, i.e. "poo/bytefun.txt"
//   * would save to a file named "bytefun.txt" to a subfolder
//   * called 'poo' inside the sketch folder. If the in-between
//   * subfolders don't exist, they'll be created.
//   */
//  public void saveBytes(String filename, byte buffer[]);
//
//  public void saveStrings(String filename, String strings[]);
//
//  /**
//   * Prepend the sketch folder path to the filename (or path) that is
//   * passed in. External libraries should use this function to save to
//   * the sketch folder.
//   * <p/>
//   * Note that when running as an applet inside a web browser,
//   * the sketchPath will be set to null, because security restrictions
//   * prevent applets from accessing that information.
//   * <p/>
//   * This will also cause an error if the sketch is not inited properly,
//   * meaning that init() was never called on the PApplet when hosted
//   * my some other main() or by other code. For proper use of init(),
//   * see the examples in the main description text for PApplet.
//   */
//  public String sketchPath(String where);
//
//  public File sketchFile(String where);
//
//  /**
//   * Returns a path inside the applet folder to save to. Like sketchPath(),
//   * but creates any in-between folders so that things save properly.
//   * <p/>
//   * All saveXxxx() functions use the path to the sketch folder, rather than
//   * its data folder. Once exported, the data folder will be found inside the
//   * jar file of the exported application or applet. In this case, it's not
//   * possible to save data into the jar file, because it will often be running
//   * from a server, or marked in-use if running from a local file system.
//   * With this in mind, saving to the data path doesn't make sense anyway.
//   * If you know you're running locally, and want to save to the data folder,
//   * use <TT>saveXxxx("data/blah.dat")</TT>.
//   */
//  public String savePath(String where);
//
//  /**
//   * Identical to savePath(), but returns a File object.
//   */
//  public File saveFile(String where);
//
//  /**
//   * Return a full path to an item in the data folder.
//   * <p>
//   * In this method, the data path is defined not as the applet's actual
//   * data path, but a folder titled "data" in the sketch's working
//   * directory. When running inside the PDE, this will be the sketch's
//   * "data" folder. However, when exported (as application or applet),
//   * sketch's data folder is exported as part of the applications jar file,
//   * and it's not possible to read/write from the jar file in a generic way.
//   * If you need to read data from the jar file, you should use createInput().
//   */
//  public String dataPath(String where);
//
//  /**
//   * Return a full path to an item in the data folder as a File object.
//   * See the dataPath() method for more information.
//   */
//  public File dataFile(String where);
//
//  public int color(int gray);
//
//  public int color(float fgray);
//
//  /**
//   * As of 0116 this also takes color(#FF8800, alpha)
//   */
//  public int color(int gray, int alpha);
//
//  public int color(float fgray, float falpha);
//
//  public int color(int x, int y, int z);
//
//  public int color(float x, float y, float z);
//
//  public int color(int x, int y, int z, int a);
//
//  public int color(float x, float y, float z, float a);
//
//  /**
//   * Override the g.pixels[] function to set the pixels[] array
//   * that's part of the PApplet object. Allows the use of
//   * pixels[] in the code, rather than g.pixels[].
//   */
//  public void loadPixels();
//
//  public void updatePixels();
//
//  public void updatePixels(int x1, int y1, int x2, int y2);
//
//  public void flush();
//
//  public void hint(int which);
//
//  public void beginShape();
//
//  public void beginShape(int kind);
//
////  public PShape beginRecord();
////
////  public void beginShapesRecorder();
////
////  public void beginShapeRecorder();
////
////  public void beginShapeRecorder(int kind);
//
//  public void edge(boolean edge);
//
////  public void autoNormal(boolean auto);
//
//  public void normal(float nx, float ny, float nz);
//
//  public void textureMode(int mode);
//
//  public void texture(PImage image);
//
////  public void noTexture();
//
//  public void vertex(float x, float y);
//
//  public void vertex(float x, float y, float z);
//
//  public void vertex(float[] v);
//
//  public void vertex(float x, float y, float u, float v);
//
//  public void vertex(float x, float y, float z, float u, float v);
//
//  public void breakShape();
//
//  public void endShape();
//
//  public void endShape(int mode);
//
//  public void endRecord();
//
////  public PShape3D endShapesRecorder();
////
////  public PShape3D endShapeRecorder();
////
////  public PShape3D endShapeRecorder(int mode);
//
//  public void bezierVertex(float x2, float y2, float x3, float y3, float x4,
//                           float y4);
//
//  public void bezierVertex(float x2, float y2, float z2, float x3, float y3,
//                           float z3, float x4, float y4, float z4);
//
//  public void curveVertex(float x, float y);
//
//  public void curveVertex(float x, float y, float z);
//
//  public void point(float x, float y);
//
//  public void point(float x, float y, float z);
//
//  public void line(float x1, float y1, float x2, float y2);
//
//  public void line(float x1, float y1, float z1, float x2, float y2, float z2);
//
//  public void triangle(float x1, float y1, float x2, float y2, float x3,
//                       float y3);
//
//  public void quad(float x1, float y1, float x2, float y2, float x3, float y3,
//                   float x4, float y4);
//
//  public void rectMode(int mode);
//
//  public void rect(float a, float b, float c, float d);
//
//  public void ellipseMode(int mode);
//
//  public void ellipse(float a, float b, float c, float d);
//
//  public void arc(float a, float b, float c, float d, float start, float stop);
//
//  public void box(float size);
//
//  public void box(float w, float h, float d);
//
//  public void sphereDetail(int res);
//
//  public void sphereDetail(int ures, int vres);
//
//  public void sphere(float r);
//
//  public float bezierPoint(float a, float b, float c, float d, float t);
//
//  public float bezierTangent(float a, float b, float c, float d, float t);
//
//  public void bezierDetail(int detail);
//
//  public void bezier(float x1, float y1, float x2, float y2, float x3,
//                     float y3, float x4, float y4);
//
//  public void bezier(float x1, float y1, float z1, float x2, float y2,
//                     float z2, float x3, float y3, float z3, float x4,
//                     float y4, float z4);
//
//  public float curvePoint(float a, float b, float c, float d, float t);
//
//  public float curveTangent(float a, float b, float c, float d, float t);
//
//  public void curveDetail(int detail);
//
//  public void curveTightness(float tightness);
//
//  public void curve(float x1, float y1, float x2, float y2, float x3, float y3,
//                    float x4, float y4);
//
//  public void curve(float x1, float y1, float z1, float x2, float y2, float z2,
//                    float x3, float y3, float z3, float x4, float y4, float z4);
//
//  public void smooth();
//
//  public void noSmooth();
//
//  public void imageMode(int mode);
//
//  public void image(PImage image, float x, float y);
//
//  public void image(PImage image, float x, float y, float c, float d);
//
//  public void image(PImage image, float a, float b, float c, float d, int u1,
//                    int v1, int u2, int v2);
//
//  public void shapeMode(int mode);
//
//  public void shape(PShape shape);
//
//  public void shape(PShape shape, float x, float y);
//
////  public void shape(PShape shape, float x, float y, float z);
//
//  public void shape(PShape shape, float x, float y, float c, float d);
//
////  public void shape(PShape shape, float x, float y, float z, float c, float d,
////                    float e);
//
//  public void textAlign(int align);
//
//  public void textAlign(int alignX, int alignY);
//
//  public float textAscent();
//
//  public float textDescent();
//
//  public void textFont(PFont which);
//
//  public void textFont(PFont which, float size);
//
//  public void textLeading(float leading);
//
//  public void textMode(int mode);
//
//  public void textSize(float size);
//
//  public float textWidth(char c);
//
//  public float textWidth(String str);
//
//  public void text(char c);
//
//  public void text(char c, float x, float y);
//
//  public void text(char c, float x, float y, float z);
//
//  public void text(String str);
//
//  public void text(String str, float x, float y);
//
//  public void text(String str, float x, float y, float z);
//
//  public void text(String str, float x1, float y1, float x2, float y2);
//
//  public void text(String s, float x1, float y1, float x2, float y2, float z);
//
//  public void text(int num, float x, float y);
//
//  public void text(int num, float x, float y, float z);
//
//  public void text(float num, float x, float y);
//
//  public void text(float num, float x, float y, float z);
//
//  public void pushMatrix();
//
//  public void popMatrix();
//
//  public void translate(float tx, float ty);
//
//  public void translate(float tx, float ty, float tz);
//
//  public void rotate(float angle);
//
//  public void rotateX(float angle);
//
//  public void rotateY(float angle);
//
//  public void rotateZ(float angle);
//
//  public void rotate(float angle, float vx, float vy, float vz);
//
//  public void scale(float s);
//
//  public void scale(float sx, float sy);
//
//  public void scale(float x, float y, float z);
//
//  public void shearX(float angle);
//
//  public void shearY(float angle);
//
//  public void resetMatrix();
//
//  public void applyMatrix(PMatrix source);
//
//  public void applyMatrix(PMatrix2D source);
//
//  public void applyMatrix(float n00, float n01, float n02, float n10,
//                          float n11, float n12);
//
//  public void applyMatrix(PMatrix3D source);
//
//  public void applyMatrix(float n00, float n01, float n02, float n03,
//                          float n10, float n11, float n12, float n13,
//                          float n20, float n21, float n22, float n23,
//                          float n30, float n31, float n32, float n33);
//
//  public PMatrix getMatrix();
//
//  public PMatrix2D getMatrix(PMatrix2D target);
//
//  public PMatrix3D getMatrix(PMatrix3D target);
//
//  public void setMatrix(PMatrix source);
//
//  public void setMatrix(PMatrix2D source);
//
//  public void setMatrix(PMatrix3D source);
//
//  public void printMatrix();
//
//  public void beginCamera();
//
//  public void endCamera();
//
//  public void camera();
//
//  public void camera(float eyeX, float eyeY, float eyeZ, float centerX,
//                     float centerY, float centerZ, float upX, float upY,
//                     float upZ);
//
//  public void printCamera();
//
//  public void ortho();
//
//  public void ortho(float left, float right, float bottom, float top);
//
//  public void ortho(float left, float right, float bottom, float top,
//                    float near, float far);
//
//  public void perspective();
//
//  public void perspective(float fovy, float aspect, float zNear, float zFar);
//
//  public void frustum(float left, float right, float bottom, float top,
//                      float near, float far);
//
//  public void printProjection();
//
//  public float screenX(float x, float y);
//
//  public float screenY(float x, float y);
//
//  public float screenX(float x, float y, float z);
//
//  public float screenY(float x, float y, float z);
//
//  public float screenZ(float x, float y, float z);
//
//  public float modelX(float x, float y, float z);
//
//  public float modelY(float x, float y, float z);
//
//  public float modelZ(float x, float y, float z);
//
//  public void pushStyle();
//
//  public void popStyle();
//
//  public void style(PStyle s);
//
//  public void strokeWeight(float weight);
//
//  public void strokeJoin(int join);
//
//  public void strokeCap(int cap);
//
//  public void noStroke();
//
//  public void stroke(int rgb);
//
//  public void stroke(int rgb, float alpha);
//
//  public void stroke(float gray);
//
//  public void stroke(float gray, float alpha);
//
//  public void stroke(float x, float y, float z);
//
//  public void stroke(float x, float y, float z, float a);
//
//  public void noTint();
//
//  public void tint(int rgb);
//
//  public void tint(int rgb, float alpha);
//
//  public void tint(float gray);
//
//  public void tint(float gray, float alpha);
//
//  public void tint(float x, float y, float z);
//
//  public void tint(float x, float y, float z, float a);
//
//  public void noFill();
//
//  public void fill(int rgb);
//
//  public void fill(int rgb, float alpha);
//
//  public void fill(float gray);
//
//  public void fill(float gray, float alpha);
//
//  public void fill(float x, float y, float z);
//
//  public void fill(float x, float y, float z, float a);
//
//  public void ambient(int rgb);
//
//  public void ambient(float gray);
//
//  public void ambient(float x, float y, float z);
//
//  public void specular(int rgb);
//
//  public void specular(float gray);
//
//  public void specular(float x, float y, float z);
//
//  public void shininess(float shine);
//
//  public void emissive(int rgb);
//
//  public void emissive(float gray);
//
//  public void emissive(float x, float y, float z);
//
//  public void lights();
//
//  public void noLights();
//
//  public void ambientLight(float red, float green, float blue);
//
//  public void ambientLight(float red, float green, float blue, float x,
//                           float y, float z);
//
//  public void directionalLight(float red, float green, float blue, float nx,
//                               float ny, float nz);
//
//  public void pointLight(float red, float green, float blue, float x, float y,
//                         float z);
//
//  public void spotLight(float red, float green, float blue, float x, float y,
//                        float z, float nx, float ny, float nz, float angle,
//                        float concentration);
//
//  public void lightFalloff(float constant, float linear, float quadratic);
//
//  public void lightSpecular(float x, float y, float z);
//
//  public void background(int rgb);
//
//  public void background(int rgb, float alpha);
//
//  public void background(float gray);
//
//  public void background(float gray, float alpha);
//
//  public void background(float x, float y, float z);
//
//  public void background(float x, float y, float z, float a);
//
//  public void background(PImage image);
//
//  public void colorMode(int mode);
//
//  public void colorMode(int mode, float max);
//
//  public void colorMode(int mode, float maxX, float maxY, float maxZ);
//
//  public void colorMode(int mode, float maxX, float maxY, float maxZ, float maxA);
//
//  public float alpha(int what);
//
//  public float red(int what);
//
//  public float green(int what);
//
//  public float blue(int what);
//
//  public float hue(int what);
//
//  public float saturation(int what);
//
//  public float brightness(int what);
//
//  public int lerpColor(int c1, int c2, float amt);
//
//  public boolean displayable();
//
//  public void setCache(Object parent, Object storage);
//
//  public Object getCache(Object parent);
//
//  public void removeCache(Object parent);
//
//  public int get(int x, int y);
//
//  public PImage get(int x, int y, int w, int h);
//
//  public PImage get();
//
//  public void set(int x, int y, int c);
//
//  public void set(int x, int y, PImage src);
//
//  public void mask(int alpha[]);
//
//  public void mask(PImage alpha);
//
//  public void filter(int kind);
//
//  public void filter(int kind, float param);
//
//  public void copy(int sx, int sy, int sw, int sh, int dx, int dy, int dw,
//                   int dh);
//
//  public void copy(PImage src, int sx, int sy, int sw, int sh, int dx, int dy,
//                   int dw, int dh);
//
//  public void blend(int sx, int sy, int sw, int sh, int dx, int dy, int dw,
//                    int dh, int mode);
//
//  public void blend(PImage src, int sx, int sy, int sw, int sh, int dx, int dy,
//                    int dw, int dh, int mode);
//
////  public void beginProjection();
////
////  public void endProjection();
////
////  public void blend(int mode);
//
////  public void noBlend();
//
////  public void textureBlend(int mode);
//
////  public void noTextureBlend();
//
////  public void texture(PImage image0, PImage image1);
////
////  public void texture(PImage image0, PImage image1, PImage image2);
////
////  public void texture(PImage image0, PImage image1, PImage image2, PImage image3);
////
////  public void texture(PImage[] images);
////
////  public void vertex(float x, float y, float u0, float v0, float u1, float v1);
////
////  public void vertex(float x, float y, float u0, float v0, float u1, float v1,
////                     float u2, float v2);
////
////  public void vertex(float x, float y, float u0, float v0, float u1, float v1,
////                     float u2, float v2, float u3, float v3);
////
////  public void vertex(float x, float y, float[] u, float[] v);
////
////  public void vertex(float x, float y, float z, float u0, float v0, float u1,
////                     float v1);
////
////  public void vertex(float x, float y, float z, float u0, float v0, float u1,
////                     float v1, float u2, float v2);
////
////  public void vertex(float x, float y, float z, float u0, float v0, float u1,
////                     float v1, float u2, float v2, float u3, float v3);
////
////  public void vertex(float x, float y, float z, float[] u, float[] v);
//	
	//////////////////////////// ANDROID
	
  
  ////////////////////////////// DESKTOP //TODO
  

//public void init();

//public int sketchWidth();
//
//public int sketchHeight();
//
//public String sketchRenderer();

/**
 * Called by the browser or applet viewer to inform this applet that it
 * should start its execution. It is called after the init method and
 * each time the applet is revisited in a Web page.
 * <p/>
 * Called explicitly via the first call to PApplet.paint(), because
 * PAppletGL needs to have a usable screen before getting things rolling.
 */
public void start();

/**
 * Called by the browser or applet viewer to inform
 * this applet that it should stop its execution.
 * <p/>
 * Unfortunately, there are no guarantees from the Java spec
 * when or if stop() will be called (i.e. on browser quit,
 * or when moving between web pages), and it's not always called.
 */
public void stop();

/**
 * Called by the browser or applet viewer to inform this applet
 * that it is being reclaimed and that it should destroy
 * any resources that it has allocated.
 * <p/>
 * destroy() supposedly gets called as the applet viewer
 * is shutting down the applet. stop() is called
 * first, and then destroy() to really get rid of things.
 * no guarantees on when they're run (on browser quit, or
 * when moving between pages), though.
 */
public void destroy();

//public void registerSize(Object o);
//
//public void registerPre(Object o);
//
//public void registerDraw(Object o);
//
//public void registerPost(Object o);
//
//public void registerMouseEvent(Object o);
//
//public void registerKeyEvent(Object o);
//
//public void registerDispose(Object o);
//
//public void unregisterSize(Object o);
//
//public void unregisterPre(Object o);
//
//public void unregisterDraw(Object o);
//
//public void unregisterPost(Object o);
//
//public void unregisterMouseEvent(Object o);
//
//public void unregisterKeyEvent(Object o);
//
//public void unregisterDispose(Object o);

public void setup();

public void draw();

/**
 * Defines the dimension of the display window in units of pixels. The <b>size()</b> function <em>must</em> be the first line in <b>setup()</b>. If <b>size()</b> is not called, the default size of the window is 100x100 pixels. The system variables <b>width</b> and <b>height</b> are set by the parameters passed to the <b>size()</b> function. <br><br>
 * Do not use variables as the parameters to <b>size()</b> command, because it will cause problems when exporting your sketch. When variables are used, the dimensions of your sketch cannot be determined during export. Instead, employ numeric values in the <b>size()</b> statement, and then use the built-in <b>width</b> and <b>height</b> variables inside your program when you need the dimensions of the display window are needed. <br><br>
 * The MODE parameters selects which rendering engine to use. For example, if you will be drawing 3D shapes for the web use <b>P3D</b>, if you want to export a program with OpenGL graphics acceleration use <b>OPENGL</b>. A brief description of the four primary renderers follows:<br><br><b>JAVA2D</b> - The default renderer. This renderer supports two dimensional drawing and provides higher image quality in overall, but generally slower than P2D.<br><br><b>P2D</b> (Processing 2D) - Fast 2D renderer, best used with pixel data, but not as accurate as the JAVA2D default. <br><br><b>P3D</b> (Processing 3D) - Fast 3D renderer for the web. Sacrifices rendering quality for quick 3D drawing.<br><br><b>OPENGL</b> - High speed 3D graphics renderer that makes use of OpenGL-compatible graphics hardware is available. Keep in mind that OpenGL is not magic pixie dust that makes any sketch faster (though it's close), so other rendering options may produce better results depending on the nature of your code. Also note that with OpenGL, all graphics are smoothed: the smooth() and noSmooth() commands are ignored. <br><br><b>PDF</b> - The PDF renderer draws 2D graphics directly to an Acrobat PDF file. This produces excellent results when you need vector shapes for high resolution output or printing. You must first use Import Library &rarr; PDF to make use of the library. More information can be found in the PDF library reference.
 * If you're manipulating pixels (using methods like get() or blend(), or manipulating the pixels[] array), P2D and P3D will usually be faster than the default (JAVA2D) setting, and often the OPENGL setting as well. Similarly, when handling lots of images, or doing video playback, P2D and P3D will tend to be faster.<br><br>
 * The P2D, P3D, and OPENGL renderers do not support strokeCap() or strokeJoin(), which can lead to ugly results when using strokeWeight(). (<a href="http://dev.processing.org/bugs/show_bug.cgi?id=955">Bug 955</a>) <br><br>
 * For the most elegant and accurate results when drawing in 2D, particularly when using smooth(), use the JAVA2D renderer setting. It may be slower than the others, but is the most complete, which is why it's the default. Advanced users will want to switch to other renderers as they learn the tradeoffs. <br><br>
 * Rendering graphics requires tradeoffs between speed, accuracy, and general usefulness of the available features. None of the renderers are perfect, so we provide multiple options so that you can decide what tradeoffs make the most sense for your project. We'd prefer all of them to have perfect visual accuracy, high performance, and support a wide range of features, but that's simply not possible. <br><br>
 * The maximum width and height is limited by your operating system, and is usually the width and height of your actual screen. On some machines it may simply be the number of pixels on your current screen, meaning that a screen that's 800x600 could support size(1600, 300), since it's the same number of pixels. This varies widely so you'll have to try different rendering modes and sizes until you get what you're looking for. If you need something larger, use <b>createGraphics</b> to create a non-visible drawing surface.
 * <br><br>Again, the size() method must be the first line of the code (or first item inside setup). Any code that appears before the size() command may run more than once, which can lead to confusing results.
 *
 * =advanced
 * Starts up and creates a two-dimensional drawing surface,
 * or resizes the current drawing surface.
 * <P>
 * This should be the first thing called inside of setup().
 * <P>
 * If using Java 1.3 or later, this will default to using
 * PGraphics2, the Java2D-based renderer. If using Java 1.1,
 * or if PGraphics2 is not available, then PGraphics will be used.
 * To set your own renderer, use the other version of the size()
 * method that takes a renderer as its last parameter.
 * <P>
 * If called once a renderer has already been set, this will
 * use the previous renderer and simply resize it.
 *
 * @webref structure
 * @param iwidth width of the display window in units of pixels
 * @param iheight height of the display window in units of pixels
 */
public void size(int iwidth, int iheight);

/**
 *
 * @param irenderer   Either P2D, P3D, JAVA2D, or OPENGL
 */
public void size(int iwidth, int iheight, String irenderer);

/**
 * Creates a new PGraphics object and sets it to the specified size.
 *
 * Note that you cannot change the renderer once outside of setup().
 * In most cases, you can call size() to give it a new size,
 * but you need to always ask for the same renderer, otherwise
 * you're gonna run into trouble.
 *
 * The size() method should *only* be called from inside the setup() or
 * draw() methods, so that it is properly run on the main animation thread.
 * To change the size of a PApplet externally, use setSize(), which will
 * update the component size, and queue a resize of the renderer as well.
 */
public void size(final int iwidth, final int iheight, String irenderer,
                 String ipath);

/**
 * Creates and returns a new <b>PGraphics</b> object of the types P2D, P3D, and JAVA2D. Use this class if you need to draw into an off-screen graphics buffer. It's not possible to use <b>createGraphics()</b> with OPENGL, because it doesn't allow offscreen use. The DXF and PDF renderers require the filename parameter.
 * <br><br>It's important to call any drawing commands between beginDraw() and endDraw() statements. This is also true for any commands that affect drawing, such as smooth() or colorMode().
 * <br><br>Unlike the main drawing surface which is completely opaque, surfaces created with createGraphics() can have transparency. This makes it possible to draw into a graphics and maintain the alpha channel. By using save() to write a PNG or TGA file, the transparency of the graphics object will be honored. Note that transparency levels are binary: pixels are either complete opaque or transparent. For the time being (as of release 0127), this means that text characters will be opaque blocks. This will be fixed in a future release (<a href="http://dev.processing.org/bugs/show_bug.cgi?id=641">Bug 641</a>).
 *
 * =advanced
 * Create an offscreen PGraphics object for drawing. This can be used
 * for bitmap or vector images drawing or rendering.
 * <UL>
 * <LI>Do not use "new PGraphicsXxxx()", use this method. This method
 * ensures that internal variables are set up properly that tie the
 * new graphics context back to its parent PApplet.
 * <LI>The basic way to create bitmap images is to use the <A
 * HREF="http://processing.org/reference/saveFrame_.html">saveFrame()</A>
 * function.
 * <LI>If you want to create a really large scene and write that,
 * first make sure that you've allocated a lot of memory in the Preferences.
 * <LI>If you want to create images that are larger than the screen,
 * you should create your own PGraphics object, draw to that, and use
 * <A HREF="http://processing.org/reference/save_.html">save()</A>.
 * For now, it's best to use <A HREF="http://dev.processing.org/reference/everything/javadoc/processing/core/PGraphics3D.html">P3D</A> in this scenario.
 * P2D is currently disabled, and the JAVA2D default will give mixed
 * results. An example of using P3D:
 * <PRE>
 *
 * PGraphics big;
 *
 * void setup() {
 *   big = createGraphics(3000, 3000, P3D);
 *
 *   big.beginDraw();
 *   big.background(128);
 *   big.line(20, 1800, 1800, 900);
 *   // etc..
 *   big.endDraw();
 *
 *   // make sure the file is written to the sketch folder
 *   big.save("big.tif");
 * }
 *
 * </PRE>
 * <LI>It's important to always wrap drawing to createGraphics() with
 * beginDraw() and endDraw() (beginFrame() and endFrame() prior to
 * revision 0115). The reason is that the renderer needs to know when
 * drawing has stopped, so that it can update itself internally.
 * This also handles calling the defaults() method, for people familiar
 * with that.
 * <LI>It's not possible to use createGraphics() with the OPENGL renderer,
 * because it doesn't allow offscreen use.
 * <LI>With Processing 0115 and later, it's possible to write images in
 * formats other than the default .tga and .tiff. The exact formats and
 * background information can be found in the developer's reference for
 * <A HREF="http://dev.processing.org/reference/core/javadoc/processing/core/PImage.html#save(java.lang.String)">PImage.save()</A>.
 * </UL>
 *
 * @webref rendering
 * @param iwidth width in pixels
 * @param iheight height in pixels
 * @param irenderer Either P2D (not yet implemented), P3D, JAVA2D, PDF, DXF
 *
 * @see processing.core.PGraphics
 *
 */
public PGraphics createGraphics(int iwidth, int iheight, String irenderer);

///**
// * Create an offscreen graphics surface for drawing, in this case
// * for a renderer that writes to a file (such as PDF or DXF).
// * @param ipath the name of the file (can be an absolute or relative path)
// */
//public PGraphics createGraphics(int iwidth, int iheight, String irenderer,
//                                String ipath);

/**
 * Creates a new PImage (the datatype for storing images). This provides a fresh buffer of pixels to play with. Set the size of the buffer with the <b>width</b> and <b>height</b> parameters. The <b>format</b> parameter defines how the pixels are stored. See the PImage reference for more information.
 * <br><br>Be sure to include all three parameters, specifying only the width and height (but no format) will produce a strange error.
 * <br><br>Advanced users please note that createImage() should be used instead of the syntax <tt>new PImage()</tt>.
 * =advanced
 * Preferred method of creating new PImage objects, ensures that a
 * reference to the parent PApplet is included, which makes save() work
 * without needing an absolute path.
 *
 * @webref image
 * @param wide width in pixels
 * @param high height in pixels
 * @param format Either RGB, ARGB, ALPHA (grayscale alpha channel)
 *
 * @see processing.core.PImage
 * @see processing.core.PGraphics
 */
public PImage createImage(int wide, int high, int format);

//public void update(Graphics screen);

//synchronized public void paint(Graphics screen) {  // shutting off for 0146
//public void paint(Graphics screen);

/**
 * Main method for the primary animation thread.
 *
 * <A HREF="http://java.sun.com/products/jfc/tsc/articles/painting/">Painting in AWT and Swing</A>
 */
public void run();

//synchronized public void handleDisplay() {
public void handleDraw();

public void redraw();

public void loop();

public void noLoop();

//public void addListeners();

///**
// * If you override this or any function that takes a "MouseEvent e"
// * without calling its super.mouseXxxx() then mouseX, mouseY,
// * mousePressed, and mouseEvent will no longer be set.
// */
//public void mousePressed(MouseEvent e);
//
//public void mouseReleased(MouseEvent e);
//
//public void mouseClicked(MouseEvent e);
//
//public void mouseEntered(MouseEvent e);
//
//public void mouseExited(MouseEvent e);
//
//public void mouseDragged(MouseEvent e);
//
//public void mouseMoved(MouseEvent e);
//
///**
// * The <b>mousePressed()</b> function is called once after every time a mouse button is pressed. The <b>mouseButton</b> variable (see the related reference entry) can be used to determine which button has been pressed.
// * =advanced
// *
// * If you must, use
// * int button = mouseEvent.getButton();
// * to figure out which button was clicked. It will be one of:
// * MouseEvent.BUTTON1, MouseEvent.BUTTON2, MouseEvent.BUTTON3
// * Note, however, that this is completely inconsistent across
// * platforms.
// * @webref input:mouse
// * @see PApplet#mouseX
// * @see PApplet#mouseY
// * @see PApplet#mousePressed
// * @see PApplet#mouseReleased()
// * @see PApplet#mouseMoved()
// * @see PApplet#mouseDragged()
// */
//public void mousePressed();
//
///**
// * The <b>mouseReleased()</b> function is called every time a mouse button is released.
// * @webref input:mouse
// * @see PApplet#mouseX
// * @see PApplet#mouseY
// * @see PApplet#mousePressed
// * @see PApplet#mousePressed()
// * @see PApplet#mouseMoved()
// * @see PApplet#mouseDragged()
// */
//public void mouseReleased();
//
///**
// * The <b>mouseClicked()</b> function is called once after a mouse button has been pressed and then released.
// * =advanced
// * When the mouse is clicked, mousePressed() will be called,
// * then mouseReleased(), then mouseClicked(). Note that
// * mousePressed is already false inside of mouseClicked().
// * @webref input:mouse
// * @see PApplet#mouseX
// * @see PApplet#mouseY
// * @see PApplet#mouseButton
// * @see PApplet#mousePressed()
// * @see PApplet#mouseReleased()
// * @see PApplet#mouseMoved()
// * @see PApplet#mouseDragged()
// */
//public void mouseClicked();
//
///**
// * The <b>mouseDragged()</b> function is called once every time the mouse moves and a mouse button is pressed.
// * @webref input:mouse
// * @see PApplet#mouseX
// * @see PApplet#mouseY
// * @see PApplet#mousePressed
// * @see PApplet#mousePressed()
// * @see PApplet#mouseReleased()
// * @see PApplet#mouseMoved()
// */
//public void mouseDragged();
//
///**
// * The <b>mouseMoved()</b> function is called every time the mouse moves and a mouse button is not pressed.
// * @webref input:mouse
// * @see PApplet#mouseX
// * @see PApplet#mouseY
// * @see PApplet#mousePressed
// * @see PApplet#mousePressed()
// * @see PApplet#mouseReleased()
// * @see PApplet#mouseDragged()
// */
//public void mouseMoved();

///**
// * Overriding keyXxxxx(KeyEvent e) functions will cause the 'key',
// * 'keyCode', and 'keyEvent' variables to no longer work;
// * key events will no longer be queued until the end of draw();
// * and the keyPressed(), keyReleased() and keyTyped() methods
// * will no longer be called.
// */
//public void keyPressed(KeyEvent e);
//
//public void keyReleased(KeyEvent e);
//
//public void keyTyped(KeyEvent e);

/**
 *
 * The <b>keyPressed()</b> function is called once every time a key is pressed. The key that was pressed is stored in the <b>key</b> variable.
 * <br><br>For non-ASCII keys, use the <b>keyCode</b> variable.
 * The keys included in the ASCII specification (BACKSPACE, TAB, ENTER, RETURN, ESC, and DELETE) do not require checking to see if they key is coded, and you should simply use the <b>key</b> variable instead of <b>keyCode</b>
 * If you're making cross-platform projects, note that the ENTER key is commonly used on PCs and Unix and the RETURN key is used instead on Macintosh.
 * Check for both ENTER and RETURN to make sure your program will work for all platforms.<br><br>Because of how operating systems handle key repeats, holding down a key may cause multiple calls to keyPressed() (and keyReleased() as well).
 * The rate of repeat is set by the operating system and how each computer is configured.
 * =advanced
 *
 * Called each time a single key on the keyboard is pressed.
 * Because of how operating systems handle key repeats, holding
 * down a key will cause multiple calls to keyPressed(), because
 * the OS repeat takes over.
 * <P>
 * Examples for key handling:
 * (Tested on Windows XP, please notify if different on other
 * platforms, I have a feeling Mac OS and Linux may do otherwise)
 * <PRE>
 * 1. Pressing 'a' on the keyboard:
 *    keyPressed  with key == 'a' and keyCode == 'A'
 *    keyTyped    with key == 'a' and keyCode ==  0
 *    keyReleased with key == 'a' and keyCode == 'A'
 *
 * 2. Pressing 'A' on the keyboard:
 *    keyPressed  with key == 'A' and keyCode == 'A'
 *    keyTyped    with key == 'A' and keyCode ==  0
 *    keyReleased with key == 'A' and keyCode == 'A'
 *
 * 3. Pressing 'shift', then 'a' on the keyboard (caps lock is off):
 *    keyPressed  with key == CODED and keyCode == SHIFT
 *    keyPressed  with key == 'A'   and keyCode == 'A'
 *    keyTyped    with key == 'A'   and keyCode == 0
 *    keyReleased with key == 'A'   and keyCode == 'A'
 *    keyReleased with key == CODED and keyCode == SHIFT
 *
 * 4. Holding down the 'a' key.
 *    The following will happen several times,
 *    depending on your machine's "key repeat rate" settings:
 *    keyPressed  with key == 'a' and keyCode == 'A'
 *    keyTyped    with key == 'a' and keyCode ==  0
 *    When you finally let go, you'll get:
 *    keyReleased with key == 'a' and keyCode == 'A'
 *
 * 5. Pressing and releasing the 'shift' key
 *    keyPressed  with key == CODED and keyCode == SHIFT
 *    keyReleased with key == CODED and keyCode == SHIFT
 *    (note there is no keyTyped)
 *
 * 6. Pressing the tab key in an applet with Java 1.4 will
 *    normally do nothing, but PApplet dynamically shuts
 *    this behavior off if Java 1.4 is in use (tested 1.4.2_05 Windows).
 *    Java 1.1 (Microsoft VM) passes the TAB key through normally.
 *    Not tested on other platforms or for 1.3.
 * </PRE>
 * @see PApplet#key
 * @see PApplet#keyCode
 * @see PApplet#keyPressed
 * @see PApplet#keyReleased()
 * @webref input:keyboard
 */
public void keyPressed();

/**
 * The <b>keyReleased()</b> function is called once every time a key is released. The key that was released will be stored in the <b>key</b> variable. See <b>key</b> and <b>keyReleased</b> for more information.
 *
 * @see PApplet#key
 * @see PApplet#keyCode
 * @see PApplet#keyPressed
 * @see PApplet#keyPressed()
 * @webref input:keyboard
 */
public void keyReleased();

/**
 * Only called for "regular" keys like letters,
 * see keyPressed() for full documentation.
 */
public void keyTyped();

//public void focusGained();
//
//public void focusGained(FocusEvent e);
//
//public void focusLost();
//
//public void focusLost(FocusEvent e);

/**
 * Returns the number of milliseconds (thousandths of a second) since starting an applet. This information is often used for timing animation sequences.
 *
 * =advanced
 * <P>
 * This is a function, rather than a variable, because it may
 * change multiple times per frame.
 *
 * @webref input:time_date
 * @see processing.core.PApplet#second()
 * @see processing.core.PApplet#minute()
 * @see processing.core.PApplet#hour()
 * @see processing.core.PApplet#day()
 * @see processing.core.PApplet#month()
 * @see processing.core.PApplet#year()
 *
 */
public int millis();

/**
 * The delay() function causes the program to halt for a specified time.
 * Delay times are specified in thousandths of a second. For example,
 * running delay(3000) will stop the program for three seconds and
 * delay(500) will stop the program for a half-second. Remember: the
 * display window is updated only at the end of draw(), so putting more
 * than one delay() inside draw() will simply add them together and the new
 * frame will be drawn when the total delay is over.
 * <br/> <br/>
 * I'm not sure if this is even helpful anymore, as the screen isn't
 * updated before or after the delay, meaning which means it just
 * makes the app lock up temporarily.
 */
public void delay(int napTime);

/**
 * Specifies the number of frames to be displayed every second.
 * If the processor is not fast enough to maintain the specified rate, it will not be achieved.
 * For example, the function call <b>frameRate(30)</b> will attempt to refresh 30 times a second.
 * It is recommended to set the frame rate within <b>setup()</b>. The default rate is 60 frames per second.
 *  =advanced
 * Set a target frameRate. This will cause delay() to be called
 * after each frame so that the sketch synchronizes to a particular speed.
 * Note that this only sets the maximum frame rate, it cannot be used to
 * make a slow sketch go faster. Sketches have no default frame rate
 * setting, and will attempt to use maximum processor power to achieve
 * maximum speed.
 * @webref environment
 * @param newRateTarget number of frames per second
 * @see PApplet#delay(int)
 */
public void frameRate(float newRateTarget);

///**
// * Reads the value of a param.
// * Values are always read as a String so if you want them to be an integer or other datatype they must be converted.
// * The <b>param()</b> function will only work in a web browser.
// * The function should be called inside <b>setup()</b>,
// * otherwise the applet may not yet be initialized and connected to its parent web browser.
// *
// * @webref input:web
// * @usage Web
// *
// * @param what name of the param to read
// */
//public String param(String what);

///**
// * Displays message in the browser's status area. This is the text area in the lower left corner of the browser.
// * The <b>status()</b> function will only work when the Processing program is running in a web browser.
// * =advanced
// * Show status in the status bar of a web browser, or in the
// * System.out console. Eventually this might show status in the
// * p5 environment itself, rather than relying on the console.
// *
// * @webref input:web
// * @usage Web
// * @param what any valid String
// */
//public void status(String what);

public void link(String here);

/**
 * Links to a webpage either in the same window or in a new window. The complete URL must be specified.
 * =advanced
 * Link to an external page without all the muss.
 * <P>
 * When run with an applet, uses the browser to open the url,
 * for applications, attempts to launch a browser with the url.
 * <P>
 * Works on Mac OS X and Windows. For Linux, use:
 * <PRE>open(new String[] { "firefox", url });</PRE>
 * or whatever you want as your browser, since Linux doesn't
 * yet have a standard method for launching URLs.
 *
 * @webref input:web
 * @param url complete url as a String in quotes
 * @param frameTitle name of the window to load the URL as a string in quotes
 *
 */
public void link(String url, String frameTitle);

/**
 * Function for an applet/application to kill itself and
 * display an error. Mostly this is here to be improved later.
 */
public void die(String what);

/**
 * Same as above but with an exception. Also needs work.
 */
public void die(String what, Exception e);

/**
 * Call to safely exit the sketch when finished. For instance,
 * to render a single frame, save it, and quit.
 */
public void exit();

///**
// * Called to dispose of resources and shut down the sketch.
// * Destroys the thread, dispose the renderer,and notify listeners.
// * <p>
// * Not to be called or overriden by users. If called multiple times,
// * will only notify listeners once. Register a dispose listener instead.
// */
//public void dispose();

public void method(String name);

public void thread(final String name);

/**
 * Intercepts any relative paths to make them absolute (relative
 * to the sketch folder) before passing to save() in PImage.
 * (Changed in 0100)
 */
public void save(String filename);

/**
 * Grab an image of what's currently in the drawing area and save it
 * as a .tif or .tga file.
 * <P>
 * Best used just before endDraw() at the end of your draw().
 * This can only create .tif or .tga images, so if neither extension
 * is specified it defaults to writing a tiff and adds a .tif suffix.
 */
public void saveFrame();

/**
 * Save the current frame as a .tif or .tga image.
 * <P>
 * The String passed in can contain a series of # signs
 * that will be replaced with the screengrab number.
 * <PRE>
 * i.e. saveFrame("blah-####.tif");
 *      // saves a numbered tiff image, replacing the
 *      // #### signs with zeros and the frame number </PRE>
 */
public void saveFrame(String what);

///**
// * Set the cursor type
// * @param cursorType either ARROW, CROSS, HAND, MOVE, TEXT, WAIT
// */
//public void cursor(int cursorType);
//
///**
// * Replace the cursor with the specified PImage. The x- and y-
// * coordinate of the center will be the center of the image.
// */
//public void cursor(PImage image);
//
///**
// * Sets the cursor to a predefined symbol, an image, or turns it on if already hidden.
// * If you are trying to set an image as the cursor, it is recommended to make the size 16x16 or 32x32 pixels.
// * It is not possible to load an image as the cursor if you are exporting your program for the Web.
// * The values for parameters <b>x</b> and <b>y</b> must be less than the dimensions of the image.
// * =advanced
// * Set a custom cursor to an image with a specific hotspot.
// * Only works with JDK 1.2 and later.
// * Currently seems to be broken on Java 1.4 for Mac OS X
// * <P>
// * Based on code contributed by Amit Pitaru, plus additional
// * code to handle Java versions via reflection by Jonathan Feinberg.
// * Reflection removed for release 0128 and later.
// * @webref environment
// * @see       PApplet#noCursor()
// * @param image       any variable of type PImage
// * @param hotspotX    the horizonal active spot of the cursor
// * @param hotspotY    the vertical active spot of the cursor
// */
//public void cursor(PImage image, int hotspotX, int hotspotY);
//
///**
// * Show the cursor after noCursor() was called.
// * Notice that the program remembers the last set cursor type
// */
//public void cursor();
//
///**
// * Hides the cursor from view. Will not work when running the program in a web browser.
// * =advanced
// * Hide the cursor by creating a transparent image
// * and using it as a custom cursor.
// * @webref environment
// * @see PApplet#cursor()
// * @usage Application
// */
//public void noCursor();

/**
 * Return a random number in the range [0, howbig).
 * <P>
 * The number returned will range from zero up to
 * (but not including) 'howbig'.
 */
public float random(float howbig);

/**
 * Return a random number in the range [howsmall, howbig).
 * <P>
 * The number returned will range from 'howsmall' up to
 * (but not including 'howbig'.
 * <P>
 * If howsmall is >= howbig, howsmall will be returned,
 * meaning that random(5, 5) will return 5 (useful)
 * and random(7, 4) will return 7 (not useful.. better idea?)
 */
public float random(float howsmall, float howbig);

public void randomSeed(long what);

/**
 * Computes the Perlin noise function value at point x.
 */
public float noise(float x);

/**
 * Computes the Perlin noise function value at the point x, y.
 */
public float noise(float x, float y);

/**
 * Computes the Perlin noise function value at x, y, z.
 */
public float noise(float x, float y, float z);

public void noiseDetail(int lod);

public void noiseDetail(int lod, float falloff);

public void noiseSeed(long what);

/**
 * Load an image from the data folder or a local directory.
 * Supports .gif (including transparency), .tga, and .jpg images.
 * In Java 1.3 or later, .png images are
 * <A HREF="http://java.sun.com/j2se/1.3/docs/guide/2d/new_features.html">
 * also supported</A>.
 * <P>
 * Generally, loadImage() should only be used during setup, because
 * re-loading images inside draw() is likely to cause a significant
 * delay while memory is allocated and the thread blocks while waiting
 * for the image to load because loading is not asynchronous.
 * <P>
 * To load several images asynchronously, see more information in the
 * FAQ about writing your own threaded image loading method.
 * <P>
 * As of 0096, returns null if no image of that name is found,
 * rather than an error.
 * <P>
 * Release 0115 also provides support for reading TIFF and RLE-encoded
 * Targa (.tga) files written by Processing via save() and saveFrame().
 * Other TIFF and Targa files will probably not load, use a different
 * format (gif, jpg and png are safest bets) when creating images with
 * another application to use with Processing.
 * <P>
 * Also in release 0115, more image formats (BMP and others) can
 * be read when using Java 1.4 and later. Because many people still
 * use Java 1.1 and 1.3, these formats are not recommended for
 * work that will be posted on the web. To get a list of possible
 * image formats for use with Java 1.4 and later, use the following:
 * <TT>println(javax.imageio.ImageIO.getReaderFormatNames())</TT>
 * <P>
 * Images are loaded via a byte array that is passed to
 * Toolkit.createImage(). Unfortunately, we cannot use Applet.getImage()
 * because it takes a URL argument, which would be a pain in the a--
 * to make work consistently for online and local sketches.
 * Sometimes this causes problems, resulting in issues like
 * <A HREF="http://dev.processing.org/bugs/show_bug.cgi?id=279">Bug 279</A>
 * and
 * <A HREF="http://dev.processing.org/bugs/show_bug.cgi?id=305">Bug 305</A>.
 * In release 0115, everything was instead run through javax.imageio,
 * but that turned out to be very slow, see
 * <A HREF="http://dev.processing.org/bugs/show_bug.cgi?id=392">Bug 392</A>.
 * As a result, starting with 0116, the following happens:
 * <UL>
 * <LI>TGA and TIFF images are loaded using the internal load methods.
 * <LI>JPG, GIF, and PNG images are loaded via loadBytes().
 * <LI>If the image still isn't loaded, it's passed to javax.imageio.
 * </UL>
 * For releases 0116 and later, if you have problems such as those seen
 * in Bugs 279 and 305, use Applet.getImage() instead. You'll be stuck
 * with the limitations of getImage() (the headache of dealing with
 * online/offline use). Set up your own MediaTracker, and pass the resulting
 * java.awt.Image to the PImage constructor that takes an AWT image.
 */
public PImage loadImage(String filename);

///**
// * Loads an image into a variable of type <b>PImage</b>. Four types of images ( <b>.gif</b>, <b>.jpg</b>, <b>.tga</b>, <b>.png</b>) images may be loaded. To load correctly, images must be located in the data directory of the current sketch. In most cases, load all images in <b>setup()</b> to preload them at the start of the program. Loading images inside <b>draw()</b> will reduce the speed of a program.
// * <br><br>The <b>filename</b> parameter can also be a URL to a file found online. For security reasons, a Processing sketch found online can only download files from the same server from which it came. Getting around this restriction requires a <a href="http://processing.org/hacks/doku.php?id=hacks:signapplet">signed applet</a>.
// * <br><br>The <b>extension</b> parameter is used to determine the image type in cases where the image filename does not end with a proper extension. Specify the extension as the second parameter to <b>loadImage()</b>, as shown in the third example on this page.
// * <br><br>If an image is not loaded successfully, the <b>null</b> value is returned and an error message will be printed to the console. The error message does not halt the program, however the null value may cause a NullPointerException if your code does not check whether the value returned from <b>loadImage()</b> is null.<br><br>Depending on the type of error, a <b>PImage</b> object may still be returned, but the width and height of the image will be set to -1. This happens if bad image data is returned or cannot be decoded properly. Sometimes this happens with image URLs that produce a 403 error or that redirect to a password prompt, because <b>loadImage()</b> will attempt to interpret the HTML as image data.
// *
// * =advanced
// * Identical to loadImage, but allows you to specify the type of
// * image by its extension. Especially useful when downloading from
// * CGI scripts.
// * <br/> <br/>
// * Use 'unknown' as the extension to pass off to the default
// * image loader that handles gif, jpg, and png.
// *
// * @webref image:loading_displaying
// * @param filename name of file to load, can be .gif, .jpg, .tga, or a handful of other image types depending on your platform.
// * @param extension the type of image to load, for example "png", "gif", "jpg"
// *
// * @see processing.core.PImage
// * @see processing.core.PApplet#image(PImage, float, float, float, float)
// * @see processing.core.PApplet#imageMode(int)
// * @see processing.core.PApplet#background(float, float, float)
// */
//public PImage loadImage(String filename, String extension);

public PImage requestImage(String filename);

///**
// * This function load images on a separate thread so that your sketch does not freeze while images load during <b>setup()</b>. While the image is loading, its width and height will be 0. If an error occurs while loading the image, its width and height will be set to -1. You'll know when the image has loaded properly because its width and height will be greater than 0. Asynchronous image loading (particularly when downloading from a server) can dramatically improve performance.<br><br>
// * The <b>extension</b> parameter is used to determine the image type in cases where the image filename does not end with a proper extension. Specify the extension as the second parameter to <b>requestImage()</b>.
// *
// * @webref image:loading_displaying
// * @param filename name of file to load, can be .gif, .jpg, .tga, or a handful of other image types depending on your platform
// * @param extension the type of image to load, for example "png", "gif", "jpg"
// *
// * @see processing.core.PApplet#loadImage(String, String)
// * @see processing.core.PImage
// */
//public PImage requestImage(String filename, String extension);

/**
 * Loads vector shapes into a variable of type <b>PShape</b>. Currently, only SVG files may be loaded.
 * To load correctly, the file must be located in the data directory of the current sketch.
 * In most cases, <b>loadShape()</b> should be used inside <b>setup()</b> because loading shapes inside <b>draw()</b> will reduce the speed of a sketch.
 * <br><br>
 * The <b>filename</b> parameter can also be a URL to a file found online.
 * For security reasons, a Processing sketch found online can only download files from the same server from which it came.
 * Getting around this restriction requires a <a href="http://processing.org/hacks/doku.php?id=hacks:signapplet">signed applet</a>.
 * <br><br>
 * If a shape is not loaded successfully, the <b>null</b> value is returned and an error message will be printed to the console.
 * The error message does not halt the program, however the null value may cause a NullPointerException if your code does not check whether the value returned from <b>loadShape()</b> is null.
 *
 * @webref shape:loading_displaying
 * @see PShape
 * @see PApplet#shape(PShape)
 * @see PApplet#shapeMode(int)
 */
public PShape loadShape(String filename);

public PFont loadFont(String filename);

public PFont createFont(String name, float size);

public PFont createFont(String name, float size, boolean smooth);

/**
 * Create a .vlw font on the fly from either a font name that's
 * installed on the system, or from a .ttf or .otf that's inside
 * the data folder of this sketch.
 * <P/>
 * Many .otf fonts don't seem to be supported by Java, perhaps because
 * they're CFF based?
 * <P/>
 * Font names are inconsistent across platforms and Java versions.
 * On Mac OS X, Java 1.3 uses the font menu name of the font,
 * whereas Java 1.4 uses the PostScript name of the font. Java 1.4
 * on OS X will also accept the font menu name as well. On Windows,
 * it appears that only the menu names are used, no matter what
 * Java version is in use. Naming system unknown/untested for 1.5.
 * <P/>
 * Use 'null' for the charset if you want to dynamically create
 * character bitmaps only as they're needed. (Version 1.0.9 and
 * earlier would interpret null as all unicode characters.)
 */
public PFont createFont(String name, float size, boolean smooth,
                        char charset[]);

///**
// * Open a platform-specific file chooser dialog to select a file for input.
// * @return full path to the selected file, or null if no selection.
// */
//public String selectInput();
//
///**
// * Opens a platform-specific file chooser dialog to select a file for input. This function returns the full path to the selected file as a <b>String</b>, or <b>null</b> if no selection.
// *
// * @webref input:files
// * @param prompt message you want the user to see in the file chooser
// * @return full path to the selected file, or null if canceled.
// *
// * @see processing.core.PApplet#selectOutput(String)
// * @see processing.core.PApplet#selectFolder(String)
// */
//public String selectInput(String prompt);
//
///**
// * Open a platform-specific file save dialog to select a file for output.
// * @return full path to the file entered, or null if canceled.
// */
//public String selectOutput();
//
///**
// * Open a platform-specific file save dialog to create of select a file for output.
// * This function returns the full path to the selected file as a <b>String</b>, or <b>null</b> if no selection.
// * If you select an existing file, that file will be replaced.
// * Alternatively, you can navigate to a folder and create a new file to write to.
// *
// * @param prompt message you want the user to see in the file chooser
// * @return full path to the file entered, or null if canceled.
// *
// * @webref input:files
// * @see processing.core.PApplet#selectInput(String)
// * @see processing.core.PApplet#selectFolder(String)
// */
//public String selectOutput(String prompt);
//
//public String selectFolder();
//
///**
// * Opens a platform-specific file chooser dialog to select a folder for input.
// * This function returns the full path to the selected folder as a <b>String</b>, or <b>null</b> if no selection.
// *
// * @webref input:files
// * @param prompt message you want the user to see in the file chooser
// * @return full path to the selected folder, or null if no selection.
// *
// * @see processing.core.PApplet#selectOutput(String)
// * @see processing.core.PApplet#selectInput(String)
// */
//public String selectFolder(final String prompt);

/**
 * I want to read lines from a file. I have RSI from typing these
 * eight lines of code so many times.
 */
public BufferedReader createReader(String filename);

/**
 * I want to print lines to a file. Why can't I?
 */
public PrintWriter createWriter(String filename);

///**
// * @deprecated As of release 0136, use createInput() instead.
// */
//public InputStream openStream(String filename);

/**
 * This is a method for advanced programmers to open a Java InputStream. The method is useful if you want to use the facilities provided by PApplet to easily open files from the data folder or from a URL, but want an InputStream object so that you can use other Java methods to take more control of how the stream is read.
 * <br><br>If the requested item doesn't exist, null is returned.
 * <br><br>In earlier releases, this method was called <b>openStream()</b>.
 * <br><br>If not online, this will also check to see if the user is asking for a file whose name isn't properly capitalized. If capitalization is different an error will be printed to the console. This helps prevent issues that appear when a sketch is exported to the web, where case sensitivity matters, as opposed to running from inside the Processing Development Environment on Windows or Mac OS, where case sensitivity is preserved but ignored.
 * <br><br>The filename passed in can be:<br>
 * - A URL, for instance openStream("http://processing.org/");<br>
 * - A file in the sketch's data folder<br>
 * - The full path to a file to be opened locally (when running as an application)
 * <br><br>
 * If the file ends with <b>.gz</b>, the stream will automatically be gzip decompressed. If you don't want the automatic decompression, use the related function <b>createInputRaw()</b>.
 *
 * =advanced
 * Simplified method to open a Java InputStream.
 * <P>
 * This method is useful if you want to use the facilities provided
 * by PApplet to easily open things from the data folder or from a URL,
 * but want an InputStream object so that you can use other Java
 * methods to take more control of how the stream is read.
 * <P>
 * If the requested item doesn't exist, null is returned.
 * (Prior to 0096, die() would be called, killing the applet)
 * <P>
 * For 0096+, the "data" folder is exported intact with subfolders,
 * and openStream() properly handles subdirectories from the data folder
 * <P>
 * If not online, this will also check to see if the user is asking
 * for a file whose name isn't properly capitalized. This helps prevent
 * issues when a sketch is exported to the web, where case sensitivity
 * matters, as opposed to Windows and the Mac OS default where
 * case sensitivity is preserved but ignored.
 * <P>
 * It is strongly recommended that libraries use this method to open
 * data files, so that the loading sequence is handled in the same way
 * as functions like loadBytes(), loadImage(), etc.
 * <P>
 * The filename passed in can be:
 * <UL>
 * <LI>A URL, for instance openStream("http://processing.org/");
 * <LI>A file in the sketch's data folder
 * <LI>Another file to be opened locally (when running as an application)
 * </UL>
 *
 * @webref input:files
 * @see processing.core.PApplet#createOutput(String)
 * @see processing.core.PApplet#selectOutput(String)
 * @see processing.core.PApplet#selectInput(String)
 *
 * @param filename the name of the file to use as input
 *
 */
public InputStream createInput(String filename);

/**
 * Call openStream() without automatic gzip decompression.
 */
public InputStream createInputRaw(String filename);

/**
 * Reads the contents of a file or url and places it in a byte array. If a file is specified, it must be located in the sketch's "data" directory/folder.
 * <br><br>The filename parameter can also be a URL to a file found online. For security reasons, a Processing sketch found online can only download files from the same server from which it came. Getting around this restriction requires a <a href="http://java.sun.com/developer/onlineTraining/Programming/JDCBook/signed.html">signed applet</a>.
 *
 * @webref input:files
 * @param filename name of a file in the data folder or a URL.
 *
 * @see processing.core.PApplet#loadStrings(String)
 * @see processing.core.PApplet#saveStrings(String, String[])
 * @see processing.core.PApplet#saveBytes(String, byte[])
 *
 */
public byte[] loadBytes(String filename);

/**
 * Reads the contents of a file or url and creates a String array of its individual lines. If a file is specified, it must be located in the sketch's "data" directory/folder.
 * <br><br>The filename parameter can also be a URL to a file found online. For security reasons, a Processing sketch found online can only download files from the same server from which it came. Getting around this restriction requires a <a href="http://java.sun.com/developer/onlineTraining/Programming/JDCBook/signed.html">signed applet</a>.
 * <br><br>If the file is not available or an error occurs, <b>null</b> will be returned and an error message will be printed to the console. The error message does not halt the program, however the null value may cause a NullPointerException if your code does not check whether the value returned is null.
 * <br><br>Starting with Processing release 0134, all files loaded and saved by the Processing API use UTF-8 encoding. In previous releases, the default encoding for your platform was used, which causes problems when files are moved to other platforms.
 *
 * =advanced
 * Load data from a file and shove it into a String array.
 * <P>
 * Exceptions are handled internally, when an error, occurs, an
 * exception is printed to the console and 'null' is returned,
 * but the program continues running. This is a tradeoff between
 * 1) showing the user that there was a problem but 2) not requiring
 * that all i/o code is contained in try/catch blocks, for the sake
 * of new users (or people who are just trying to get things done
 * in a "scripting" fashion. If you want to handle exceptions,
 * use Java methods for I/O.
 *
 * @webref input:files
 * @param filename name of the file or url to load
 *
 * @see processing.core.PApplet#loadBytes(String)
 * @see processing.core.PApplet#saveStrings(String, String[])
 * @see processing.core.PApplet#saveBytes(String, byte[])
 */
public String[] loadStrings(String filename);

/**
 * Similar to createInput() (formerly openStream), this creates a Java
 * OutputStream for a given filename or path. The file will be created in
 * the sketch folder, or in the same folder as an exported application.
 * <p/>
 * If the path does not exist, intermediate folders will be created. If an
 * exception occurs, it will be printed to the console, and null will be
 * returned.
 * <p/>
 * Future releases may also add support for handling HTTP POST via this
 * method (for better symmetry with createInput), however that's maybe a
 * little too clever (and then we'd have to add the same features to the
 * other file functions like createWriter). Who you callin' bloated?
 */
public OutputStream createOutput(String filename);

/**
 * Save the contents of a stream to a file in the sketch folder.
 * This is basically saveBytes(blah, loadBytes()), but done
 * more efficiently (and with less confusing syntax).
 */
public boolean saveStream(String targetFilename, String sourceLocation);

/**
 * Identical to the other saveStream(), but writes to a File
 * object, for greater control over the file location.
 * <p/>
 * Note that unlike other api methods, this will not automatically
 * compress or uncompress gzip files.
 */
public boolean saveStream(File targetFile, String sourceLocation);

//public boolean saveStream(String targetFilename, InputStream sourceStream);

/**
 * Saves bytes to a file to inside the sketch folder.
 * The filename can be a relative path, i.e. "poo/bytefun.txt"
 * would save to a file named "bytefun.txt" to a subfolder
 * called 'poo' inside the sketch folder. If the in-between
 * subfolders don't exist, they'll be created.
 */
public void saveBytes(String filename, byte buffer[]);

public void saveStrings(String filename, String strings[]);

/**
 * Prepend the sketch folder path to the filename (or path) that is
 * passed in. External libraries should use this function to save to
 * the sketch folder.
 * <p/>
 * Note that when running as an applet inside a web browser,
 * the sketchPath will be set to null, because security restrictions
 * prevent applets from accessing that information.
 * <p/>
 * This will also cause an error if the sketch is not inited properly,
 * meaning that init() was never called on the PApplet when hosted
 * my some other main() or by other code. For proper use of init(),
 * see the examples in the main description text for PApplet.
 */
public String sketchPath(String where);

public File sketchFile(String where);

/**
 * Returns a path inside the applet folder to save to. Like sketchPath(),
 * but creates any in-between folders so that things save properly.
 * <p/>
 * All saveXxxx() functions use the path to the sketch folder, rather than
 * its data folder. Once exported, the data folder will be found inside the
 * jar file of the exported application or applet. In this case, it's not
 * possible to save data into the jar file, because it will often be running
 * from a server, or marked in-use if running from a local file system.
 * With this in mind, saving to the data path doesn't make sense anyway.
 * If you know you're running locally, and want to save to the data folder,
 * use <TT>saveXxxx("data/blah.dat")</TT>.
 */
public String savePath(String where);

/**
 * Identical to savePath(), but returns a File object.
 */
public File saveFile(String where);

/**
 * Return a full path to an item in the data folder.
 * <p>
 * In this method, the data path is defined not as the applet's actual
 * data path, but a folder titled "data" in the sketch's working
 * directory. When running inside the PDE, this will be the sketch's
 * "data" folder. However, when exported (as application or applet),
 * sketch's data folder is exported as part of the applications jar file,
 * and it's not possible to read/write from the jar file in a generic way.
 * If you need to read data from the jar file, you should use other methods
 * such as createInput(), createReader(), or loadStrings().
 */
public String dataPath(String where);

/**
 * Return a full path to an item in the data folder as a File object.
 * See the dataPath() method for more information.
 */
public File dataFile(String where);

public int color(int gray);

public int color(float fgray);

/**
 * As of 0116 this also takes color(#FF8800, alpha)
 *
 * @param gray number specifying value between white and black
 */
public int color(int gray, int alpha);

public int color(float fgray, float falpha);

public int color(int x, int y, int z);

public int color(float x, float y, float z);

public int color(int x, int y, int z, int a);

/**
 * Creates colors for storing in variables of the <b>color</b> datatype. The parameters are interpreted as RGB or HSB values depending on the current <b>colorMode()</b>. The default mode is RGB values from 0 to 255 and therefore, the function call <b>color(255, 204, 0)</b> will return a bright yellow color. More about how colors are stored can be found in the reference for the <a href="color_datatype.html">color</a> datatype.
 *
 * @webref color:creating_reading
 * @param x red or hue values relative to the current color range
 * @param y green or saturation values relative to the current color range
 * @param z blue or brightness values relative to the current color range
 * @param a alpha relative to current color range
 *
 * @see processing.core.PApplet#colorMode(int)
 * @ref color_datatype
 */
public int color(float x, float y, float z, float a);

///**
// * Set this sketch to communicate its state back to the PDE.
// * <p/>
// * This uses the stderr stream to write positions of the window
// * (so that it will be saved by the PDE for the next run) and
// * notify on quit. See more notes in the Worker class.
// */
//public void setupExternalMessages();

///**
// * Set up a listener that will fire proper component resize events
// * in cases where frame.setResizable(true) is called.
// */
//public void setupFrameResizeListener();

///**
// * Begin recording to a new renderer of the specified type, using the width
// * and height of the main drawing surface.
// */
//public PGraphics beginRecord(String renderer, String filename);
//
///**
// * Begin recording (echoing) commands to the specified PGraphics object.
// */
//public void beginRecord(PGraphics recorder);

	public void endRecord();

///**
// * Begin recording raw shape data to a renderer of the specified type,
// * using the width and height of the main drawing surface.
// *
// * If hashmarks (###) are found in the filename, they'll be replaced
// * by the current frame number (frameCount).
// */
//public PGraphics beginRaw(String renderer, String filename);
//
///**
// * Begin recording raw shape data to the specified renderer.
// *
// * This simply echoes to g.beginRaw(), but since is placed here (rather than
// * generated by preproc.pl) for clarity and so that it doesn't echo the
// * command should beginRecord() be in use.
// */
//public void beginRaw(PGraphics rawGraphics);
//
///**
// * Stop recording raw shape data to the specified renderer.
// *
// * This simply echoes to g.beginRaw(), but since is placed here (rather than
// * generated by preproc.pl) for clarity and so that it doesn't echo the
// * command should beginRecord() be in use.
// */
//public void endRaw();

/**
 * Loads the pixel data for the display window into the <b>pixels[]</b> array. This function must always be called before reading from or writing to <b>pixels[]</b>.
 * <br><br>Certain renderers may or may not seem to require <b>loadPixels()</b> or <b>updatePixels()</b>. However, the rule is that any time you want to manipulate the <b>pixels[]</b> array, you must first call <b>loadPixels()</b>, and after changes have been made, call <b>updatePixels()</b>. Even if the renderer may not seem to use this function in the current Processing release, this will always be subject to change.
 * =advanced
 * Override the g.pixels[] function to set the pixels[] array
 * that's part of the PApplet object. Allows the use of
 * pixels[] in the code, rather than g.pixels[].
 *
 * @webref image:pixels
 * @see processing.core.PApplet#pixels
 * @see processing.core.PApplet#updatePixels()
 */
public void loadPixels();

/**
 * Updates the display window with the data in the <b>pixels[]</b> array. Use in conjunction with <b>loadPixels()</b>. If you're only reading pixels from the array, there's no need to call <b>updatePixels()</b> unless there are changes.
 * <br><br>Certain renderers may or may not seem to require <b>loadPixels()</b> or <b>updatePixels()</b>. However, the rule is that any time you want to manipulate the <b>pixels[]</b> array, you must first call <b>loadPixels()</b>, and after changes have been made, call <b>updatePixels()</b>. Even if the renderer may not seem to use this function in the current Processing release, this will always be subject to change.
 * <br><br>Currently, none of the renderers use the additional parameters to <b>updatePixels()</b>, however this may be implemented in the future.
 *
 * @webref image:pixels
 *
 * @see processing.core.PApplet#loadPixels()
 * @see processing.core.PApplet#updatePixels()
 *
 */
public void updatePixels();

public void updatePixels(int x1, int y1, int x2, int y2);

public void flush();

/**
 * Set various hints and hacks for the renderer. This is used to handle obscure rendering features that cannot be implemented in a consistent manner across renderers. Many options will often graduate to standard features instead of hints over time.
 * <br><br>hint(ENABLE_OPENGL_4X_SMOOTH) - Enable 4x anti-aliasing for OpenGL. This can help force anti-aliasing if it has not been enabled by the user. On some graphics cards, this can also be set by the graphics driver's control panel, however not all cards make this available. This hint must be called immediately after the size() command because it resets the renderer, obliterating any settings and anything drawn (and like size(), re-running the code that came before it again).
 * <br><br>hint(DISABLE_OPENGL_2X_SMOOTH) - In Processing 1.0, Processing always enables 2x smoothing when the OpenGL renderer is used. This hint disables the default 2x smoothing and returns the smoothing behavior found in earlier releases, where smooth() and noSmooth() could be used to enable and disable smoothing, though the quality was inferior.
 * <br><br>hint(ENABLE_NATIVE_FONTS) - Use the native version fonts when they are installed, rather than the bitmapped version from a .vlw file. This is useful with the JAVA2D renderer setting, as it will improve font rendering speed. This is not enabled by default, because it can be misleading while testing because the type will look great on your machine (because you have the font installed) but lousy on others' machines if the identical font is unavailable. This option can only be set per-sketch, and must be called before any use of textFont().
 * <br><br>hint(DISABLE_DEPTH_TEST) - Disable the zbuffer, allowing you to draw on top of everything at will. When depth testing is disabled, items will be drawn to the screen sequentially, like a painting. This hint is most often used to draw in 3D, then draw in 2D on top of it (for instance, to draw GUI controls in 2D on top of a 3D interface). Starting in release 0149, this will also clear the depth buffer. Restore the default with hint(ENABLE_DEPTH_TEST), but note that with the depth buffer cleared, any 3D drawing that happens later in draw() will ignore existing shapes on the screen.
 * <br><br>hint(ENABLE_DEPTH_SORT) - Enable primitive z-sorting of triangles and lines in P3D and OPENGL. This can slow performance considerably, and the algorithm is not yet perfect. Restore the default with hint(DISABLE_DEPTH_SORT).
 * <br><br>hint(DISABLE_OPENGL_ERROR_REPORT) - Speeds up the OPENGL renderer setting by not checking for errors while running. Undo with hint(ENABLE_OPENGL_ERROR_REPORT).
 * <br><br><!--hint(ENABLE_ACCURATE_TEXTURES) - Enables better texture accuracy for the P3D renderer. This option will do a better job of dealing with textures in perspective. hint(DISABLE_ACCURATE_TEXTURES) returns to the default. This hint is not likely to last long.
 * <br/> <br/>-->As of release 0149, unhint() has been removed in favor of adding additional ENABLE/DISABLE constants to reset the default behavior. This prevents the double negatives, and also reinforces which hints can be enabled or disabled.
 *
 * @webref rendering
 * @param which name of the hint to be enabled or disabled
 *
 * @see processing.core.PGraphics
 * @see processing.core.PApplet#createGraphics(int, int, String, String)
 * @see processing.core.PApplet#size(int, int)
 */
public void hint(int which);

/**
 * Start a new shape of type POLYGON
 */
public void beginShape();

/**
 * Start a new shape.
 * <P>
 * <B>Differences between beginShape() and line() and point() methods.</B>
 * <P>
 * beginShape() is intended to be more flexible at the expense of being
 * a little more complicated to use. it handles more complicated shapes
 * that can consist of many connected lines (so you get joins) or lines
 * mixed with curves.
 * <P>
 * The line() and point() command are for the far more common cases
 * (particularly for our audience) that simply need to draw a line
 * or a point on the screen.
 * <P>
 * From the code side of things, line() may or may not call beginShape()
 * to do the drawing. In the beta code, they do, but in the alpha code,
 * they did not. they might be implemented one way or the other depending
 * on tradeoffs of runtime efficiency vs. implementation efficiency &mdash
 * meaning the speed that things run at vs. the speed it takes me to write
 * the code and maintain it. for beta, the latter is most important so
 * that's how things are implemented.
 */
public void beginShape(int kind);

/**
 * Sets whether the upcoming vertex is part of an edge.
 * Equivalent to glEdgeFlag(), for people familiar with OpenGL.
 */
public void edge(boolean edge);

/**
 * Sets the current normal vector. Only applies with 3D rendering
 * and inside a beginShape/endShape block.
 * <P/>
 * This is for drawing three dimensional shapes and surfaces,
 * allowing you to specify a vector perpendicular to the surface
 * of the shape, which determines how lighting affects it.
 * <P/>
 * For the most part, PGraphics3D will attempt to automatically
 * assign normals to shapes, but since that's imperfect,
 * this is a better option when you want more control.
 * <P/>
 * For people familiar with OpenGL, this function is basically
 * identical to glNormal3f().
 */
public void normal(float nx, float ny, float nz);

/**
 * Set texture mode to either to use coordinates based on the IMAGE
 * (more intuitive for new users) or NORMALIZED (better for advanced chaps)
 */
public void textureMode(int mode);

/**
 * Set texture image for current shape.
 * Needs to be called between @see beginShape and @see endShape
 *
 * @param image reference to a PImage object
 */
public void texture(PImage image);

public void vertex(float x, float y);

public void vertex(float x, float y, float z);

/**
 * Used by renderer subclasses or PShape to efficiently pass in already
 * formatted vertex information.
 * @param v vertex parameters, as a float array of length VERTEX_FIELD_COUNT
 */
public void vertex(float[] v);

public void vertex(float x, float y, float u, float v);

public void vertex(float x, float y, float z, float u, float v);

/** This feature is in testing, do not use or rely upon its implementation */
public void breakShape();

public void endShape();

public void endShape(int mode);

public void bezierVertex(float x2, float y2, float x3, float y3, float x4,
                         float y4);

public void bezierVertex(float x2, float y2, float z2, float x3, float y3,
                         float z3, float x4, float y4, float z4);

public void curveVertex(float x, float y);

public void curveVertex(float x, float y, float z);

public void point(float x, float y);

/**
 * Draws a point, a coordinate in space at the dimension of one pixel.
 * The first parameter is the horizontal value for the point, the second
 * value is the vertical value for the point, and the optional third value
 * is the depth value. Drawing this shape in 3D using the <b>z</b>
 * parameter requires the P3D or OPENGL parameter in combination with
 * size as shown in the above example.
 * <br><br>Due to what appears to be a bug in Apple's Java implementation,
 * the point() and set() methods are extremely slow in some circumstances
 * when used with the default renderer. Using P2D or P3D will fix the
 * problem. Grouping many calls to point() or set() together can also
 * help. (<a href="http://dev.processing.org/bugs/show_bug.cgi?id=1094">Bug 1094</a>)
 *
 * @webref shape:2d_primitives
 * @param x x-coordinate of the point
 * @param y y-coordinate of the point
 * @param z z-coordinate of the point
 *
 * @see PGraphics#beginShape()
 */
public void point(float x, float y, float z);

public void line(float x1, float y1, float x2, float y2);

/**
 * Draws a line (a direct path between two points) to the screen.
 * The version of <b>line()</b> with four parameters draws the line in 2D.
 * To color a line, use the <b>stroke()</b> function. A line cannot be
 * filled, therefore the <b>fill()</b> method will not affect the color
 * of a line. 2D lines are drawn with a width of one pixel by default,
 * but this can be changed with the <b>strokeWeight()</b> function.
 * The version with six parameters allows the line to be placed anywhere
 * within XYZ space. Drawing this shape in 3D using the <b>z</b> parameter
 * requires the P3D or OPENGL parameter in combination with size as shown
 * in the above example.
 *
 * @webref shape:2d_primitives
 * @param x1 x-coordinate of the first point
 * @param y1 y-coordinate of the first point
 * @param z1 z-coordinate of the first point
 * @param x2 x-coordinate of the second point
 * @param y2 y-coordinate of the second point
 * @param z2 z-coordinate of the second point
 *
 * @see PGraphics#strokeWeight(float)
 * @see PGraphics#strokeJoin(int)
 * @see PGraphics#strokeCap(int)
 * @see PGraphics#beginShape()
 */
public void line(float x1, float y1, float z1, float x2, float y2, float z2);

/**
 * A triangle is a plane created by connecting three points. The first two
 * arguments specify the first point, the middle two arguments specify
 * the second point, and the last two arguments specify the third point.
 *
 * @webref shape:2d_primitives
 * @param x1 x-coordinate of the first point
 * @param y1 y-coordinate of the first point
 * @param x2 x-coordinate of the second point
 * @param y2 y-coordinate of the second point
 * @param x3 x-coordinate of the third point
 * @param y3 y-coordinate of the third point
 *
 * @see PApplet#beginShape()
 */
public void triangle(float x1, float y1, float x2, float y2, float x3,
                     float y3);

/**
 * A quad is a quadrilateral, a four sided polygon. It is similar to
 * a rectangle, but the angles between its edges are not constrained
 * ninety degrees. The first pair of parameters (x1,y1) sets the
 * first vertex and the subsequent pairs should proceed clockwise or
 * counter-clockwise around the defined shape.
 *
 * @webref shape:2d_primitives
 * @param x1 x-coordinate of the first corner
 * @param y1 y-coordinate of the first corner
 * @param x2 x-coordinate of the second corner
 * @param y2 y-coordinate of the second corner
 * @param x3 x-coordinate of the third corner
 * @param y3 y-coordinate of the third corner
 * @param x4 x-coordinate of the fourth corner
 * @param y4 y-coordinate of the fourth corner
 *
 */
public void quad(float x1, float y1, float x2, float y2, float x3, float y3,
                 float x4, float y4);

public void rectMode(int mode);

/**
 * Draws a rectangle to the screen. A rectangle is a four-sided shape with
 * every angle at ninety degrees. The first two parameters set the location,
 * the third sets the width, and the fourth sets the height. The origin is
 * changed with the <b>rectMode()</b> function.
 *
 * @webref shape:2d_primitives
 * @param a x-coordinate of the rectangle
 * @param b y-coordinate of the rectangle
 * @param c width of the rectangle
 * @param d height of the rectangle
 *
 * @see PGraphics#rectMode(int)
 * @see PGraphics#quad(float, float, float, float, float, float, float, float)
 */
public void rect(float a, float b, float c, float d);

//public void rect(float a, float b, float c, float d, float hr, float vr);
//
//public void rect(float a, float b, float c, float d, float tl, float tr,
//                 float bl, float br);

/**
 * The origin of the ellipse is modified by the <b>ellipseMode()</b>
 * function. The default configuration is <b>ellipseMode(CENTER)</b>,
 * which specifies the location of the ellipse as the center of the shape.
 * The RADIUS mode is the same, but the width and height parameters to
 * <b>ellipse()</b> specify the radius of the ellipse, rather than the
 * diameter. The CORNER mode draws the shape from the upper-left corner
 * of its bounding box. The CORNERS mode uses the four parameters to
 * <b>ellipse()</b> to set two opposing corners of the ellipse's bounding
 * box. The parameter must be written in "ALL CAPS" because Processing
 * syntax is case sensitive.
 *
 * @webref shape:attributes
 *
 * @param mode        Either CENTER, RADIUS, CORNER, or CORNERS.
 * @see PApplet#ellipse(float, float, float, float)
 */
public void ellipseMode(int mode);

/**
 * Draws an ellipse (oval) in the display window. An ellipse with an equal
 * <b>width</b> and <b>height</b> is a circle. The first two parameters set
 * the location, the third sets the width, and the fourth sets the height.
 * The origin may be changed with the <b>ellipseMode()</b> function.
 *
 * @webref shape:2d_primitives
 * @param a x-coordinate of the ellipse
 * @param b y-coordinate of the ellipse
 * @param c width of the ellipse
 * @param d height of the ellipse
 *
 * @see PApplet#ellipseMode(int)
 */
public void ellipse(float a, float b, float c, float d);

/**
 * Draws an arc in the display window.
 * Arcs are drawn along the outer edge of an ellipse defined by the
 * <b>x</b>, <b>y</b>, <b>width</b> and <b>height</b> parameters.
 * The origin or the arc's ellipse may be changed with the
 * <b>ellipseMode()</b> function.
 * The <b>start</b> and <b>stop</b> parameters specify the angles
 * at which to draw the arc.
 *
 * @webref shape:2d_primitives
 * @param a x-coordinate of the arc's ellipse
 * @param b y-coordinate of the arc's ellipse
 * @param c width of the arc's ellipse
 * @param d height of the arc's ellipse
 * @param start angle to start the arc, specified in radians
 * @param stop angle to stop the arc, specified in radians
 *
 * @see PGraphics#ellipseMode(int)
 * @see PGraphics#ellipse(float, float, float, float)
 */
public void arc(float a, float b, float c, float d, float start, float stop);

/**
 * @param size dimension of the box in all dimensions, creates a cube
 */
public void box(float size);

/**
 * A box is an extruded rectangle. A box with equal dimension
 * on all sides is a cube.
 *
 * @webref shape:3d_primitives
 * @param w dimension of the box in the x-dimension
 * @param h dimension of the box in the y-dimension
 * @param d dimension of the box in the z-dimension
 *
 * @see PApplet#sphere(float)
 */
public void box(float w, float h, float d);

/**
 * @param res number of segments (minimum 3) used per full circle revolution
 */
public void sphereDetail(int res);

/**
 * Controls the detail used to render a sphere by adjusting the number of
 * vertices of the sphere mesh. The default resolution is 30, which creates
 * a fairly detailed sphere definition with vertices every 360/30 = 12
 * degrees. If you're going to render a great number of spheres per frame,
 * it is advised to reduce the level of detail using this function.
 * The setting stays active until <b>sphereDetail()</b> is called again with
 * a new parameter and so should <i>not</i> be called prior to every
 * <b>sphere()</b> statement, unless you wish to render spheres with
 * different settings, e.g. using less detail for smaller spheres or ones
 * further away from the camera. To control the detail of the horizontal
 * and vertical resolution independently, use the version of the functions
 * with two parameters.
 *
 * =advanced
 * Code for sphereDetail() submitted by toxi [031031].
 * Code for enhanced u/v version from davbol [080801].
 *
 * @webref shape:3d_primitives
 * @param ures number of segments used horizontally (longitudinally)
 *        per full circle revolution
 * @param vres number of segments used vertically (latitudinally)
 *        from top to bottom
 *
 * @see PGraphics#sphere(float)
 */
/**
 * Set the detail level for approximating a sphere. The ures and vres params
 * control the horizontal and vertical resolution.
 *
 */
public void sphereDetail(int ures, int vres);

/**
 * Draw a sphere with radius r centered at coordinate 0, 0, 0.
 * A sphere is a hollow ball made from tessellated triangles.
 * =advanced
 * <P>
 * Implementation notes:
 * <P>
 * cache all the points of the sphere in a static array
 * top and bottom are just a bunch of triangles that land
 * in the center point
 * <P>
 * sphere is a series of concentric circles who radii vary
 * along the shape, based on, er.. cos or something
 * <PRE>
 * [toxi 031031] new sphere code. removed all multiplies with
 * radius, as scale() will take care of that anyway
 *
 * [toxi 031223] updated sphere code (removed modulos)
 * and introduced sphereAt(x,y,z,r)
 * to avoid additional translate()'s on the user/sketch side
 *
 * [davbol 080801] now using separate sphereDetailU/V
 * </PRE>
 *
 * @webref shape:3d_primitives
 * @param r the radius of the sphere
 */
public void sphere(float r);

/**
 * Evalutes quadratic bezier at point t for points a, b, c, d.
 * The parameter t varies between 0 and 1. The a and d parameters are the
 * on-curve points, b and c are the control points. To make a two-dimensional
 * curve, call this function once with the x coordinates and a second time
 * with the y coordinates to get the location of a bezier curve at t.
 *
 * =advanced
 * For instance, to convert the following example:<PRE>
 * stroke(255, 102, 0);
 * line(85, 20, 10, 10);
 * line(90, 90, 15, 80);
 * stroke(0, 0, 0);
 * bezier(85, 20, 10, 10, 90, 90, 15, 80);
 *
 * // draw it in gray, using 10 steps instead of the default 20
 * // this is a slower way to do it, but useful if you need
 * // to do things with the coordinates at each step
 * stroke(128);
 * beginShape(LINE_STRIP);
 * for (int i = 0; i <= 10; i++) {
 *   float t = i / 10.0f;
 *   float x = bezierPoint(85, 10, 90, 15, t);
 *   float y = bezierPoint(20, 10, 90, 80, t);
 *   vertex(x, y);
 * }
 * endShape();</PRE>
 *
 * @webref shape:curves
 * @param a coordinate of first point on the curve
 * @param b coordinate of first control point
 * @param c coordinate of second control point
 * @param d coordinate of second point on the curve
 * @param t value between 0 and 1
 *
 * @see PGraphics#bezier(float, float, float, float, float, float, float, float, float, float, float, float)
 * @see PGraphics#bezierVertex(float, float, float, float, float, float)
 * @see PGraphics#curvePoint(float, float, float, float, float)
 */
public float bezierPoint(float a, float b, float c, float d, float t);

/**
 * Calculates the tangent of a point on a Bezier curve. There is a good
 * definition of "tangent" at Wikipedia: <a href="http://en.wikipedia.org/wiki/Tangent" target="new">http://en.wikipedia.org/wiki/Tangent</a>
 *
 * =advanced
 * Code submitted by Dave Bollinger (davol) for release 0136.
 *
 * @webref shape:curves
 * @param a coordinate of first point on the curve
 * @param b coordinate of first control point
 * @param c coordinate of second control point
 * @param d coordinate of second point on the curve
 * @param t value between 0 and 1
 *
 * @see PGraphics#bezier(float, float, float, float, float, float, float, float, float, float, float, float)
 * @see PGraphics#bezierVertex(float, float, float, float, float, float)
 * @see PGraphics#curvePoint(float, float, float, float, float)
 */
public float bezierTangent(float a, float b, float c, float d, float t);

/**
 * Sets the resolution at which Beziers display. The default value is 20. This function is only useful when using the P3D or OPENGL renderer as the default (JAVA2D) renderer does not use this information.
 *
 * @webref shape:curves
 * @param detail resolution of the curves
 *
 * @see PApplet#curve(float, float, float, float, float, float, float, float, float, float, float, float)
 * @see PApplet#curveVertex(float, float)
 * @see PApplet#curveTightness(float)
 */
public void bezierDetail(int detail);

/**
 * Draws a Bezier curve on the screen. These curves are defined by a series
 * of anchor and control points. The first two parameters specify the first
 * anchor point and the last two parameters specify the other anchor point.
 * The middle parameters specify the control points which define the shape
 * of the curve. Bezier curves were developed by French engineer Pierre
 * Bezier. Using the 3D version of requires rendering with P3D or OPENGL
 * (see the Environment reference for more information).
 *
 * =advanced
 * Draw a cubic bezier curve. The first and last points are
 * the on-curve points. The middle two are the 'control' points,
 * or 'handles' in an application like Illustrator.
 * <P>
 * Identical to typing:
 * <PRE>beginShape();
 * vertex(x1, y1);
 * bezierVertex(x2, y2, x3, y3, x4, y4);
 * endShape();
 * </PRE>
 * In Postscript-speak, this would be:
 * <PRE>moveto(x1, y1);
 * curveto(x2, y2, x3, y3, x4, y4);</PRE>
 * If you were to try and continue that curve like so:
 * <PRE>curveto(x5, y5, x6, y6, x7, y7);</PRE>
 * This would be done in processing by adding these statements:
 * <PRE>bezierVertex(x5, y5, x6, y6, x7, y7)
 * </PRE>
 * To draw a quadratic (instead of cubic) curve,
 * use the control point twice by doubling it:
 * <PRE>bezier(x1, y1, cx, cy, cx, cy, x2, y2);</PRE>
 *
 * @webref shape:curves
 * @param x1 coordinates for the first anchor point
 * @param y1 coordinates for the first anchor point
 * @param z1 coordinates for the first anchor point
 * @param x2 coordinates for the first control point
 * @param y2 coordinates for the first control point
 * @param z2 coordinates for the first control point
 * @param x3 coordinates for the second control point
 * @param y3 coordinates for the second control point
 * @param z3 coordinates for the second control point
 * @param x4 coordinates for the second anchor point
 * @param y4 coordinates for the second anchor point
 * @param z4 coordinates for the second anchor point
 *
 * @see PGraphics#bezierVertex(float, float, float, float, float, float)
 * @see PGraphics#curve(float, float, float, float, float, float, float, float, float, float, float, float)
 */
public void bezier(float x1, float y1, float x2, float y2, float x3,
                   float y3, float x4, float y4);

public void bezier(float x1, float y1, float z1, float x2, float y2,
                   float z2, float x3, float y3, float z3, float x4,
                   float y4, float z4);

/**
 * Evalutes the Catmull-Rom curve at point t for points a, b, c, d. The
 * parameter t varies between 0 and 1, a and d are points on the curve,
 * and b and c are the control points. This can be done once with the x
 * coordinates and a second time with the y coordinates to get the
 * location of a curve at t.
 *
 * @webref shape:curves
 * @param a coordinate of first point on the curve
 * @param b coordinate of second point on the curve
 * @param c coordinate of third point on the curve
 * @param d coordinate of fourth point on the curve
 * @param t value between 0 and 1
 *
 * @see PGraphics#curve(float, float, float, float, float, float, float, float, float, float, float, float)
 * @see PGraphics#curveVertex(float, float)
 * @see PGraphics#bezierPoint(float, float, float, float, float)
 */
public float curvePoint(float a, float b, float c, float d, float t);

/**
 * Calculates the tangent of a point on a Catmull-Rom curve. There is a good definition of "tangent" at Wikipedia: <a href="http://en.wikipedia.org/wiki/Tangent" target="new">http://en.wikipedia.org/wiki/Tangent</a>.
 *
 * =advanced
 * Code thanks to Dave Bollinger (Bug #715)
 *
 * @webref shape:curves
 * @param a coordinate of first point on the curve
 * @param b coordinate of first control point
 * @param c coordinate of second control point
 * @param d coordinate of second point on the curve
 * @param t value between 0 and 1
 *
 * @see PGraphics#curve(float, float, float, float, float, float, float, float, float, float, float, float)
 * @see PGraphics#curveVertex(float, float)
 * @see PGraphics#curvePoint(float, float, float, float, float)
 * @see PGraphics#bezierTangent(float, float, float, float, float)
 */
public float curveTangent(float a, float b, float c, float d, float t);

/**
 * Sets the resolution at which curves display. The default value is 20.
 * This function is only useful when using the P3D or OPENGL renderer as
 * the default (JAVA2D) renderer does not use this information.
 *
 * @webref shape:curves
 * @param detail resolution of the curves
 *
 * @see PGraphics#curve(float, float, float, float, float, float, float, float, float, float, float, float)
 * @see PGraphics#curveVertex(float, float)
 * @see PGraphics#curveTightness(float)
 */
public void curveDetail(int detail);

/**
 * Modifies the quality of forms created with <b>curve()</b> and
 *<b>curveVertex()</b>. The parameter <b>squishy</b> determines how the
 * curve fits to the vertex points. The value 0.0 is the default value for
 * <b>squishy</b> (this value defines the curves to be Catmull-Rom splines)
 * and the value 1.0 connects all the points with straight lines.
 * Values within the range -5.0 and 5.0 will deform the curves but
 * will leave them recognizable and as values increase in magnitude,
 * they will continue to deform.
 *
 * @webref shape:curves
 * @param tightness amount of deformation from the original vertices
 *
 * @see PGraphics#curve(float, float, float, float, float, float, float, float, float, float, float, float)
 * @see PGraphics#curveVertex(float, float)
 *
 */
public void curveTightness(float tightness);

/**
 * Draws a curved line on the screen. The first and second parameters
 * specify the beginning control point and the last two parameters specify
 * the ending control point. The middle parameters specify the start and
 * stop of the curve. Longer curves can be created by putting a series of
 * <b>curve()</b> functions together or using <b>curveVertex()</b>.
 * An additional function called <b>curveTightness()</b> provides control
 * for the visual quality of the curve. The <b>curve()</b> function is an
 * implementation of Catmull-Rom splines. Using the 3D version of requires
 * rendering with P3D or OPENGL (see the Environment reference for more
 * information).
 *
 * =advanced
 * As of revision 0070, this function no longer doubles the first
 * and last points. The curves are a bit more boring, but it's more
 * mathematically correct, and properly mirrored in curvePoint().
 * <P>
 * Identical to typing out:<PRE>
 * beginShape();
 * curveVertex(x1, y1);
 * curveVertex(x2, y2);
 * curveVertex(x3, y3);
 * curveVertex(x4, y4);
 * endShape();
 * </PRE>
 *
 * @webref shape:curves
 * @param x1 coordinates for the beginning control point
 * @param y1 coordinates for the beginning control point
 * @param z1 coordinates for the beginning control point
 * @param x2 coordinates for the first point
 * @param y2 coordinates for the first point
 * @param z2 coordinates for the first point
 * @param x3 coordinates for the second point
 * @param y3 coordinates for the second point
 * @param z3 coordinates for the second point
 * @param x4 coordinates for the ending control point
 * @param y4 coordinates for the ending control point
 * @param z4 coordinates for the ending control point
 *
 * @see PGraphics#curveVertex(float, float)
 * @see PGraphics#curveTightness(float)
 * @see PGraphics#bezier(float, float, float, float, float, float, float, float, float, float, float, float)
 */
public void curve(float x1, float y1, float x2, float y2, float x3, float y3,
                  float x4, float y4);

public void curve(float x1, float y1, float z1, float x2, float y2, float z2,
                  float x3, float y3, float z3, float x4, float y4, float z4);

/**
 * If true in PImage, use bilinear interpolation for copy()
 * operations. When inherited by PGraphics, also controls shapes.
 */
public void smooth();

/**
 * Disable smoothing. See smooth().
 */
public void noSmooth();

/**
 * Modifies the location from which images draw. The default mode is
 * <b>imageMode(CORNER)</b>, which specifies the location to be the
 * upper-left corner and uses the fourth and fifth parameters of
 * <b>image()</b> to set the image's width and height. The syntax
 * <b>imageMode(CORNERS)</b> uses the second and third parameters of
 * <b>image()</b> to set the location of one corner of the image and
 * uses the fourth and fifth parameters to set the opposite corner.
 * Use <b>imageMode(CENTER)</b> to draw images centered at the given
 * x and y position.
 * <br><br>The parameter to <b>imageMode()</b> must be written in
 * ALL CAPS because Processing syntax is case sensitive.
 *
 * @webref image:loading_displaying
 * @param mode Either CORNER, CORNERS, or CENTER
 *
 * @see processing.core.PApplet#loadImage(String, String)
 * @see processing.core.PImage
 * @see processing.core.PApplet#image(PImage, float, float, float, float)
 * @see processing.core.PGraphics#background(float, float, float, float)
 */
public void imageMode(int mode);

public void image(PImage image, float x, float y);

/**
 * Displays images to the screen. The images must be in the sketch's "data"
 * directory to load correctly. Select "Add file..." from the "Sketch" menu
 * to add the image. Processing currently works with GIF, JPEG, and Targa
 * images. The color of an image may be modified with the <b>tint()</b>
 * function and if a GIF has transparency, it will maintain its transparency.
 * The <b>img</b> parameter specifies the image to display and the <b>x</b>
 * and <b>y</b> parameters define the location of the image from its
 * upper-left corner. The image is displayed at its original size unless
 * the <b>width</b> and <b>height</b> parameters specify a different size.
 * The <b>imageMode()</b> function changes the way the parameters work.
 * A call to <b>imageMode(CORNERS)</b> will change the width and height
 * parameters to define the x and y values of the opposite corner of the
 * image.
 *
 * =advanced
 * Starting with release 0124, when using the default (JAVA2D) renderer,
 * smooth() will also improve image quality of resized images.
 *
 * @webref image:loading_displaying
 * @param image the image to display
 * @param x x-coordinate of the image
 * @param y y-coordinate of the image
 * @param c width to display the image
 * @param d height to display the image
 *
 * @see processing.core.PApplet#loadImage(String, String)
 * @see processing.core.PImage
 * @see processing.core.PGraphics#imageMode(int)
 * @see processing.core.PGraphics#tint(float)
 * @see processing.core.PGraphics#background(float, float, float, float)
 * @see processing.core.PGraphics#alpha(int)
 */
public void image(PImage image, float x, float y, float c, float d);

/**
 * Draw an image(), also specifying u/v coordinates.
 * In this method, the  u, v coordinates are always based on image space
 * location, regardless of the current textureMode().
 */
public void image(PImage image, float a, float b, float c, float d, int u1,
                  int v1, int u2, int v2);

/**
 * Modifies the location from which shapes draw.
 * The default mode is <b>shapeMode(CORNER)</b>, which specifies the
 * location to be the upper left corner of the shape and uses the third
 * and fourth parameters of <b>shape()</b> to specify the width and height.
 * The syntax <b>shapeMode(CORNERS)</b> uses the first and second parameters
 * of <b>shape()</b> to set the location of one corner and uses the third
 * and fourth parameters to set the opposite corner.
 * The syntax <b>shapeMode(CENTER)</b> draws the shape from its center point
 * and uses the third and forth parameters of <b>shape()</b> to specify the
 * width and height.
 * The parameter must be written in "ALL CAPS" because Processing syntax
 * is case sensitive.
 *
 * @param mode One of CORNER, CORNERS, CENTER
 *
 * @webref shape:loading_displaying
 * @see PGraphics#shape(PShape)
 * @see PGraphics#rectMode(int)
 */
public void shapeMode(int mode);

public void shape(PShape shape);

/**
 * Convenience method to draw at a particular location.
 */
public void shape(PShape shape, float x, float y);

/**
 * Displays shapes to the screen. The shapes must be in the sketch's "data"
 * directory to load correctly. Select "Add file..." from the "Sketch" menu
 * to add the shape.
 * Processing currently works with SVG shapes only.
 * The <b>sh</b> parameter specifies the shape to display and the <b>x</b>
 * and <b>y</b> parameters define the location of the shape from its
 * upper-left corner.
 * The shape is displayed at its original size unless the <b>width</b>
 * and <b>height</b> parameters specify a different size.
 * The <b>shapeMode()</b> function changes the way the parameters work.
 * A call to <b>shapeMode(CORNERS)</b>, for example, will change the width
 * and height parameters to define the x and y values of the opposite corner
 * of the shape.
 * <br><br>
 * Note complex shapes may draw awkwardly with P2D, P3D, and OPENGL. Those
 * renderers do not yet support shapes that have holes or complicated breaks.
 *
 * @param shape
 * @param x x-coordinate of the shape
 * @param y y-coordinate of the shape
 * @param c width to display the shape
 * @param d height to display the shape
 *
 * @webref shape:loading_displaying
 * @see PShape
 * @see PGraphics#loadShape(String)
 * @see PGraphics#shapeMode(int)
 */
public void shape(PShape shape, float x, float y, float c, float d);

/**
 * Sets the alignment of the text to one of LEFT, CENTER, or RIGHT.
 * This will also reset the vertical text alignment to BASELINE.
 */
public void textAlign(int align);

/**
 * Sets the horizontal and vertical alignment of the text. The horizontal
 * alignment can be one of LEFT, CENTER, or RIGHT. The vertical alignment
 * can be TOP, BOTTOM, CENTER, or the BASELINE (the default).
 */
public void textAlign(int alignX, int alignY);

/**
 * Returns the ascent of the current font at the current size.
 * This is a method, rather than a variable inside the PGraphics object
 * because it requires calculation.
 */
public float textAscent();

/**
 * Returns the descent of the current font at the current size.
 * This is a method, rather than a variable inside the PGraphics object
 * because it requires calculation.
 */
public float textDescent();

/**
 * Sets the current font. The font's size will be the "natural"
 * size of this font (the size that was set when using "Create Font").
 * The leading will also be reset.
 */
public void textFont(PFont which);

/**
 * Useful function to set the font and size at the same time.
 */
public void textFont(PFont which, float size);

/**
 * Set the text leading to a specific value. If using a custom
 * value for the text leading, you'll have to call textLeading()
 * again after any calls to textSize().
 */
public void textLeading(float leading);

/**
 * Sets the text rendering/placement to be either SCREEN (direct
 * to the screen, exact coordinates, only use the font's original size)
 * or MODEL (the default, where text is manipulated by translate() and
 * can have a textSize). The text size cannot be set when using
 * textMode(SCREEN), because it uses the pixels directly from the font.
 */
public void textMode(int mode);

/**
 * Sets the text size, also resets the value for the leading.
 */
public void textSize(float size);

public float textWidth(char c);

/**
 * Return the width of a line of text. If the text has multiple
 * lines, this returns the length of the longest line.
 */
public float textWidth(String str);

///**
// * TODO not sure if this stays...
// */
//public float textWidth(char[] chars, int start, int length);

/**
 * Write text where we just left off.
 */
public void text(char c);

/**
 * Draw a single character on screen.
 * Extremely slow when used with textMode(SCREEN) and Java 2D,
 * because loadPixels has to be called first and updatePixels last.
 */
public void text(char c, float x, float y);

/**
 * Draw a single character on screen (with a z coordinate)
 */
public void text(char c, float x, float y, float z);

/**
 * Write text where we just left off.
 */
public void text(String str);

/**
 * Draw a chunk of text.
 * Newlines that are \n (Unix newline or linefeed char, ascii 10)
 * are honored, but \r (carriage return, Windows and Mac OS) are
 * ignored.
 */
public void text(String str, float x, float y);

///**
// * Method to draw text from an array of chars. This method will usually be
// * more efficient than drawing from a String object, because the String will
// * not be converted to a char array before drawing.
// */
//public void text(char[] chars, int start, int stop, float x, float y);
//
///**
// * Same as above but with a z coordinate.
// */
//public void text(String str, float x, float y, float z);
//
//public void text(char[] chars, int start, int stop, float x, float y, float z);

/**
 * Draw text in a box that is constrained to a particular size.
 * The current rectMode() determines what the coordinates mean
 * (whether x1/y1/x2/y2 or x/y/w/h).
 * <P/>
 * Note that the x,y coords of the start of the box
 * will align with the *ascent* of the text, not the baseline,
 * as is the case for the other text() functions.
 * <P/>
 * Newlines that are \n (Unix newline or linefeed char, ascii 10)
 * are honored, and \r (carriage return, Windows and Mac OS) are
 * ignored.
 */
public void text(String str, float x1, float y1, float x2, float y2);

public void text(String s, float x1, float y1, float x2, float y2, float z);

public void text(int num, float x, float y);

public void text(int num, float x, float y, float z);

/**
 * This does a basic number formatting, to avoid the
 * generally ugly appearance of printing floats.
 * Users who want more control should use their own nf() cmmand,
 * or if they want the long, ugly version of float,
 * use String.valueOf() to convert the float to a String first.
 */
public void text(float num, float x, float y);

public void text(float num, float x, float y, float z);

/**
 * Push a copy of the current transformation matrix onto the stack.
 */
public void pushMatrix();

/**
 * Replace the current transformation matrix with the top of the stack.
 */
public void popMatrix();

/**
 * Translate in X and Y.
 */
public void translate(float tx, float ty);

/**
 * Translate in X, Y, and Z.
 */
public void translate(float tx, float ty, float tz);

/**
 * Two dimensional rotation.
 *
 * Same as rotateZ (this is identical to a 3D rotation along the z-axis)
 * but included for clarity. It'd be weird for people drawing 2D graphics
 * to be using rotateZ. And they might kick our a-- for the confusion.
 *
 * <A HREF="http://www.xkcd.com/c184.html">Additional background</A>.
 */
public void rotate(float angle);

/**
 * Rotate around the X axis.
 */
public void rotateX(float angle);

/**
 * Rotate around the Y axis.
 */
public void rotateY(float angle);

/**
 * Rotate around the Z axis.
 *
 * The functions rotate() and rotateZ() are identical, it's just that it make
 * sense to have rotate() and then rotateX() and rotateY() when using 3D;
 * nor does it make sense to use a function called rotateZ() if you're only
 * doing things in 2D. so we just decided to have them both be the same.
 */
public void rotateZ(float angle);

/**
 * Rotate about a vector in space. Same as the glRotatef() function.
 */
public void rotate(float angle, float vx, float vy, float vz);

/**
 * Scale in all dimensions.
 */
public void scale(float s);

/**
 * Scale in X and Y. Equivalent to scale(sx, sy, 1).
 *
 * Not recommended for use in 3D, because the z-dimension is just
 * scaled by 1, since there's no way to know what else to scale it by.
 */
public void scale(float sx, float sy);

/**
 * Scale in X, Y, and Z.
 */
public void scale(float x, float y, float z);

///**
// * Shear along X axis
// */
//public void shearX(float angle);
//
///**
// * Shear along Y axis
// */
//public void shearY(float angle);

/**
 * Set the current transformation matrix to identity.
 */
public void resetMatrix();

public void applyMatrix(PMatrix source);

public void applyMatrix(PMatrix2D source);

/**
 * Apply a 3x2 affine transformation matrix.
 */
public void applyMatrix(float n00, float n01, float n02, float n10,
                        float n11, float n12);

public void applyMatrix(PMatrix3D source);

/**
 * Apply a 4x4 transformation matrix.
 */
public void applyMatrix(float n00, float n01, float n02, float n03,
                        float n10, float n11, float n12, float n13,
                        float n20, float n21, float n22, float n23,
                        float n30, float n31, float n32, float n33);

public PMatrix getMatrix();

/**
 * Copy the current transformation matrix into the specified target.
 * Pass in null to create a new matrix.
 */
public PMatrix2D getMatrix(PMatrix2D target);

/**
 * Copy the current transformation matrix into the specified target.
 * Pass in null to create a new matrix.
 */
public PMatrix3D getMatrix(PMatrix3D target);

/**
 * Set the current transformation matrix to the contents of another.
 */
public void setMatrix(PMatrix source);

/**
 * Set the current transformation to the contents of the specified source.
 */
public void setMatrix(PMatrix2D source);

/**
 * Set the current transformation to the contents of the specified source.
 */
public void setMatrix(PMatrix3D source);

/**
 * Print the current model (or "transformation") matrix.
 */
public void printMatrix();

public void beginCamera();

public void endCamera();

public void camera();

public void camera(float eyeX, float eyeY, float eyeZ, float centerX,
                   float centerY, float centerZ, float upX, float upY,
                   float upZ);

public void printCamera();

public void ortho();

public void ortho(float left, float right, float bottom, float top,
                  float near, float far);

public void perspective();

public void perspective(float fovy, float aspect, float zNear, float zFar);

public void frustum(float left, float right, float bottom, float top,
                    float near, float far);

public void printProjection();

/**
 * Given an x and y coordinate, returns the x position of where
 * that point would be placed on screen, once affected by translate(),
 * scale(), or any other transformations.
 */
public float screenX(float x, float y);

/**
 * Given an x and y coordinate, returns the y position of where
 * that point would be placed on screen, once affected by translate(),
 * scale(), or any other transformations.
 */
public float screenY(float x, float y);

/**
 * Maps a three dimensional point to its placement on-screen.
 * <P>
 * Given an (x, y, z) coordinate, returns the x position of where
 * that point would be placed on screen, once affected by translate(),
 * scale(), or any other transformations.
 */
public float screenX(float x, float y, float z);

/**
 * Maps a three dimensional point to its placement on-screen.
 * <P>
 * Given an (x, y, z) coordinate, returns the y position of where
 * that point would be placed on screen, once affected by translate(),
 * scale(), or any other transformations.
 */
public float screenY(float x, float y, float z);

/**
 * Maps a three dimensional point to its placement on-screen.
 * <P>
 * Given an (x, y, z) coordinate, returns its z value.
 * This value can be used to determine if an (x, y, z) coordinate
 * is in front or in back of another (x, y, z) coordinate.
 * The units are based on how the zbuffer is set up, and don't
 * relate to anything "real". They're only useful for in
 * comparison to another value obtained from screenZ(),
 * or directly out of the zbuffer[].
 */
public float screenZ(float x, float y, float z);

/**
 * Returns the model space x value for an x, y, z coordinate.
 * <P>
 * This will give you a coordinate after it has been transformed
 * by translate(), rotate(), and camera(), but not yet transformed
 * by the projection matrix. For instance, his can be useful for
 * figuring out how points in 3D space relate to the edge
 * coordinates of a shape.
 */
public float modelX(float x, float y, float z);

/**
 * Returns the model space y value for an x, y, z coordinate.
 */
public float modelY(float x, float y, float z);

/**
 * Returns the model space z value for an x, y, z coordinate.
 */
public float modelZ(float x, float y, float z);

public void pushStyle();

public void popStyle();

public void style(PStyle s);

public void strokeWeight(float weight);

public void strokeJoin(int join);

public void strokeCap(int cap);

/**
 * Disables drawing the stroke (outline). If both <b>noStroke()</b> and
 * <b>noFill()</b> are called, no shapes will be drawn to the screen.
 *
 * @webref color:setting
 *
 * @see PGraphics#stroke(float, float, float, float)
 */
public void noStroke();

/**
 * Set the tint to either a grayscale or ARGB value.
 * See notes attached to the fill() function.
 * @param rgb color value in hexadecimal notation
 * (i.e. #FFCC00 or 0xFFFFCC00) or any value of the color datatype
 */
public void stroke(int rgb);

public void stroke(int rgb, float alpha);

/**
 *
 * @param gray specifies a value between white and black
 */
public void stroke(float gray);

public void stroke(float gray, float alpha);

public void stroke(float x, float y, float z);

/**
 * Sets the color used to draw lines and borders around shapes. This color
 * is either specified in terms of the RGB or HSB color depending on the
 * current <b>colorMode()</b> (the default color space is RGB, with each
 * value in the range from 0 to 255).
 * <br><br>When using hexadecimal notation to specify a color, use "#" or
 * "0x" before the values (e.g. #CCFFAA, 0xFFCCFFAA). The # syntax uses six
 * digits to specify a color (the way colors are specified in HTML and CSS).
 * When using the hexadecimal notation starting with "0x", the hexadecimal
 * value must be specified with eight characters; the first two characters
 * define the alpha component and the remainder the red, green, and blue
 * components.
 * <br><br>The value for the parameter "gray" must be less than or equal
 * to the current maximum value as specified by <b>colorMode()</b>.
 * The default maximum value is 255.
 *
 * @webref color:setting
 * @param alpha opacity of the stroke
 * @param x red or hue value (depending on the current color mode)
 * @param y green or saturation value (depending on the current color mode)
 * @param z blue or brightness value (depending on the current color mode)
 */
public void stroke(float x, float y, float z, float a);

/**
 * Removes the current fill value for displaying images and reverts to displaying images with their original hues.
 *
 * @webref image:loading_displaying
 * @see processing.core.PGraphics#tint(float, float, float, float)
 * @see processing.core.PGraphics#image(PImage, float, float, float, float)
 */
public void noTint();

/**
 * Set the tint to either a grayscale or ARGB value.
 */
public void tint(int rgb);

/**
 * @param rgb color value in hexadecimal notation
 * (i.e. #FFCC00 or 0xFFFFCC00) or any value of the color datatype
 * @param alpha opacity of the image
 */
public void tint(int rgb, float alpha);

/**
 * @param gray any valid number
 */
public void tint(float gray);

public void tint(float gray, float alpha);

public void tint(float x, float y, float z);

/**
 * Sets the fill value for displaying images. Images can be tinted to
 * specified colors or made transparent by setting the alpha.
 * <br><br>To make an image transparent, but not change it's color,
 * use white as the tint color and specify an alpha value. For instance,
 * tint(255, 128) will make an image 50% transparent (unless
 * <b>colorMode()</b> has been used).
 *
 * <br><br>When using hexadecimal notation to specify a color, use "#" or
 * "0x" before the values (e.g. #CCFFAA, 0xFFCCFFAA). The # syntax uses six
 * digits to specify a color (the way colors are specified in HTML and CSS).
 * When using the hexadecimal notation starting with "0x", the hexadecimal
 * value must be specified with eight characters; the first two characters
 * define the alpha component and the remainder the red, green, and blue
 * components.
 * <br><br>The value for the parameter "gray" must be less than or equal
 * to the current maximum value as specified by <b>colorMode()</b>.
 * The default maximum value is 255.
 * <br><br>The tint() method is also used to control the coloring of
 * textures in 3D.
 *
 * @webref image:loading_displaying
 * @param x red or hue value
 * @param y green or saturation value
 * @param z blue or brightness value
 *
 * @see processing.core.PGraphics#noTint()
 * @see processing.core.PGraphics#image(PImage, float, float, float, float)
 */
public void tint(float x, float y, float z, float a);

/**
 * Disables filling geometry. If both <b>noStroke()</b> and <b>noFill()</b>
 * are called, no shapes will be drawn to the screen.
 *
 * @webref color:setting
 *
 * @see PGraphics#fill(float, float, float, float)
 *
 */
public void noFill();

/**
 * Set the fill to either a grayscale value or an ARGB int.
 * @param rgb color value in hexadecimal notation (i.e. #FFCC00 or 0xFFFFCC00) or any value of the color datatype
 */
public void fill(int rgb);

public void fill(int rgb, float alpha);

/**
 * @param gray number specifying value between white and black
 */
public void fill(float gray);

public void fill(float gray, float alpha);

public void fill(float x, float y, float z);

/**
 * Sets the color used to fill shapes. For example, if you run <b>fill(204, 102, 0)</b>, all subsequent shapes will be filled with orange. This color is either specified in terms of the RGB or HSB color depending on the current <b>colorMode()</b> (the default color space is RGB, with each value in the range from 0 to 255).
 * <br><br>When using hexadecimal notation to specify a color, use "#" or "0x" before the values (e.g. #CCFFAA, 0xFFCCFFAA). The # syntax uses six digits to specify a color (the way colors are specified in HTML and CSS). When using the hexadecimal notation starting with "0x", the hexadecimal value must be specified with eight characters; the first two characters define the alpha component and the remainder the red, green, and blue components.
 * <br><br>The value for the parameter "gray" must be less than or equal to the current maximum value as specified by <b>colorMode()</b>. The default maximum value is 255.
 * <br><br>To change the color of an image (or a texture), use tint().
 *
 * @webref color:setting
 * @param x red or hue value
 * @param y green or saturation value
 * @param z blue or brightness value
 * @param alpha opacity of the fill
 *
 * @see PGraphics#noFill()
 * @see PGraphics#stroke(float)
 * @see PGraphics#tint(float)
 * @see PGraphics#background(float, float, float, float)
 * @see PGraphics#colorMode(int, float, float, float, float)
 */
public void fill(float x, float y, float z, float a);

public void ambient(int rgb);

public void ambient(float gray);

public void ambient(float x, float y, float z);

public void specular(int rgb);

public void specular(float gray);

public void specular(float x, float y, float z);

public void shininess(float shine);

public void emissive(int rgb);

public void emissive(float gray);

public void emissive(float x, float y, float z);

public void lights();

public void noLights();

public void ambientLight(float red, float green, float blue);

public void ambientLight(float red, float green, float blue, float x,
                         float y, float z);

public void directionalLight(float red, float green, float blue, float nx,
                             float ny, float nz);

public void pointLight(float red, float green, float blue, float x, float y,
                       float z);

public void spotLight(float red, float green, float blue, float x, float y,
                      float z, float nx, float ny, float nz, float angle,
                      float concentration);

public void lightFalloff(float constant, float linear, float quadratic);

public void lightSpecular(float x, float y, float z);

/**
 * Set the background to a gray or ARGB color.
 * <p>
 * For the main drawing surface, the alpha value will be ignored. However,
 * alpha can be used on PGraphics objects from createGraphics(). This is
 * the only way to set all the pixels partially transparent, for instance.
 * <p>
 * Note that background() should be called before any transformations occur,
 * because some implementations may require the current transformation matrix
 * to be identity before drawing.
 *
 * @param rgb color value in hexadecimal notation (i.e. #FFCC00 or 0xFFFFCC00)<br/>or any value of the color datatype
 */
public void background(int rgb);

/**
 * See notes about alpha in background(x, y, z, a).
 */
public void background(int rgb, float alpha);

/**
 * Set the background to a grayscale value, based on the
 * current colorMode.
 */
public void background(float gray);

/**
 * See notes about alpha in background(x, y, z, a).
 * @param gray specifies a value between white and black
 * @param alpha opacity of the background
 */
public void background(float gray, float alpha);

/**
 * Set the background to an r, g, b or h, s, b value,
 * based on the current colorMode.
 */
public void background(float x, float y, float z);

/**
 * The <b>background()</b> function sets the color used for the background of the Processing window. The default background is light gray. In the <b>draw()</b> function, the background color is used to clear the display window at the beginning of each frame.
 * <br><br>An image can also be used as the background for a sketch, however its width and height must be the same size as the sketch window. To resize an image 'b' to the size of the sketch window, use b.resize(width, height).
 * <br><br>Images used as background will ignore the current tint() setting.
 * <br><br>It is not possible to use transparency (alpha) in background colors with the main drawing surface, however they will work properly with <b>createGraphics</b>.
 *
 * =advanced
 * <p>Clear the background with a color that includes an alpha value. This can
 * only be used with objects created by createGraphics(), because the main
 * drawing surface cannot be set transparent.</p>
 * <p>It might be tempting to use this function to partially clear the screen
 * on each frame, however that's not how this function works. When calling
 * background(), the pixels will be replaced with pixels that have that level
 * of transparency. To do a semi-transparent overlay, use fill() with alpha
 * and draw a rectangle.</p>
 *
 * @webref color:setting
 * @param x red or hue value (depending on the current color mode)
 * @param y green or saturation value (depending on the current color mode)
 * @param z blue or brightness value (depending on the current color mode)
 *
 * @see PGraphics#stroke(float)
 * @see PGraphics#fill(float)
 * @see PGraphics#tint(float)
 * @see PGraphics#colorMode(int)
 */
public void background(float x, float y, float z, float a);

/**
 * Takes an RGB or ARGB image and sets it as the background.
 * The width and height of the image must be the same size as the sketch.
 * Use image.resize(width, height) to make short work of such a task.
 * <P>
 * Note that even if the image is set as RGB, the high 8 bits of each pixel
 * should be set opaque (0xFF000000), because the image data will be copied
 * directly to the screen, and non-opaque background images may have strange
 * behavior. Using image.filter(OPAQUE) will handle this easily.
 * <P>
 * When using 3D, this will also clear the zbuffer (if it exists).
 */
public void background(PImage image);

/**
 * @param mode Either RGB or HSB, corresponding to Red/Green/Blue and Hue/Saturation/Brightness
 * @param max range for all color elements
 */
public void colorMode(int mode);

public void colorMode(int mode, float max);

/**
 * Set the colorMode and the maximum values for (r, g, b)
 * or (h, s, b).
 * <P>
 * Note that this doesn't set the maximum for the alpha value,
 * which might be confusing if for instance you switched to
 * <PRE>colorMode(HSB, 360, 100, 100);</PRE>
 * because the alpha values were still between 0 and 255.
 */
public void colorMode(int mode, float maxX, float maxY, float maxZ);

/**
 * Changes the way Processing interprets color data. By default, the parameters for <b>fill()</b>, <b>stroke()</b>, <b>background()</b>, and <b>color()</b> are defined by values between 0 and 255 using the RGB color model. The <b>colorMode()</b> function is used to change the numerical range used for specifying colors and to switch color systems. For example, calling <b>colorMode(RGB, 1.0)</b> will specify that values are specified between 0 and 1. The limits for defining colors are altered by setting the parameters range1, range2, range3, and range 4.
 *
 * @webref color:setting
 * @param maxX range for the red or hue depending on the current color mode
 * @param maxY range for the green or saturation depending on the current color mode
 * @param maxZ range for the blue or brightness depending on the current color mode
 * @param maxA range for the alpha
 *
 * @see PGraphics#background(float)
 * @see PGraphics#fill(float)
 * @see PGraphics#stroke(float)
 */
public void colorMode(int mode, float maxX, float maxY, float maxZ, float maxA);

/**
 * Extracts the alpha value from a color.
 *
 * @webref color:creating_reading
 * @param what any value of the color datatype
 */
public float alpha(int what);

/**
 * Extracts the red value from a color, scaled to match current <b>colorMode()</b>. This value is always returned as a  float so be careful not to assign it to an int value.<br><br>The red() function is easy to use and undestand, but is slower than another technique. To achieve the same results when working in <b>colorMode(RGB, 255)</b>, but with greater speed, use the &gt;&gt; (right shift) operator with a bit mask. For example, the following two lines of code are equivalent:<br><pre>float r1 = red(myColor);<br>float r2 = myColor &gt;&gt; 16 &amp; 0xFF;</pre>
 *
 * @webref color:creating_reading
 * @param what any value of the color datatype
 *
 * @see PGraphics#green(int)
 * @see PGraphics#blue(int)
 * @see PGraphics#hue(int)
 * @see PGraphics#saturation(int)
 * @see PGraphics#brightness(int)
 * @ref rightshift
 */
public float red(int what);

/**
 * Extracts the green value from a color, scaled to match current <b>colorMode()</b>. This value is always returned as a  float so be careful not to assign it to an int value.<br><br>The <b>green()</b> function is easy to use and undestand, but is slower than another technique. To achieve the same results when working in <b>colorMode(RGB, 255)</b>, but with greater speed, use the &gt;&gt; (right shift) operator with a bit mask. For example, the following two lines of code are equivalent:<br><pre>float r1 = green(myColor);<br>float r2 = myColor &gt;&gt; 8 &amp; 0xFF;</pre>
 *
 * @webref color:creating_reading
 * @param what any value of the color datatype
 *
 * @see PGraphics#red(int)
 * @see PGraphics#blue(int)
 * @see PGraphics#hue(int)
 * @see PGraphics#saturation(int)
 * @see PGraphics#brightness(int)
 * @ref rightshift
 */
public float green(int what);

/**
 * Extracts the blue value from a color, scaled to match current <b>colorMode()</b>. This value is always returned as a  float so be careful not to assign it to an int value.<br><br>The <b>blue()</b> function is easy to use and undestand, but is slower than another technique. To achieve the same results when working in <b>colorMode(RGB, 255)</b>, but with greater speed, use a bit mask to remove the other color components. For example, the following two lines of code are equivalent:<br><pre>float r1 = blue(myColor);<br>float r2 = myColor &amp; 0xFF;</pre>
 *
 * @webref color:creating_reading
 * @param what any value of the color datatype
 *
 * @see PGraphics#red(int)
 * @see PGraphics#green(int)
 * @see PGraphics#hue(int)
 * @see PGraphics#saturation(int)
 * @see PGraphics#brightness(int)
 */
public float blue(int what);

/**
 * Extracts the hue value from a color.
 *
 * @webref color:creating_reading
 * @param what any value of the color datatype
 *
 * @see PGraphics#red(int)
 * @see PGraphics#green(int)
 * @see PGraphics#blue(int)
 * @see PGraphics#saturation(int)
 * @see PGraphics#brightness(int)
 */
public float hue(int what);

/**
 * Extracts the saturation value from a color.
 *
 * @webref color:creating_reading
 * @param what any value of the color datatype
 *
 * @see PGraphics#red(int)
 * @see PGraphics#green(int)
 * @see PGraphics#blue(int)
 * @see PGraphics#hue(int)
 * @see PGraphics#brightness(int)
 */
public float saturation(int what);

/**
 * Extracts the brightness value from a color.
 *
 *
 * @webref color:creating_reading
 * @param what any value of the color datatype
 *
 * @see PGraphics#red(int)
 * @see PGraphics#green(int)
 * @see PGraphics#blue(int)
 * @see PGraphics#hue(int)
 * @see PGraphics#saturation(int)
 */
public float brightness(int what);

/**
 * Calculates a color or colors between two color at a specific increment. The <b>amt</b> parameter is the amount to interpolate between the two values where 0.0 equal to the first point, 0.1 is very near the first point, 0.5 is half-way in between, etc.
 *
 * @webref color:creating_reading
 * @param c1 interpolate from this color
 * @param c2 interpolate to this color
 * @param amt between 0.0 and 1.0
 *
 * @see PGraphics#blendColor(int, int, int)
 * @see PGraphics#color(float, float, float, float)
 */
public int lerpColor(int c1, int c2, float amt);

/**
 * Return true if this renderer should be drawn to the screen. Defaults to
 * returning true, since nearly all renderers are on-screen beasts. But can
 * be overridden for subclasses like PDF so that a window doesn't open up.
 * <br/> <br/>
 * A better name? showFrame, displayable, isVisible, visible, shouldDisplay,
 * what to call this?
 */
public boolean displayable();

///**
// * Store data of some kind for a renderer that requires extra metadata of
// * some kind. Usually this is a renderer-specific representation of the
// * image data, for instance a BufferedImage with tint() settings applied for
// * PGraphicsJava2D, or resized image data and OpenGL texture indices for
// * PGraphicsOpenGL.
// */
//public void setCache(Object parent, Object storage);
//
///**
// * Get cache storage data for the specified renderer. Because each renderer
// * will cache data in different formats, it's necessary to store cache data
// * keyed by the renderer object. Otherwise, attempting to draw the same
// * image to both a PGraphicsJava2D and a PGraphicsOpenGL will cause errors.
// * @param parent The PGraphics object (or any object, really) associated
// * @return data stored for the specified parent
// */
//public Object getCache(Object parent);
//
///**
// * Remove information associated with this renderer from the cache, if any.
// * @param parent The PGraphics object whose cache data should be removed
// */
//public void removeCache(Object parent);

/**
 * Returns an ARGB "color" type (a packed 32 bit int with the color.
 * If the coordinate is outside the image, zero is returned
 * (black, but completely transparent).
 * <P>
 * If the image is in RGB format (i.e. on a PVideo object),
 * the value will get its high bits set, just to avoid cases where
 * they haven't been set already.
 * <P>
 * If the image is in ALPHA format, this returns a white with its
 * alpha value set.
 * <P>
 * This function is included primarily for beginners. It is quite
 * slow because it has to check to see if the x, y that was provided
 * is inside the bounds, and then has to check to see what image
 * type it is. If you want things to be more efficient, access the
 * pixels[] array directly.
 */
public int get(int x, int y);

/**
 * Reads the color of any pixel or grabs a group of pixels. If no parameters are specified, the entire image is returned. Get the value of one pixel by specifying an x,y coordinate. Get a section of the display window by specifing an additional <b>width</b> and <b>height</b> parameter. If the pixel requested is outside of the image window, black is returned. The numbers returned are scaled according to the current color ranges, but only RGB values are returned by this function. Even though you may have drawn a shape with <b>colorMode(HSB)</b>, the numbers returned will be in RGB.
 * <br><br>Getting the color of a single pixel with <b>get(x, y)</b> is easy, but not as fast as grabbing the data directly from <b>pixels[]</b>. The equivalent statement to "get(x, y)" using <b>pixels[]</b> is "pixels[y*width+x]". Processing requires calling <b>loadPixels()</b> to load the display window data into the <b>pixels[]</b> array before getting the values.
 * <br><br>As of release 0149, this function ignores <b>imageMode()</b>.
 *
 * @webref
 * @brief Reads the color of any pixel or grabs a rectangle of pixels
 * @param x x-coordinate of the pixel
 * @param y y-coordinate of the pixel
 * @param w width of pixel rectangle to get
 * @param h height of pixel rectangle to get
 *
 * @see processing.core.PImage#set(int, int, int)
 * @see processing.core.PImage#pixels
 * @see processing.core.PImage#copy(PImage, int, int, int, int, int, int, int, int)
 */
public PImage get(int x, int y, int w, int h);

/**
 * Returns a copy of this PImage. Equivalent to get(0, 0, width, height).
 */
public PImage get();

/**
 * Changes the color of any pixel or writes an image directly into the display window. The <b>x</b> and <b>y</b> parameters specify the pixel to change and the <b>color</b> parameter specifies the color value. The color parameter is affected by the current color mode (the default is RGB values from 0 to 255). When setting an image, the x and y parameters define the coordinates for the upper-left corner of the image.
 * <br><br>Setting the color of a single pixel with <b>set(x, y)</b> is easy, but not as fast as putting the data directly into <b>pixels[]</b>. The equivalent statement to "set(x, y, #000000)" using <b>pixels[]</b> is "pixels[y*width+x] = #000000". You must call <b>loadPixels()</b> to load the display window data into the <b>pixels[]</b> array before setting the values and calling <b>updatePixels()</b> to update the window with any changes.
 * <br><br>As of release 1.0, this function ignores <b>imageMode()</b>.
 * <br><br>Due to what appears to be a bug in Apple's Java implementation, the point() and set() methods are extremely slow in some circumstances when used with the default renderer. Using P2D or P3D will fix the problem. Grouping many calls to point() or set() together can also help. (<a href="http://dev.processing.org/bugs/show_bug.cgi?id=1094">Bug 1094</a>)
 * =advanced
 * <br><br>As of release 0149, this function ignores <b>imageMode()</b>.
 *
 * @webref image:pixels
 * @param x x-coordinate of the pixel
 * @param y y-coordinate of the pixel
 * @param c any value of the color datatype
 */
public void set(int x, int y, int c);

/**
 * Efficient method of drawing an image's pixels directly to this surface.
 * No variations are employed, meaning that any scale, tint, or imageMode
 * settings will be ignored.
 */
public void set(int x, int y, PImage src);

/**
 * Set alpha channel for an image. Black colors in the source
 * image will make the destination image completely transparent,
 * and white will make things fully opaque. Gray values will
 * be in-between steps.
 * <P>
 * Strictly speaking the "blue" value from the source image is
 * used as the alpha color. For a fully grayscale image, this
 * is correct, but for a color image it's not 100% accurate.
 * For a more accurate conversion, first use filter(GRAY)
 * which will make the image into a "correct" grayscale by
 * performing a proper luminance-based conversion.
 *
 * @param maskArray any array of Integer numbers used as the alpha channel, needs to be same length as the image's pixel array
 */
public void mask(int maskArray[]);

/**
 * Masks part of an image from displaying by loading another image and using it as an alpha channel.
 *  This mask image should only contain grayscale data, but only the blue color channel is used.
 *  The mask image needs to be the same size as the image to which it is applied.
 *  In addition to using a mask image, an integer array containing the alpha channel data can be specified directly.
 *  This method is useful for creating dynamically generated alpha masks.
 *  This array must be of the same length as the target image's pixels array and should contain only grayscale data of values between 0-255.
 * @webref
 * @brief     Masks part of the image from displaying
 * @param maskImg any PImage object used as the alpha channel for "img", needs to be same size as "img"
 */
public void mask(PImage maskImg);

public void filter(int kind);

/**
 * Filters an image as defined by one of the following modes:<br><br>THRESHOLD - converts the image to black and white pixels depending if they are above or below the threshold defined by the level parameter. The level must be between 0.0 (black) and 1.0(white). If no level is specified, 0.5 is used.<br><br>GRAY - converts any colors in the image to grayscale equivalents<br><br>INVERT - sets each pixel to its inverse value<br><br>POSTERIZE - limits each channel of the image to the number of colors specified as the level parameter<br><br>BLUR - executes a Guassian blur with the level parameter specifying the extent of the blurring. If no level parameter is used, the blur is equivalent to Guassian blur of radius 1.<br><br>OPAQUE - sets the alpha channel to entirely opaque.<br><br>ERODE - reduces the light areas with the amount defined by the level parameter.<br><br>DILATE - increases the light areas with the amount defined by the level parameter
 * =advanced
 * Method to apply a variety of basic filters to this image.
 * <P>
 * <UL>
 * <LI>filter(BLUR) provides a basic blur.
 * <LI>filter(GRAY) converts the image to grayscale based on luminance.
 * <LI>filter(INVERT) will invert the color components in the image.
 * <LI>filter(OPAQUE) set all the high bits in the image to opaque
 * <LI>filter(THRESHOLD) converts the image to black and white.
 * <LI>filter(DILATE) grow white/light areas
 * <LI>filter(ERODE) shrink white/light areas
 * </UL>
 * Luminance conversion code contributed by
 * <A HREF="http://www.toxi.co.uk">toxi</A>
 * <P/>
 * Gaussian blur code contributed by
 * <A HREF="http://incubator.quasimondo.com">Mario Klingemann</A>
 *
 * @webref
 * @brief Converts the image to grayscale or black and white
 * @param kind Either THRESHOLD, GRAY, INVERT, POSTERIZE, BLUR, OPAQUE, ERODE, or DILATE
 * @param param in the range from 0 to 1
 */
public void filter(int kind, float param);

/**
 * Copy things from one area of this image
 * to another area in the same image.
 */
public void copy(int sx, int sy, int sw, int sh, int dx, int dy, int dw,
                 int dh);

/**
 * Copies a region of pixels from one image into another. If the source and destination regions aren't the same size, it will automatically resize source pixels to fit the specified target region. No alpha information is used in the process, however if the source image has an alpha channel set, it will be copied as well.
 * <br><br>As of release 0149, this function ignores <b>imageMode()</b>.
 *
 * @webref
 * @brief     Copies the entire image
 * @param sx X coordinate of the source's upper left corner
 * @param sy Y coordinate of the source's upper left corner
 * @param sw source image width
 * @param sh source image height
 * @param dx X coordinate of the destination's upper left corner
 * @param dy Y coordinate of the destination's upper left corner
 * @param dw destination image width
 * @param dh destination image height
 * @param src an image variable referring to the source image.
 *
 * @see processing.core.PGraphics#alpha(int)
 * @see processing.core.PImage#blend(PImage, int, int, int, int, int, int, int, int, int)
 */
public void copy(PImage src, int sx, int sy, int sw, int sh, int dx, int dy,
                 int dw, int dh);

/**
 * Blends one area of this image to another area.
 *
 * @see processing.core.PImage#blendColor(int,int,int)
 */
public void blend(int sx, int sy, int sw, int sh, int dx, int dy, int dw,
                  int dh, int mode);

/**
 * Blends a region of pixels into the image specified by the <b>img</b> parameter. These copies utilize full alpha channel support and a choice of the following modes to blend the colors of source pixels (A) with the ones of pixels in the destination image (B):<br><br>
 * BLEND - linear interpolation of colours: C = A*factor + B<br><br>
 * ADD - additive blending with white clip: C = min(A*factor + B, 255)<br><br>
 * SUBTRACT - subtractive blending with black clip: C = max(B - A*factor, 0)<br><br>
 * DARKEST - only the darkest colour succeeds: C = min(A*factor, B)<br><br>
 * LIGHTEST - only the lightest colour succeeds: C = max(A*factor, B)<br><br>
 * DIFFERENCE - subtract colors from underlying image.<br><br>
 * EXCLUSION - similar to DIFFERENCE, but less extreme.<br><br>
 * MULTIPLY - Multiply the colors, result will always be darker.<br><br>
 * SCREEN - Opposite multiply, uses inverse values of the colors.<br><br>
 * OVERLAY - A mix of MULTIPLY and SCREEN. Multiplies dark values, and screens light values.<br><br>
 * HARD_LIGHT - SCREEN when greater than 50% gray, MULTIPLY when lower.<br><br>
 * SOFT_LIGHT - Mix of DARKEST and LIGHTEST. Works like OVERLAY, but not as harsh.<br><br>
 * DODGE - Lightens light tones and increases contrast, ignores darks. Called "Color Dodge" in Illustrator and Photoshop.<br><br>
 * BURN - Darker areas are applied, increasing contrast, ignores lights. Called "Color Burn" in Illustrator and Photoshop.<br><br>
 * All modes use the alpha information (highest byte) of source image pixels as the blending factor. If the source and destination regions are different sizes, the image will be automatically resized to match the destination size. If the <b>srcImg</b> parameter is not used, the display window is used as the source image.<br><br>
 * As of release 0149, this function ignores <b>imageMode()</b>.
 *
 * @webref
 * @brief  Copies a pixel or rectangle of pixels using different blending modes
 * @param src an image variable referring to the source image
 * @param sx X coordinate of the source's upper left corner
 * @param sy Y coordinate of the source's upper left corner
 * @param sw source image width
 * @param sh source image height
 * @param dx X coordinate of the destinations's upper left corner
 * @param dy Y coordinate of the destinations's upper left corner
 * @param dw destination image width
 * @param dh destination image height
 * @param mode Either BLEND, ADD, SUBTRACT, LIGHTEST, DARKEST, DIFFERENCE, EXCLUSION, MULTIPLY, SCREEN, OVERLAY, HARD_LIGHT, SOFT_LIGHT, DODGE, BURN
 *
 * @see processing.core.PGraphics#alpha(int)
 * @see processing.core.PGraphics#copy(PImage, int, int, int, int, int, int, int, int)
 * @see processing.core.PImage#blendColor(int,int,int)
 */
public void blend(PImage src, int sx, int sy, int sw, int sh, int dx, int dy,
                  int dw, int dh, int mode);

  
  ////////////////////////////// DESKTOP
	


  	//// ADDED MYSELF ////////////////////////

	//TODO getGL(), beginGL(), endGL()

//// ADDED MYSELF ////////////////////////

	public PGraphics getPGraphics();
	
//	public PMatrix3D getModelView();
//	
//	public PMatrix3D getModelViewInv();

	public GLCommon beginGL();
    
    public void endGL();


}
