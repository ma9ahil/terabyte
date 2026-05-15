package de.di.similarity_measures;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class Levenshtein implements SimilarityMeasure {

    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    // The choice of whether Levenshtein or DamerauLevenshtein should be calculated.
    private final boolean withDamerau;

    /**
     * Calculates the Levenshtein similarity of the two input strings.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The (Damerau) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String string1, final String string2) {
        double levenshteinSimilarity = 0;

        int[] upperupperLine = new int[string1.length() + 1];   // line for Demarau lookups
        int[] upperLine = new int[string1.length() + 1];        // line for regular Levenshtein lookups
        int[] lowerLine = new int[string1.length() + 1];        // line to be filled next by the algorithm

        // Fill the first line with the initial positions (= edits to generate string1 from nothing)
        for (int i = 0; i <= string1.length(); i++)
            upperLine[i] = i;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Use the three provided lines to successively calculate the Levenshtein matrix with the dynamic programming //
        // algorithm. Depending on whether the inner flag withDamerau is set, the Damerau extension rule should be    //
        // used during calculation or not. Hint: Implement the Levenshtein algorithm here first, then copy the code   //
        // to the String tuple function and adjust it a bit to work on the arrays - the algorithm is the same.        //



        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int n = string1.length();
        int m = string2.length();

        // Edge case: Both empty strings are identical
        if (n == 0 && m == 0) return 1.0;
        // Edge case: One string is empty, distance is the length of the other string
        if (n == 0 || m == 0) return 0.0;

        for (int i = 1; i <= m; i++) {
            lowerLine[0] = i;
            for (int j = 1; j <= n; j++) {
                // Cost of substitution: 0 if characters match, 1 otherwise
                int cost = (string1.charAt(j - 1) == string2.charAt(i - 1)) ? 0 : 1;

                // Standard Levenshtein: Min of Deletion, Insertion, Substitution
                lowerLine[j] = min(
                        upperLine[j] + 1,        // Deletion
                        lowerLine[j - 1] + 1,    // Insertion
                        upperLine[j - 1] + cost  // Substitution
                );

                // Damerau extension: Check for transpositions
                if (withDamerau && i > 1 && j > 1 &&
                        string1.charAt(j - 1) == string2.charAt(i - 2) &&
                        string1.charAt(j - 2) == string2.charAt(i - 1)) {

                    // The cost of a transposition is 1 + distance before the two swapped characters
                    lowerLine[j] = Math.min(lowerLine[j], upperupperLine[j - 2] + 1);
                }
            }

            // Rotate the lines for the next iteration
            // upperupperLine becomes the row from 2 steps ago (i-1 before the shift)
            System.arraycopy(upperLine, 0, upperupperLine, 0, n + 1);
            // upperLine becomes the current row (i) to be used as i-1 in the next step
            System.arraycopy(lowerLine, 0, upperLine, 0, n + 1);
        }

        // Similarity = 1 - (Normalized Distance)
        int finalDistance = upperLine[n];
        levenshteinSimilarity = 1.0 - ((double) finalDistance / Math.max(n, m));
        return levenshteinSimilarity;
    }

    /**
     * Calculates the Levenshtein similarity of the two input string lists.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * For string lists, we consider each list as an ordered list of tokens and calculate the distance as the number of
     * token insertions, deletions, replacements (and swaps) that transform one list into the other.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The (multiset) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        double levenshteinSimilarity = 0;

        int[] upperupperLine = new int[strings1.length + 1];   // line for Damerau lookups
        int[] upperLine = new int[strings1.length + 1];        // line for regular Levenshtein lookups
        int[] lowerLine = new int[strings1.length + 1];        // line to be filled next by the algorithm

        // Fill the first line with the initial positions (= edits to generate string1 from nothing)
        for (int i = 0; i <= strings1.length; i++)
            upperLine[i] = i;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Use the three provided lines to successively calculate the Levenshtein matrix with the dynamic programming //
        // algorithm. Depending on whether the inner flag withDamerau is set, the Damerau extension rule should be    //
        // used during calculation or not. Hint: Implement the Levenshtein algorithm above first, then copy the code  //
        // to this function and adjust it a bit to work on the arrays - the algorithm is the same.                    //



        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int n = strings1.length;
        int m = strings2.length;

        if (n == 0 && m == 0) return 1.0;
        if (n == 0 || m == 0) return 0.0;

        for (int i = 1; i <= m; i++) {
            lowerLine[0] = i;
            for (int j = 1; j <= n; j++) {
                // Use .equals() for String content comparison in arrays
                int cost = (strings1[j - 1].equals(strings2[i - 1])) ? 0 : 1;

                lowerLine[j] = min(
                        upperLine[j] + 1,
                        lowerLine[j - 1] + 1,
                        upperLine[j - 1] + cost
                );

                if (withDamerau && i > 1 && j > 1 &&
                        strings1[j - 1].equals(strings2[i - 2]) &&
                        strings1[j - 2].equals(strings2[i - 1])) {

                    lowerLine[j] = Math.min(lowerLine[j], upperupperLine[j - 2] + 1);
                }
            }

            System.arraycopy(upperLine, 0, upperupperLine, 0, n + 1);
            System.arraycopy(lowerLine, 0, upperLine, 0, n + 1);
        }

        int finalDistance = upperLine[n];
        levenshteinSimilarity = 1.0 - ((double) finalDistance / Math.max(n, m));
        return levenshteinSimilarity;
    }
}
