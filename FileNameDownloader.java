import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class FileNameDownloader {
    final static String SET = "MRD";
    static int START_NUM = 0;

    public static void main(String[] args) throws IOException {
        ArrayList<String> urls = getURLs();
        String fileLocation = SET + "/html/";
        for (int i=0; i<urls.size(); i++) {
            downloadUsingNIO(urls.get(i), fileLocation + String.format("%03d", START_NUM + i) + ".html");
        }
    }

    private static ArrayList<String> getURLs() throws IOException {
        Scanner input = new Scanner(new FileReader(SET + ".txt"));
        input.useDelimiter("\n");
        Stream<String> tokens = input.tokens();

        ArrayList<String> cards = new ArrayList<String>();
        tokens.forEachOrdered(cards::add);
        input.close();

        ArrayList<String> urls = new ArrayList<String>(cards.size() - 1);
        for (int i=0; i<cards.size()-1; i++) {
            String[] card = cards.get(i + 1).split("\t");

            String name = formatName(card[0], false);
            String uName = formatName(card[0], true);
            String set = card[1];
            String rarity = formatRarity(card[4]);
            urls.add("https://yugioh.fandom.com/wiki/Card_Gallery:" + uName + "?file=" + name + "-" + set + "-EN-" + rarity + "-UE.png");
        }
        return urls;
    }

    private static String formatName(String name, boolean underscores) {
        char[] nameArr = name.toCharArray();
        int numWhiteSpaces = 0;
        for (int i=0; i<nameArr.length - numWhiteSpaces; i++) {
            if (!(Character.isUpperCase(nameArr[i]) || Character.isLowerCase(nameArr[i]) || Character.isDigit(nameArr[i]))) {
                if (underscores) {
                    if (nameArr[i] == '&') {
                        char[] biggerNameArr =Arrays.copyOf(nameArr, nameArr.length+2);
                        biggerNameArr[i] = '%';
                        biggerNameArr[i + 1] = '2';
                        biggerNameArr[i + 2] = '6';
                        for (int j = i + 1; j<nameArr.length; j++) {
                            biggerNameArr[j + 2] = nameArr[j];
                        }
                        nameArr = biggerNameArr;
                    }
                    else if (nameArr[i] != '-' && nameArr[i] != '\'' && nameArr[i] != ',' && nameArr[i] != '.') {
                        nameArr[i] = '_';
                    }
                }
                else {
                    numWhiteSpaces++;
                    for (int j = i; j < nameArr.length - 1; j++) {
                        nameArr[j] = nameArr[j + 1];
                    }
                    i -= 1;
                }
            }
        }
        return String.copyValueOf(nameArr, 0, nameArr.length - numWhiteSpaces);
    }

    private static String formatRarity(String rarity) {
        switch (rarity) {
            case "SE":
                return "ScR";
            case "SS":
                return "SSP";
            case "ST":
                return "SP";
        }

        return rarity;
    }

    private static void downloadUsingNIO(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
