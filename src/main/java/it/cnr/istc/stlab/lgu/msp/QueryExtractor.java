package it.cnr.istc.stlab.lgu.msp;

import it.cnr.istc.stlab.lgu.msp.querytransformers.LiteralMask;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryExtractor {

    private static final Logger logger = LoggerFactory.getLogger(QueryExtractor.class);

    public static void main(String[] args) throws Exception {
        String queryString = FileUtils.readFileToString(new File("src/main/resources/queryLight.sparql"),
                Charset.defaultCharset());

        String endpoint = "http://localhost:3030/lsq-dbpedia/sparql";
        String csvFile = "/Users/lgu/Desktop/a.csv";
        extractQueries(queryString, endpoint, csvFile, -1);
    }

    static void extractQueries(String queryString, String endpoint, String csvFile, int limit) throws Exception {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvFile));
        Query query = QueryFactory.create(queryString);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

        List<String> headers = new ArrayList<>();

        headers.add("query");
        headers.add("original_query");
        headers.add("masked_query_string_match");
        headers.add("masked_query_transform");

        Set<String> variableNames = new HashSet<>();
        query.getProjectVars().forEach(v -> variableNames.add(v.getVarName()));

        if (variableNames.contains("endpoint")) {
            headers.add("endpoint");
        }

        if (variableNames.contains("time")) {
            headers.add("time");
        }

        if (variableNames.contains("host")) {
            headers.add("host");
        }


        csvPrinter.printRecord(headers);
        csvPrinter.flush();

        int alreadyDownloaded = 0, downloadedInChunk = 0;
        while (true) {
            query = QueryFactory.create(queryString + " LIMIT 100000\nOFFSET " + alreadyDownloaded);
            logger.info("Executing\n{}", query.toString(Syntax.defaultQuerySyntax));
            QueryExecution qexec = QueryExecution.service(endpoint, query);
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                List<String> record = new ArrayList<>();

                String uriQuery = qs.get("query").asNode().getURI();
                String originalQuery = qs.get("text").asLiteral().getString();
                String transformedQuery = "";
                String stringMatching = Utils.maskQueryLiterals(originalQuery);

                try {
                    transformedQuery = Utils.maskQueryUsingTransformer(originalQuery, new LiteralMask());
                } catch (Exception e) {
                    logger.error("Error with QUERY {}", uriQuery);
                }

                record.add(uriQuery);
                record.add(originalQuery);
                record.add(stringMatching);
                record.add(transformedQuery);


                if (qs.contains("endpoint")) {
                    record.add(qs.get("endpoint").asNode().getURI());
                }

                if (qs.contains("time")) {
                    record.add(qs.get("time").asLiteral().getString());
                }

                if (qs.contains("time")) {
                    record.add(qs.get("host").asLiteral().getString());
                }


                csvPrinter.printRecord(record);

                alreadyDownloaded++;
                downloadedInChunk++;
            }

            csvPrinter.flush();

            if (downloadedInChunk == 0) {
                break;
            }
            downloadedInChunk = 0;

            if (limit > 0 && alreadyDownloaded > limit) {
                logger.info("Limit reached, closing the process");
                break;
            }

        }

        csvPrinter.flush();
        csvPrinter.close();
    }


}
