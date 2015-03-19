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
package org.normlogic.navigator.app;

import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

class SimpleLayoutAlgorithm extends AbstractLayoutAlgorithm {
    
    public SimpleLayoutAlgorithm(final int styles) {
        super(styles);
    }
    
    @Override
    protected void applyLayoutInternal(final InternalNode[] entitiesToLayout, final InternalRelationship[] relationshipsToConsider, final double boundsX,
            final double boundsY, final double boundsWidth, final double boundsHeight) {
        
        for (int i = 0; i < entitiesToLayout.length; i++) {
            if (entitiesToLayout[i].getCurrentX() == 0) {
                int count = 0;
                for (int j = 0; j < relationshipsToConsider.length; j++) {
                    InternalNode dest = relationshipsToConsider[j].getDestination();
                    InternalNode src = relationshipsToConsider[j].getSource();
                    if (dest.equals(entitiesToLayout[i])) {
                        count++;
                        entitiesToLayout[i].setLocation(src.getCurrentX()+src.getWidthInLayout()+count*20, src.getCurrentY()-count*20);
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    protected int getCurrentLayoutStep() {
        return 0;
    }
    
    @Override
    protected int getTotalNumberOfLayoutSteps() {
        return 0;
    }
    
    @Override
    protected boolean isValidConfiguration(final boolean asynchronous,final boolean continuous) {
        return true;
    }
    
    @Override
    protected void postLayoutAlgorithm(final InternalNode[] entitiesToLayout,final InternalRelationship[] relationshipsToConsider) {
        // Do nothing
    }
    
    @Override
    protected void preLayoutAlgorithm(final InternalNode[] entitiesToLayout,
            final InternalRelationship[] relationshipsToConsider, final double x,
            final double y, final double width, final double height) {
    }
    
    @Override
    public void setLayoutArea(final double x, final double y, final double width,
            final double height) {
        // do nothing
    }
}
