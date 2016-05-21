package com.cody.ID3;

public class MainProg {

    public static void main(String[] args) {

        String fileNameLearning = "poker.dat";// reading from param
        String fileNameTesting = "poker.dat";
        int percentLearn = 100;// this is the default percent , which shows how
        // many line to be read from input

        // ID3Learner
        // parameter 1: Learning File Name
        // parameter 2: % of Learn file use to learn the file
        ID3Learner learner = new ID3Learner(fileNameLearning, percentLearn);

        TreeNode rootNode = learner.startLearning();// This function starts
        // learning and creates the
        // tree, which is returned
        if (rootNode != null) {
            rootNode.printTree(rootNode, 0);// print to stdout the tree

            {// Calculating the accuracy on Training Set
                MatrixData matrixTests = new MatrixData();
                matrixTests.prepareMatrix(fileNameLearning, 100);
                // matrixTests.printMatrix();
                matrixTests.getTestAccuracy(rootNode, "Training Set");


            }
            {// Calculating the accuracy on Test Set
                MatrixData matrixTests2 = new MatrixData();
                matrixTests2.prepareMatrix(fileNameTesting, 100);
                // matrixTests.printMatrix();
                matrixTests2.getTestAccuracy(rootNode, "Test Set");
            }

        } else {
            System.out.println("Something Went wrong");

        }
    }
}
