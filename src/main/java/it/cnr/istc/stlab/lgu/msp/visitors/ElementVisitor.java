package it.cnr.istc.stlab.lgu.msp.visitors;

import it.cnr.istc.stlab.lgu.msp.Utils;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.*;

import java.util.HashMap;
import java.util.Map;

public class ElementVisitor implements org.apache.jena.sparql.syntax.ElementVisitor {

    private final Map<String, Object> features = new HashMap<>();
    private final Map<String, TriplePath> paths = new HashMap<>();

    private boolean extractEntityNames = false;

    public Map<String, Object> getFeatures() {
        return features;
    }

    public Map<String, TriplePath> getPaths() {
        return paths;
    }

    public void setExtractEntityNames(boolean extractEntityNames) {
        this.extractEntityNames = extractEntityNames;
    }

    @Override
    public void visit(ElementTriplesBlock el) {

        el.getPattern().iterator().forEachRemaining(t -> {
            if (extractEntityNames) {
                features.put(t.getSubject().toString(), 1);
                features.put(t.getPredicate().toString(), 1);
                features.put(t.getObject().toString(), 1);

            }
            features.put(Utils.getNodeType(t.getSubject()) + Utils.getNodeType(t.getPredicate()) + Utils.getNodeType(t.getObject()), 1);
        });

    }

    @Override
    public void visit(ElementPathBlock el) {
        el.getPattern().iterator().forEachRemaining(t -> {
            if (extractEntityNames) {
                features.put(t.getSubject().toString(), 1);
                if (t.getPredicate() != null) {
                    features.put(t.getPredicate().toString(), 1);
                }
                features.put(t.getObject().toString(), 1);
            }
            features.put(Utils.getNodeType(t.getSubject()) + Utils.getNodeType(t.getPredicate()) + Utils.getNodeType(t.getObject()), 1);
            if (t.getPredicate().isURI()) {
                features.put(Utils.getNodeType(t.getSubject()) + t.getPredicate().getLocalName() + Utils.getNodeType(t.getObject()), 1);
            }

            paths.put(t.getSubject().toString(), t);
        });
    }

    @Override
    public void visit(ElementFilter el) {
    }

    @Override
    public void visit(ElementAssign el) {
    }

    @Override
    public void visit(ElementBind el) {
    }

    @Override
    public void visit(ElementData el) {
    }

    @Override
    public void visit(ElementUnion els) {
        for (Element el : els.getElements()) {
            el.visit(this);
        }
    }

    @Override
    public void visit(ElementOptional el) {
        el.getOptionalElement().visit(this);
    }

    @Override
    public void visit(ElementGroup els) {
        for (Element el : els.getElements()) {
            el.visit(this);
        }
    }

    @Override
    public void visit(ElementDataset el) {
        el.getElement().visit(this);
    }

    @Override
    public void visit(ElementNamedGraph el) {
        el.getElement().visit(this);
    }

    @Override
    public void visit(ElementExists el) {
        el.getElement().visit(this);
    }

    @Override
    public void visit(ElementNotExists el) {
        el.getElement().visit(this);
    }

    @Override
    public void visit(ElementMinus el) {
        el.getMinusElement().visit(this);
    }

    @Override
    public void visit(ElementService el) {
        el.getElement().visit(this);

    }

    @Override
    public void visit(ElementSubQuery el) {
        el.getQuery().getQueryPattern().visit(this);
    }
}
