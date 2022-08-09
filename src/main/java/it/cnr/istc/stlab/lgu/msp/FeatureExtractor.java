package it.cnr.istc.stlab.lgu.msp;

import it.cnr.istc.stlab.lgu.msp.visitors.ElementVisitor;
import it.cnr.istc.stlab.lgu.msp.visitors.PathExtractor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.syntax.Element;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FeatureExtractor {

    private static Map<String, Object> extractFeatures(Query q, boolean extractEntityNames, ElementVisitor ev) {
        Element e = q.getQueryPattern();
        ev.setExtractEntityNames(extractEntityNames);
        e.visit(ev);
        Map<String, Object> features = ev.getFeatures();
        PathExtractor pe = new PathExtractor(ev.getPaths());
        e.visit(pe);
        features.putAll(pe.getFeatures());
        return features;
    }


    public static void main(String[] args) throws IOException {
        String inputFolder = "/Users/lgu/Desktop/MSQ/dbpedia/";
        boolean extractEntityNames = false;
        extractFeaturesFromQueryLog(inputFolder, extractEntityNames);
    }

    private static void extractFeaturesFromQueryLog(String inputFolder, boolean extractEntityNames) throws IOException {
        String inputFilepath = inputFolder + "queryResult.csv";
        String outputFilepathFeatures = inputFolder + "extracted_features.csv";
        String outputFilepathFeatureNames = inputFolder + "feature_names.csv";
        String outputFilepathQueries = inputFolder + "queries.csv";

        CSVParser csvParser = new CSVParser(new FileReader(inputFilepath), CSVFormat.DEFAULT.builder().setHeader("query", "original_query", "masked_query_string_match", "masked_query_transform", "endpoint", "time", "host").setSkipHeaderRecord(true).build());

        BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilepathFeatures));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

        BufferedWriter writerFeatureNames = Files.newBufferedWriter(Paths.get(outputFilepathFeatureNames));
        CSVPrinter csvPrinterFeatureNames = new CSVPrinter(writerFeatureNames, CSVFormat.DEFAULT);

        BufferedWriter writerQueries = Files.newBufferedWriter(Paths.get(outputFilepathQueries));
        CSVPrinter csvPrinterQueries = new CSVPrinter(writerQueries, CSVFormat.DEFAULT);

        Map<String, Integer> featureNames = new HashMap<>();
        int featureNumber = 0;

        final AtomicInteger i = new AtomicInteger();
        for (CSVRecord record : csvParser) {
            try {

                Query q = QueryFactory.create(record.get("query"));

                csvPrinterQueries.printRecord(i.get(), q.toString(Syntax.defaultSyntax));

                ElementVisitor ev = new ElementVisitor();
                Map<String, Object> features = extractFeatures(q, extractEntityNames, ev);

                for (String f : features.keySet()) {
                    if (!featureNames.containsKey(f)) {
                        featureNames.put(f, featureNumber);
                        featureNumber++;
                    }
                }

                ev.getFeatures().forEach((k, v) -> {
                    try {
                        csvPrinter.printRecord(i.get(), featureNames.get(k), v);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                i.incrementAndGet();
                if (i.get() % 100000 == 0) {
                    System.out.println("Processed " + i.get());
                }
                csvPrinterQueries.flush();
                csvPrinter.flush();
            } catch (Exception | OutOfMemoryError e) {
                System.err.println(e.getMessage());
            }


        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(featureNames.entrySet());
        list.sort(Map.Entry.comparingByValue());

        list.forEach(e -> {
            try {
                csvPrinterFeatureNames.printRecord(e.getKey(), e.getValue());
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        });

        csvPrinter.flush();
        csvPrinter.close();

        csvPrinterFeatureNames.flush();
        csvPrinterFeatureNames.close();

        csvPrinterQueries.flush();
        csvPrinterQueries.close();
    }
}