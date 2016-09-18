package view;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.text.NumberFormatter;

import control.Controller;
import model.Company;
import model.Model;
import model.Photo;

public class ControlWindow extends JFrame
{
	private static final long serialVersionUID = 1L;

	private static ControlWindow self = null;
	
	private JFormattedTextField thresholdField, clusterField;
	private JSlider thresholdSlider, clusterSlider;
	private ScrollLister<Company> companyLister;
	private ScrollLister<Photo> imageLister;
	JToggleButton[] transferButtons;
	
	private JComponent heightReference;

	public static ControlWindow getInstance()
	{
		if(self == null) self = new ControlWindow();
		return self;
	}
	
	private ControlWindow()
	{
		Controller controller = Controller.getInstance();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);

    	JLabel thresholdLabel = new JLabel("Threshold:");
    	JLabel clusterLabel = new JLabel("Cluster size:");
    	thresholdSlider = new JSlider(JSlider.HORIZONTAL, 0, Model.MAXTHRESHOLD, Model.threshold)
    	{
			private static final long serialVersionUID = 1L;
			public String toString() { return Controller.THRESHOLD; }
    	};
    	clusterSlider = new JSlider(JSlider.HORIZONTAL, 0, Model.MAXCLUSTER, Model.cluster)
    	{
			private static final long serialVersionUID = 1L;
			public String toString() { return Controller.CLUSTER; }
    	};
    	JSlider[] sl = {thresholdSlider, clusterSlider};
    	for(JSlider s : sl)
    	{
    		s.addChangeListener(controller);
        	s.setMajorTickSpacing(50);
        	s.setMinorTickSpacing(10);
        	s.setPaintTicks(true);
        	s.setPaintLabels(true);
    	}

