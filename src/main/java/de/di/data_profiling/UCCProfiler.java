package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.AttributeList;
import de.di.data_profiling.structures.PositionListIndex;
import de.di.data_profiling.structures.UCC;

import java.util.ArrayList;
import java.util.List;

public class UCCProfiler {

    /**
     * Discovers all minimal, non-trivial unique column combinations in the provided relation.
     * @param relation The relation that should be profiled for unique column combinations.
     * @return The list of all minimal, non-trivial unique column combinations in ths provided relation.
     */
    public List<UCC> profile(Relation relation) {
        int numAttributes = relation.getAttributes().length;
        List<UCC> uniques = new ArrayList<>();
        List<PositionListIndex> currentNonUniques = new ArrayList<>();

        // Calculate all unary UCCs and unary non-UCCs
        for (int attribute = 0; attribute < numAttributes; attribute++) {
            AttributeList attributes = new AttributeList(attribute);
            PositionListIndex pli = new PositionListIndex(attributes, relation.getColumns()[attribute]);
            if (pli.isUnique())
                uniques.add(new UCC(relation, attributes));
            else
                currentNonUniques.add(pli);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all unique column combinations of size n>1 by traversing the lattice level-wise. Make sure to     //
        // generate only minimal candidates while moving upwards and to prune non-minimal ones. Hint: The class       //
        // AttributeList offers some helpful functions to test for sub- and superset relationships. Use PLI           //
        // intersection to validate the candidates in every lattice level. Advances techniques, such as random walks, //
        // hybrid search strategies, or hitting set reasoning can be used, but are optional to pass the assignment.   //
        // Track the current level's attribute combinations as arrays and their corresponding PLIs
        // Level-wise lattice traversal for UCCs of size n > 1
        
        while (!currentNonUniques.isEmpty()) {
            List<PositionListIndex> nextNonUniques = new ArrayList<>();

            for (int i = 0; i < currentNonUniques.size(); i++) {
                for (int j = i + 1; j < currentNonUniques.size(); j++) {
                    PositionListIndex pliA = currentNonUniques.get(i);
                    PositionListIndex pliB = currentNonUniques.get(j);

                    AttributeList attrA = pliA.getAttributes();
                    AttributeList attrB = pliB.getAttributes();

                    if (!attrA.samePrefixAs(attrB))
                        continue;

                    AttributeList candidateAttrs = attrA.union(attrB);

                    boolean nonMinimal = false;
                    for (UCC ucc : uniques) {
                        if (candidateAttrs.getAttributeSet().containsAll(ucc.getAttributeList().getAttributeSet())) {
                            nonMinimal = true;
                            break;
                        }
                    }
                    if (nonMinimal)
                        continue;

                    PositionListIndex candidatePli = pliA.intersect(pliB);

                    if (candidatePli.isUnique())
                        uniques.add(new UCC(relation, candidateAttrs));
                    else
                        nextNonUniques.add(candidatePli);
                }
            }

            currentNonUniques = nextNonUniques;
        }
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return uniques;
    }
}
