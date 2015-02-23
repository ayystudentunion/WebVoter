/*
 *  Copyright (C) 2005 Johannes Heinonen <johannes.heinonen@iki.fi>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package codegen.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import codegen.Generator;

/**
 * Very simple user interface for the password generator.
 * @author Johannes Heinonen <johannes.heinonen@iki.fi>
 */
public class GeneratorUI extends JFrame {

    private JTextArea output;
    private JFileChooser fc;
    
    private String[] results;
    private boolean uniqueCodesState = true;
    
    private JTextField inputLetters = new JTextField("1234567890");
    private JTextField codeLength = new JTextField("5");
    private JTextField codeCount = new JTextField("50");
    private JCheckBox uniqueCodes = new JCheckBox("Kaikki salasanat erilaisia", uniqueCodesState);

    public GeneratorUI() {
        super("Salasanapulautin");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        centerWindow(this);
        createUI();
        this.setVisible(true);
    }

    private void centerWindow(Component c) {
	    float xWeight = 0.5f;
	    float yWeight = 0.5f;
	    
	    Rectangle r = c.getGraphicsConfiguration().getBounds();	    
	    int x = (int) (r.x + xWeight * (r.width - c.getWidth()));
	    int y = (int) (r.y + yWeight * (r.height - c.getHeight()));	    
	    c.setLocation(x, y);
    }
    
    private void createUI() {
        output = new JTextArea(1, 40);
        output.setEditable(false);
        output.setBorder(BorderFactory.createTitledBorder("Esikatselu"));
        fc = new JFileChooser();
        
        JPanel ui = new JPanel(new BorderLayout());
        ui.add(createUpperPanel(), BorderLayout.NORTH);
        ui.add(new JScrollPane(output), BorderLayout.CENTER);
        ui.add(createLowerPanel(), BorderLayout.SOUTH);
        this.getContentPane().add(ui);
    }
    
    private JComponent createUpperPanel() {
        JPanel p = new JPanel();
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.VERTICAL;
        
        // 1st column
        
        c.gridx = 0;
        c.gridy = 0;
        JLabel label1 = new JLabel("Merkistö: ");
        gridbag.setConstraints(label1, c);
        p.add(label1);

        c.gridx = 0;
        c.gridy = 1;
        JLabel label2 = new JLabel("Salasanojen pituus: ");
        gridbag.setConstraints(label2, c);
        p.add(label2);
        
        c.gridx = 0;
        c.gridy = 2;
        JLabel label3 = new JLabel("Salasanojen lukumäärä: ");
        gridbag.setConstraints(label3, c);
        p.add(label3);
        
        c.gridx = 0;
        c.gridy = 3;
        JLabel label4 = new JLabel("Salasanojen tyyppi:");
        gridbag.setConstraints(label4, c);
        p.add(label4);        
        
        // 2nd column
        
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.0;

        c.gridx = 1;
        c.gridy = 0;
        gridbag.setConstraints(inputLetters, c);
        p.add(inputLetters);
        
        c.gridx = 1;
        c.gridy = 1;
        gridbag.setConstraints(codeLength, c);
        p.add(codeLength);
        
        c.gridx = 1;
        c.gridy = 2;
        gridbag.setConstraints(codeCount, c);
        p.add(codeCount);
        
        c.gridx = 1;
        c.gridy = 3;
        gridbag.setConstraints(uniqueCodes, c);
        p.add(uniqueCodes);
        
        uniqueCodes.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                uniqueCodesState = e.getStateChange() == ItemEvent.SELECTED;
            }
        });
        
        // 3rd column
        
        JButton launchButton = new JButton("Luo salasanat");
        launchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action();
            }
        });
        
        c.gridheight = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;

        c.gridx = 2;
        c.gridy = 0;
        gridbag.setConstraints(launchButton, c);
        p.add(launchButton);

        return p;
    }
    
    private JComponent createLowerPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JButton saveAsFileButton = new JButton("Tallenna salasanat");
        saveAsFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int val = fc.showSaveDialog(GeneratorUI.this);
                if (val == JFileChooser.APPROVE_OPTION) {
                    store(fc.getSelectedFile());
                }
            }
        });
        
        p.add(saveAsFileButton, BorderLayout.CENTER);
        return p;
    }
    
    private void setText(String s) {
        output.setText(s);
        output.setCaretPosition(0);
    }
    
    private void setText(String[] s) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < s.length; i++) {
            b.append(s[i]);
            b.append('\n');
        }
        setText(b.toString());
    }
    
    private void action() {
        try {
            results = Generator.generateMulti(inputLetters.getText(),
                    Integer.parseInt(codeLength.getText()),
                    Integer.parseInt(codeCount.getText()),
                    uniqueCodesState);
            setText(results);
        } catch (NumberFormatException nfe) {
            setText("Salasanojen pituus ja lukumäärä tulee antaa numeroina!");
        } catch (NullPointerException npe) {
            setText("Salasanojen luominen antamillasi tiedoilla ei onnistunut!");
        }
    }
    
    private void store(File f) {
        try {
            dumpToFile(f, results);
            setText("Salasanat kirjoitettu tiedostoon " + f);
        } catch (IOException ioe) {
            setText("Virhe tietojen tallentamisessa!\n\nVirhe oli: " + ioe.getMessage());
        }
    }
    
    private void dumpToFile(File f, Object[] data) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(f));
        for (int i = 0; i < data.length; i++) {
            pw.println(data[i].toString());
        }
        pw.close();
    }
    
    public static void main(String[] args) {
        new GeneratorUI();
    }
}
