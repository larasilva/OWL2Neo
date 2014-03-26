/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlneo;

import java.util.ArrayList;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author lara
 */
public class ClassMatch {
    private final OWLReasoner reasoner;
    private final OWLOntology ontology;
    private ArrayList<OWLClass> classList;
    private ArrayList<String> classShort;
    
    public ClassMatch(OWLOntology ontology, OWLReasoner reasoner){
        this.reasoner = reasoner;
        this.ontology = ontology;
        classList = new ArrayList();
        classShort = new ArrayList();
        
        Set<OWLClass> temp = ontology.getClassesInSignature();
        
        //creates the arraylists, with each name corresponding to the owlclass
        for(OWLClass c:temp){
            classList.add(c);
            classShort.add(Structure2Cypher.shortName(c.toString()));   
        }
        
    }
    
    public ArrayList<OWLClass> getOWLClassList(){
        return classList;
    }
    
    public ArrayList<String> getShortClassList(){
        return classShort;
    }
}
