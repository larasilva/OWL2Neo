/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlneo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
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
    private Structure2Cypher structure;
    private int numberCSVfields;
    
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
        structure = new Structure2Cypher(manager, ontology, reasoner);
        
        //test
        ClassHashCreator chc = new ClassHashCreator(ontology);
        HashMap <String, OWLClass> hash = chc.getHash();
        for (String s:hash.keySet()){
            System.out.println(s);
        }
        
        
        OWLInicialized = true;
        
        return OWLInicialized;
    }
    
    public boolean isOWLSetup(){
        return OWLInicialized;
    }
    
    public String getOWLCreateCypher() {
        if (OWLInicialized) {
            structure.buildClassCypher();
            return structure.getClassCypher();
        } else {
            return "Please load your OWL File.";
        }
    }
    
    public String getOWLSuperclassCypher(){
        if (OWLInicialized){
            structure.buildSuperclassCypher();
            return structure.getSuperclassCypher();
        }
        else {
            return ".";
        }
    }
    
    public String getAnnotationsCypher(){
        if (OWLInicialized){
            return structure.getAnnotationsCypher();
        }
        else return "";
    }

    public void defineIDField(String headerField){
        CSVinserter.setIDField(headerField);
    }
    
    public ArrayList <String> getCSVHeader(){   
        return CSVinserter.getHeader(CSVFile);
    }
    
    public String numberOfFieldsLeft(){
        return Integer.toString(numberCSVfields);
    }
    
    public boolean setupCSV(File file){
        CSVinserter=new CSVInserter(file, CSVdelim, CSVliteral);
        CSVInicialized = true;
        numberCSVfields = CSVinserter.getHeader(file).size();
        return CSVInicialized;
    }
    
    public boolean saveAsCypherScript(File file, String textAreaContents){
        boolean success = false;
        try { 
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(textAreaContents);
            bw.close();
            success = true;
        } catch (IOException ex) {
            Logger.getLogger(MainSup.class.getName()).log(Level.SEVERE, null, ex);
            return success;
        }
        return success;
    }
    
    private HashMap<String, OWLClass> generateTreeStrings(){
        Set<OWLClass> set=ontology.getClassesInSignature();
        HashMap<String, OWLClass> result = new HashMap();
        
        
        return result;
    }
    
    
    
    //TODO
    public void setupField(){
        
        
    }
    
}
