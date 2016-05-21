package com.cody.ID3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ID3Learner {
    String FileNameToRead;
    int PercentageOfDataToLEarnFrom;

    // Paramaterized constructor
    public ID3Learner(String FileName, int percs) {
        FileNameToRead = FileName;
        PercentageOfDataToLEarnFrom = percs;
    }

    /*
     * Function :: startLearning, This function, reads the file values into a
     * MxN Matrix datatype, The data from Matrix is further split across a set
     * of Training Vectors, and a FinalClass Vector. As our ID3 algortihm takes
     * set of Training vectors & final class vector as inputs. This
     * function,internally calls the learnTree Function, which is an
     * implementation of ID3 algorithm
     */
    public TreeNode startLearning() {

        if (FileNameToRead == null) {
            System.out.println("---- Error ------");
            System.out.println("---- Please Specify test data set ------");
        }

        if (PercentageOfDataToLEarnFrom < 0) {
            System.out.println("---- Error ------");
            System.out.println("---- Please Specify %correctly ------");
        }

        MatrixData matrix = new MatrixData();// Prepares a new matrix datatype
        matrix.prepareMatrix(FileNameToRead, PercentageOfDataToLEarnFrom);// reads
        // values
        // from
        // file
        // into
        // the
        // matrix
        // data
        // type
        // If you want to see the matrix learned, remove this comment
        // matrix.printMatrix();

        // This is a Hashmap, which contains Training Vectors
        HashMap<String, int[]> setTrainingVector = new HashMap<String, int[]>();
        // Now i need a set of R training vectors
        for (int i = 0; i < matrix.coloumns - 1; i++) {// Training Vectors being
            // filled into the map
            // fromthe matrix
            int[] trainingVector = new int[matrix.Numrows];
            matrix.fillArray(trainingVector, i);
            setTrainingVector.put(matrix.Headers.get(i), trainingVector);
        }

        // i need final class vector
        int[] FinalClass = new int[matrix.Numrows];
        matrix.fillArray(FinalClass, matrix.coloumns - 1);// final class vector
        // being filled from
        // the matrix

        // generated a Tree node,
        TreeNode rootNode = new TreeNode();
        rootNode.setAtrvalue(-1);// since its a root node

        // Calling the ID3 implementation algorithm
        learnTree(setTrainingVector, FinalClass, rootNode, matrix);
        return rootNode;
    }

    /*
     * Function :: startLearning, Recursive Function. AN exact copy of ID3
     * algorithm(http://en.wikipedia.org/wiki/ID3_algorithm) This function
     * generates a decision tree recursively. Parameters: 1.A Hashmap containing
     * Training Vectors :: HashMap<String,int[]> setTrainingVector 2.A VEctor of
     * Final class :: int[] FinalClass 3.THe decision tree NOde::TreeNode node
     * 4.The MAtrix datatype, which is used in constructing vectors of train
     * data::MatrixData matrix
     */
    public void learnTree(HashMap<String, int[]> setTrainingVector,
                          int[] FinalClass, TreeNode node, MatrixData matrix) {

        // if all values the same in FinalClass
        // return a leaf node
        for (int i = 0; i < 10; i++) {
            if (checkFinalClass(FinalClass, i)) {
                node.fClass = i;
                return;
            }
        }

        // If there is only one attribute in a training vector, then we select
        // the FinalClass as max occurance
        if (setTrainingVector.entrySet().size() == 1) {
            int[] count = getCount(FinalClass);
            int max = count[0];
            int maxCount = 0;
            for (int i = 1; i < count.length; i++) {
                if (count[i] > max) {
                    max = count[i];
                    maxCount = i;
                }
            }
            node.fClass = maxCount;
            return;
        } else {

            HashMap<String, Double> attributesGains = new HashMap<String, Double>(); // The

            HashMap<String, ArrayList<Integer>> mapAttributesValuesInListUnique = new HashMap<String, ArrayList<Integer>>();// The


            double entropyS = getEntropy(FinalClass);// initial entropy
            // System.out.println("Entropy Default \t"+entropyS);

            for (Map.Entry entry : setTrainingVector.entrySet()) {
                // System.out.println();
                // System.out.println("Finding Gain for :: "+entry.getKey());
                HashMap<Integer, Integer> atrPositive = new HashMap<Integer, Integer>();
                HashMap<Integer, Integer> atrNegative = new HashMap<Integer, Integer>();
                ArrayList<Integer> atrUnique = new ArrayList<Integer>();

                int[] trainingClass = (int[]) entry.getValue();
                for (int i = 0; i < trainingClass.length; i++) {// NOw finding
                    // individual
                    // entropies
                    addOnlyUnique(atrUnique, trainingClass[i]);
                    if (FinalClass[i] == 0)// its a positive
                    {
                        if (atrPositive.containsKey(trainingClass[i])) {
                            atrPositive.put(trainingClass[i],
                                    atrPositive.get(trainingClass[i]) + 1);
                        } else {
                            atrPositive.put(trainingClass[i], 1);
                        }
                    } else {// FinalClass is negative
                        if (atrNegative.containsKey(trainingClass[i])) {
                            atrNegative.put(trainingClass[i],
                                    atrNegative.get(trainingClass[i]) + 1);
                        } else {
                            atrNegative.put(trainingClass[i], 1);
                        }

                    }
                }

                mapAttributesValuesInListUnique.put((String) entry.getKey(),
                        atrUnique);
                // now calculate gain
                {
                    double gain = entropyS;
                    for (int tempAttr : atrUnique) {
                        double entropyTemp = 0.0;
                        int positives = 0;
                        int negatives = 0;
                        if (atrPositive.get(tempAttr) != null)
                            positives = atrPositive.get(tempAttr);
                        if (atrNegative.get(tempAttr) != null)
                            negatives = atrNegative.get(tempAttr);

                        // System.out.print("\tFor attribute :"+tempAttr);
                        // System.out.print("Positives :"+positives);
                        // System.out.print(",Negatives :"+negatives);

                        double val1 = (double) (positives)
                                / (positives + negatives);
                        double val2 = (double) (negatives)
                                / (positives + negatives);
                        entropyTemp = -(val1 * log2(val1))
                                - (val2 * log2(val2));
                        // System.out.print(",entropy temp :"+entropyTemp+"\n");

                        gain = gain
                                - ((((double) positives + negatives) / trainingClass.length) * entropyTemp);
                    }
                    // System.out.println("Gain came out::"+gain);
                    attributesGains.put((String) entry.getKey(), gain);
                }

            }// loop ends

            // now select the maximum gain
            String attributeWithMAxGain = "";
            double maxGainValue = 0.0;
            int indexToChoose = 0;
            for (Map.Entry entry : setTrainingVector.entrySet()) {
                double tempGain = attributesGains.get((String) entry.getKey());
                if (indexToChoose == 0) {
                    maxGainValue = tempGain;
                    attributeWithMAxGain = (String) entry.getKey();
                    indexToChoose++;
                }

                if (tempGain > maxGainValue) {
                    maxGainValue = tempGain;
                    attributeWithMAxGain = (String) entry.getKey();
                }
            }

            node.setAttributeName(attributeWithMAxGain);
            node.setfClass(-1);
            node.setGain(maxGainValue);

            // Now we will call this algorithm recursively for how mwany
            // attributes values of max attribute.
            ArrayList<Integer> atrUniqueValuesForAttrMaxGain = mapAttributesValuesInListUnique
                    .get(attributeWithMAxGain);

            for (int tempAtrUniqueValue : atrUniqueValuesForAttrMaxGain) {

                TreeNode NodeChild = new TreeNode();
                NodeChild.setAtrvalue(tempAtrUniqueValue);// since its a child
                // node
                node.getBranches().add(NodeChild);
                MatrixData matrixChild = matrix.splitMatrix(
                        attributeWithMAxGain, tempAtrUniqueValue);
                // matrixChild.printMatrix();
                // calling the algorithm
                HashMap<String, int[]> setTrainingVectorChild = new HashMap<String, int[]>();
                // Now i need a set of R training vectors
                for (int i = 0; i < matrixChild.coloumns - 1; i++) {
                    int[] trainingVectorChild = new int[matrixChild.Numrows];
                    matrixChild.fillArray(trainingVectorChild, i);
                    setTrainingVectorChild.put(matrixChild.Headers.get(i),
                            trainingVectorChild);
                }

                // i need final class vector
                int[] FinalClassChild = new int[matrixChild.Numrows];
                matrixChild
                        .fillArray(FinalClassChild, matrixChild.coloumns - 1);

                learnTree(setTrainingVectorChild, FinalClassChild, NodeChild,
                        matrixChild);

            }

            return;

        }

    }

    public boolean checkFinalClass(int[] FinalClass, int valueToChecked) {
        for (int i = 0; i < FinalClass.length; i++) {
            if (FinalClass[i] != valueToChecked)
                return false;
        }
        return true;
    }


    public int[] getCount(int[] FinalClass) {
        int[] result = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < FinalClass.length; i++) {
            int j = FinalClass[i];
            result[j] = result[j] + 1;
        }
        return result;
    }


    // Function:getEntropy
    // Returns entropy calculated for a given set of vector
    public double getEntropy(int[] vector) {
        double entropy = 0.0;
        int positives = 0;
        int negatives = 0;
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] == 0)// its a positive
            {
                positives++;
            } else {// FinalClass is negative
                negatives++;
            }
        }
        double val1 = (double) (positives) / (positives + negatives);
        double val2 = (double) (negatives) / (positives + negatives);
        entropy = -(val1 * log2(val1)) - (val2 * log2(val2));
        return entropy;
    }

    public static double log2(double num) {
        if (num <= 0)
            return 0.0;
        return (Math.log(num) / Math.log(2));
    }

    public void addOnlyUnique(ArrayList<Integer> data, int val) {
        if (!data.contains(val))
            data.add(val);
    }

}
