package unipi.mircv;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoreFunction {
    private String[] queryTerms;
    private HashMap<String, Double> idf;
    private HashMap<Integer, DocInfo> docInfo;
    private double avgDocumentLength;
    private String scoreType;

    public ScoreFunction(HashMap<String, ArrayList<Posting>> postingLists, String[] queryTerms, HandleIndex handleIndex, String scoreType) {
        double nDocuments = handleIndex.getCollectionStatistics().getDocuments();
        this.queryTerms = queryTerms;

        this.idf = new HashMap<>();
        for (String term : postingLists.keySet()) {
            double df = handleIndex.getLexicon().getLexicon().get(term).getPostingListLength();
            idf.put(term, Math.log(nDocuments / df));
        }

        this.avgDocumentLength = handleIndex.getCollectionStatistics().getAvgDocumentLength();
        this.docInfo = handleIndex.getDocumentIndex().getDocumentIndex();

        this.scoreType = scoreType;
    }

    public double scoreF(String term,Posting posting, String scoreType) {
        double k1 = 1.6;
        double b = 0.75;
        if (scoreType.equals("bm25")) {

            double tf = posting.getTermFrequency();
            double denominator = k1 * ((1 - b) + b * ((double) docInfo.get(posting.getDocID()).getDocLen() / avgDocumentLength)) + tf;
            double idf = this.idf.get(term);

            return (tf * idf) / denominator;
        } else if (scoreType.equals("tfidf")) {
            double tf = 1 + Math.log(posting.getTermFrequency());
            double idf = this.idf.get(term);
            return tf * idf;
        } else {
        return 1;
        }
    }
}

