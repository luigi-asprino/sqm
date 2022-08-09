package it.cnr.istc.stlab.lgu.msp;

import static org.junit.Assert.assertTrue;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.junit.Test;

import it.cnr.istc.stlab.lgu.msp.querytransformers.LiteralMask;

public class LiteralMaskTest {

	@Test
	public void test1() {

		String queryInputString = "SELECT * {?s ?p \"ABC\"}";
		String queryExpectedString = "SELECT * {?s ?p \"LITERAL\"}";
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new LiteralMask());
			Query queryExpected = QueryFactory.create(queryExpectedString);
			Query queryActual = QueryFactory.create(queryActualString);
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test2() {

		String queryInputString = "SELECT * {?s ?p 1}";
		String queryExpectedString = "SELECT * {?s ?p \"LITERAL\"}";
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new LiteralMask());
			Query queryExpected = QueryFactory.create(queryExpectedString);
			Query queryActual = QueryFactory.create(queryActualString);
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test3() {
		String queryInputString = "SELECT * {?s ?p ?v .  FILTER regex(?v, \"leu\", \"i\") }";
		String queryExpectedString = "SELECT * {?s ?p ?v .  FILTER regex(?v, \"LITERAL\", \"i\") }";
		Query queryExpected = QueryFactory.create(queryExpectedString);
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new LiteralMask());
			Query queryActual = QueryFactory.create(queryActualString);
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4() {
		String queryInputString = "SELECT * {?s ?p ?v .  FILTER (?v = \"i\") }";
		String queryExpectedString = "SELECT * {?s ?p ?v .  FILTER (?v = \"LITERAL\") }";
		Query queryExpected = QueryFactory.create(queryExpectedString);
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new LiteralMask());
			Query queryActual = QueryFactory.create(queryActualString);
			System.out.println(queryActual.toString());
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test5() {
		String queryInputString = "SELECT * {BIND (\"i\" AS ?v) }";
		String queryExpectedString = "SELECT * { BIND (\"LITERAL\" AS ?v) }";
		Query queryExpected = QueryFactory.create(queryExpectedString);
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new LiteralMask());
			Query queryActual = QueryFactory.create(queryActualString);
			System.out.println(queryActual.toString());
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPredicate() {

		String queryInputString = "SELECT * {?s <http://test.org/test> \"ABC\"}";
		String queryExpectedString = "SELECT * {?s <http://test.org/test> \"LITERAL\"}";
		try {
			String queryActualString = Utils.maskQueryUsingTransformer(queryInputString, new LiteralMask());
			Query queryExpected = QueryFactory.create(queryExpectedString);
			Query queryActual = QueryFactory.create(queryActualString);
			assertTrue(queryExpected.equals(queryActual));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

}
