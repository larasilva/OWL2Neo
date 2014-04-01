/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package owlneo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author lara
 */
public class ClassHashCreator {

    private HashMap<String, OWLClass> result;
    private OWLOntology ontology;
    private OWLReasoner reasoner;

    public ClassHashCreator(OWLOntology ontology) {
        result = new HashMap();
        this.ontology = ontology;
        reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);
        Set<OWLClass> thingSet = reasoner.getTopClassNode().getEntities();
        
        for(OWLClass thing: thingSet){
            genSubClass(thing);
        }
    }

    private void genSubClass(OWLClass subclass) {
        
        String temp = getName(subclass);
        result.put(temp, subclass);
        Set <OWLClass> classSet=
                reasoner.getSubClasses(subclass, true).getFlattened();
        ArrayList<OWLClass> output = new ArrayList();
        for (OWLClass c: classSet) {
            if (!c.isOWLNothing()){
                output.add(c);
            }
        }
        if (!output.isEmpty()) {
            for (OWLClass c : output) {
                genSubClass(c);
            }
        }        
    }
    
    public HashMap<String, OWLClass> getHash(){
        return result;
    }

    private String getName(OWLClass namedClass) {
        return namedClass.toStringID().substring
        (namedClass.toStringID().indexOf("#")+1);
    }

}
