/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlneo;

import java.util.ArrayList;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import org.semanticweb.owlapi.model.OWLOntology;

/**
 *
 * @author lara
 */
public class ObjectProperty2String {
    private final OWLObjectProperty objectProperty;
    private final OWLOntology ontology;
    private ArrayList<String> rangestr;
    private ArrayList<String> domainstr;
    private ArrayList<OWLClassExpression> rangecl;
    private ArrayList<OWLClassExpression> domaincl;
    
    /**
     *
     * @param ontology
     * @param objectProperty
     */
    public ObjectProperty2String(OWLOntology ontology, OWLObjectProperty objectProperty){
        this.objectProperty = objectProperty;
        this.ontology = ontology;
        domaincl=new ArrayList();
        domainstr=new ArrayList();
        rangecl=new ArrayList();
        rangestr=new ArrayList();
        genArrayLists();
    }
    
    private void genArrayLists(){
        Set<OWLClassExpression> domainSet = objectProperty.getDomains(ontology);
        for (OWLClassExpression ce:domainSet){
            domaincl.add(ce);
            domainstr.add(Structure2Cypher.shortName(ce.toString()));
        }
        Set<OWLClassExpression> rangeSet = objectProperty.getRanges(ontology);
        for (OWLClassExpression ce:rangeSet){
            rangecl.add(ce);
            rangestr.add(Structure2Cypher.shortName(ce.toString()));
        }
    }
    //GETTERS
    public ArrayList<String> getRangeStrings(){
        return rangestr;
    }
    
    public ArrayList<String> getDomainStrings(){
        return domainstr;
    }
    
    public ArrayList<OWLClassExpression> getRangeClassExpression(){
        return rangecl;
    }
    
    public ArrayList<OWLClassExpression> getDomainClassExpression(){
        return domaincl;
    }
    
    public OWLObjectProperty getObjectProperty(){
        return objectProperty;
    }
    
    public String getObjectPropertyName(){
        return Structure2Cypher.shortName(objectProperty.toStringID());
    }
    //COMPARERS
    
    public boolean belongs2range(OWLClassExpression classExpression){
        return rangecl.contains(classExpression);
    }
    
    public boolean belongs2range(OWLClass owlclass){     
        for (OWLClassExpression ce: rangecl){
            if (ce.asOWLClass().equals(owlclass)) return true;
        }
        return false;
    }
    
    public boolean belongs2domain(OWLClassExpression classExpression){
        return domaincl.contains(classExpression);
    }
    
    public boolean belongs2domain(OWLClass owlclass){     
        for (OWLClassExpression ce: domaincl){
            if (ce.asOWLClass().equals(owlclass)) return true;
        }
        return false;
    }
    
    public boolean belongsToDataProperty(OWLObjectProperty objProperty){
        return objProperty.equals(objectProperty);
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        String result=getObjectPropertyName()+" <ranges> ";
        for (String s: rangestr){
            result = result + " " + s;
        }
        result = result + " <domains> ";
        for (String s: domainstr){
            result = result + " " + s;
        }
        return result;
    }
}
