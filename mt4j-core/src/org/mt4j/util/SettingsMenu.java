package org.mt4j.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import processing.core.PApplet;

public class SettingsMenu extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String classToInstantiate;

//	private JLabel displayLabel;
//
//	private JTextField displayField;

	private JCheckBox fullScreenCheckBox;

	private JButton startButton;

	private JCheckBox fullScreenExclusiveCheckBox;

	private JLabel widthLabel;

	private JTextField widthField;

	private JLabel heightLabel;

	private JTextField heightField;

	private JCheckBox verticalSyncCheckbox;

	private JLabel frameRateLabel;

	private JTextField frameRateField;

	private JComboBox displayComboBox;

	private JLabel displayLabel;

	private JComboBox rendererComboBox;

	private JLabel numSamplesLabel;

	private JComboBox numSamplesComboBox;

	private JLabel rendererLabel; 

	
	public SettingsMenu(String classToInstantiate){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("MT4j Settings");
		
		this.classToInstantiate = classToInstantiate;
		
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Set System L&F
	    } catch (UnsupportedLookAndFeelException e) {
	       e.printStackTrace();
	    } catch (ClassNotFoundException e) {
	    	e.printStackTrace();
	    } catch (InstantiationException e) {
	    	e.printStackTrace();
	    } catch (IllegalAccessException e) {
	    	e.printStackTrace();
	    }
		
//		this.getContentPane().setLayout(new SpringLayout());
		
		JPanel springPanel = new JPanel();
		springPanel.setLayout(new SpringLayout());
		
		getContentPane().setLayout(new BorderLayout(5, 5));
		
//		this.getContentPane().setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 10));
		
//		//GROUP LAYOUT
//	    GroupLayout layout = new GroupLayout(this.getContentPane());
//	    this.getContentPane().setLayout(layout);
//	    //We specify automatic gap insertion:
//	    layout.setAutoCreateGaps(true);
//	    layout.setAutoCreateContainerGaps(true);
	    
		fullScreenCheckBox = new JCheckBox("Fullscreen", false);
		fullScreenExclusiveCheckBox = new JCheckBox("Exclusive Fullscreen Mode", false);
		
		widthField = new JTextField("1024");
		widthLabel = new JLabel("Window Width:");
		widthLabel.setLabelFor(widthField);
		
		heightField = new JTextField("768");
		heightLabel = new JLabel("Window Height:");
		heightLabel.setLabelFor(heightField);
		
		verticalSyncCheckbox = new JCheckBox("Vertical Screen Sync", false);
		
		frameRateLabel = new JLabel("Max. Framerate:");
		frameRateField = new JTextField("60");
		
		rendererComboBox = new JComboBox(new String[]{"OpenGL (HW-Accel.)", "P3D (Software)"});
		rendererLabel = new JLabel("Renderer:");
		
		numSamplesLabel = new JLabel("OpenGL Multisampling Level:");
		numSamplesComboBox = new JComboBox(new String[]{"0", "2", "4", "8"});
		
		JPanel panel = new JPanel();
		
		startButton = new JButton("Start");
		startButton.setPreferredSize(new Dimension(100, 50));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startMT4jApp();
			}
		});
		
		JComponent comptToAddTo = springPanel;
		
		comptToAddTo.add(rendererLabel);
		comptToAddTo.add(rendererComboBox);
		
		comptToAddTo.add(widthLabel);
		comptToAddTo.add(widthField);
		
		comptToAddTo.add(heightLabel);
		comptToAddTo.add(heightField);
		
		comptToAddTo.add(fullScreenCheckBox);
		comptToAddTo.add(fullScreenExclusiveCheckBox);
		
		comptToAddTo.add(frameRateLabel);
		comptToAddTo.add(frameRateField);
		
		comptToAddTo.add(numSamplesLabel);
		comptToAddTo.add(numSamplesComboBox);
		

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try { 
			GraphicsDevice[] gs = ge.getScreenDevices(); // Get number of screens 
			int numScreens = gs.length; 
			
			String[] st = new String[numScreens];
			for (int i = 1; i < (st.length+1); i++) {
				st[i-1] = String.valueOf(i);
			}
			displayComboBox = new JComboBox(st);
			displayLabel = new JLabel("Screen:");
			
			comptToAddTo.add(displayLabel);
			comptToAddTo.add(displayComboBox);
		} catch (HeadlessException e) { // Is thrown if there are no screen devices 
			System.err.println("Couldnt retrieve number of Screens");
		}
		
		comptToAddTo.add(panel);
		comptToAddTo.add(verticalSyncCheckbox);
		
		getContentPane().add(springPanel, BorderLayout.NORTH);
		getContentPane().add(startButton, BorderLayout.SOUTH);
		
		//FIXME For SpringLayout
		makeCompactGrid(comptToAddTo,
				(int)Math.round(Math.floor(comptToAddTo.getComponentCount()/2.0)), 2, 
				5, 5,
				5, 5);
		
		this.pack();
		this.setAlwaysOnTop(true);
		
		//Center on screen
