package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;


/**
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed.
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        // Step 1: Make a graph representing the 'internet'
        IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Step 2: Use this graph to compute the page rank for each webpage
        this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the
        // page ranks, we no longer need it!
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        IDictionary<URI, ISet<URI>> linkGraph = new ChainedHashDictionary<URI, ISet<URI>>();
        ISet<URI> allLinks = new ChainedHashSet<URI>();
        for (Webpage page:webpages) {
            // create a set of links first
            allLinks.add(page.getUri());
        }
        for (Webpage page:webpages) {
            ISet<URI> links = new ChainedHashSet<URI>();
            for (URI link:page.getLinks()) {  // process all this page's links
                if (allLinks.contains(link)) {  // external links are omitted
                    if (!page.getUri().equals(link)) {  // omit self-links
                        links.add(link);
                    }
                }
            }
            linkGraph.put(page.getUri(), links);
        }
        return linkGraph;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        // Step 1: The initialize step should go here
        IDictionary<URI, Double> pageRankScore = new ChainedHashDictionary<URI, Double>();
        Integer n = graph.size();
        for (KVPair<URI, ISet<URI>> pair:graph) {  // initialize pagerank score
            pageRankScore.put(pair.getKey(), 1.0/n);
        }
        Double complementFactor = (1 - decay)/n;
        for (int i = 0; i < limit; i++) {  
            // Step 2: The update step should go here
            IDictionary<URI, Double> newPagerank = new ChainedHashDictionary<URI, Double>();
            for (KVPair<URI, Double> pair:pageRankScore) {
                // initialize new pagerank dictionary with 0
                newPagerank.put(pair.getKey(), 0.0);
            }
            for (KVPair<URI, ISet<URI>> pair:graph) { 
                // iterate through the graph: update pagerank score for out-linked node
                Double newCurScore = newPagerank.get(pair.getKey()) + complementFactor;
                newPagerank.put(pair.getKey(), newCurScore);
                URI curURI = pair.getKey();
                ISet<URI> curOutLinks = pair.getValue();            
                if (curOutLinks.size() == 0) {
                    // no out-links, jump to a random page
                    Double contribution = (decay * pageRankScore.get(curURI))/n;
                    for (KVPair<URI, Double> uriScorePair:newPagerank) {
                        newPagerank.put(uriScorePair.getKey(), uriScorePair.getValue()+contribution);
                    }
                }
                else {
                    Double contribution = (decay*pageRankScore.get(curURI))/curOutLinks.size();                    
                    for (URI outLink:curOutLinks) {
                        // add current URI's contribution to all out-links
                        Double newScore = newPagerank.get(outLink)+contribution;                      
                        newPagerank.put(outLink, newScore);
                    }
                }   
            }          
            // Step 3: the convergence step should go here.
            boolean isConverge = true;
            for (KVPair<URI, Double> pair:pageRankScore) {
                if (Math.abs(pair.getValue() - newPagerank.get(pair.getKey())) > epsilon) {    
                    isConverge = false;
                    break;
                }
            }
            pageRankScore = newPagerank;
            // Return early if we've converged.
            if (isConverge) {           
                break;
            }           
        }
        return pageRankScore;
    }

    /**
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        // Implementation note: this method should be very simple: just one line!
        return pageRanks.get(pageUri);
    }
}
