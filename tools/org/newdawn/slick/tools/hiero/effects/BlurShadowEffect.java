package org.newdawn.slick.tools.hiero.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * The distance the shadow falls
 *
 * @author kevin
 * @hack jos :f
 */
public class BlurShadowEffect implements Effect {
	/** The numberof kernels to apply */
    public static final int NUM_KERNELS = 16;
    /** The blur kernels applied across the effect */ 
	public static final float[][] GAUSSIAN_BLUR_KERNELS = generateGaussianBlurKernels(NUM_KERNELS);

    /** The shadow distance */
    private float distance = 3;
    /** The size of the blur kernel applied */
    private float kernelsize = 3;
    /** The number of blur passes */
    private float passes = 1;
    /** The opacity of the blur effect */
    private float opacity = 0.5f;
    /** The color of the outline */
    private Color col = new Color(0,0,0);
    /** The configuration panel for this effect */
    private ConfigPanel confPanel = new ConfigPanel();

    /**
     * @see org.newdawn.slick.tools.hiero.effects.Effect#postGlyphRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext, org.newdawn.slick.tools.hiero.effects.Glyph)
     */
    public void postGlyphRender(Graphics2D g, DrawingContext context, Glyph glyph) {
    }

    /**
     * @see org.newdawn.slick.tools.hiero.effects.Effect#postPageRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext)
     */
    public void postPageRender(Graphics2D g, DrawingContext context) {
    }

