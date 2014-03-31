/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlneo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 *
 * @author lara
 */

public class ClassHashCreator {
    HashMap<String, OWLClass> result;
    OWLOntology ontology;
    public ClassHashCreator(OWLOntology ontology){
        result = new HashMap();
        this.ontology=ontology;
    }
    
    private ArrayList<OWLClass> getSubClass(OWLClass subclass){
        Set<OWLSubClassOfAxiom> subClassAxioms = 
                ontology.getSubClassAxiomsForSuperClass(subclass);
        ArrayList <OWLClass> output = new ArrayList();
        for (OWLSubClassOfAxiom s:subClassAxioms){
            output.add(s.getSubClass().asOWLClass());
        }
        return output;
    }
}
