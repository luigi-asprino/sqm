
package it.cnr.istc.stlab.lgu.msp.querytransformers;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.PathBlock;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformCopyBase;
import org.apache.jena.vocabulary.RDF;

public class ConstantMask extends ElementTransformCopyBase {

//	private static final Logger logger = LoggerFactory.getLogger(ConstantMask.class);

	public static final Node CONSTANT = NodeFactory.createURI("https://w3id.org/msp/constant");

	public ConstantMask() {
		super(true);
	}

	@Override
	public Triple transform(Triple triple) {
		Node subject = triple.getSubject();
		Node predicate = triple.getPredicate();
		Node object = triple.getObject();

		if (subject.isURI()) {
			subject = CONSTANT;
		}

		if (object.isURI() && !predicate.equals(RDF.type.asNode())) {
			object = CONSTANT;
		}

		return new Triple(subject, predicate, object);
	}

	@Override
	public Element transform(ElementPathBlock el) {
		PathBlock before = el.getPattern();
		PathBlock copy = new PathBlock();
		before.getList().forEach(tp -> {
			copy.add(new TriplePath(transform(new Triple(tp.getSubject(), tp.getPredicate(), tp.getObject()))));
		});
		el = new ElementPathBlock(copy);

		return el;
	}

}
