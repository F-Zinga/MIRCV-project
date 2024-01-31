package unipi.mircv;

/**
 * A class representing a term in an inverted index, containing various metadata such as offsets,
 * posting list length, and a term upper bound.
 */
public class Term {

    public int offsetDocId;
    public int offsetFreq;
    public int offsetLastDocIds;
    public int offsetSkipPointer;
    public int postingListLength;
    public float termUpperBound;

    /**
     * Constructs a Term with the given metadata.
     *
     * @param offsetDocId        Offset of the document identifier.
     * @param offsetFreq         Offset of the term frequency.
     * @param offsetLastDocIds   Offset of the last document identifiers.
     * @param offsetSkipPointer    Offset of the skip block.
     * @param postingListLength  Length of the posting list.
     * @param termUpperBound     Upper bound for the term.
     */
    public Term(int offsetDocId, int offsetFreq, int offsetLastDocIds, int offsetSkipPointer, int postingListLength, float termUpperBound) {
        this.offsetDocId = offsetDocId;
        this.offsetFreq = offsetFreq;
        this.offsetLastDocIds = offsetLastDocIds;
        this.offsetSkipPointer = offsetSkipPointer;
        this.postingListLength = postingListLength;
        this.termUpperBound = termUpperBound;
    }

    public float getTermUpperBound() {
        return termUpperBound;
    }

    public void setTermUpperBound(float termUpperBound) {
        this.termUpperBound = termUpperBound;
    }


    public int getPostingListLength() {
        return postingListLength;
    }

    public void setPostingListLength(int postingListLength) {
        this.postingListLength = postingListLength;
    }

    public int getOffsetDocId() {
        return offsetDocId;
    }

    public int getOffsetFreq() {
        return offsetFreq;
    }


    public int getOffsetLastDocIds() {
        return offsetLastDocIds;
    }
    public int getOffsetSkipPointers() {
        return offsetSkipPointer;
    }

    public void setOffsetLastDocIds(int offsetDocId) {
        this.offsetLastDocIds = offsetLastDocIds;
    }

    /**
     * Overrides the toString method to provide a string representation of the Term's metadata.
     *
     * @return A string representation of the Term.
     */
    @Override
    public String toString() {
        return offsetDocId + " " + offsetFreq + " " + offsetLastDocIds + " "
                + offsetSkipPointer + " " + postingListLength + " " + termUpperBound;
    }
}


