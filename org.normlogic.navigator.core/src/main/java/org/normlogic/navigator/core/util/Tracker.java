/*******************************************************************************
 * Copyright (c) 2015 Georg Schwarz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Georg Schwarz - initial API and implementation
 ******************************************************************************/
package org.normlogic.navigator.core.util;

public class Tracker {
	long time;
	String text;
	int trackNumber;
	Tracker(long time, String text) {
		this.time = time;
		this.text = text;
		trackNumber = globalTrackNumber++;
	}
	static int globalTrackNumber = 0;
	static public Tracker start(String text) {
		for (int i = 0; i<globalTrackNumber; i++) System.out.print("  ");
		System.out.println("-> Start Tracking: " + text);
		return new Tracker(System.currentTimeMillis(), text);
	}
	public void stop() {
		for (int i = 0; i<trackNumber; i++) System.out.print("  ");
		System.out.println("<- Stop Tracking: " + text + ", (" + (System.currentTimeMillis()-time) + "ms)");
		time = System.currentTimeMillis();
		globalTrackNumber--;
	}
}
