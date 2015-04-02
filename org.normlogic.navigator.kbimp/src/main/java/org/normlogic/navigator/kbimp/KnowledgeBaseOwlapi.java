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
package org.normlogic.navigator.kbimp;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.normlogic.navigator.core.IAssertionTriple;
import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IExpression;
import org.normlogic.navigator.core.IHierarchy;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.IKnowledgeBaseChangeListener;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IOntology;
import org.normlogic.navigator.core.IProperty;
import org.normlogic.navigator.core.ISituationViewer;
import org.normlogic.navigator.core.impl.Concept;
import org.normlogic.navigator.core.impl.Expression;
import org.normlogic.navigator.core.impl.Individual;
import org.normlogic.navigator.core.impl.IndividualAssertionTriple;
import org.normlogic.navigator.core.impl.KnowledgeBase;
import org.normlogic.navigator.core.impl.Norm;
import org.normlogic.navigator.core.impl.NormContext;
import org.normlogic.navigator.core.impl.NormedWorld;
import org.normlogic.navigator.core.impl.Ontology;
import org.normlogic.navigator.core.impl.Property;
import org.normlogic.navigator.core.impl.WorldTriple;
import org.normlogic.navigator.core.util.LoadException;
import org.normlogic.navigator.core.util.Tracker;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

public class KnowledgeBaseOwlapi extends KnowledgeBase {

	private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private OWLOntology baseOntology;
	private Set<OWLOntology> loadedOntologies = new HashSet<>();
	private Reasoner reasoner;
	
	private Set<IKnowledgeBaseChangeListener> changeListeners = new HashSet<IKnowledgeBaseChangeListener>();
	
    PointerMap<IAssertionTriple, IndividualAssertionTriple, OWLObjectPropertyAssertionAxiom> ASSERTIONTRIPLE = new PointerMap<>(this, IndividualAssertionTriple.class);   
    PointerMap<IIndividual, Individual, OWLNamedIndividual> INDIVIDUAL = new PointerMap<>(this, Individual.class);
    PointerMap<IConcept, Concept, OWLClass> CONCEPT = new PointerMap<>(this, Concept.class);
    PointerMap<IProperty, Property, OWLObjectProperty> PROPERTY = new PointerMap<>(this, Property.class);
    PointerMap<INorm, Norm, OWLNamedIndividual> NORM = new PointerMap<>(this, Norm.class);
    PointerMap<IOntology, Ontology, OWLOntology> ONTOLOGY = new PointerMap<>(this, Ontology.class);
    PointerMap<IExpression, Expression, OWLClassExpression> EXPRESSION = new PointerMap<>(this, Expression.class);
        
    
    public KnowledgeBaseOwlapi() throws Exception {
		baseOntology = manager.createOntology();
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
        reasoner = new Reasoner(configuration, baseOntology);
	}
	
