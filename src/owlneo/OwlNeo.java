/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package owlneo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.neo4j.graphdb.Transaction;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import owlparse.OntTreeGetter;

/**
 *
 * @author Rubi
 */
public class OwlNeo {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {

        //Ubuntu
        File file = new File("/home/lara/Downloads/clinicaleval_v2rdf.owl");
        //Windows
        //File file = new File("C:\\Users\\Rubi\\24fev\\clinicaleval_v2_2.owl");

        //prepare ontology and reasoner 
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
        //Hermit reasoner - needed to query the ontology
        OWLReasoner reasoner
                = new Reasoner.ReasonerFactory().createReasoner(ontology);
        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        //get thing node
        Node<OWLClass> thingNode = reasoner.getTopClassNode();
        OWLClass thingClass = thingNode.getRepresentativeElement();

        //getting all the classes and annotations
        ArrayList<String> classList = new ArrayList();
        //Cypher commands for annotations (hasXDbref and comment)
        ArrayList<String> annotationCypherStr = new ArrayList();
        for (OWLClass c : ontology.getClassesInSignature(true)) {
            String classString = c.toString();
            Set<OWLAnnotationAssertionAxiom> anotAxioms
                    = c.getAnnotationAssertionAxioms(ontology);
            Annotation2Cypher annotationcypher = new Annotation2Cypher(anotAxioms);
            annotationcypher.createCypher();
            ArrayList<String> temp = annotationcypher.getCypher();
            for (String t : temp) {
                annotationCypherStr.add(t);
            }

            /*
             for (OWLAnnotationAssertionAxiom ax:anotAxioms){
             System.out.println(ax.getValue().toString());
             }
             */
            if (classString.contains("#")) {
                classString = classString.substring(
                        classString.indexOf("#") + 1, classString.lastIndexOf(">"));
            }
            classList.add(classString);

        }

        //creating a list of all isA statments
        ArrayList<String> isAList = new ArrayList();

        //OntTreeGetter whaaaa = new OntTreeGetter(manager, ontology, reasoner);
        //whaaaa.treeParser(thingNode);
        //String s = shortFormProvider.getShortForm(thingClass);
        /*
         System.out.println(thingNode.toString());
         System.out.println(s);
        
         System.out.println("Classes:");
         */
        //Array with create node queries
        ArrayList<String> cypherClass = new ArrayList();
        //node thing
        cypherClass.add("CREATE (n {owl: thing});");
        for (String a : classList) {
            cypherClass.add("Create (n {owl: " + a + "});");
            //System.out.println(a);
        }

        for (String b : cypherClass) {
            //System.out.println(b);
        }

        Structure2Cypher test = new Structure2Cypher(manager, ontology, reasoner);
        test.buildCypher();

        /*
         CopyPasterino from: https://github.com/owlcs/owlapi/wiki/DL-Queries-with-a-real-reasoner
         DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner,
         shortFormProvider), shortFormProvider);
         */
        //isA
        Structure2Cypher isa = new Structure2Cypher(manager, ontology, reasoner);
        isa.buildCypher();
        isa.printCypher();

        //Arraylist de individuals
        ArrayList<String> individualString = new ArrayList();
        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
        for (OWLNamedIndividual i : individuals) {

            //Set of axxioms referencing individuals
            Set<OWLAxiom> axSet = i.getReferencingAxioms(ontology);

            //Filtering by class assertion
            Set<OWLAxiom> classAssertion = AxiomType.getAxiomsOfTypes(axSet, AxiomType.CLASS_ASSERTION);
            for (OWLAxiom a : classAssertion) {
                String superclass = Structure2Cypher.shortName(a.getClassesInSignature().toString());
                String indiv = Structure2Cypher.shortName(a.getIndividualsInSignature().toString());
                individualString.add("MATCH (a {owl: " + superclass + "}),\n"
                        + "MERGE (a) <- [r: IsA] - (b {individual: " + indiv + "});\n");
                /*System.out.println(Structure2Cypher.shortName(a.getClassesInSignature().toString())
                 +" - "+Structure2Cypher.shortName(a.getIndividualsInSignature().toString()));*/
            }

        }

        for (String s : annotationCypherStr) {
            System.out.println(s);
        }

        /*
         //print to script file
         File fileout = new File("/home/lara/Downloads/clinicaleval_v2rdf.cql");
         if (!fileout.exists()) {
         fileout.createNewFile();
         }
        
         FileWriter fw = new FileWriter(fileout.getAbsoluteFile());
         BufferedWriter bw = new BufferedWriter(fw);
        
         //write classes
        
         for (String s:cypherClass){
         bw.write(s+"\n");
         }
        
        
         //write annotations
         bw.write(annotationCypher+"\n");
        
         //individuals
         for (String s : individualString) {
         bw.write(s+"\n");
         }
        
         bw.close();
         System.out.print("done");
         */
        //test for individuals/class list
        ArrayList<ArrayList<String>> temp = test.getIndividualList();

        for (ArrayList<String> i : temp) {
            for (String s : i) {
                System.out.print(s + "  ");
            }
            System.out.println();

        }
    }

}
