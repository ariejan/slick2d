package org.newdawn.slick.tools.peditor;

import org.newdawn.slick.particles.ConfigurableEmitter;

/**
 * A set of controls for the spawning position of particles from the emitter
 *
 * @author kevin
 */
public class PositionControls extends ControlPanel implements InputPanelListener {
	/**
	 * Create a new set of particle spawn poistion controls
	 */
	public PositionControls() {
		setLayout(null);
	
		addMinMax("x", new MinMaxPanel("X Offset",-1000,1000,1,1,"The offset on the x-axis at which particles will appear"));
		addMinMax("y", new MinMaxPanel("Y Offset",-1000,1000,1,1,"The offset on the y-axis at which particles will appear"));
		addValue("spread", new ValuePanel("Spread Angle (degrees)",0,360,360,"The range of angles the particles can spew out in"));
		addValue("angularOffset", new ValuePanel("Angular Offset (degrees)",0,360,360,"The direction the particles should spill out at"));
		addMinMax("initialDistance", new MinMaxPanel("Initial Distance",0,10000,0,0,"The distance from the emitter center particles will appear at"));
	}

	/**
	 * @see org.newdawn.slick.tools.peditor.ControlPanel#linkEmitterToFields(org.newdawn.slick.particles.ConfigurableEmitter)
	 */
	protected void linkEmitterToFields(ConfigurableEmitter emitter) {
		link(emitter.xOffset, "x");
		link(emitter.yOffset, "y");
		link(emitter.spread, "spread");
		link(emitter.angularOffset, "angularOffset");
		link(emitter.initialDistance, "initialDistance");
	}

}
