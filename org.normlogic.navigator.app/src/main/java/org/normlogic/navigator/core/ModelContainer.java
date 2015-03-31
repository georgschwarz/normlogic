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
package org.normlogic.navigator.core;

import java.io.OutputStream;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.normlogic.navigator.core.IKnowledgeBase;
import org.normlogic.navigator.core.IKnowledgeBaseChangeListener;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IOntology;
import org.normlogic.navigator.core.IPursuedConclusion;
import org.normlogic.navigator.core.impl.NormedWorld;
import org.normlogic.navigator.kbimp.KnowledgeBaseOwlapi;
import org.normlogic.navigator.util.Messages;

/**
 * TODO: Document type ModelContainer.
 */
public class ModelContainer implements ITreeContentProvider {
    
   
    private IKnowledgeBase kb;  
    private INormedWorld world;
    private IPursuedConclusion pursuedNorms;
    
    static private ModelContainer container = new ModelContainer();
    static public ModelContainer getContainer() {return container;}
    
    public ModelContainer() {
    	try {
    		// TODO has to be changed to a more generic way...
			kb = new KnowledgeBaseOwlapi();
			world = kb.getNormedWorld();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Loads the specified ontology.
     *
     * @param fileName
     *            the name of the ontology
     * @param monitor
     *            the progress monitor
     * @throws Exception
     *             if the ontology could not be created
     */
    public void load(final String fileName, final IEventBroker broker) throws Exception {
    	if (kb == null)
    		throw new Exception("KnowledgeBaseOwlapi failed to initialize!");
	    kb.load(fileName);
	    world = kb.getNormedWorld(); 	  
	    kb.addChangeListener(new IKnowledgeBaseChangeListener() {
	        @Override
	        public void knowledgeBaseChanged() {
	            Display.getDefault().syncExec(new Runnable() {
	                @Override
	                public void run() {
	                	if (pursuedNorms != null) {
	                		pursuedNorms.update();
	                	}
	                    broker.send(Event.ONTOLOGY_CHANGED, kb);
	                }
	            });
	        }
	    });
	    broker.send(Event.ONTOLOGY_LOADED, kb);
	    broker.send(Event.NORMED_WORLD_CHANGED, world);
    }
    
    
    public void setPursuedNorms(IPursuedConclusion pursuedNorms) {
    	this.pursuedNorms = pursuedNorms;
    }
    
    /**
     * TODO: Document method save
     * @param output
     * @throws Exception
     */
    public void save(final OutputStream output) throws Exception {
        // ontology.save(output);
    }

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement == Messages.LibraryPart_Titel) {
			if (kb != null) {
				return kb.getLoadedOntologies().toArray();
			}
		}
		Object[] objects = {};  
		return objects;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IOntology) {
			return ((IOntology)parentElement).getNorms().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IOntology) {
			return ((IOntology)element).getNorms().size() != 0;
		}
		return false;
	}
}

