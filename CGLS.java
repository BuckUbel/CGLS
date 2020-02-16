import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CGLS {

    Output o;
    View view;

    double[][] matrixA;
    double[][] vectorB;

    String inputMatrix;
    String inputVector;
    String outputPath;
    double tolerance;
    int length;
    int height;

    boolean isInitialised = false;
    boolean inverted = false;
    String file_Extension = "png";

    CGLS(String inputMatrix, String inputVector, String outputPath, double tolerance, int length, int height) {
        this.inputMatrix = inputMatrix;
        this.inputVector = inputVector;
        this.outputPath = outputPath;
        this.tolerance = tolerance;
        this.length = length;
        this.height = height;
    }

    // Funktionen zum Setzen von Eigenschaften

    public void setOutput(Output o) {
        this.o = o;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    // Funtions to read data from files
    public void inputData() throws Exception {
        this.matrixA = CGLS.fileToMatrix(this.inputMatrix, this.length, this.height);
        this.vectorB = CGLS.fileToMatrix(this.inputVector, this.length, 1);
        this.o.print("Data import is successful");

        this.isInitialised = true;
    }

    public static double[][] fileToMatrix(String path, int length, int height) throws Exception {
        File f = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(f));

        double[][] matrix = new double[length][height];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = Double.parseDouble(br.readLine());
            }
        }
        return matrix;
    }

    // der Algorithmnus selber
    public void cgls() {

        // Test, ob alle Daten eingelesen wurden
        if (isInitialised) {

            // die Ergebnismatrix, welche aus Vektor U generiert wird
            double[][] resultMatrix;

            double[][] vectorU = new double[this.height][1];

            //  Vector U standardgemäß am Anfang mit 0 füllen
            for(int i=0; i<vectorU.length; i++){
                vectorU[i][0] = 0;
            }

            // Residuenvektor
            double[][] vectorR = substractionMatrix(this.vectorB, multiplicationMatrix(this.matrixA, vectorU));

            // Transponierte Matrix A * Residuenvektor
            double[][] vectorG = multiplicationMatrix(transposeMatrix(this.matrixA), vectorR);
            double[][] vectorP = vectorG;

            // Quadrat der Laenge von vectorG
            double delta = multiplicationMatrix(transposeMatrix(vectorG), vectorG)[0][0];
            int k = 0;

            double delta_;
            double deltaNew;
            double alpha;
            double my;

            this.o.print("Calculate to tolerance: " + this.tolerance);

            do {
                // Quadrat der Laenge von (A*p)
                delta_ = multiplicationMatrix(transposeMatrix(multiplicationMatrix(this.matrixA, vectorP)), multiplicationMatrix(this.matrixA, vectorP))[0][0];

                // Quotient von den Quadraten der Laengen von vectorG und A*p
                alpha = delta / delta_;


                vectorU = additionMatrix(vectorU, numberMultiMatrix(alpha, vectorP));

                // Neuberechnung Residuenvektor
                vectorR = substractionMatrix(vectorR, numberMultiMatrix(alpha, multiplicationMatrix(this.matrixA, vectorP)));

                // Transponierte Matrix A * vectorR
                vectorG = multiplicationMatrix(transposeMatrix(this.matrixA), vectorR);

                deltaNew = multiplicationMatrix(transposeMatrix(vectorG), vectorG)[0][0];
                my = deltaNew / delta;
                vectorP = additionMatrix(vectorG, numberMultiMatrix(my, vectorP));
                delta = deltaNew;
                k++;

                //generieren der Ergebnismatrix
                resultMatrix = genMatrix(vectorU);
                matrixToImage(resultMatrix, this.outputPath + k);
                o.print("Durchlauf: " + k + " Residuenvektoren-Betrag: " + Double.toString(normMatrix(vectorR)));
            }
            while (normMatrix(vectorR) >= this.tolerance);

            this.o.print("Finish");
        } else {
            this.o.print("No data.");
        }

    }

    // Umgekehrte Indikator Funktion
    // Nutze die Funktion für Vektor U, zum Generieren der Matrix für das Bild
    public static double[][] genMatrix(double[][] matrix) {

        int n = new Double(Math.sqrt(matrix.length)).intValue();
        double[][] returnMatrix = new double[n][n];

        int i;
        for (int k = 1; k <= n; k++) {
            for (int l = 0; l < n; l++) {
                i = (k - 1) * n + l;
                returnMatrix[k - 1][l] = matrix[i][0];
            }
        }

        return returnMatrix;
    }

    // Bildfunktion
    // verschiebe die Pixelwerte in den RGB-Raum
    public void matrixToImage(double[][] matrix, String outputPath) {

        double max = 0;
        double min = 0;
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {

                if (matrix[x][y] < min) {
                    min = matrix[x][y];
                }
                if (matrix[x][y] > max) {
                    max = matrix[x][y];
                }

            }
        }
        double offset = max - min;
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {


                if (inverted) {
//                meine Lösung invertiert
                    matrix[x][y] = ((((matrix[x][y] - min) / offset) * 255) - 255) * (-1);
                } else {
//                meine Lösung
                    matrix[x][y] = ((matrix[x][y] - min) / offset) * 255;
                }
                // lösung skript
                // matrix[x][y] = 256 - 100 * matrix[x][y];

            }
        }
        writeSWImage(outputPath, matrix);
    }

    // Funktion zur Generierung des Bildes
    public void writeSWImage(String path, double[][] light) {

        //image dimension
        int width = light.length;
        int height = light[0].length;

        String fileExtension = "png";

        //create buffered image object img
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //file object
        File file = null;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                int a = 255; //alpha
//                int r = (int)(Math.random()*256); //red
//                int g = (int)(Math.random()*256); //green
//                int b = (int)(Math.random()*256); //blue
                int l = new Double(light[x][y]).intValue(); //red
                int p = (a << 24) | (l << 16) | (l << 8) | l;
//                int p = (a<<24) | (r<<16) | (g<<8) | b;
                img.setRGB(x, y, p);
            }
        }
        //write image
        try {
            file = new File(path + "." + fileExtension);
            ImageIO.write(img, fileExtension, file);
            this.view.displayImage(path + "." + fileExtension);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }


    // Mathematische Funktionen zum Rechnen mit Matrizen

    // Euklidische Norm
    public static double normMatrix(double[][] matrix) {
        double returnValue = 0.0;

        for (int i = 0; i < matrix.length; i++) {
            returnValue += Math.pow(matrix[i][0], 2);

        }
//        returnValue = returnValue.sqrt();
        returnValue = Math.sqrt(returnValue);
        /**
         *
         * überarbeiten !
         */
        return returnValue;
    }

    public static double[][] transposeMatrix(double[][] matrix) {

        double[][] returnMatrix = new double[matrix[0].length][matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {

                returnMatrix[j][i] = matrix[i][j];
            }
        }

        return returnMatrix;
    }

    public static double[][] additionMatrix(double[][] matrix1, double[][] matrix2) {

        if (matrix1.length == matrix2.length || matrix1[0].length == matrix2.length) {
            double[][] returnMatrix = new double[matrix1.length][matrix1[0].length];
            for (int i = 0; i < returnMatrix.length; i++) {
                for (int j = 0; j < returnMatrix[i].length; j++) {
                    returnMatrix[i][j] = matrix1[i][j] + matrix2[i][j];
                }
            }
            return returnMatrix;
        } else {
            return null;
        }
    }

    public static double[][] substractionMatrix(double[][] matrix1, double[][] matrix2) {

        if (matrix1.length == matrix2.length || matrix1[0].length == matrix2.length) {
            double[][] returnMatrix = new double[matrix1.length][matrix1[0].length];
            for (int i = 0; i < returnMatrix.length; i++) {
                for (int j = 0; j < returnMatrix[i].length; j++) {
                    returnMatrix[i][j] = matrix1[i][j] - matrix2[i][j];
                }
            }
            return returnMatrix;
        } else {
            return null;
        }
    }

    // Multiplikation eines Skalars mit einer Matrix
    public static double[][] numberMultiMatrix(double m, double[][] matrix) {

        double[][] returnMatrix = new double[matrix.length][matrix[0].length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {

                returnMatrix[i][j] = matrix[i][j] * m;
            }
        }

        return returnMatrix;
    }

    public static double[][] multiplicationMatrix(double[][] matrix1, double[][] matrix2) {

        double[][] returnMatrix = new double[matrix1.length][matrix2[0].length];

        double tmpValue = 0.0;
        if (matrix1[0].length == matrix2.length) {
            for (int i = 0; i < returnMatrix.length; i++) {
                for (int j = 0; j < returnMatrix[i].length; j++) {
                    for (int k = 0; k < matrix2.length; k++) {
                        tmpValue += matrix1[i][k] * (matrix2[k][j]);
                    }
                    returnMatrix[i][j] = tmpValue;
                    tmpValue = 0.0;
                }
            }
        } else {
            System.err.println("Die Anzahl der Spalten der ersten Matrix muss der Anzahl der Zeilen der zweiten Matrix gleich sein!");
        }
        return returnMatrix;

    }


    // Testfunktion zum Anzeigen der Matrix
    public static void printMatrix(double[][] matrix) {

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
