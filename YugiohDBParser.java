import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;
import java.util.stream.Stream;

public class YugiohDBParser {
    final static String SET = "MRD";
    final static String REGION = "NA";
    final static int START_NUM = 0;
    final static int TYPE_LOC = 5;
    final static int NAME_LOC = 10;
    final static int NONMONSTER_SUBTYPE_LOC = 17;
    final static int ATTRIBUTE_LOC = 17;
    final static int RACE1_LOC = 20;
    final static int RACE2_LOC = 23;
    final static int RACE3_LOC = 27;
    final static int RACE4_LOC = 31;
    final static int RANK_LOC = 33;
    final static int ATT_LOC = 39;
    final static int DEF_LOC = 47;

    public static void main(String[] args) throws IOException {

        ArrayList<String> rawData = new ArrayList<String>();
        getRawData(rawData);

        BufferedWriter out = new BufferedWriter(new FileWriter(SET + "_S1.txt"));
        out.write("Name\tSet\tImageFile\tSet#\tType\tSubType\tAttribute\tRace\tRank\tTuner?\tATK\tDEF\tText\n");

        for (int i=0; i<rawData.size(); i++) {
            String[] rowData = rawData.get(i).split("\\n");

            Pattern typePattern = Pattern.compile("title=\"[\\w/]*\"");
            String type = extractField(typePattern, rowData[TYPE_LOC], 7, 8);

            char[] nameLine = rowData[NAME_LOC].toCharArray();
            String name = String.copyValueOf(nameLine, 11, nameLine.length-15);

            if (type.equals("Trap") || type.equals("Spell")) {
                writeNonMonster(type, name, rowData, START_NUM + i, out);
            }
            else {
                writeMonster(type, name, rowData, START_NUM + i, out);
            }
        }

        out.close();
    }

    public static void getRawData(ArrayList<String> dest) throws IOException {
        Scanner s = new Scanner(new FileReader(SET + "RAW.txt"));
        s.useDelimiter("</tr>");
        Stream<String> tokens = s.tokens();
        tokens.forEachOrdered(dest::add);
        s.close();
        dest.set(0, "\n\n" + dest.get(0));
    }

    public static void writeNonMonster(String type, String name, String[] data, int setNum, BufferedWriter out) throws IOException {
        String subType = data[NONMONSTER_SUBTYPE_LOC].trim().split(" ")[0];
        String formattedSetNum = String.format("%03d", setNum);

        out.write(name + "\t" + SET + "\t" + SET + "/" + formattedSetNum + "\t" +
                formattedSetNum + "\t" + type + "\t" + subType + "\t\t\t\t\t\t\tNot implemented yet ;)\n");
    }

    public static void writeMonster(String type, String name, String[] data, int setNum, BufferedWriter out) throws IOException {
        Pattern attributePattern = Pattern.compile("[\\w]*&");
        Pattern racePattern = Pattern.compile("[\\w ]*<");

        String attribute = extractField(attributePattern, data[ATTRIBUTE_LOC], 0, 1);
        String race1 = extractField(racePattern, data[RACE1_LOC], 0, 1);
        String race2 = extractField(racePattern, data[RACE2_LOC], 0, 1);
        String race3 = extractField(racePattern, data[RACE3_LOC], 0, 1);
        String race4 = extractField(racePattern, data[RACE4_LOC], 0, 1);
        int offset = 0;
        if (!race3.equals("")) {
            offset += 3;
            if (!race4.equals(""))
                offset += 3;
        }
        String rank = data[RANK_LOC + offset].trim();
        String attack = data[ATT_LOC + offset].trim();
        String defense = data[DEF_LOC + offset].trim();
        String broadType = getCardType(type, race2, race3, race4);
        String subType = getMonsterType(race2, race3, race4);

        boolean isTuner = false;
        if (race2.equals("Tuner")) {
            isTuner = true;
        }
        else if (race3.equals("Tuner")) {
            isTuner = true;
        }

        String formattedSetNum = String.format("%03d", setNum);

        out.write(name + "\t" + SET + "\t" + SET + "/" + REGION + formattedSetNum + "\t" +
                formattedSetNum + "\t" + broadType + "\t" + subType + "\t" +
                attribute + "\t" + race1 + "\t" + rank + "\t");
        if (isTuner) {
            out.write("Y\t");
        }
        else {
            out.write("N\t");
        }
        out.write(attack + "\t" + defense + "\t" + "Not implemented yet ;)\n");

    }

    public static String extractField(Pattern p, String data, int lOffset, int rOffset) {
        Matcher m = p.matcher(data);
        if (m.find()) {
            char[] fieldArr = m.group().toCharArray();
            String field = String.copyValueOf(fieldArr, lOffset, fieldArr.length - rOffset);
            return field.trim();
        }
        else {
            return "";
        }
    }

    public static String getCardType(String rawCardType, String race2, String race3, String race4) {
        if (rawCardType.equals("Trap") || rawCardType.equals("Spell")) {
            return rawCardType;
        }

        else if (rawCardType.substring(0, 6).equals(("Effect")) || race2.equals("Effect") || race3.equals("Effect")) {
            return "Effect Monster";
        }

        else if (race2.equals("Flip") || race3.equals("Flip") || race4.equals("Flip")) {
            return "Flip Monster";
        }

        else if (race2.equals("Gemini") || race3.equals("Gemini") || race4.equals("Gemini")) {
            return "Gemini Monster";
        }

        else if (race2.equals("Spirit") || race3.equals("Spirit") || race4.equals("Spirit")) {
            return "Spirit Monster";
        }

        else if (race2.equals("Union") || race3.equals("Union") || race4.equals("Union")) {
            return "Union Monster";
        }

        else {
            return "Monster";
        }
    }

    public static String getMonsterType(String race2, String race3, String race4) {
        if (race2.equals("Fusion") || race2.equals("Link") || race2.equals("Pendulum") ||
                race2.equals("Ritual") || race2.equals("Synchro") || race2.equals("Xyz")) {
            return race2;
        }

        else if (race3.equals("Fusion") || race3.equals("Link") || race3.equals("Pendulum") ||
                race3.equals("Ritual") || race3.equals("Synchro") || race3.equals("Xyz")) {
            return race3;
        }

        else if (race4.equals("Fusion") || race4.equals("Link") || race4.equals("Pendulum") ||
                race4.equals("Ritual") || race4.equals("Synchro") || race4.equals("Xyz")) {
            return race4;
        }

        else {
            return "Normal";
        }
    }
}
