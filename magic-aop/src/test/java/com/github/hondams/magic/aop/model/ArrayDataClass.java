package com.github.hondams.magic.aop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArrayDataClass {

    private boolean[] booleanArrayField;
    private byte[] byteArrayField;
    private char[] charArrayField;
    private short[] shortArrayField;
    private int[] intArrayField;
    private long[] longArrayField;
    private float[] floatArrayField;
    private double[] doubleArrayField;
    private String[] stringArrayField;
    private Object[] objectArrayField;

    private boolean[][] booleanArrayArrayField;
    private byte[][] byteArrayArrayField;
    private char[][] charArrayArrayField;
    private short[][] shortArrayArrayField;
    private int[][] intArrayArrayField;
    private long[][] longArrayArrayField;
    private float[][] floatArrayArrayField;
    private double[][] doubleArrayArrayField;
    private String[][] stringArrayArrayField;
    private Object[][] objectArrayArrayField;
}
