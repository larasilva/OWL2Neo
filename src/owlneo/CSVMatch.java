/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlneo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author lara
 */
public class CSVMatch {
    private final OWLClass superclass;
    private final OWLReasoner reasoner;
    private final OWLOntology ontology;
    private final String headerField;
    private HashMap<String, OWLIndividual> value2individual;
    
    public CSVMatch(OWLOntology ontology, OWLClass superclass, String headerField,
            OWLReasoner reasoner){
        this.headerField = headerField;
        this.ontology = ontology;
        this.superclass = superclass;
        value2individual = new HashMap();
        this.reasoner = reasoner;
    }
    
    //GETTERS
    
    public String getCSVField(){
        return headerField;
    }
    
    public OWLClass getSuperClass(){
        return superclass;
    }
    
    public Set<OWLIndividual> getIndividualList(){
        return superclass.getIndividuals(ontology);
    }
    
    public HashMap<String, OWLIndividual> getValueMap(){
        return value2individual;          
    }
    
    public Set<String> getValueList(){
        return value2individual.keySet();
    }
    
    /**
     *
     * @return A Set<OWLIndividual> created with the hashset constructor
     */
    public Set<OWLIndividual> getIndividualsList(){
        Collection<OWLIndividual> temp = value2individual.values();
        Set<OWLIndividual> result = new HashSet(temp);
        return result;
    }
    
    private NodeSet<OWLNamedIndividual> getIndividualsFromClass(){
        return reasoner.getInstances(superclass.getNNF(), false);       
    }
    
    private Set<OWLClass> expression2class(Set<OWLClassExpression> set){
        Set<OWLClass> result = new HashSet();
        for (OWLClassExpression e:set){
            if (!e.isAnonymous()){
                result.add(e.asOWLClass());
            }
        }
        return result;
    }
    
    //SETTERS
    public void setNewPair(String value, OWLIndividual individual){
        if (value2individual.containsKey(value)){
            value2individual.put(value, individual);
        }
        else{            
            //TODO: passar isto para uma jabela de erro no main.java
            System.out.println("Value already assigned");
        }
    }
    
    public void setChangedMatch(String value, OWLIndividual individual){
        if (value2individual.containsKey(value)){
            OWLIndividual test = value2individual.put(value, individual);
            if (test.equals(individual)){
                //Just for testing purposes (remover)
                System.out.println("Already had this connection.");
            }
        }
        //TODO: passar isto para uma jabela de erro no main.java
        else System.out.println("Error: key not bound");
    }
    
    
    
    
    
}
