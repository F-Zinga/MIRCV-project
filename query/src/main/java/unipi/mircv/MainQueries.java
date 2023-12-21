package unipi.mircv;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Scanner;


public class MainQueries {

        private static Lexicon lexicon = new Lexicon();

        //Flag to indicate if the scoring function is bm25 (true) or TFIDF (false)
        private static boolean bm25scoring = false;

        //Flag to indicate if the queryType is disjunctive (true) or conjunctive (false)
        private static boolean queryType = true;


        public static void main( String[] args )
        {

            Settings settings = new Settings();

            //If no configuration is found, then no inverted index is present. The program exits.
            if(!settings.loadSettings())
                return;

            System.out.println("[QUERY PROCESSOR] Building inverted index configuration:");

            System.out.println("[QUERY PROCESSOR] Loading the lexicon in memory...");
            lexicon = new Lexicon();
            lexicon.loadLexicon();
            if(settings.getDebug()){
                System.out.println("[DEBUG] Lexicon size: " + lexicon.size());
            }

            System.out.println("[QUERY PROCESSOR] Loading the document index in memory...");
            DocIndex documentIndex = new DocIndex();
            documentIndex.loadDocumentIndex();
            if(settings.getDebug()){
                System.out.println("[DEBUG] Document index size: " + documentIndex.size());
            }

            System.out.println("[QUERY PROCESSOR] Data structures loaded in memory.");

            //Flag to indicate if the stopwords removal and stemming are enabled, this must be retrieved from the configuration
            boolean stopwordsRemovalAndStemming = settings.getStemmingAndStopWords();

            //Set the initial parameters for the query processor
            setQueryProcessorParameters(settings);

            //Wait for a new command, the while is used to prevent malformed inputs
            //This must be modified in order to have also the possibility to change the query parameters
            while (true) {

                //Get a command from the command line
                int command = getCommand();

                //Check the command
                if(command == 0) { //New query command

                    //Read the next query
                    String query = getQuery();

                    //Parse the query
                    String[] queryTerms = parseQuery(query, stopwordsRemovalAndStemming);

                    //If the query string is equal to null it means that the query contains all stopwords or all the terms
                    // were written in a bad way or not present in the lexicon.
                    if(queryTerms == null || queryTerms.length == 0) {
                        System.out.println("You're query is too vague, try to reformulate it.");
                        continue;
                    }

                    //Load the posting list of the terms of the query
                    PostingList[] postingLists = new PostingList[queryTerms.length];

                    //For each term in the query terms array
                    for (int i = 0; i < queryTerms.length; i++) {

                        //Instantiate the posting for the i-th query term
                        postingLists[i] = new PostingList();

                        //Load in memory the posting list of the i-th query term
                        postingLists[i].openList(lexicon.get(queryTerms[i]));

                        //Debug
                        //System.out.println(queryTerms[i] + ": " + postingLists[i].size());
                    }

                    ArrayList<Pair<Long, Double>> result;

                    //Score the collection
                    if(queryType){
                        result = MaxScore.scoreCollectionDisjunctive(postingLists,documentIndex, bm25scoring, settings.getDebug());
                    }else {
                        result = MaxScore.scoreCollectionConjunctive(postingLists,documentIndex, bm25scoring, settings.getDebug());
                    }

                    System.out.println(documentIndex.get(result.get(0).getValue0()));

                    //Print the results in a formatted way
                    System.out.println("\n#\tDOCNO\t\tSCORE");
                    for(int i = 0; i < result.size(); i++){
                        System.out.println((i+1) +
                                ")\t" +
                                documentIndex.get(result.get(i).getValue0()).getDocNo() +
                                "\t"+result.get(i).getValue1());
                    }

                    System.out.println();

                    //Close the posting lists
                    for (PostingList postingList : postingLists) {
                        postingList.closeList();
                    }

                } else if(command == 1) { //Change settings command

                    //Request the new query processor settings then change it
                    changeSettings(settings);
                    System.out.println("Settings changed!");

                } else if (command == 2) { //Exit command

                    System.out.println("See you next query!");
                    return;
                }
                //DEFAULT BEHAVIOUR: start the loop again
            }

        }


