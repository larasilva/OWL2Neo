/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlneo;

import java.util.ArrayList;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;

/**
 *
 * @author lara
 */
public class Annotation2Cypher {
    private String cypher;
    private Set<OWLAnnotationAssertionAxiom> axioms;
    
    public Annotation2Cypher(Set<OWLAnnotationAssertionAxiom> axioms){
        this.axioms=axioms;
        cypher= "";
    }
    
    private String getEntityString(String entity){
        
        if (entity.contains("#")){
            entity = entity.substring(
                entity.indexOf("#")+1);
        }
        return entity;
    }
    
    private String getPropertyString(OWLAnnotationProperty property){
        String properString=property.toString();
        if (property.isComment()){
            properString="comment";
        }
        if (properString.contains("#")){
            properString = properString.substring(
                properString.indexOf("#")+1,properString.lastIndexOf(">"));
        }
        return properString;
    }
    
    private String getValueString(OWLAnnotationValue value){
        return value.toString();
    }
    
    public String getCypher(){
        return cypher;
    }
        
    
    public void createCypher(){
        
        
        for(OWLAnnotationAssertionAxiom a:axioms){
            String ent=getEntityString(a.getSubject().toString());
            String prop=getPropertyString(a.getProperty());
            String value=getValueString(a.getValue());
            String s="MATCH (a) \nWHERE a.name=\""+ent+"\"\nSET a."
                    +prop+" = "+value+";\n\n";
            cypher=cypher+s;
        }
        
       
        

    }
    
}
