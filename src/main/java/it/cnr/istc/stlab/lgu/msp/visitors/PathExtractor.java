package it.cnr.istc.stlab.lgu.msp.visitors;

import it.cnr.istc.stlab.lgu.msp.Utils;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.*;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PathExtractor implements org.apache.jena.sparql.syntax.ElementVisitor {

    private final Map<String, Object> features = new HashMap<>();
    private final Map<String, TriplePath> paths ;

    public PathExtractor(Map<String, TriplePath> p){
        this.paths = p;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }



    @Override
    public void visit(ElementTriplesBlock el) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ElementPathBlock el) {

        StreamSupport.stream(el.getPattern().spliterator(), true).forEach(t -> {
            TriplePath tp = t;
            StringBuilder sb = new StringBuilder();

            Set<TriplePath> alreadyseenOnThePath = new HashSet<>();

            while (true) {
                alreadyseenOnThePath.add(tp);
                sb.append(Utils.getNodeType(tp.getSubject()));
                if (tp.getPredicate().isURI()) {
                    sb.append(tp.getPredicate().getLocalName());
                } else {
                    sb.append(Utils.getNodeType(tp.getPredicate()));
                }
                sb.append(Utils.getNodeType(tp.getObject()));
                if (paths.get(tp.getObject().toString()) != null && !alreadyseenOnThePath.contains(paths.get(tp.getObject().toString()))) {
                    tp = paths.get(tp.getObject().toString());
                } else {
                    break;
                }
            }

            features.put(sb.toString(), 1);
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
