package basic.javaGUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import org.mt4j.AbstractMTApplication;
import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTOverlayContainer;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;


public class StartSwingExample extends JFrame {
	private static final long serialVersionUID = 1L;

	//TODO init logging

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        	e.printStackTrace();
        }
//		StartSwingExample swingFrame = 
			new StartSwingExample();
	}
	
	
	public StartSwingExample(){
		this.setTitle("Swing Test");
		//Should be called early before initializing opengl stuff
		this.setVisible(true); 
		
		final Container content = this.getContentPane();
		this.setLayout(new BorderLayout());
        
		//Create our mt4j applet
        final AbstractMTApplication instance = new TestMTApplication();
        instance.frame = this; //Important for registering the Windows 7 Touch input
        instance.init();
        
        JPanel panel1 = new JPanel(new FlowLayout());
        content.add(panel1, BorderLayout.WEST);
       
        JButton e2 = new JButton("Clear");
        e2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (instance.getCurrentScene() != null){
					instance.invokeLater(new Runnable() {
						public void run() {
							MTComponent[] ch = instance.getCurrentScene().getCanvas().getChildren();
                            for (MTComponent mtComponent : ch) {
                                if (!(mtComponent instanceof MTOverlayContainer)) {
                                    mtComponent.destroy();
                                }
                            }
						}
					});
					
				}
			}
		});
        panel1.add(e2); // Add components to the content
        
        //Add MT4j applet
        JPanel pane = new JPanel(new GridLayout(0,1));
        pane.add(instance);
        content.add(pane, BorderLayout.SOUTH);
        
        /////////MEnu
        //So that the menu will overlap the heavyweight opengl canvas
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        
        //Create the menu bar.
        JMenuBar menuBar = new JMenuBar();

        //Build the first menu.
        JMenu menu = new JMenu("Add");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);

        JMenuItem addRectItem = new JMenuItem("MTRectangle", KeyEvent.VK_T);
        addRectItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (instance.getCurrentScene() != null){
					//If we want to modify the MT4j applet from the swing thread
					//we should wrap the actions into an invokeLater runnable which 
					//will be executed the next time the mt4j thread runs.
					instance.invokeLater(new Runnable() {
						public void run() {
							MTRectangle r = new MTRectangle(instance,0,0,ToolsMath.getRandom(50, 250), ToolsMath.getRandom(50, 250));
							r.setFillColor(new MTColor(ToolsMath.getRandom(50,255),ToolsMath.getRandom(50,255),ToolsMath.getRandom(50,255)));
							r.addGestureListener(DragProcessor.class, new InertiaDragAction());
							instance.getCurrentScene().getCanvas().addChild(r);
							r.setPositionGlobal(new Vector3D(ToolsMath.getRandom(0, instance.width), ToolsMath.getRandom(0, instance.height)));
						}
					});
					
				}
			}
		});
        menu.add(addRectItem);
        
        JMenuItem addRoundRectItem = new JMenuItem("MTRoundRectangle", KeyEvent.VK_E);
        addRoundRectItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (instance.getCurrentScene() != null){
					//If we want to modify the MT4j applet from the swing thread
					//we should wrap the actions into an invokeLater runnable which 
					//will be executed the next time the mt4j thread runs.
					instance.invokeLater(new Runnable() {
						public void run() {
							float arc = ToolsMath.getRandom(8, 25);
							MTRoundRectangle r = new MTRoundRectangle(instance,0,0, 0,ToolsMath.getRandom(50, 250), ToolsMath.getRandom(50, 250), arc, arc);
							r.setFillColor(new MTColor(ToolsMath.getRandom(50,255),ToolsMath.getRandom(50,255),ToolsMath.getRandom(50,255)));
							r.addGestureListener(DragProcessor.class, new InertiaDragAction());
							instance.getCurrentScene().getCanvas().addChild(r);
							r.setPositionGlobal(new Vector3D(ToolsMath.getRandom(0, instance.width), ToolsMath.getRandom(0, instance.height)));
						}
					});
					
				}
			}
		});
        menu.add(addRoundRectItem);
        
        JMenuItem addEllItem = new JMenuItem("MTEllipse", KeyEvent.VK_E);
        addEllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (instance.getCurrentScene() != null){
					//If we want to modify the MT4j applet from the swing thread
					//we should wrap the actions into an invokeLater runnable which 
					//will be executed the next time the mt4j thread runs.
					instance.invokeLater(new Runnable() {
						public void run() {
							MTEllipse e = new MTEllipse(instance, new Vector3D(0,0), ToolsMath.getRandom(50, 150),ToolsMath.getRandom(50, 150));
							e.setFillColor(new MTColor(ToolsMath.getRandom(50,255),ToolsMath.getRandom(50,255),ToolsMath.getRandom(50,255)));
							e.addGestureListener(DragProcessor.class, new InertiaDragAction());
							instance.getCurrentScene().getCanvas().addChild(e);
							e.setPositionGlobal(new Vector3D(ToolsMath.getRandom(0, instance.width), ToolsMath.getRandom(0, instance.height)));
						}
					});
					
				}
			}
		});
        menu.add(addEllItem);
        
        //Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        JMenuItem aboutItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JDialog f = new SimpleAboutDialog(StartSwingExample.this);
			    f.setVisible(true);
			}
		});
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        
        this.setJMenuBar(menuBar);
        ////////Menu
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();   // does layout of components.
	}
	
	
	private class TestMTApplication extends MTApplication{
		private static final long serialVersionUID = 1L;

		@Override
		public void startUp() {
			//This causeD the craetion of a new rendrer (-> new opengl context)
			//so opengl objects like displaylists etc would get deleted!
//			setPreferredSize(new Dimension(MT4jSettings.getInstance().getScreenWidth(),MT4jSettings.getInstance().getScreenHeight()));
//			pack();
//			setResizable(false);
			
			this.addScene(new SwingIntegrationScene(this, "test scene"));
		}
	}
	
	
	
	public class SimpleAboutDialog extends JDialog {
		private static final long serialVersionUID = 1L;

		public SimpleAboutDialog(JFrame parent) {
		    super(parent, "About", true);

		    Box b = Box.createVerticalBox();
		    b.add(Box.createGlue());
		    b.add(new JLabel(" 	MT4j example application"));
		    b.add(new JLabel(" 	This shows how to integrate MT4j into"));
		    b.add(new JLabel(" 	a java swing/awt application"));
		    b.add(new JLabel(" 	Visit www.mt4j.org"));
		    b.add(Box.createGlue());
		    getContentPane().add(b, BorderLayout.CENTER);

		    JPanel p2 = new JPanel();
		    JButton ok = new JButton("Ok");
		    p2.add(ok);
		    getContentPane().add(p2, BorderLayout.SOUTH);

		    ok.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		        setVisible(false);
		      }
		    });
		    setSize(250, 150);
		  }
		}
	
}
