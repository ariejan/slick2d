package org.newdawn.slick.tools.peditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;

import org.newdawn.slick.Color;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ConfigurableEmitter.ColorRecord;

/**
 * A panel allowing the configuration of the colour and alpha values of the 
 * particles.
 *
 * @author kevin
 */
public class ColorPanel extends ControlPanel {
	/** The editor used to define the change in color */
	private GradientEditor grad;
	/** True if we should ignore update events */
	private boolean blockUpdates = false;
	
	/**
	 * Create a new panel to allow particle colour configuration
	 */
	public ColorPanel() {
		grad = new GradientEditor();
		grad.setBorder(BorderFactory.createTitledBorder("Color Change"));
		grad.setBounds(0,0,280,100);
		add(grad);
		
		grad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateColors();
			}
		});
		
		yPos+=90;
		addValue("startAlpha",new ValuePanel("Starting Alpha",0,255,255,"The alpha value for particles at their birth"));
		addValue("endAlpha",new ValuePanel("Ending Alpha",0,255,0,"The alpha value for particles at their death"));
	}
	
	/**
	 * Update the state of the emitter based on colours in the editor
	 */
	private void updateColors() {
		if (blockUpdates) {
			return;
		}
		emitter.colors.clear();
		for (int i=0;i<grad.getControlPointCount();i++) {
			float pos = grad.getPointPos(i);
			java.awt.Color col = grad.getColor(i);
			Color slick = new Color(col.getRed() / 255.0f, col.getGreen() / 255.0f, col.getBlue() / 255.0f, 1.0f);
			
			emitter.addColorPoint(pos, slick);
		}
	}
	
	/**
	 * @see org.newdawn.slick.tools.peditor.ControlPanel#linkEmitterToFields(org.newdawn.slick.particles.ConfigurableEmitter)
	 */
	protected void linkEmitterToFields(ConfigurableEmitter emitter) {
		blockUpdates = true;
		link(emitter.startAlpha, "startAlpha");
		link(emitter.endAlpha, "endAlpha");
		
		grad.clearPoints();
		Color start = ((ColorRecord) emitter.colors.get(0)).col;
		Color end = ((ColorRecord) emitter.colors.get(emitter.colors.size()-1)).col;
		
		grad.setStart(new java.awt.Color(start.r,start.g,start.b,1.0f));
		grad.setEnd(new java.awt.Color(end.r,end.g,end.b,1.0f));
		
		for (int i=1;i<emitter.colors.size()-1;i++) {
			float pos = ((ColorRecord) emitter.colors.get(i)).pos;
			Color col = ((ColorRecord) emitter.colors.get(i)).col;
			grad.addPoint(pos, new java.awt.Color(col.r,col.g,col.b,1.0f));
		}
		blockUpdates = false;
	}

}
