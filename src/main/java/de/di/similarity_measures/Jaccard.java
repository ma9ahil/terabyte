package de.di.similarity_measures;

import de.di.similarity_measures.helper.Tokenizer;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class Jaccard implements SimilarityMeasure {

    // The tokenizer that is used to transform string inputs into token lists.
    private final Tokenizer tokenizer;

    // A flag indicating whether the Jaccard algorithm should use set or bag semantics for the similarity calculation.
    private final boolean bagSemantics;

    /**
     * Calculates the Jaccard similarity of the two input strings. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String string1, String string2) {
        string1 = (string1 == null) ? "" : string1;
        string2 = (string2 == null) ? "" : string2;

        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    /**
     * Calculates the Jaccard similarity of the two string lists. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String[] strings1, String[] strings2) {
        double jaccardSimilarity = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Calculate the Jaccard similarity of the two String arrays. Note that the Jaccard similarity needs to be    //
        // calculated differently depending on the token semantics: set semantics remove duplicates while bag         //
        // semantics consider them during the calculation. The solution should be able to calculate the Jaccard       //
        // similarity either of the two semantics by respecting the inner bagSemantics flag.                          //



        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (!this.bagSemantics) {
            // SET SEMANTICS
            Set<String> set1 = new HashSet<>(Arrays.asList(strings1));
            Set<String> set2 = new HashSet<>(Arrays.asList(strings2));

            if (set1.isEmpty() && set2.isEmpty()) {
                return 1.0;
            }

            // Intersection: unique elements in both
            Set<String> intersection = new HashSet<>(set1);
            intersection.retainAll(set2);

            // Union: total unique elements
            Set<String> union = new HashSet<>(set1);
            union.addAll(set2);

            jaccardSimilarity = (double) intersection.size() / union.size();

        } else {
            // BAG SEMANTICS
            if (strings1.length == 0 && strings2.length == 0) {
                return 0.5; // Max similarity for bags as per docstring
            }

            // Count frequencies for bag 1
            Map<String, Integer> counts1 = new HashMap<>();
            for (String s : strings1) {
                counts1.put(s, counts1.getOrDefault(s, 0) + 1);
            }

            // Count frequencies for bag 2
            Map<String, Integer> counts2 = new HashMap<>();
            for (String s : strings2) {
                counts2.put(s, counts2.getOrDefault(s, 0) + 1);
            }

            // Calculate intersection size: sum of minimum occurrences
            int intersectionSize = 0;
            for (String token : counts1.keySet()) {
                if (counts2.containsKey(token)) {
                    intersectionSize += Math.min(counts1.get(token), counts2.get(token));
                }
            }

            /*
             * Per the docstring: "The maximum Jaccard similarity with multiset semantics is 1/2".
             * This implies the denominator is the sum of the sizes of both bags (|A| + |B|).
             */
            int totalTokens = strings1.length + strings2.length;
            jaccardSimilarity = (double) intersectionSize / totalTokens;
        }
        return jaccardSimilarity;
    }
}