	@Override
	public void visualize(ISituationViewer viewer) {
		Set<OWLClassAssertionAxiom> classAssertionAxioms = baseOntology.getAxioms(AxiomType.CLASS_ASSERTION, false);
		for (OWLClassAssertionAxiom axiom : classAssertionAxioms) {
			viewer.add(INDIVIDUAL.createFrom(axiom.getIndividual().asOWLNamedIndividual()));
		}
		Set<OWLObjectPropertyAssertionAxiom> propertyAssertionAxioms = baseOntology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, false);
		for (OWLObjectPropertyAssertionAxiom axiom : propertyAssertionAxioms) {
			viewer.add(ASSERTIONTRIPLE.createFrom(axiom));
		}
	}

	@Override
	public IOntology load(String fileName) throws LoadException {
		try {
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(fileName));
			IRI iri = ontology.getOntologyID().getOntologyIRI();
			manager.addIRIMapper(new SimpleIRIMapper(iri, IRI.create(fileName)));
			OWLImportsDeclaration importDeclaration = OWLManager.getOWLDataFactory().getOWLImportsDeclaration(iri);
	        manager.applyChange(new AddImport(baseOntology, importDeclaration));
	        // TODO: the following sequence is mandatory. I have to look for a better design...
	        loadedOntologies.add(ontology);
	        IOntology loadedOntology = ONTOLOGY.createFrom(ontology); 
	        rebuildNormedWorld();
			return loadedOntology;
		} catch(Exception e) {
			throw new LoadException(e.getMessage(), e);
		}
	}

	private final class ExpressionVisitor extends OWLClassExpressionVisitorAdapter {
	     
		NormedWorld world;
		Set<NormContext> normContext;
		// OWLObjectProperty lastProperty;
		OWLQuantifiedObjectRestriction restriction;
		Set<OWLClass> lastClasses;
		Set<OWLClass> classes = new HashSet<OWLClass>();
		
		 
        ExpressionVisitor(NormedWorld world, Set<NormContext> normedContext, Set<OWLClass> lastClasses, OWLQuantifiedObjectRestriction restriction /*OWLObjectProperty lastProperty*/) {
            super();
            this.world = world;
            this.normContext = normedContext;
            this.lastClasses = lastClasses;
            this.restriction = restriction;
        }
        
        @Override
        public void visit(OWLClass desc) {
        	if (restriction /*lastProperty*/ != null) {
        		for (OWLClass lastClass : lastClasses) {
        			IConcept domain = CONCEPT.createFrom(lastClass);
        			IProperty property = PROPERTY.createFrom(restriction.getProperty()/*lastProperty*/.asOWLObjectProperty());
        			IConcept range = CONCEPT.createFrom(desc);
        			
        			WorldTriple triple = new WorldTriple(domain, property, range);
        			world.addWorldTriple(triple);
        			world.mapNormsToWorldTriple(triple, normContext, EXPRESSION.createFrom(restriction));
        			/*
        			Set<OWLObjectPropertyExpression> inverseProperties = reasoner.getInverseObjectProperties(lastProperty).getEntities();
        			for (OWLObjectPropertyExpression p : inverseProperties) {
        				if (!p.isAnonymous()) {
		       				IProperty inverse = PROPERTY.createFrom(p.asOWLObjectProperty());
		       				WorldTriple inverseTriple = new WorldTriple(range, inverse, domain);
		       				world.addWorldTriple(inverseTriple);
		           			world.mapNormsToWorldTriple(inverseTriple, normContext);
		           			break;
        				}
        			}
        			*/
        		}
        	}
        	classes.add(desc);
        }

        @Override
        public void visit(OWLObjectSomeValuesFrom desc) {
        	if (!desc.getProperty().isAnonymous()) {
	        	desc.getFiller().accept(new ExpressionVisitor(world, normContext, classes, desc));
        	}
        }
        
        @Override
        public void visit(OWLObjectAllValuesFrom desc) {
        	desc.getFiller().accept(new ExpressionVisitor(world, normContext, classes, desc));
        }
        @Override
        public void visit(OWLObjectComplementOf desc) {
        	desc.getOperand().accept(this);
        }
        @Override
        public void visit(OWLObjectIntersectionOf desc) {
        	for (OWLClassExpression expression : desc.getOperands()) {
        		expression.accept(this);
        	}
        }
        @Override
        public void visit(OWLObjectUnionOf desc) {
        	for (OWLClassExpression expression : desc.getOperands()) {
        		expression.accept(this);
        	}
        }
	 }
	 
	
	
	private void registerExpressionToNormedWorld(NormedWorld world, OWLClass expression, OWLAnnotationProperty property, NormContext.Type type) {
		final Set<NormContext> normedContext = new HashSet<>();
		for (OWLOntology ontology : loadedOntologies) {
			Set<OWLAnnotation> annotations = new HashSet<>();
			annotations.addAll(expression.getAnnotations(ontology, property));
			for (OWLAnnotation annotation : annotations) {
				if (annotation.getValue() instanceof IRI) {
					IRI iri = (IRI)annotation.getValue();
					INorm norm = NORM.createFrom(OWLManager.getOWLDataFactory().getOWLNamedIndividual(iri));
					world.mapNormToOntology(ONTOLOGY.wrapFromValue(ontology), norm);
					NormContext normContext = new NormContext(norm, type);
					normedContext.add(normContext);
					normContextToExpression.put(normContext, expression);
				}
			}
		}
		for (OWLClassExpression equivalent : expression.getEquivalentClasses(loadedOntologies)) {
			equivalent.accept(new ExpressionVisitor(world, normedContext, new HashSet<OWLClass>(), null));
		}
	}
	
	NormedWorld world = new NormedWorld(this);
	Map<NormContext, OWLClass> normContextToExpression = new HashMap<>(); 
	final OWLAnnotationProperty propertyContext = OWLManager.getOWLDataFactory().getOWLAnnotationProperty(IRI.create(getIRI().toString() + "#kontext_von"));
	final OWLAnnotationProperty propertyObligation = OWLManager.getOWLDataFactory().getOWLAnnotationProperty(IRI.create(getIRI().toString() + "#verpflichtend_durch"));
	
	private INormedWorld rebuildNormedWorld() throws Exception {
		
		world = new NormedWorld(this);
		normContextToExpression.clear();
		
		final Set<OWLClass> expressionsContext = new HashSet<OWLClass>();
		final Set<OWLClass> expressionsObligation = new HashSet<OWLClass>();

		// first get the normative context and obligation expressions
		Set<OWLAnnotationAssertionAxiom> axiomsAnnotation = new HashSet<>();
		for (OWLOntology ontology : loadedOntologies) {
				axiomsAnnotation.addAll(ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION));
		}
		for (final OWLAnnotationAssertionAxiom axiom : axiomsAnnotation) {
			axiom.getSubject().accept(new OWLAnnotationSubjectVisitor() {
				@Override
				public void visit(OWLAnonymousIndividual individual) {}
				@Override
				public void visit(IRI iri) {
					if (axiom.getProperty().equals(propertyContext)) {
						expressionsContext.add(OWLManager.getOWLDataFactory().getOWLClass(iri));
					}
					else if (axiom.getProperty().equals(propertyObligation)) {
						expressionsObligation.add(OWLManager.getOWLDataFactory().getOWLClass(iri));
					}
				}
			});
		}
		
		
		for (OWLClass context : expressionsContext) {
			registerExpressionToNormedWorld(world, context, propertyContext, NormContext.CONDITION);
		}
		for (OWLClass obligation : expressionsObligation) {
			registerExpressionToNormedWorld(world, obligation, propertyObligation, NormContext.OBLIGATION);
		}
		return world;
	}

	public IRI getIRI() {
		return IRI.create("http://www.normlogic.org/baseOntology.owl");
	}

	void  fireChangeEvent() {
		for (IKnowledgeBaseChangeListener listener : changeListeners) {
			listener.knowledgeBaseChanged();
		}
	}
	
	@Override
	public void addChangeListener(IKnowledgeBaseChangeListener listener) {
		changeListeners.add(listener);
	}


	@Override
	public void delete(IIndividual individual) {

		OWLNamedIndividual owlIndividual = INDIVIDUAL.wrap(individual);
		Set<OWLAxiom> axioms = new HashSet<>();
		for (OWLObjectPropertyAssertionAxiom axiom : baseOntology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
			if (axiom.getObject().equals(owlIndividual) || axiom.getSubject().equals(owlIndividual)) {
				axioms.add(axiom);
				ASSERTIONTRIPLE.deleteFromValue(axiom);
			}
		}
		for (OWLClassAssertionAxiom axiom : baseOntology.getAxioms(AxiomType.CLASS_ASSERTION)) {
			if (axiom.getIndividual().asOWLNamedIndividual().equals(owlIndividual)) {
				axioms.add(axiom);
				INDIVIDUAL.delete(individual);
				break;
			}
		}
		if (axioms.isEmpty()) {
			return;
		}
		manager.removeAxioms(baseOntology, axioms);
		fireChangeEvent();
	}


	public boolean insert(Set<OWLAxiom> axioms,  boolean simulation) {
		Set<OWLDifferentIndividualsAxiom> oldDifferentAxioms = baseOntology.getAxioms(AxiomType.DIFFERENT_INDIVIDUALS);
		manager.removeAxioms(baseOntology,oldDifferentAxioms);
		OWLDifferentIndividualsAxiom newDifferentAxiom = OWLManager.getOWLDataFactory().getOWLDifferentIndividualsAxiom(baseOntology.getIndividualsInSignature());
		manager.addAxioms(baseOntology, axioms);
		manager.addAxiom(baseOntology, newDifferentAxiom);
		boolean consistent = reasoner.isConsistent();
		if (consistent && !simulation) {
			fireChangeEvent();
			return consistent;
		}
		manager.removeAxiom(baseOntology, newDifferentAxiom);
		manager.removeAxioms(baseOntology, axioms);
		manager.addAxioms(baseOntology, oldDifferentAxioms);
		return consistent;

	}

	public boolean isIndividualAssertableWithTriple(IIndividual individual, IProperty property, IConcept concept) {

		OWLNamedIndividual owlIndividual = INDIVIDUAL.wrap(individual);
		OWLObjectProperty owlProperty = PROPERTY.wrap(property);
		OWLClass owlType = CONCEPT.wrap(concept);
		
		// let�s see how much relations the individual to the objective concept has
		Set<OWLObjectPropertyAssertionAxiom> assertionAxioms = baseOntology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
		int numberIndividuals = 0; 
		for (OWLObjectPropertyAssertionAxiom axiom : assertionAxioms) {
			if (axiom.getSubject() == owlIndividual) {
				Set<OWLClassAssertionAxiom> classAxioms = baseOntology.getAxioms(AxiomType.CLASS_ASSERTION);
				for (OWLClassAssertionAxiom classAxiom : classAxioms) {
					if (classAxiom.getClassExpression() == owlType) {
						numberIndividuals++;
						break;
					}
				}
			}
		}
		
		// an form the expression domainClass and property min individual.size() concept 
		OWLObjectMinCardinality expressionCardinality = OWLManager.getOWLDataFactory().getOWLObjectMinCardinality(++numberIndividuals, owlProperty, owlType);
		OWLObjectComplementOf expressionNegCardinality = OWLManager.getOWLDataFactory().getOWLObjectComplementOf(expressionCardinality);
		// System.out.println("Checking " + owlIndividual.getIRI().getFragment() + " for " + expressionNegCardinality.toString());
		
		// now we can do the type-check.
		return !reasoner.hasType(owlIndividual, /* expressionCheck*/ expressionNegCardinality, false);
	}

	@Override
	protected boolean isIndividualAssertableWithType(final Individual individual, final IConcept concept) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(OWLManager.getOWLDataFactory().getOWLClassAssertionAxiom(CONCEPT.wrap(concept), INDIVIDUAL.wrap(individual)));
		return insert(axioms, true);
	}

	@Override
	protected String getLabelOf(IConcept concept) {
		return CONCEPT.wrap(concept).getIRI().getFragment();
	}
	
	@Override
	protected String getLabelOf(IProperty property) {
		return PROPERTY.wrap(property).getIRI().getFragment();
	}
	
	@Override
	protected String getLabelOf(IIndividual individual) {
		return INDIVIDUAL.wrap(individual).getIRI().getFragment();
	}
	
	@Override
	protected String getLabelOf(IOntology ontology) {
		OWLOntology ont = ONTOLOGY.wrap(ontology);
		Set <OWLAnnotation> annotations = ont.getAnnotations();
		for (OWLAnnotation annotation : annotations) {
			if (annotation.getProperty().equals( OWLManager.getOWLDataFactory().getRDFSLabel())) {
				OWLAnnotationValue value = annotation.getValue();
				if (value instanceof OWLLiteral) {
					return new String(((OWLLiteral)value).getLiteral());
				}
			}
		}
		return ont.toString();
	}
	
	@Override
	protected String getLabelOf(INorm norm) {
		OWLNamedIndividual individual =  NORM.wrap(norm);
		Set<OWLAnnotation> annotations = new HashSet<>();
		for (OWLOntology ontology : loadedOntologies) {
			annotations.addAll(individual.getAnnotations(ontology, OWLManager.getOWLDataFactory().getRDFSLabel()));
		}
		for (OWLAnnotation annotation : annotations) {
			OWLAnnotationValue value = annotation.getValue();
			if (value instanceof OWLLiteral) {
				return new String(((OWLLiteral)value).getLiteral());
			}
		}
		return individual.getIRI().toString();
	}

	@Override
	protected String getLabelOf(IAssertionTriple triple) {
		return ASSERTIONTRIPLE.wrap(triple).toString();
	}
	
	@Override
	protected Set<IConcept> getSubClassesOf(IConcept concept, boolean direct) {
		Set<IConcept> subConcepts = new TreeSet<>();
		Set<OWLClass> subClasses = reasoner.getSubClasses(CONCEPT.wrap(concept), direct).getFlattened();
		for (OWLClass subClass : subClasses) {
			subConcepts.add(CONCEPT.createFrom(subClass));
		}
		return subConcepts;
	}

	@Override
	protected Set<IConcept> getSuperClassesOf(IConcept concept, boolean direct) {
		Set<IConcept> superConcepts = new TreeSet<>();
		Set<OWLClass> superClasses = reasoner.getSuperClasses(CONCEPT.wrap(concept), direct).getFlattened();
		for (OWLClass superClass : superClasses) {
			superConcepts.add(CONCEPT.createFrom(superClass));
		}
		return superConcepts;
	}


	@Override
	protected boolean addIndividualAssertion(IIndividual subject,
			IProperty property, IIndividual object, IConcept concept) {

		OWLNamedIndividual owlSubject = INDIVIDUAL.wrap(subject);
		OWLObjectProperty owlProperty = PROPERTY.wrap(property);
		OWLNamedIndividual owlObject = INDIVIDUAL.wrap(object);
		OWLClass owlType = CONCEPT.wrap(concept);

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(OWLManager.getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(owlProperty, owlSubject, owlObject));
		axioms.add(OWLManager.getOWLDataFactory().getOWLClassAssertionAxiom(owlType, owlObject));
		return insert(axioms, false);
	}

	@Override
	protected Set<IConcept> getAssertedTypes(IIndividual individual) {
		final Set<IConcept> result = new HashSet<>();
		OWLNamedIndividual owlIndividual = INDIVIDUAL.wrap(individual);
		Set<OWLClassAssertionAxiom> axioms = new HashSet<>(); 
		axioms.addAll(baseOntology.getClassAssertionAxioms(owlIndividual));
		for (OWLOntology ontology : loadedOntologies) {
			axioms.addAll(ontology.getClassAssertionAxioms(owlIndividual));
		}
		for (OWLClassAssertionAxiom axiom : axioms) {
			axiom.getClassExpression().accept(new OWLClassExpressionVisitorAdapter() {
				@Override
				public void visit(OWLClass ce) {
					result.add(CONCEPT.createFrom(ce));
				}
			});
		}
		return result;
	}
	
	@Override
	protected boolean checkForNegativeType(final IIndividual individual, final IConcept concept) {
		OWLNamedIndividual owlIndidivdual = INDIVIDUAL.wrap(individual);
		OWLClassExpression owlExpression = CONCEPT.wrap(concept).getObjectComplementOf();
		return reasoner.hasType(owlIndidivdual, owlExpression, false);
	}

	@Override
	protected IIndividual getIndividual(String name) {
		IRI iri = IRI.create(getIRI().toString() + "#" + name);
		return INDIVIDUAL.createFrom(OWLManager.getOWLDataFactory().getOWLNamedIndividual(iri));
	}


	@Override
	protected IIndividual getSubjectOfTriple(IAssertionTriple triple) {
		OWLIndividual individual = ASSERTIONTRIPLE.wrap(triple).getSubject();
		if (individual.isAnonymous()) {
			return null;
		}
		return INDIVIDUAL.createFrom((OWLNamedIndividual)individual);
	}


	@Override
	protected IIndividual getObjectOfTriple(IAssertionTriple triple) {
		OWLIndividual individual = ASSERTIONTRIPLE.wrap(triple).getObject();
		if (individual.isAnonymous()) {
			return null;
		}
		return INDIVIDUAL.createFrom((OWLNamedIndividual)individual);
	}


	@Override
	protected Set<IConcept> getTopLevelConceptsOf(Set<IConcept> concepts) {
		Set<IConcept> topConcepts = new HashSet<>();
		for  (IConcept concept : concepts) {
			OWLClass owlClass = CONCEPT.wrap(concept);
			Set<OWLClass> superClasses = reasoner.getSuperClasses(owlClass, false).getFlattened();
			Set<IConcept> superConcepts = new HashSet<>();
			for (OWLClass superClass :  superClasses) {
				superConcepts.add(CONCEPT.createFrom(superClass));
			}
			superConcepts.retainAll(concepts);
			if (superConcepts.isEmpty()) {
				topConcepts.add(concept);
			}
		}
		return topConcepts;
	}
 
	@Override
    public int hashCode() {
	   int hash = 31;
	   hash = 7 * hash + ((baseOntology == null) ? 0 : baseOntology.hashCode());
	   for (OWLOntology ontology : loadedOntologies) {
		   hash = 7 * hash + ((ontology == null) ? 0 : ontology.hashCode());
	   }
       return hash;
    }
	    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KnowledgeBaseOwlapi other = (KnowledgeBaseOwlapi)obj;
        if (baseOntology == null) {
            if (other.baseOntology != null) {
                return false;
            }
        }
        if (loadedOntologies == null) {
            if (other.loadedOntologies != null) {
                return false;
            }
        }
        else if (!baseOntology.equals(other.baseOntology)) {
            return false;
        }
        else if (!loadedOntologies.equals(other.loadedOntologies)) {
        	return false;
        }
        return true;
    }

	@Override
	public Set<IOntology> getLoadedOntologies() {
		Set<IOntology> ontologies = new HashSet<>();
		for (OWLOntology ontology : loadedOntologies) {
			ontologies.add(ONTOLOGY.createFrom(ontology));
		}
		return ontologies;
			
	}

	@Override
	protected Set<INorm> getNorms(Ontology ontology) {
		return world.getNormsOf(ontology);
	}

	@Override
	public NormedWorld getNormedWorld() {
		return world;
	}

	@Override
	protected String getTextOf(Norm norm) {
		OWLNamedIndividual individual = NORM.wrap(norm);
		Set<OWLAnnotation> annotations = new HashSet<>();
		for (OWLOntology ontology : loadedOntologies) {
			annotations.addAll(individual.getAnnotations(ontology, OWLManager.getOWLDataFactory().getRDFSComment()));
		}
		for (OWLAnnotation annotation : annotations) {
			OWLAnnotationValue value = annotation.getValue();
			if (value instanceof OWLLiteral) {
				return new String(((OWLLiteral)value).getLiteral());
			}
		}
		return individual.getIRI().toString();
	}

	@Override
	protected boolean isNormFullfilled(Norm norm, IIndividual individual) {
		OWLClass condition = normContextToExpression.get(new NormContext(norm, NormContext.CONDITION));
		OWLClass obligation = normContextToExpression.get(new NormContext(norm, NormContext.OBLIGATION));
		OWLNamedIndividual owlIndividual = INDIVIDUAL.wrap(individual);
		if (owlIndividual == null) return false;
		if (condition == null) return false;
		if (obligation == null) return false;
		if (reasoner.hasType(owlIndividual, condition, false) && reasoner.hasType(owlIndividual, obligation, false)) return true;
		return false;
	}

	@Override
	protected boolean hasNormToBeFullfilled(INorm norm, IIndividual individual) {
		OWLClass condition = normContextToExpression.get(new NormContext(norm, NormContext.CONDITION));
		OWLClass obligation = normContextToExpression.get(new NormContext(norm, NormContext.OBLIGATION));
		OWLNamedIndividual owlIndividual = INDIVIDUAL.wrap(individual);
		if (owlIndividual == null) return false;
		if (condition == null) return false;
		if (obligation == null) return false;
		if (reasoner.hasType(owlIndividual, condition, false) && !reasoner.hasType(owlIndividual, obligation, false)) return true;
		return false;
	}
	
	@Override
	protected boolean isNormFullfilled(INorm norm) {
		OWLClass condition = normContextToExpression.get(new NormContext(norm, NormContext.CONDITION));
		OWLClass obligation = normContextToExpression.get(new NormContext(norm, NormContext.OBLIGATION));
		if (condition == null) return false;
		NodeSet<OWLNamedIndividual> nodes = reasoner.getInstances(condition, false);
		for (OWLNamedIndividual individual : nodes.getFlattened()) {
			if (obligation == null) return true;
			if (reasoner.hasType(individual, obligation, false)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasNormToBeFullfilled(INorm norm) {
		OWLClass condition = normContextToExpression.get(new NormContext(norm, NormContext.CONDITION));
		OWLClass obligation = normContextToExpression.get(new NormContext(norm, NormContext.OBLIGATION));
		if (condition == null) return false;
		NodeSet<OWLNamedIndividual> nodes = reasoner.getInstances(condition, false);
		for (OWLNamedIndividual individual : nodes.getFlattened()) {
			if (obligation == null) return false;
			if (!reasoner.hasType(individual, obligation, false)) {
				return true;
			}
		}
		return false;
	}


	@Override
	protected boolean addIndividualAssertion(IIndividual individual,
			IConcept concept) {
		OWLClass type = CONCEPT.wrap(concept);
		OWLIndividual owlIndividual = INDIVIDUAL.wrap(individual);
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(OWLManager.getOWLDataFactory().getOWLClassAssertionAxiom(type, owlIndividual));
		return insert(axioms, false);
	}

	@Override
	protected boolean hasIndividualAssertion(IIndividual individual, IProperty property, IConcept concept) {
		OWLNamedIndividual owlIndividual = INDIVIDUAL.wrap(individual);
		OWLObjectProperty owlProperty = PROPERTY.wrap(property);
		OWLClass owlConcept = CONCEPT.wrap(concept);
		Set<OWLNamedIndividual> values = reasoner.getObjectPropertyValues(owlIndividual, owlProperty).getFlattened();
		for (OWLNamedIndividual value : values) {
			if (reasoner.hasType(value, owlConcept, false)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean dependNormedConclusionOnTriple(IIndividual individual, Set<INorm> norms, IProperty property, IConcept concept) {
		
		OWLNamedIndividual owlIndividual = INDIVIDUAL.wrap(individual);
		IHierarchy<IConcept> types = world.retainIncluded(individual.getTypes().getEntites());
		Set<IExpression> expressions = new HashSet<>();
		for (IConcept type : types.getEntites()) {
			for (INorm norm : norms) {
				IExpression expression = world.getExpressionFor(new WorldTriple(type, property, concept), new NormContext(norm, NormContext.CONDITION)); 
				if (expression != null) {
					expressions.add(expression);
				}
			}
		}
		if (expressions.isEmpty()) return false;
		for (IExpression expression : expressions) {
			if (reasoner.hasType(owlIndividual, EXPRESSION.wrap(expression), false)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected void updateTypes(final Individual individual, final Set<IConcept> types) {
		Set<OWLClass> owlTypes = reasoner.getTypes(INDIVIDUAL.wrap(individual), false).getFlattened();
		Set<IConcept> newTypes = new HashSet<>();
		for (OWLClass owlType : owlTypes) {
			if (!owlType.isAnonymous() && !owlType.isOWLNothing() && !owlType.isOWLThing()) {
				newTypes.add(CONCEPT.createFrom(owlType));
			}
		}
		types.clear();
		types.addAll(newTypes);
	}

	@Override
	public void updateIndividualTypes() {
		Set<OWLNamedIndividual> individuals = baseOntology.getIndividualsInSignature(false);
		for (OWLNamedIndividual individual : individuals) {
			INDIVIDUAL.createFrom(individual).updateTypes();
		}
	}

	@Override
	protected boolean assertType(Individual individual, IConcept concept) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		axioms.add(OWLManager.getOWLDataFactory().getOWLClassAssertionAxiom(CONCEPT.wrap(concept), INDIVIDUAL.wrap(individual)));
		return insert(axioms, false);
	}

	@Override
	protected boolean removeType(Individual individual, IConcept concept) {
		OWLNamedIndividual owlIndividual = INDIVIDUAL.wrap(individual);
		OWLClass owlType = CONCEPT.wrap(concept);
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (OWLClassAssertionAxiom axiom : baseOntology.getAxioms(AxiomType.CLASS_ASSERTION)) {
			if (axiom.getIndividual().asOWLNamedIndividual().equals(owlIndividual) && axiom.getClassExpression().asOWLClass().equals(owlType)) {
				axioms.add(axiom);
				break;
			}
		}
		if (axioms.isEmpty()) {
			return true;
		}
		manager.removeAxioms(baseOntology, axioms);
		fireChangeEvent();
		return true;
	}

	@Override
	protected boolean isSubClassOf(IConcept concept, Set<IConcept> types) {
		OWLClass owlClass = CONCEPT.wrap(concept);
		Set<OWLClass> subClasses = reasoner.getSuperClasses(owlClass, false).getFlattened();
		for (IConcept type : types) {
			if (subClasses.contains(CONCEPT.wrap(type))) {
				return true;
			}
		}
		return false;
	}

}
