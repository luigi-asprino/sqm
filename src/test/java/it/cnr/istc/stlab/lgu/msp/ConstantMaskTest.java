package it.cnr.istc.stlab.lgu.msp;

import static org.junit.Assert.assertTrue;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.junit.Test;

import it.cnr.istc.stlab.lgu.msp.querytransformers.ConstantMask;

public class ConstantMaskTest {

	@Test
	public void test1() {

		String queryInputString = "SELECT * {?s <http://test.org/predicate> \"ABC\"}";
		String queryExpectedString = "SELECT * {?s <http://test.org/predicate> \"ABC\"}";
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new ConstantMask());
			Query queryExpected = QueryFactory.create(queryExpectedString);
			Query queryActual = QueryFactory.create(queryActualString);
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test2() {

		String queryInputString = "SELECT * {?s  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> \"ABC\"}";
		String queryExpectedString = "SELECT * {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> \"ABC\"}";
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new ConstantMask());
			Query queryExpected = QueryFactory.create(queryExpectedString);
			Query queryActual = QueryFactory.create(queryActualString);
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test3() {

		String queryInputString = "SELECT * {?s  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://test.org/class> }";
		String queryExpectedString = "SELECT * {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://test.org/class> }";
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new ConstantMask());
			Query queryExpected = QueryFactory.create(queryExpectedString);
			Query queryActual = QueryFactory.create(queryActualString);
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test5() {

		String queryInputString = "SELECT * {<http://test.org/subject>  <http://test.org/predicate> <http://test.org/object> }";
		String queryExpectedString = String.format("SELECT * {<%s> <http://test.org/predicate> <%s>}",
				ConstantMask.CONSTANT.getURI(), ConstantMask.CONSTANT.getURI());
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new ConstantMask());
			Query queryExpected = QueryFactory.create(queryExpectedString);
			Query queryActual = QueryFactory.create(queryActualString);
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
