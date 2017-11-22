package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;
    
    // Feel free to add extra fields and helper methods.
    private IDictionary<URI, Double> normVector;
    
    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.
        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);
        this.normVector = this.computeDocumentNorm(webpages);
    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: Feel free to change or modify these methods if you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    /**
     * This method should return a dictionary mapping every single unique word found
     * in any documents to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        Integer totalIntPagesNum = pages.size();  
        Double totalPagesNum =totalIntPagesNum.doubleValue();  // number of pages
        // idf dictionary
        IDictionary<String, Double> idfDict = new ChainedHashDictionary<String, Double>();
        // number of docs containing the word
        IDictionary<String, Integer> wordContainedCount = new ChainedHashDictionary<String, Integer>(); 
        // iterate all pages
        for (Webpage page:pages) {
            ISet<String> inPage = new ChainedHashSet<String>();  //word set in this page
            for (String word:page.getWords()) {          
                inPage.add(word);  // add to set, if word is already in this set, do nothing
            }
            for (String word:inPage) {
                // for all words in page's word set
                if (wordContainedCount.containsKey(word)) {
                    // if this word already appears in other pages, count++
                    Integer newCount = wordContainedCount.get(word)+1;
                    wordContainedCount.put(word, newCount);
                }
                else {
                    // this word appears for the first time
                    wordContainedCount.put(word, 1);
                }
            }
        }
        for (KVPair<String, Integer> pair:wordContainedCount) {
            String key = pair.getKey();
            Double idf = Math.log(totalPagesNum / (pair.getValue().doubleValue()));
            idfDict.put(key, idf);
        }
        return idfDict;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * We are treating the list of words as if it were a document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        
        IDictionary<String, Double> wordCount = new ChainedHashDictionary<String, Double>();
        Integer pageIntSize = words.size();
        Double pageSize = pageIntSize.doubleValue();
        Double onePortion = 1.0/pageSize;
        for (String word:words) {
            if (wordCount.containsKey(word)) {
                Double newTf = wordCount.get(word) + onePortion;
                wordCount.put(word, newTf);
            }
            else {
                wordCount.put(word, onePortion);
            }
        }
        return wordCount;
    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        // Hint: this method should use the idfScores field and
        // call the computeTfScores(...) method.
        IDictionary<URI, IDictionary<String, Double>> tfidfScores = 
                new ChainedHashDictionary<URI, IDictionary<String, Double>>();
        // iterate all pages
        for (Webpage page:pages) {
            // for every page, create a tfidfVector which stores tfidf scores for all words
            IDictionary<String, Double> tfidfVector = new ChainedHashDictionary<String, Double>();
            IDictionary<String, Double> tfScores = computeTfScores(page.getWords());
            for (KVPair<String, Double> p:tfScores) {
                String word = p.getKey();
                Double tfidf = 0.0;
                if (this.idfScores.containsKey(word)) {
                    // if word doesn't appears in any pages, idf=0, tfidf=0
                    // if word appears, tfidf = tf*idf
                    tfidf = p.getValue() * this.idfScores.get(word);                   
                }
                tfidfVector.put(word, tfidf);
            }
            tfidfScores.put(page.getUri(), tfidfVector);
        }
        return tfidfScores;
    }

    private IDictionary<String, Double> computeQueryTfIdfScores(IList<String> query) {
        IDictionary<String, Double> tfQuery = computeTfScores(query);
        IDictionary<String, Double> tfidfQuery = new ChainedHashDictionary<String, Double>();
        for (KVPair<String, Double> pair:tfQuery) {
            String word = pair.getKey();
            Double tfidf = 0.0;
            if (idfScores.containsKey(word)) {
                tfidf = idfScores.get(word);
            }
            tfidfQuery.put(word, tfidf * pair.getValue());
        }
        return tfidfQuery;
    }

    private IDictionary<URI, Double> computeDocumentNorm(ISet<Webpage> pages) {
        // compute norms for every page, store them in dictionary: {page's uri:norm value}
        IDictionary<URI, Double> norm = new ChainedHashDictionary<URI, Double>();
        for (Webpage page:pages) {
            Double n = computeNorm(documentTfIdfVectors.get(page.getUri()));
            norm.put(page.getUri(), n);
        }
        return norm;
    }
    
    private Double computeNorm(IDictionary<String, Double> tfidfVector) {
        Double output = 0.0;
        for (KVPair<String, Double> pair:tfidfVector) {
            output += pair.getValue() * pair.getValue();
        }
        return Math.sqrt(output);
    }
    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        IDictionary<String, Double> tfidfQuery = computeQueryTfIdfScores(query);
        IDictionary<String, Double> tfidfPage = documentTfIdfVectors.get(pageUri);
        Double numerator = 0.0;
        for (String word:query) {
            Double pageScore = 0.0; 
            if (tfidfPage.containsKey(word)) {
                pageScore = tfidfPage.get(word);
            }
            Double queryScore = tfidfQuery.get(word);
            numerator += (pageScore * queryScore);
        }
        Double normQuery = computeNorm(tfidfQuery);
        Double normPage = normVector.get(pageUri);
        Double denominator = normPage * normQuery;
        if (denominator != 0) {
            return (numerator/denominator);
        }
        else {
            return 0.0;
        }
    }
}
