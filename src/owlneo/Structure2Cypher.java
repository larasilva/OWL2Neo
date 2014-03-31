/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlneo;

import java.util.ArrayList;
import java.util.Set;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author lara
 */
public class Structure2Cypher {
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLReasoner reasoner;
    
    private String superclassCypher;
    private String classCreateCypher;
    
    public Structure2Cypher(OWLOntologyManager manager,OWLOntology ontology, 
            OWLReasoner reasoner){
        
        this.manager=manager;
        this.ontology=ontology;
        this.reasoner=reasoner;
        superclassCypher="";
        classCreateCypher="";
    }
    
    public void setUpdatedOntology(OWLOntology ontology){
        this.ontology=ontology;
        reasoner=new Reasoner.ReasonerFactory().createReasoner(ontology);
    }
    
    
    public void buildClassCypher(){
        Set<OWLClass> classes = ontology.getClassesInSignature();
        //cleans the variable of previous calls to the method
        classCreateCypher="";
        for (OWLClass c:classes){
            String temp = shortName(c.toStringID());
            String toAdd = "CREATE (n {owl:\"class\", name:\""+temp+"\", "
                    + "iri:\""+c.toStringID()+"\"});\n\n";
            classCreateCypher=classCreateCypher+toAdd;
        }
    }
    
    public String getAnnotationsCypher(){
        Set<OWLAnnotationAssertionAxiom> axioms =
                ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION);
        Annotation2Cypher annot = new Annotation2Cypher(axioms);
        annot.createCypher();
        return annot.getCypher();
 
    }
    
    public String getClassCypher(){
        return classCreateCypher;
    }
    
    public ArrayList<ArrayList<String>> getIndividualList(){
        ArrayList <ArrayList<String>> individuals = new ArrayList();
        //fetches the individuals in the ontology
        Set<OWLNamedIndividual> individualSet = ontology.getIndividualsInSignature();
        //for each individuals gets the class assertion axioms refering to the individual
        
        for(OWLNamedIndividual i:individualSet){
            Set <OWLClassAssertionAxiom> axiom = ontology.getClassAssertionAxioms(i);
            //getting each of individual-superclass pairs
            for (OWLClassAssertionAxiom a:axiom){
                String superclass = shortName(a.getClassExpression().toString());
                String ind = shortName(a.getIndividual().toString());
                
                //creates a new temp arraylist with individual on the 1st row
                //and the superclass on the 2nd
                ArrayList <String> temp = new ArrayList();
                temp.add(ind);
                temp.add(superclass);
                individuals.add(temp);
            }
        }
                
        return individuals;
    }
    
    public void buildSuperclassCypher(){
        //get all the classes
        Set<OWLClass> set=ontology.getClassesInSignature();
        
        for (OWLClass s:set){
            String superclass = shortName(s.toString());
            Set <OWLClassExpression> subclasses = s.getSubClasses(ontology);
            if (!subclasses.isEmpty()){
                for (OWLClassExpression exp:subclasses){
                    String tempQuery = "MATCH (a {owl:\"class\", name:\""+
                            superclass+"\"}),\n"
                            + "(b {owl:\"class\", name:\""+shortName(exp.toString())+
                            "\"})\n"
                            + "MERGE (b)-[r:ISA]->(a)\n\n";
                    superclassCypher=superclassCypher+tempQuery;
                }
                
            }
        }                
    }
    
    public void printCypher(){
        System.out.println(superclassCypher);
    }
    
    public String getSuperclassCypher(){
        return superclassCypher;
    }
    
    public static String shortName(String owl2string){
        if (owl2string.contains("#")){
            if(owl2string.contains(">")){
            return owl2string.substring(owl2string.indexOf("#")+1, owl2string.lastIndexOf(">"));
            }
            else return owl2string.substring(owl2string.indexOf("#")+1);
        }
        else return owl2string;
    }
}
