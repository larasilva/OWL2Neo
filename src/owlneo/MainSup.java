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
import org.semanticweb.owlapi.model.DataRangeType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
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
    private ArrayList<String> headerList;


    public MainSup() {
        CSVInicialized = false;
        OWLInicialized = false;
        CSVdelim = ";";
        CSVliteral = "\"";
        headerList = new ArrayList();

    }

    public void setupCSVDelim(String delim) {
        CSVdelim = delim;
    }

    public void setupCSVLiteral(String literal) {
        CSVliteral = literal;
    }

    public String getCSVDelim() {
        return CSVdelim;
    }

    public String getCSVLiteral() {
        return CSVliteral;
    }

    public boolean setupOWL(File file) {
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
        HashMap<String, OWLClass> hash = chc.getHash();
        HashMap<String, ObjectProperty2String> OPtester
                = getObjectPropertyHashMap();
        for (String s : OPtester.keySet()) {
            System.out.println(s);
        }
        OWLInicialized = true;

        HashMap<String, OWLDataProperty> test = getDataPropertyList();
        /*for (String s : test.keySet()) {
         System.out.println(s);
         }*/

        return OWLInicialized;
    }

    public boolean isOWLSetup() {
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

    public String getOWLSuperclassCypher() {
        if (OWLInicialized) {
            structure.buildSuperclassCypher();
            return structure.getSuperclassCypher();
        } else {
            return ".";
        }
    }

    public String getAnnotationsCypher() {
        if (OWLInicialized) {
            return structure.getAnnotationsCypher();
        } else {
            return "";
        }
    }

    public HashMap<String, OWLDataProperty> getDataPropertyList() {

        if (!OWLInicialized) {
            return null;
        }

        Set<OWLDataProperty> dataPropSet = ontology.getDataPropertiesInSignature();
        HashMap<String, OWLDataProperty> result = new HashMap();
        for (OWLDataProperty dp : dataPropSet) {
            String temp = dp.toStringID();
            temp = temp.substring(temp.indexOf("#") + 1);

            for (OWLDataRange dr : dp.getRanges(ontology)) {
                System.out.println(temp + " = " + getDataPropertyRange(dp));
            }
            OWLDataProperty put = result.put(temp, dp);
            if (put != null) {
                System.out.println("Duplicate name of Data property");
            }
        }
        return result;
    }

    /**
     * Method that returns a String representation of the values accepted by the
     * data property, return an empty String if the DataProperty has no range
     * declared
     *
     * @param dataProperty
     * @return String representation of the values accepted by the data property
     */
    public String getDataPropertyRange(OWLDataProperty dataProperty) {
        String result = "";
        for (OWLDataRange dr : dataProperty.getRanges(ontology)) {
            //datarange = datatype
            if (dr.isDatatype()) {
                String drString = dr.toString();
                result = "\n" + result + drString.substring(drString.indexOf(":") + 1);
            }
            if (dr.getDataRangeType() == DataRangeType.DATA_ONE_OF) {
                String drString = dr.toString();
                drString = drString.substring(drString.indexOf("(") + 1,
                        drString.indexOf(")") - 1);
                //replaces all the spaces between double quotes with commas, 
                //keeping the double quotes
                result = "\n" + result + drString.replace("\" \"", "\",\"");
            }

        }
        if (result.length() > 0) {
            result = result.substring(1);
        }
        return result;
    }

    public HashMap<String, ObjectProperty2String> getObjectPropertyHashMap() {
        HashMap<String, ObjectProperty2String> result = new HashMap();
        Set<OWLObjectProperty> objectProperties
                = ontology.getObjectPropertiesInSignature();
        for (OWLObjectProperty op : objectProperties) {

            result.put(Structure2Cypher.shortName(op.toStringID()),
                    new ObjectProperty2String(ontology, op));
        }

        return result;
    }

    public void defineIDField(String headerField) {
        CSVinserter.setIDField(headerField);
    }

    public ArrayList<String> getCSVHeader() {
        return headerList;
    }

    public String[] getCSVHeaderArray() {

        String[] result = new String[headerList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = headerList.get(i);
        }
        return result;
    }

    public String numberOfFieldsLeft() {
        return Integer.toString(numberCSVfields);
    }

    public boolean setupCSV(File file) {
        CSVinserter = new CSVInserter(file, CSVdelim, CSVliteral);
        CSVInicialized = true;
        headerList = CSVinserter.getHeader(file);
        numberCSVfields = headerList.size();
        return CSVInicialized;
    }

    public boolean saveAsCypherScript(File file, String textAreaContents) {
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

    private HashMap<String, OWLClass> generateTreeStrings() {
        Set<OWLClass> set = ontology.getClassesInSignature();
        HashMap<String, OWLClass> result = new HashMap();

        return result;
    }
    
    public String getCSVFieldSummary(String header){
        return CSVinserter.getFieldSummary(header);
    }

    //TODO
    
    public void setupField(String header) {
        

    }
    
    public String getCSVColumnUniques(String header){
        int index=0;
        String temp = headerList.get(index);
        while (!header.equalsIgnoreCase(temp)){
            index++;
        }
        String result="";
        for(String s:CSVInserter.getUniques(CSVinserter.getColumnAtIndex(index))){
            result = result + s +"\n";
        }
        return result;
    }

}
