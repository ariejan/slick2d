package org.newdawn.slick.tools.peditor;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A panel to allow editing on a single configurable value
 *
 * @author kevin
 */
public class ValuePanel extends DefaultPanel {
	/** The name given for the particlar value */
	private String name;
	/** The slider to set the value */
	private JSlider slider;
	/** The list of listeners to be notified of updates */
	private ArrayList listeners = new ArrayList();
	
	/**
	 * Create a new value panel for a single configurable emitter setting
	 * 
	 * @param name The name of the value
	 * @param min The minimum value allowed
	 * @param max The maximum value allowed
	 * @param value The initial value
	 * @param toolTip The description of the setting
	 */
	public ValuePanel(String name, int min, int max, int value, String toolTip) {
		setLayout(null);
		
		setToolTipText(toolTip);
		
		this.name = name;
		slider = new JSlider(min, max, value);
		slider.setBounds(10,20,260,40);
		slider.setFocusable(false);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing((max-min) / 3);
		slider.setMinorTickSpacing((max-min) / 10);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireUpdated(e.getSource());
			}
		});
		
		add(slider);
		setBorder(BorderFactory.createTitledBorder(name));
	}
	
	/**
	 * Set the new value
	 * 
	 * @param value The value to be assigned
	 */
	public void setValue(int value) {
		slider.setValue(value);
	}
	
	/**
	 * Get the current value
	 * 
	 * @return The current value
	 */
	public int getValue() {
		return slider.getValue();
	}
	
	/**
	 * Add a listener to be notified of updates to this panel
	 * 
	 * @param listener The listener to be notified of updates to this panel
	 */
	public void addListener(InputPanelListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Fire notification of updates to all listeners
	 * 
	 * @param source The source of the update
	 */
	private void fireUpdated(Object source) {
		for (int i=0;i<listeners.size();i++) {
			((InputPanelListener) listeners.get(i)).valueUpdated(this);
		}
	}
}
