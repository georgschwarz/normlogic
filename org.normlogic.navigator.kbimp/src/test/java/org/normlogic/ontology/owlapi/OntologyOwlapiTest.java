package org.normlogic.ontology.owlapi;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.normlogic.navigator.core.util.Tracker;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

public class OntologyOwlapiTest {

	@Test
	public void testLoad() {
		try {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		String fileName = "C:\\Users\\gschwarz\\owl\\protoyp-hausrat-deckung.owl";
		OWLOntology ontology;
		// assertNotNull(new File(fileName));
		ontology = manager.loadOntologyFromOntologyDocument(new File(fileName));
		IRI baseIri = ontology.getOntologyID().getOntologyIRI();
	    manager.addIRIMapper(new SimpleIRIMapper(baseIri, IRI.create(fileName)));
        Configuration configuration = new Configuration();
        configuration.bufferChanges = false;
        configuration.reasonerProgressMonitor = new ReasonerProgressMonitor() {
			Tracker t = null;
        	@Override
			public void reasonerTaskStarted(String taskName) {
        		t = Tracker.start("Reasoner, " + taskName);
			}

			@Override
			public void reasonerTaskStopped() {
				t.stop();
			}

			@Override
			public void reasonerTaskProgressChanged(int value, int max) {
			}

			@Override
			public void reasonerTaskBusy() {
				System.out.println(" + Busy");				
			}

        };
        Reasoner reasoner = new Reasoner(configuration, ontology);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			fail("Exception: " + e);
		}
	}

}
