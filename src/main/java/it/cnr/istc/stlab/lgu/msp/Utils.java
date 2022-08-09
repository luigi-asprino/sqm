
package it.cnr.istc.stlab.lgu.msp;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.expr.ExprTransformCopy;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransform;
import org.apache.jena.sparql.syntax.syntaxtransform.QueryTransformOps;

import it.cnr.istc.stlab.lgu.msp.querytransformers.LiteralMask;

public class Utils {
	public static String maskQueryLiterals(String q) throws Exception {
		return q.replaceAll("\".*?\"", "\"LITERAL\"");
	}

	public static String getNodeType(Node n) {
		if (n == null) {
			return "P";
		}
		if (n.isBlank()) {
			return "B";
		}
		if (n.isLiteral()) {
			return "L";
		}
		if (n.isURI()) {
			return "U";
		}
		if (n.isVariable()) {
			return "V";
		}
		return "O";
	}

	public static String maskQueryUsingTransformer(String queryString, ElementTransform... elts) throws Exception {
		Query query = QueryFactory.create(queryString);

		ExprTransformCopy cp = new ExprTransformCopy(true);
		Query result = query;
		for (ElementTransform elt : elts) {
			result = QueryTransformOps.transform(query, elt, cp);
		}

		return result.toString(Syntax.defaultQuerySyntax);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(maskQueryUsingTransformer("SELECT * {?s ?p \"a\"}", new LiteralMask()));
	}

}
