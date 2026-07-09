package de.di.schema_matching;

import de.di.schema_matching.structures.CorrespondenceMatrix;
import de.di.schema_matching.structures.SimilarityMatrix;

import java.util.Arrays;

public class SecondLineSchemaMatcher {

    /**
     * Translates the provided similarity matrix into a binary correspondence matrix by selecting possibly optimal
     * attribute correspondences from the similarities.
     * @param similarityMatrix A matrix of pair-wise attribute similarities.
     * @return A CorrespondenceMatrix of pair-wise attribute correspondences.
     */
    public CorrespondenceMatrix match(SimilarityMatrix similarityMatrix) {
        double[][] simMatrix = similarityMatrix.getMatrix();

        int[][] corrMatrix = null;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Translate the similarity matrix into a binary correlation matrix by implementing either the StableMarriage //
        // algorithm or the Hungarian method.                                                                         //
        int numSources = simMatrix.length;
        int numTargets = simMatrix[0].length;

        int[] sourceAssignments = new int[numSources];
        Arrays.fill(sourceAssignments, -1);

        int[] targetAssignments = new int[numTargets];
        Arrays.fill(targetAssignments, -1);

        int[][] preferences = new int[numSources][numTargets];

        for (int s = 0; s < numSources; s++) {

            final int sourceIndex = s;

            Integer[] targets = new Integer[numTargets];

            for (int t = 0; t < numTargets; t++) {
                targets[t] = t;
            }

            Arrays.sort(
                    targets,
                    (a, b) -> Double.compare(
                            simMatrix[sourceIndex][b],
                            simMatrix[sourceIndex][a]
                    )
            );

            for (int t = 0; t < numTargets; t++) {
                preferences[s][t] = targets[t];
            }
        }

        int[] nextProposal = new int[numSources];

        boolean progress = true;

        while (progress) {

            progress = false;

            for (int source = 0; source < numSources; source++) {

                if (sourceAssignments[source] != -1)
                    continue;

                if (nextProposal[source] >= numTargets)
                    continue;

                progress = true;

                int target = preferences[source][nextProposal[source]];
                nextProposal[source]++;

                if (targetAssignments[target] == -1) {

                    sourceAssignments[source] = target;
                    targetAssignments[target] = source;

                } else {

                    int currentSource = targetAssignments[target];

                    if (simMatrix[source][target]
                            > simMatrix[currentSource][target]) {

                        sourceAssignments[currentSource] = -1;

                        sourceAssignments[source] = target;
                        targetAssignments[target] = source;
                    }
                }
            }
        }

        corrMatrix = assignmentArray2correlationMatrix(
                sourceAssignments,
                simMatrix
        );


        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return new CorrespondenceMatrix(corrMatrix, similarityMatrix.getSourceRelation(), similarityMatrix.getTargetRelation());
    }

    /**
     * Translate an array of source assignments into a correlation matrix. For example, [0,3,2] maps 0->1, 1->3, 2->2
     * and, therefore, translates into [[1,0,0,0][0,0,0,1][0,0,1,0]].
     * @param sourceAssignments The list of source assignments.
     * @param simMatrix The original similarity matrix; just used to determine the number of source and target attributes.
     * @return The correlation matrix extracted form the source assignments.
     */
    private int[][] assignmentArray2correlationMatrix(int[] sourceAssignments, double[][] simMatrix) {
        int[][] corrMatrix = new int[simMatrix.length][];
        for (int i = 0; i < simMatrix.length; i++) {
            corrMatrix[i] = new int[simMatrix[i].length];
            for (int j = 0; j < simMatrix[i].length; j++)
                corrMatrix[i][j] = 0;
        }
        for (int i = 0; i < sourceAssignments.length; i++)
            if (sourceAssignments[i] >= 0)
                corrMatrix[i][sourceAssignments[i]] = 1;
        return corrMatrix;
    }
}