    /**
     * @see org.newdawn.slick.tools.hiero.effects.Effect#preGlyphRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext, org.newdawn.slick.tools.hiero.effects.Glyph)
     */
    public void preGlyphRender(Graphics2D g, DrawingContext context, Glyph glyph) {
        BufferedImage src=new BufferedImage(glyph.getWidth(), glyph.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2=(Graphics2D)src.getGraphics();
        Color col2=new Color(col.getRed(),col.getGreen(),col.getBlue(),Math.round(opacity*255));
        g2.setColor(col2);
        g2.translate(glyph.getDrawX()+distance-glyph.getX(), glyph.getDrawY()+distance-glyph.getY());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fill(glyph.getOutlineShape());
        g2.dispose();
        
		if(kernelsize>1){
			BufferedImage blurred=blurImage(src,Math.round(kernelsize),Math.round(passes));
        	g.drawImage(blurred,glyph.getX(),glyph.getY(),null);
		}else{
			g.drawImage(src,glyph.getX(),glyph.getY(),null);
		}
    }

    /**
     * @see org.newdawn.slick.tools.hiero.effects.Effect#prePageRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext)
     */
    public void prePageRender(Graphics2D g, DrawingContext context) {
    }

    /**
     * @see org.newdawn.slick.tools.hiero.effects.Effect#getConfigurationPanel()
     */
    public JPanel getConfigurationPanel() {
        confPanel.newCol = col;

        return confPanel;
    }

    /**
     * @see org.newdawn.slick.tools.hiero.effects.Effect#getEffectName()
     */
    public String getEffectName() {
        return "BlurShadowEffect (jos)";
    }

    /**
     * @see org.newdawn.slick.tools.hiero.effects.Effect#getInstance()
     */
    public Effect getInstance() {
        return new BlurShadowEffect();
    }

    /**
     * @see org.newdawn.slick.tools.hiero.effects.Effect#setConfigurationFromPanel(javax.swing.JPanel)
     */
    public void setConfigurationFromPanel(JPanel panel) {
        col = confPanel.newCol;
        distance = ((Integer) confPanel.dspinner.getValue()).intValue();
        kernelsize = ((Integer) confPanel.kspinner.getValue()).intValue();
        passes = ((Integer) confPanel.pspinner.getValue()).intValue();
        opacity = (float)((Double) confPanel.ospinner.getValue()).doubleValue();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
		int c=(Math.round(opacity*255)<<24)|(col.getRed()<<16)|(col.getGreen()<<8)|(col.getBlue());
        return "BlurShadow (d:"+Math.round(distance)+"|k:"+Math.round(kernelsize)+"|p:"+Math.round(passes)+"|c:"+Integer.toHexString(c)+")";
    }

    /**
     * Blur the image
     * 
     * @param sourceImage The image to blur
     * @param kernelSize The kernel to apply
     * @param numOfPasses The number of passes of blurring
     * @return The blurred image
     */
	private BufferedImage blurImage(BufferedImage sourceImage, int kernelSize, int numOfPasses){
		if (kernelSize<1 || kernelSize>NUM_KERNELS){
			return sourceImage;
		}

		if (numOfPasses<1){
			return sourceImage;
		}

		float[] matrix=GAUSSIAN_BLUR_KERNELS[kernelSize - 1];

		Kernel gaussianBlur1=new Kernel(matrix.length, 1, matrix);
		Kernel gaussianBlur2=new Kernel(1, matrix.length, matrix);
		RenderingHints hints=new RenderingHints(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		ConvolveOp gaussianOp1=new ConvolveOp(gaussianBlur1, ConvolveOp.EDGE_NO_OP, hints);
		ConvolveOp gaussianOp2=new ConvolveOp(gaussianBlur2, ConvolveOp.EDGE_NO_OP, hints);

		BufferedImage tempImage=new BufferedImage(sourceImage.getWidth(),sourceImage.getHeight(), sourceImage.getType());
		BufferedImage finalImage=new BufferedImage(sourceImage.getWidth(),sourceImage.getHeight(), sourceImage.getType());

		BufferedImage nextSource=sourceImage;

		for (int i=0; i<numOfPasses; i++){
			tempImage=gaussianOp1.filter(nextSource, tempImage);
			finalImage=gaussianOp2.filter(tempImage, finalImage);

			nextSource=finalImage;
		}

		return finalImage;
	}

	/**
	 * Generate the blur kernels which will be repeatedly applied when blurring images
	 * 
	 * @param level The number of kernels to generate
	 * @return The kernels generated
	 */
	private static float[][] generateGaussianBlurKernels(int level){
		float[][] pascalsTriangle=generatePascalsTriangle(level);
		float[][] gaussianTriangle=new float[pascalsTriangle.length][];

		for (int i=0; i<gaussianTriangle.length; i++){
			float total=0.0f;
			gaussianTriangle[i]=new float[pascalsTriangle[i].length];

			for (int j=0; j<pascalsTriangle[i].length; j++){
				total += pascalsTriangle[i][j];
			}

			float coefficient=1 / total;
			for (int j=0; j<pascalsTriangle[i].length; j++){
				gaussianTriangle[i][j]=coefficient * pascalsTriangle[i][j];
			}

		}

		return gaussianTriangle;
	}

	/**
	 * Generate Pascal's triangle (wtf?)
	 * 
	 * @param level The level of the triangle to generate
	 * @return The Pascal's triangle kernel
	 */
	private static float[][] generatePascalsTriangle(int level){
		if (level<2){
			level=2;
		}

		float[][] triangle=new float[level][];
		triangle[0]=new float[1];
		triangle[1]=new float[2];
		triangle[0][0]=1.0f;
		triangle[1][0]=1.0f;
		triangle[1][1]=1.0f;

		for (int i=2; i<level; i++){
			triangle[i]=new float[i+1];
			triangle[i][0]=1.0f;
			triangle[i][i]=1.0f;
			for (int j=1; j<triangle[i].length-1;j++){
				triangle[i][j]=triangle[i-1][j-1] + triangle[i-1][j];
			}
		}

		return triangle;
	}

    /**
     * A panel to configure this effect
     *
     * @author kevin
     */
    private class ConfigPanel extends JPanel {
        /** The width information */
        private JSpinner dspinner = new JSpinner(new SpinnerNumberModel(3,0,10,1));
        /** The kernel count widget */
        private JSpinner kspinner = new JSpinner(new SpinnerNumberModel(3,1,10,1));
        /** The passes count widget */
        private JSpinner pspinner = new JSpinner(new SpinnerNumberModel(1,1,10,1));
        /** The opacity count widget */
        private JSpinner ospinner = new JSpinner(new SpinnerNumberModel(0.5,0.0,1.0,0.01));
        /** The button to change the color */
        private JButton colButton = new JButton("Set..");
        /** The color */
        private Color newCol;

        /**
         * Create a new configuration panel
         */
        public ConfigPanel() {
            setLayout(null);

            JLabel label = new JLabel("Distance");
            label.setBounds(5,0,200,20);
            add(label);
            dspinner.setBounds(5,25,200,20);
            add(dspinner);

			label = new JLabel("Kernel Size");
			label.setBounds(5,50,200,20);
			add(label);
			kspinner.setBounds(5,75,200,20);
			add(kspinner);

			label = new JLabel("Passes");
			label.setBounds(5,100,200,20);
			add(label);
			pspinner.setBounds(5,125,200,20);
			add(pspinner);

			label = new JLabel("Opacity");
			label.setBounds(5,150,200,20);
			add(label);
			ospinner.setBounds(5,175,200,20);
			add(ospinner);

            label = new JLabel("Color");
            label.setBounds(5,200,200,20);
            add(label);
            colButton.setBounds(5,225,150,20);
            add(colButton);

            colButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Color newCol2 = JColorChooser.showDialog(ConfigPanel.this, "Choose Shadow Color", newCol);
                    if(newCol2!=null)
                    	newCol=newCol2;
                }
            });

            setSize(300,250);
        }

        /**
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        public void paint(Graphics g) {
            super.paint(g);

            g.setColor(newCol);
            g.fillRect(160,225,100,20);
            g.setColor(Color.black);
            g.drawRect(160,225,100,20);
        }
    }
}