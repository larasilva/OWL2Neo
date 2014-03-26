/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlneo;

import java.awt.Window;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author lara
 */
public class MainSup {
    private File OWLFile;
    private File CSVFile;
    private boolean CSVInicialized;
    private boolean OWLInicialized;
    private OWLOntology ontology;
    private OWLOntologyManager manager;
    private OWLReasoner reasoner;
    private String CSVdelim;
    private String CSVliteral;
    private CSVInserter CSVinserter;
    
    public MainSup(){
        CSVInicialized = false;
        OWLInicialized = false;
        CSVdelim = ";";
        CSVliteral = "\"";
    }
    
    public void setupCSVDelim(String delim){
        CSVdelim = delim;   
    }
    
    public void setupCSVLiteral(String literal){
        CSVliteral = literal;
    }
    
    public String getCSVDelim(){
        return CSVdelim;
    }
    
    public String getCSVLiteral(){
        return CSVliteral;
    }
    
    public boolean setupOWL(File file){
        //prepare ontology and reasoner 
        manager = OWLManager.createOWLOntologyManager();
        try {
            ontology = manager.loadOntologyFromOntologyDocument(file);
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(MainSup.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        //Hermit reasoner - needed to query the ontology
        reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);
        
        OWLInicialized = true;
        
        return OWLInicialized;
    }
    
    public boolean setupCSV(File file){
        CSVinserter=new CSVInserter(file, CSVdelim, CSVliteral);
        CSVInicialized = true;        
        return CSVInicialized;
    }
    
}
