import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    List<Integer> activeMinesX;
    List<Integer> activeMinesY;
    List<Integer> activeMinesZ;
    List<Integer> inactiveMinesX;
    List<Integer> inactiveMinesY;
    List<Integer> inactiveMinesZ;
    int shipX, shipY, shipZ, stepNum;
    enum COMMAND { ALPHA, BETA, GAMMA, DELTA,
        NORTH, SOUTH, EAST, WEST, WS;

        String[] toString = new String[] { "alpha", "beta", "gamma", "delta", "north", "south", "east", "west", ""};

        @Override
        public String toString() {
            return this.toString[this.ordinal()];
        }
    }

    int shotsFired, moveCMDs, numMines;

    List<COMMAND>[] steps;

    public Main() {
        shipX = 0;
        shipY = 0;
        shipZ = 0;
        shotsFired = 0;
        moveCMDs = 0;
        stepNum = 1;
        activeMinesX = new ArrayList<Integer>();
        activeMinesY = new ArrayList<Integer>();
        activeMinesZ = new ArrayList<Integer>();
        inactiveMinesX = new ArrayList<Integer>();
        inactiveMinesY = new ArrayList<Integer>();
        inactiveMinesZ = new ArrayList<Integer>();
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java Main FieldFile ScriptFile");
            System.exit(1);
        }
        try {
            File fieldFile = new File(args[0]);
            BufferedReader fieldReader = new BufferedReader(new InputStreamReader(new FileInputStream(fieldFile),
                    "UTF-8"));
            File scriptFile = new File(args[1]);
            BufferedReader scriptReader = new BufferedReader(new InputStreamReader(new FileInputStream(scriptFile),
                    "UTF-8"));
            List<String> fieldLines = new ArrayList<String>();
            List<String> scriptLines = new ArrayList<String>();
            String line;
            while ((line = fieldReader.readLine()) != null) {
                fieldLines.add(line);
            }

            while ((line = scriptReader.readLine()) != null) {
                scriptLines.add(line);
            }
            Main m = new Main();
            m.parseField(fieldLines.toArray(new String[1]));
            m.parseScript(scriptLines.toArray(new String[1]));
            m.run();

        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Found encoding other than UTF-8");
        } catch (IOException e) {
            System.err.println("IO Exception");
        } catch (ParseException e) {
            System.err.println(e.toString());
        }
    }

    private void run() {
        // while we have yet to perform all commands and there are still active mines and no mines have been missed
        do {
            System.out.println("Step " + stepNum);
            System.out.println("");
            printMineField();
            System.out.println("");
            printCommand();
            executeCommand();
            System.out.println("");
            printMineField();
            System.out.println("");
            stepNum++;
        } while (stepNum <= steps.length && activeMinesX.size() > 0 && !hasPassedMine());
        printScore();
    }

    private void printCommand() {
        StringBuilder s = new StringBuilder();
        for (COMMAND c : steps[stepNum - 1]) {
            s.append(c.toString() + " ");
        }
        System.out.println(s.toString());
    }

    private void printScore() {
        if (hasPassedMine()) {
            System.out.println("fail (0)");
        } else if (activeMinesX.size() > 0 && stepNum == steps.length + 1) {
            System.out.println("fail (0)");
        } else if (activeMinesX.size() == 0 && stepNum == steps.length) {
            System.out.println("pass (1)");
        } else {
            int startingScore = numMines*10;
            startingScore = startingScore - Math.min(5 * numMines, 5 * shotsFired);
            startingScore = startingScore - Math.min(2 * moveCMDs, 3 * numMines);
            System.out.println("pass (" + startingScore + ")");
        }

    }

    private void executeCommand() {
        // First, fire photons and destroy mines as appropriate
        for (COMMAND c : steps[stepNum - 1]) {
            if (c == COMMAND.ALPHA ) {
                List<Integer> minesSE = findMinesOnXYLine(shipX + 1, shipY + 1);
                for (Integer m : minesSE) {
                    destroyMine(m);
                }
                List<Integer> minesNE = findMinesOnXYLine(shipX + 1, shipY - 1);
                for (Integer m : minesNE) {
                    destroyMine(m);
                }
                List<Integer> minesSW = findMinesOnXYLine(shipX - 1, shipY + 1);
                for (Integer m : minesSW) {
                    destroyMine(m);
                }
                List<Integer> minesNW = findMinesOnXYLine(shipX - 1, shipY - 1);
                for (Integer m : minesNW) {
                    destroyMine(m);
                }
                shotsFired++;
            } else if (c == COMMAND.BETA) {
                List<Integer> minesN = findMinesOnXYLine(shipX, shipY - 1);
                for (Integer m : minesN) {
                    destroyMine(m);
                }
                List<Integer> minesS = findMinesOnXYLine(shipX, shipY + 1);
                for (Integer m : minesS) {
                    destroyMine(m);
                }
                List<Integer> minesE = findMinesOnXYLine(shipX + 1, shipY);
                for (Integer m : minesE) {
                    destroyMine(m);
                }
                List<Integer> minesW = findMinesOnXYLine(shipX - 1, shipY);
                for (Integer m : minesW) {
                    destroyMine(m);
                }
                shotsFired++;
            } else if (c == COMMAND.GAMMA) {
                List<Integer> minesE = findMinesOnXYLine(shipX + 1, shipY);
                for (Integer m : minesE) {
                    destroyMine(m);
                }
                List<Integer> minesW = findMinesOnXYLine(shipX - 1, shipY);
                for (Integer m : minesW) {
                    destroyMine(m);
                }
                List<Integer> minesOnLine = findMinesOnXYLine(shipX, shipY);
                for (Integer m : minesOnLine) {
                    destroyMine(m);
                }
                shotsFired++;
            } else if (c == COMMAND.DELTA) {
                List<Integer> minesN = findMinesOnXYLine(shipX, shipY - 1);
                for (Integer m : minesN) {
                    destroyMine(m);
                }
                List<Integer> minesS = findMinesOnXYLine(shipX, shipY + 1);
                for (Integer m : minesS) {
                    destroyMine(m);
                }
                List<Integer> minesOnLine = findMinesOnXYLine(shipX, shipY);
                for (Integer m : minesOnLine) {
                    destroyMine(m);
                }
                shotsFired++;
            } else if (c == COMMAND.NORTH) {
                shipY--;
                moveCMDs++;
            } else if(c == COMMAND.SOUTH) {
                shipY++;
                moveCMDs++;
            } else if(c == COMMAND.EAST) {
                shipX++;
                moveCMDs++;
            } else if(c == COMMAND.WEST) {
                shipX--;
                moveCMDs++;
            }
        }

        // Always move down
        shipZ--;
    }

    /**
     * Returns true when there is an active mine with a Z coordinate
     * that is > current position. NOTE: Z position is always decremented after every step.
     * @return
     */
    private boolean hasPassedMine() {
        for (Integer z : activeMinesZ) {
            if (z >= shipZ) {
                return true;
            }
        }
        return false;
    }

    private void parseField(String[] lines) throws ParseException {
        int maxX = lines[0].length();
        int maxY = lines.length;
        if (maxX % 2 == 0 || maxY % 2 == 0) {
            throw new ParseException("Could not determine center of FieldFile", 0);
        }
        int originX = Math.round(maxX/2), originY = Math.round(maxY/2);
        shipX = originX;
        shipY = originY;
        for (int l = 0; l < lines.length; l++) {
            String line = lines[l];
            if (line.length() != maxX) {
                throw new ParseException("Found lines of different lengths in FieldFile", l );
            }
            for (int c = 0; c < line.length(); c++) {
                if (line.charAt(c) == '.') {
                    // Found empty space
                } else if (line.substring(c, c+1).matches("[a-zA-Z]")) {
                    addMine(c, l, charToZ(line.charAt(c)));
                    numMines++;
                } else {
                    throw new ParseException("Found unexpected character in FieldFile", l);
                }
            }
        }
    }

    /**
     * Uses ASCII values to quickly convert characters to position in space.
     * @param c
     * @return
     */
    private int charToZ(char c) {
        if ((int) c >= 65 && (int) c <= 90) {
            // in range A - Z
            return (-1)*((int) c - 38);
        } else {
            // in range a - z
            return (-1)*((int) c - 96);
        }
    }

    /**
     * Convert the distance to the mine into a character
     * @param d
     * @return
     */
    private char DistToChar(int d) {
        if (d <= 26 && d >=  1) {
            // in range a - z
            return (char) (d + 96);
        } else if (d > 26 && d <= 52) {
            // in range A - Z
            return (char) (d + 38);
        } else {
            return '*';
        }
    }

    /**
     * Add another mine while maintaining condition that the activeMines variables are always the same length.
     * @param x
     * @param y
     * @param z
     */
    private void addMine(int x, int y, int z) {
        activeMinesX.add(x);
        activeMinesY.add(y);
        activeMinesZ.add(z);
    }

    /**
     * Deactivate a mine while maintaining condition that the activeMines variables are always the same length.
     * @param mineNumber
     */
    private void destroyMine(int mineNumber) {
        int x = activeMinesX.remove(mineNumber);
        int y = activeMinesY.remove(mineNumber);
        int z = activeMinesZ.remove(mineNumber);
        inactiveMinesX.add(x);
        inactiveMinesY.add(y);
        inactiveMinesZ.add(z);
    }

    /**
     * Updates commands[] array to reflect contents of script file.
     * @param lines
     * @throws ParseException
     */
    private void parseScript(String[] lines) throws ParseException {
        steps = new List[lines.length];
        for (int i = 0; i < lines.length; i++) {
            steps[i] = new ArrayList<COMMAND>();
            if (lines[i].trim().equals("")) {
                steps[i].add(COMMAND.WS);
            } else {
                String[] curSteps = lines[i].split("\\s+");
                if (curSteps.length > 2) {
                    throw new ParseException("Improperly formatted Script File: Too Many Commands On a Line", i);
                }
                for (String s : curSteps) {
                    if (s.equals("alpha")) {
                        steps[i].add(COMMAND.ALPHA);
                    } else if (s.equals("beta")) {
                        steps[i].add(COMMAND.BETA);
                    } else if (s.equals("gamma")) {
                        steps[i].add(COMMAND.GAMMA);
                    } else if (s.equals("delta")) {
                        steps[i].add(COMMAND.DELTA);
                    } else if (s.equals("north")) {
                        steps[i].add(COMMAND.NORTH);
                    } else if (s.equals("south")) {
                        steps[i].add(COMMAND.SOUTH);
                    } else if (s.equals("east")) {
                        steps[i].add(COMMAND.EAST);
                    } else if (s.equals("west")) {
                        steps[i].add(COMMAND.WEST);
                    } else {
                        throw new ParseException("Unrecognized command in Script File", i);
                    }
                }
            }
        }
    }

    /**
     * Find the indices of all mines on the line defined by x, y (where z can vary)
     * @return
     */
    private List<Integer> findMinesOnXYLine(int x, int y) {
        List<Integer> mines = new ArrayList<Integer>();
        for (int m = 0; m < activeMinesX.size(); m++) {
            if (activeMinesX.get(m) == x && activeMinesY.get(m) == y) {
                mines.add(m);
            }
        }
        return mines;
    }

    private void printMineField() {
        // Determine the minimum size of the grid s.t. the ship is in the center
        int sizeX = 0, sizeY = 0;
        for (int m = 0; m < activeMinesX.size(); m++) {
            if (Math.abs(activeMinesX.get(m)) > sizeX) {
                sizeX = Math.abs(activeMinesX.get(m) - shipX);
            }
            if (Math.abs(activeMinesY.get(m)) > sizeY) {
                sizeY = Math.abs(activeMinesY.get(m) - shipY);
            }
        }
        char[][] mineField = new char[2*sizeY + 1][2*sizeX + 1];
        for (int r = 0; r < mineField.length; r++) {
            for (int c = 0; c < mineField[0].length; c++) {
                mineField[r][c] = '.';
            }
        }
        for (int m = 0; m < activeMinesX.size(); m++) {
            mineField[transformY(activeMinesY.get(m), sizeY)][transformX(activeMinesX.get(m), sizeX)]
                    = DistToChar(shipZ - activeMinesZ.get(m));
        }
        for (int r = 0; r < mineField.length; r++) {
            System.out.println(new String(mineField[r]));
        }
    }

    /**
     * Transforms a point in (0,0) coordinate system to one used by printMineField() function.
     * @param x
     * @param maxX
     * @return
     */
    private int transformX(int x, int maxX) {
        return x - (shipX - maxX);
    }

    /**
     * Transforms a point in (0,0) coordinate system to one used by printMineField() function.
     * @param y
     * @param maxY
     * @return
     */
    private int transformY(int y, int maxY) {
        return y - (shipY - maxY);
    }

}