    	thresholdField = new JFormattedTextField(getFormatter(0,255))
    	{
    		private static final long serialVersionUID = 1L;
    		public String toString() { return Controller.THRESHOLD; }
    	};
    	thresholdField.setText("" + Model.threshold);
    	thresholdField.setValue(Model.threshold);
    	thresholdField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                thresholdField.setText("");
            }
        });
    	clusterField = new JFormattedTextField(getFormatter(0,500))
    	{
			private static final long serialVersionUID = 1L;
			public String toString() { return Controller.CLUSTER; }
    	};
    	clusterField.setText("" + Model.cluster);
    	clusterField.setValue(Model.cluster);
    	clusterField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                clusterField.setText("");
            }
        });
    	
    	JButton copyButton = new JButton(Controller.COPY);
    	JButton pasteButton = new JButton(Controller.PASTE);
    	JButton detectButton = new JButton(Controller.DETECT);
    	JButton cancelButton = new JButton(Controller.SWITCH);
    	JLabel companiesLabel = new JLabel("Company");
    	JLabel transferLabel = new JLabel("Transfer");
    	JLabel imagesLabel = new JLabel("Images");
    	companyLister = new ScrollLister<Company>(controller)
    	{
			private static final long serialVersionUID = 1L;
			public String toString() { return Controller.COMPANY; }
    	};
    	ButtonGroup group = new ButtonGroup();
    	transferButtons = new JToggleButton[5];
    	for(int i = 0; i < 5; i++)
    	{
    		JToggleButton b = new JToggleButton("\u2190");
    		b.setActionCommand("" + i);
    		transferButtons[i] = b;
    		group.add(b);
    	}
    	imageLister = new ScrollLister<Photo>(controller)
    	{
    		private static final long serialVersionUID = 2L;
    		public String toString() { return Controller.PHOTO; }
    	};


    	Font f = generateFont();
    	JComponent[] comps = {null, null, null, null, null,
    			thresholdLabel, clusterLabel, companiesLabel, transferLabel, imagesLabel,
    			thresholdField, clusterField, copyButton, pasteButton, detectButton, cancelButton};
    	for(int i = 0; i < 5; i++) comps[i] = transferButtons[i];
    	for(JComponent comp : comps)
    	{
    		comp.setFont(f);
    	}
    	companyLister.setListFont(f.deriveFont(Font.PLAIN));
    	
    	setLayout(new GridBagLayout());
    	GridBagConstraints g = new GridBagConstraints();
    	g.weightx = 1;
    	g.weighty = 1;
    	g.insets = new Insets(10, 10, 10, 10);
    	g.anchor = GridBagConstraints.EAST;
    	g.fill = GridBagConstraints.BOTH;
    	
    	JPanel p = new JPanel();
    	p.setLayout(new GridBagLayout());
    	add(p, g);
    	
    	g.gridwidth = 1;
    	
    	g.weightx = 0;
    	g.fill = GridBagConstraints.NONE;
    	g.gridx = 0;
    	g.gridy = 0;
    	p.add(thresholdLabel, g);
    	g.gridx = 0;
    	g.gridy = 1;
    	p.add(clusterLabel, g);
    	
    	g.gridwidth = 3;
    	g.fill = GridBagConstraints.BOTH;
    	g.weightx = 1;

    	g.gridx = 1;
    	g.gridy = 0;
    	p.add(thresholdSlider, g);
    	g.gridx = 1;
    	g.gridy = 1;
    	p.add(clusterSlider, g);
    	
    	g.gridwidth = 1;

    	//Why is this necessary? I don't know. It's pretty annoying, though.
    	//(it fixes the sliders to actually span all three columns)
    	g.gridy = 0;
    	JLabel[] jl = {null, null, null};
    	for(int i = 0; i < 3; i++)
    	{
    		g.weightx = (i == 1 ? 0 : 1);
    		jl[i] = new JLabel();
    		g.gridx = i+1;
    		p.add(jl[i], g);
    	}
    	g.weightx = 0.0;
    	
    	g.gridx = 4;
    	g.gridy = 0;
    	p.add(thresholdField, g);
    	g.gridx = 4;
    	g.gridy = 1;
    	p.add(clusterField, g);
    	
    	g.weightx = 0.25;
    	g.gridx = 5;
    	g.gridy = 0;
    	p.add(copyButton, g);
    	g.gridx = 5;
    	g.gridy = 1;
    	p.add(pasteButton, g);
    	
    	g.gridx = 6;
    	g.gridy = 0;
    	p.add(detectButton, g);
    	g.gridx = 6;
    	g.gridy = 1;
    	p.add(cancelButton, g);
    	
    	g.weightx = 1;
    	g.gridwidth = 4;
    	g.gridx = 0;
    	
    	g.anchor = GridBagConstraints.CENTER;
    	g.fill = GridBagConstraints.NONE;
    	g.gridy = 2;
    	p.add(companiesLabel, g);
    	g.fill = GridBagConstraints.BOTH;
    	g.gridy = 3;
    	g.gridheight = 5;
    	p.add(companyLister, g);
    	g.gridheight = 1;
    	
    	g.weightx = 0;
    	g.gridwidth = 1;
    	g.gridx = 4;
    	g.gridy = 2;
    	p.add(transferLabel, g);
    	for(int i = 3; i < 8; i++)
    	{
    		g.gridy = i;
    		if(i == 3) g.insets = new Insets(20, 10, 10, 10);
    		if(i == 4) g.insets = new Insets(10, 10, 10, 10);
    		if(i == 7) g.insets = new Insets(10, 10, 20, 10);

    		p.add(transferButtons[i-3], g);
    	}
    	g.weightx = 1;

    	g.weightx = 0;
    	g.fill = GridBagConstraints.NONE;
    	g.gridwidth = 2;
    	g.gridx = 5;
    	g.gridy = 2;
    	p.add(imagesLabel, g);
    	g.fill = GridBagConstraints.BOTH;
    	g.gridy = 3;
    	g.gridheight = 5;
    	p.add(imageLister, g);
    	
    	AbstractButton[] actionMakers = {null, null, null, null, null,
    			copyButton, pasteButton, detectButton, cancelButton};
    	for(int i = 0; i < 5; i++) actionMakers[i] = transferButtons[i];
    	for(AbstractButton actionMaker : actionMakers)
    		actionMaker.addActionListener(controller);
    	thresholdField.addActionListener(controller);
    	clusterField.addActionListener(controller);
    	
    	companyLister.setList(Model.companies.keySet());
    	heightReference = transferButtons[0];
	}
	
	public static void fixCompanyHeights()
	{
		int targetSize = self.heightReference.getHeight()+19;
		self.companyLister.getList().setFixedCellHeight(targetSize);
		self.imageLister.getList().setFixedCellHeight(targetSize);
		int currentPoint = 80;
		boolean found = false;
		int targetWidth = self.companyLister.getWidth();
		while(!found)
		{
			Font f = generateFont().deriveFont(Font.PLAIN, currentPoint);
			FontMetrics fm = self.companyLister.getFontMetrics(f);
			double width = fm.getStringBounds("Mind's Eye Innovations", self.companyLister.getList().getGraphics()).getWidth();
			if(width - targetWidth < -5) found = true;
			else currentPoint--;
		}
		Font f = generateFont().deriveFont(Font.PLAIN, currentPoint);
		self.companyLister.setListFont(f);
		self.imageLister.setListFont(f);
	}
	
	private static Font generateFont()
	{
		return new Font(Font.SANS_SERIF, Font.BOLD, 65);
	}
	
	private static NumberFormatter getFormatter(int min, int max)
	{
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(min);
        formatter.setMaximum(max);
        return formatter;
	}
	
	public static JFormattedTextField getField(JSlider source)
	{
		if(source == self.thresholdSlider) return self.thresholdField;
		if(source == self.clusterSlider) return self.clusterField;
		return null;
	}
	
	public static JSlider getSlider(JFormattedTextField source)
	{
		if(source == self.thresholdField) return self.thresholdSlider;
		if(source == self.clusterField) return self.clusterSlider;
		return null;
	}
	
	public static int getThreshold()
	{ return self.thresholdSlider.getValue(); }
	
	public static int getCluster()
	{ return self.clusterSlider.getValue(); }
	
	public static void setCluster(int value)
	{
		self.clusterSlider.setValue(value);
		self.clusterField.setText("" + value);
		self.clusterField.setValue(value);
	}
	
	public static void setThreshold(int value)
	{
		self.thresholdSlider.setValue(value);
		self.thresholdField.setText("" + value);
		self.thresholdField.setValue(value);
	}
	
	public void repaint()
	{
		synchronizeModel();
		super.repaint();
	}
	
	private void synchronizeModel()
	{
		companyLister.setSelected(Model.currentCompany);
		toggle(companyLister.getSelectedIndex());
		imageLister.setList(Model.companies.get(Model.currentCompany));
		imageLister.setSelected(Model.currentPhoto);
		setThreshold(Model.threshold);
		setCluster(Model.cluster);
	}
	
	public static Company getCompany(int i)
	{	return self.companyLister.getElement(i);	}
	
	public static boolean toggle(int i)
	{
		if(i > 4 || i < 0) return false;
		boolean change = !self.transferButtons[i].isSelected();
		self.transferButtons[i].setSelected(true);
		return change;
	}
}
