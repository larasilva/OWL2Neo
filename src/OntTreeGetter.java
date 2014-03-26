package owlparse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;


/**
 *
 * @author Rubi
 */
public class OntTreeGetter {
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private ArrayList <String> queries;
    private ArrayList <String> classDeclarations;
    private SimpleShortFormProvider shortFormProvider;
    
    public OntTreeGetter(OWLOntologyManager manager, OWLOntology ontology,
            OWLReasoner reasoner){
        
        this.manager=manager;
        this.ontology=ontology;
        this.reasoner=reasoner;
        queries = new ArrayList();
        shortFormProvider = new SimpleShortFormProvider();
    }
    
    public ArrayList<String> getList(){
        return queries;
    }
    
    public void treeParser(Node<OWLClass> node){
        Set <OWLClass> entities = node.getEntities();
        Iterator <OWLClass> i=entities.iterator();
        OWLClass nodeRep = node.getRepresentativeElement();
        String className = shortFormProvider.getShortForm(nodeRep);
        while(i.hasNext()){
            System.out.print(className);
           Set <OWLClassExpression> exp =i.next().getSubClasses(ontology);
           System.out.print(exp);
        }
        
    }

}