        /**
         * Method used to take a query in input from the user.
         * @return The input query.
         */
        private static String getQuery(){

            //Scanner to read from the standard input stream
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter a query:");

            //Read the query, the -1 at the beginning is used to signal that the string is a query, used during the parsing
            return "-1\t" + scanner.nextLine();
        }


        /**
         * Method used to get the command from the user.
         * @return 0 if the command is to enter a new query, 1 to change the settings, exit to stop the program.
         */
        private static int getCommand(){
            do {

                //Scanner to read from the standard input stream
                Scanner scanner = new Scanner(System.in);

                System.out.println(
                        "0 -> Enter a query\n" +
                                "1 -> Change settings\n" +
                                "2 -> Exit");

                String result;

                if(scanner.hasNext()) {
                    result = scanner.nextLine();
                    switch (result) {
                        case "0":
                            return 0;
                        case "1":
                            return 1;
                        case "2":
                            return 2;
                    }
                }

                System.out.println("Input not valid, enter one of the following commands: ");
            } while (true);
        }

        /**
         * Method used to change the settings of the query processor, it can be used to change the scoring function.
         */
        private static void changeSettings(Settings settings){
            setQueryProcessorParameters(settings);
        }

        /**
         * Parses the query and returns the list of terms containing the query, the parsing process must be the same as the
         * one used during the indexing phase.
         * @param query the query string to parse
         * @param stopStemming if true remove the stopwords and applies the stemming procedure.
         * @return the array of terms after the parsing of the query
         */
        public static String[] parseQuery(String query, boolean stopStemming) {

            //Array of terms to build the result
            ArrayList<String> results = new ArrayList<>();

            //Parse the query using the same configuration of the indexer
            DocParsed docParsed = Parser.processDocument(query, stopStemming);

            //If no terms are returned by the parser then return null
            if(docParsed == null){
                return null;
            }

            //Remove the query terms that are not present in the lexicon
            for(String term : docParsed.getTerms()){
                if(lexicon.get(term) != null){
                    results.add(term);
                }
            }

            //Return an array of String containing the results of the parsing process
            return results.toArray(new String[0]);
        }

        /**
         * updates the query parameters for what regards the scoring metric (tfidf/bm25) and the type of query (conjunctive/disjunctive)
         */
        private static void setQueryProcessorParameters(Settings settings){
            //Scanner to read from the standard input stream
            Scanner scanner = new Scanner(System.in);
            boolean correctParameters = false;

            while (!correctParameters) {
                System.out.println("\nSet the query processor parameters:");
                System.out.println("Scoring function:\n0 -> TFIDF\n1 -> BM25");

                String result;

                if (scanner.hasNext()) {
                    result = scanner.nextLine();
                    //If 0 => bm25scoring is false, otherwise is true, so we'll use the bm25 scoring function
                    switch (result) {
                        case "0":
                            bm25scoring = false;
                            correctParameters = true;
                            break;
                        case "1":
                            bm25scoring = true;
                            correctParameters = true;
                            break;
                    }
                }

                if(!correctParameters)
                    System.out.println("Input not valid, enter one of the following commands: ");
            }

            correctParameters = false;
            while (!correctParameters) {
                System.out.println("Query type:\n0 -> Disjunctive\n1 -> Conjunctive");

                String result;

                if (scanner.hasNext()) {
                    result = scanner.nextLine();
                    //If 0 => disjunctive, 1 => conjunction, queryType is true with disjunctive and false with conjunctive queries
                    switch (result) {
                        case "0":
                            queryType = true;
                            correctParameters = true;
                            break;
                        case "1":
                            queryType = false;
                            correctParameters = true;
                            break;
                    }
                }

                if(!correctParameters)
                    System.out.println("Input not valid, enter one of the following commands: ");
            }

            correctParameters = false;
            while (!correctParameters) {
                System.out.println("Do you need debug mode?\n0 -> Yes\n1 -> No");

                String result;

                if (scanner.hasNext()) {
                    result = scanner.nextLine();
                    //If 0 => debug mode on, 1 => debug mode off
                    switch (result) {
                        case "0":
                            settings.setDebug(true);
                            correctParameters = true;
                            break;
                        case "1":
                            settings.setDebug(false);
                            correctParameters = true;
                            break;
                    }
                }

                if(!correctParameters)
                    System.out.println("Input not valid, enter one of the following commands: ");
            }
        }
}