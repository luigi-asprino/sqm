
package it.cnr.istc.stlab.lgu.msp.querytransformers;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.PathBlock;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.graph.NodeTransform;
import org.apache.jena.sparql.serializer.SerializationContext;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformCopyBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiteralMask extends ElementTransformCopyBase {

	private static final Logger logger = LoggerFactory.getLogger(LiteralMask.class);

	private static final Node LITERAL = NodeFactory.createLiteral("LITERAL");

	public LiteralMask() {
		super(true);
	}

	@Override
	public Triple transform(Triple triple) {
		if (triple.getObject().isLiteral()) {
			return new Triple(triple.getSubject(), triple.getPredicate(), LITERAL);
		}
		return triple;
	}

	@Override
	public Element transform(ElementPathBlock el) {
		PathBlock before = el.getPattern();
		PathBlock copy = new PathBlock();
		before.getList().forEach(tp -> {
			if (tp.getObject().isLiteral()) {
				Triple t = new Triple(tp.getSubject(), tp.getPredicate(), LITERAL);
				copy.add(new TriplePath(t));
			} else {
				copy.add(tp);
			}
		});
		el = new ElementPathBlock(copy);

		return el;
	}

	@Override
	public Element transform(ElementFilter el, Expr expr2) {
		logger.trace("Transform Filter");
		if (expr2.isFunction()) {
			logger.trace("Function {} {}", expr2.getFunction().toString(),
					expr2.getFunction().getFunctionName(new SerializationContext()));

			boolean isRegex = expr2.getFunction().getFunctionName(new SerializationContext()).equals("regex");

			Expr res = expr2.applyNodeTransform(new NodeTransform() {

				@Override
				public Node apply(Node t) {
					if (t.isLiteral()) {
						logger.trace("LITERAL {}", t.toString());
						if (isRegex && t.getLiteralValue().toString().matches("s|m|i|x|q")) {
							logger.trace("matched {}", NodeFactory.createLiteral(t.getLiteralValue().toString()));
							return NodeFactory.createLiteral(t.getLiteralValue().toString());
						}
						logger.trace("Returning Literal");
						return LITERAL;
					}
					return t;
				}
			});

			return new ElementFilter(res);

		}

		return new ElementFilter(expr2);
	}

	@Override
	public Element transform(ElementBind el, Var v, Expr expr2) {
		Expr expr = expr2.applyNodeTransform(new NodeTransform() {

			@Override
			public Node apply(Node t) {
				if (t.isLiteral()) {
					return LITERAL;
				}
				return t;
			}
		});
		return new ElementBind(v, expr);
	}

}
