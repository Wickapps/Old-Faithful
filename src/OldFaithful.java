import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import weka.core.converters.ConverterUtils;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/*
 * Copyright (C) 2018 Mark Wickham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either implied...
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * For full details of the license, please refer to the link referenced above.
 */

public class OldFaithful extends JFrame {
	
	static JFrame jf;	
	static GroupLayout layout;
	static JTextField waitTime, eruptTime;
	static JLabel priorLabel;
	static TitledBorder tBorderWarm, tBorderHot, tBorderUnknown;
    static Border borderOrange = BorderFactory.createLineBorder(Color.orange, 5, true);
    static Border borderRed = BorderFactory.createLineBorder(Color.red, 5, true);
    static Border borderGray = BorderFactory.createLineBorder(Color.gray, 5, true);
    static String modeTitle = "Old Faithful Mode";
	
    // The windows filenames are next
	static String wekaDataStrWin =            "C:/WekaFiles/old_faithful_single_instance.arff";
    static File wekaModelFileWin = new File(  "C:/WekaFiles/old_faithful_rf_i10.model" );
    
    // Alternatively, the Unix filenames are next
	static String wekaDataStrUnix =            "/home/pi/Java-proj/Weka/old_faithful_single_instance.arff";
	static File wekaModelFileUnix = new File(  "/home/pi/Java-proj/Weka/old_faithful_rf_i10.model" );
    
	static Classifier rf;
    static Instances dataSet;
	
	public OldFaithful() {
		super("OldFaithful");
    	// Init Frame
		JFrame.setDefaultLookAndFeelDecorated(true);
    	jf = new JFrame();
    	jf.setTitle("Old Faithful Geyser Clasifier");
		jf.setResizable(true);
		jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
		jf.setUndecorated(false);	// true for no title and menu	
    	jf.setVisible(true);
        layout = new GroupLayout(jf.getContentPane());
        jf.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
	}
	    
	public static void main(String args[]){
		// Run the constructor
		new OldFaithful();
	    try {
	        // Load a Test data instance so we can classify more easily
	    	ConverterUtils.DataSource sourceTest = null;
	        sourceTest = new ConverterUtils.DataSource(wekaDataStrUnix);
	        dataSet = sourceTest.getDataSet();
	        // Set the class attribute (Label) as the last class, the ClusterID
	        dataSet.setClassIndex(3);
	        
	        // Load the model, a RF model created from the ARFF data and saved in Weka Explorer
	    	FileInputStream fis = new FileInputStream(wekaModelFileUnix);
	        rf = (Classifier) weka.core.SerializationHelper.read(fis);
	        fis.close();
	        
	        // Setup the labels
            JLabel labelWait = new JLabel("Enter Waiting Time:");
            JLabel labelErupt = new JLabel("Enter Eruption Time:");
            JLabel labelResult = new JLabel("Results:");
            
            // Set the initial mode to Unknown
            tBorderUnknown = BorderFactory.createTitledBorder(borderGray, 
 				   modeTitle, 
 				   TitledBorder.CENTER, 
 				   TitledBorder.CENTER,
 				   Font.decode("Arial-bold-14"));
	        priorLabel = new JLabel(" Unknown Mode ");
	        priorLabel.setFont(Font.decode("Arial-bold-28"));
	        priorLabel.setBorder(tBorderUnknown);
            
	        // Predict Text Edit Fields
            waitTime = new JTextField();
            eruptTime = new JTextField();
            
            // Classify button
            JButton classifyButton = new JButton("Predict Geyser Mode");
            classifyButton.addActionListener(new ActionListener() { 
            	public void actionPerformed(ActionEvent e) { 
            		predictButtonPressed();
            	} 
            } );

            // Build the layout using the Swing GroupLayout
            layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
            		.addComponent(labelWait)
            		.addComponent(labelErupt))
                .addGroup(layout.createParallelGroup()
                    .addComponent(waitTime)
                    .addComponent(eruptTime)
                    .addComponent(classifyButton)
                    .addComponent(labelResult)
                    .addComponent(priorLabel))
            );
            layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                    .addComponent(labelWait)
                    .addComponent(waitTime))
                .addGroup(layout.createParallelGroup()
                    .addComponent(labelErupt)
                    .addComponent(eruptTime))
                .addComponent(classifyButton)
                .addComponent(labelResult)
                .addComponent(priorLabel)
            );
         
            jf.pack();            
	    	jf.validate();
			jf.repaint();
	
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private static void predictButtonPressed() {
		// Get a prediction from the classifer and update the Geyser Mode
		try {
			double wait = Double.valueOf(waitTime.getText());
			double erupt = Double.valueOf(eruptTime.getText());
			
			// Create a new instance to classify
			Instance newInst = new DenseInstance(4);
			newInst.setDataset(dataSet);
			newInst.setValue(0, 0);
			newInst.setValue(1, erupt);
			newInst.setValue(2, wait);
			newInst.setValue(3, 0);
			
			// Classify the Instance
	        double result = rf.classifyInstance(newInst);
	        
	        if (result == 1.0) setGeyserMode(1); 
	        else if (result == 0.0) setGeyserMode(2);
	        else setGeyserMode(0);
		}
		catch (NumberFormatException e) {
		     //Not a double so set unknown mode
			setGeyserMode(0);
		}    
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private static void setGeyserMode(int mode) {
		// Udate the results depending on the mode the RF classifier has returned
        JLabel label = null;
        if (mode == 2) {
            tBorderHot = BorderFactory.createTitledBorder(borderRed, 
 				   modeTitle, 
 				   TitledBorder.CENTER, 
 				   TitledBorder.CENTER,
 				   Font.decode("Arial-bold-14"));
	        label  = new JLabel("   Hot Mode   ");
	        label.setFont(Font.decode("Arial-bold-28"));
	        label.setBorder(tBorderHot);
        } else if (mode == 1) {
            tBorderWarm = BorderFactory.createTitledBorder(borderOrange, 
 				   modeTitle, 
 				   TitledBorder.CENTER, 
 				   TitledBorder.CENTER,
 				   Font.decode("Arial-bold-14"));
	        label  = new JLabel("   Warm Mode  ");
	        label.setFont(Font.decode("Arial-bold-28"));
	        label.setBorder(tBorderWarm);        	
        } else {
            tBorderUnknown = BorderFactory.createTitledBorder(borderGray, 
 				   modeTitle, 
 				   TitledBorder.CENTER, 
 				   TitledBorder.CENTER,
 				   Font.decode("Arial-bold-14"));
	        label  = new JLabel(" Unknown Mode ");
	        label.setFont(Font.decode("Arial-bold-28"));
	        label.setBorder(tBorderUnknown);
        }
        layout.replace(priorLabel, label);
        // reset the priorLabel so it can be updated next time
        priorLabel = label;
	}
}
