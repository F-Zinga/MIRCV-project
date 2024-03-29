package unipi.mircv;

/**
 * Represents statistics related to the execution, such as the number of terms, the total number of documents,
 * the average document length and the number of postings.
 */
public class Statistics {

        private int terms; // Number of terms
        private int nDocs; // Total number of documents
        private double avdl; // Average document length
        private int postings; //Number of posting


    /**
     * Overrides the toString method to provide a string representation of the Statistics object.
     *
     * @return A string representation of the Statistics.
     */
    @Override
    public String toString() {
        return "Statistics{" +
                "terms=" + terms +
                ", nDocs=" + nDocs +
                ", avdl=" + avdl +
                ", postings" + postings +
                '}';
    }

    /**
     * Constructs a Statistics object with the given values.
     */
    public Statistics(int nDocs, double avdl, int terms, int postings) {
        this.nDocs = nDocs;
        this.avdl = avdl;
        this.terms = terms;
        this.postings = postings;
    }

    public void setnDocs(int nDocs) { this.nDocs = nDocs; }
    public int getNDocs() { return nDocs; }

    public double getAvdl() { return  avdl; }
    public void setAvdl (double avdl) { this.avdl = avdl; }

    public int getPostings() {
        return postings;
    }

    public void setPostings(int postings) {
        this.postings = postings;
    }

}
