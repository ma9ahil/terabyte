package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.IND;

import java.util.*;
import java.util.stream.Collectors;

public class INDProfiler {

    /**
     * Discovers all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     * @param relations The relations that should be profiled for inclusion dependencies.
     * @return The list of all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     */
    public List<IND> profile(List<Relation> relations, boolean discoverNary) {
        List<IND> inclusionDependencies = new ArrayList<>();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all inclusion dependencies and return them in inclusion dependencies list. The boolean flag       //
        // discoverNary indicates, whether only unary or both unary and n-ary INDs should be discovered. To solve     //
        // this assignment, only unary INDs need to be discovered. Discovering also n-ary INDs is optional.           //

        // Extract unique values for each column in each relation
        Map<Relation, List<Set<String>>> relationColumnValues = new HashMap<>();
        for (Relation relation : relations) {
            String[][] columns = relation.getColumns();
            List<Set<String>> columnValues = new ArrayList<>();
            for (String[] column : columns) {
                columnValues.add(new HashSet<>(Arrays.asList(column)));
            }
            relationColumnValues.put(relation, columnValues);
        }

        // Check all pairs of columns for inclusion dependencies
        for (int i = 0; i < relations.size(); i++) {
            Relation lhsRelation = relations.get(i);
            for (int lhsAttribute = 0; lhsAttribute < lhsRelation.getAttributes().length; lhsAttribute++) {
                for (int j = 0; j < relations.size(); j++) {
                    Relation rhsRelation = relations.get(j);
                    for (int rhsAttribute = 0; rhsAttribute < rhsRelation.getAttributes().length; rhsAttribute++) {
                        // Skip trivial INDs (same attribute in same relation)
                        if (i == j && lhsAttribute == rhsAttribute) {
                            continue;
                        }

                        // Check if lhs values are a subset of rhs values
                        Set<String> lhsValues = relationColumnValues.get(lhsRelation).get(lhsAttribute);
                        Set<String> rhsValues = relationColumnValues.get(rhsRelation).get(rhsAttribute);

                        if (rhsValues.containsAll(lhsValues)) {
                            IND ind = new IND(lhsRelation, lhsAttribute, rhsRelation, rhsAttribute);
                            inclusionDependencies.add(ind);
                        }
                    }
                }
            }
        }

        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (discoverNary)
            // Here, the lattice search would start if n-ary IND discovery would be supported.
            throw new RuntimeException("Sorry, n-ary IND discovery is not supported by this solution.");

        return inclusionDependencies;
    }

    private List<Set<String>> toColumnSets(String[][] columns) {
        return Arrays.stream(columns)
                .map(column -> new HashSet<>(new ArrayList<>(List.of(column))))
                .collect(Collectors.toList());
    }
}