//        Point center = ge.getCenterPoint();
        Rectangle bounds = ge.getMaximumWindowBounds();
//        int w = Math.max(bounds.width/2, Math.min(this.getWidth(), bounds.width));
//        int h = Math.max(bounds.height/2, Math.min(this.getHeight(), bounds.height));
//        int x = center.x - w/2, y = center.y - h/2;
//        this.setBounds(x, y, w, h);
//        if (w == bounds.width && h == bounds.height)
//        	this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setLocation(new Point(bounds.width/2 - this.getWidth()/2, bounds.height/2 - this.getHeight()/2));
       
        this.validate();
        
        startButton.requestFocusInWindow();
	}
	
	
	
	private void startMT4jApp(){
		//Set which display to use
		String displayString = "--display=" + 1;
		if (displayComboBox != null){
			displayString = "--display=" + displayComboBox.getSelectedItem().toString();
		}
		
		MT4jSettings.getInstance().display = Integer.parseInt(displayComboBox.getSelectedItem().toString());
		
		//Set Fullscreen mode
		MT4jSettings.fullscreen = fullScreenCheckBox.isSelected();
		MT4jSettings.getInstance().fullscreenExclusive = fullScreenExclusiveCheckBox.isSelected();
		
		//Set screen dimensions
		MT4jSettings.getInstance().windowWidth = Integer.parseInt(widthField.getText().trim());
		MT4jSettings.getInstance().windowHeight = Integer.parseInt(heightField.getText().trim());
		
		//Set Max framerate
		MT4jSettings.getInstance().maxFrameRate = Integer.parseInt(frameRateField.getText().trim());

		//Set vertical sync
		MT4jSettings.getInstance().vSync = verticalSyncCheckbox.isSelected();

		//Set Renderer
		MT4jSettings.getInstance().renderer = (rendererComboBox.getSelectedItem().toString().startsWith("Open"))? MT4jSettings.OPENGL_MODE : MT4jSettings.P3D_MODE;
		
		//Set opengl multisampling value
		MT4jSettings.getInstance().numSamples = Integer.parseInt(numSamplesComboBox.getSelectedItem().toString());

//		/*
		//Print settings
		System.out.println("Renderer: " + MT4jSettings.getInstance().getRendererMode());
		System.out.println("Window Width: " + MT4jSettings.getInstance().getWindowWidth());
		System.out.println("Window Height: " + MT4jSettings.getInstance().getWindowHeight());

		System.out.println("Fullscreen: " + MT4jSettings.getInstance().isFullscreen());
		System.out.println("Fullscreen Exclusive: " + MT4jSettings.getInstance().isFullscreenExclusive());

		System.out.println("Framerate: " + MT4jSettings.getInstance().getMaxFrameRate());
		System.out.println("Multisampling samples: " + MT4jSettings.getInstance().getNumSamples());
		
		System.out.println("Display: " + displayComboBox.getSelectedItem().toString());
		System.out.println("Vertical Synchronization: " + MT4jSettings.getInstance().isVerticalSynchronization());
//		*/
		
		this.setVisible(false);
		
		// Launch processing PApplet main() function
	    if (MT4jSettings.getInstance().isFullscreen()){
	    	//Set screen size to screen dimensions if fullscreen and ignore custom sizes
	    	if (MT4jSettings.getInstance().isFullscreenExclusive()){
	    		PApplet.main(new String[] {
	    				displayString,
						   "--present", 
						   "--exclusive", 
						   "--bgcolor=#000000", 
						   "--hide-stop",
						   classToInstantiate
	    		}); 
	    	}else{
	    		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		    	MT4jSettings.getInstance().windowWidth = screenSize.width;
		    	MT4jSettings.getInstance().windowHeight = screenSize.height;
	    		PApplet.main(new String[] {
	    				displayString,
						   "--present", 
						   "--bgcolor=#000000", 
						   "--hide-stop",
						   classToInstantiate
	    		}); 
	    	}
	    }else{
	    	PApplet.main(new String[] { 
	    			 displayString,
	    			 classToInstantiate }); 
	    }
	    
	    this.dispose();
	}
	
	
	
	
    /**
     * Aligns the first <code>rows</code> * <code>cols</code>
     * components of <code>parent</code> in
     * a grid. Each component is as big as the maximum
     * preferred width and height of the components.
     * The parent is made just big enough to fit them all.
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param initialX x location to start the grid at
     * @param initialY y location to start the grid at
     * @param xPad x padding between cells
     * @param yPad y padding between cells
     */
    public static void makeGrid(Container parent,
                                int rows, int cols,
                                int initialX, int initialY,
                                int xPad, int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout)parent.getLayout();
        } catch (ClassCastException exc) {
            System.err.println("The first argument to makeGrid must use SpringLayout.");
            return;
        }

        Spring xPadSpring = Spring.constant(xPad);
        Spring yPadSpring = Spring.constant(yPad);
        Spring initialXSpring = Spring.constant(initialX);
        Spring initialYSpring = Spring.constant(initialY);
        int max = rows * cols;

        //Calculate Springs that are the max of the width/height so that all
        //cells have the same size.
        Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).
                                    getWidth();
        Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).
                                    getWidth();
        for (int i = 1; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(
                                            parent.getComponent(i));

            maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
            maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
        }

        //Apply the new width/height Spring. This forces all the
        //components to have the same size.
        for (int i = 0; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(
                                            parent.getComponent(i));

            cons.setWidth(maxWidthSpring);
            cons.setHeight(maxHeightSpring);
        }

        //Then adjust the x/y constraints of all the cells so that they
        //are aligned in a grid.
        SpringLayout.Constraints lastCons = null;
        SpringLayout.Constraints lastRowCons = null;
        for (int i = 0; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
            if (i % cols == 0) { //start of new row
                lastRowCons = lastCons;
                cons.setX(initialXSpring);
            } else { //x position depends on previous component
                cons.setX(Spring.sum(lastCons.getConstraint(SpringLayout.EAST), xPadSpring));
            }

            if (i / cols == 0) { //first row
                cons.setY(initialYSpring);
            } else { //y position depends on previous row
                cons.setY(Spring.sum(lastRowCons.getConstraint(SpringLayout.SOUTH), yPadSpring));
            }
            lastCons = cons;
        }

        //Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH,
                            Spring.sum(
                                Spring.constant(yPad),
                                lastCons.getConstraint(SpringLayout.SOUTH)));
        pCons.setConstraint(SpringLayout.EAST,
                            Spring.sum(
                                Spring.constant(xPad),
                                lastCons.getConstraint(SpringLayout.EAST)));
    }

    /* Used by makeCompactGrid. */
    private static SpringLayout.Constraints getConstraintsForCell(
                                                int row, int col,
                                                Container parent,
                                                int cols) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }

    /**
     * Aligns the first <code>rows</code> * <code>cols</code>
     * components of <code>parent</code> in
     * a grid. Each component in a column is as wide as the maximum
     * preferred width of the components in that column;
     * height is similarly determined for each row.
     * The parent is made just big enough to fit them all.
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param initialX x location to start the grid at
     * @param initialY y location to start the grid at
     * @param xPad x padding between cells
     * @param yPad y padding between cells
     */
    public static void makeCompactGrid(Container parent,
                                       int rows, int cols,
                                       int initialX, int initialY,
                                       int xPad, int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout)parent.getLayout();
        } catch (ClassCastException exc) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        //Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width,
                                   getConstraintsForCell(r, c, parent, cols).
                                       getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        //Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height,
                                    getConstraintsForCell(r, c, parent, cols).
                                        getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        //Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
    }



}
