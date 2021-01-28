import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ImageDownloader {
    static final int START_NUM = 0;
    static final int LAST_NUM = 144;
    static final String SET = "MRD";
    static final String REGION = "NA";
    static final String filePrefix = SET + "/html/";

    public static void main(String[] args) throws IOException {
        for (int i=START_NUM; i<LAST_NUM; i++) {
            String url = getURL(i);
            try {
                if (url.contains(".png")) {
                    downloadUsingNIO(url, String.format("%s/%s%03d.png", SET, REGION, i));
                }
                else {
                    downloadUsingNIO(url, String.format("%s/%s%03d.jpg", SET, REGION, i));
                }
            }
            catch (IOException e) {
                System.out.printf("Card image not available. %03d.png was not created.\n", i);
            }
        }
    }

    private static String getURL(int num) throws IOException {
        Scanner input = new Scanner(new FileReader(filePrefix + String.format("%03d.html", num)));
        input.useDelimiter("\n");
        Stream<String> tokens = input.tokens();
        ArrayList<String> lines = new ArrayList<String>();
        tokens.forEachOrdered(lines::add);
        input.close();
        int galleryRow = 0;
        for (int i=0; i<lines.size(); i++) {
            String row = lines.get(i);
            if (row.length() >= 679 && row.substring(0, 17).equals("<div id=\"gallery-")) {
                galleryRow++;
                if (galleryRow > 1) {
                    String[] fields = row.split("\"");
                    for (int j = 0; j < fields.length; j++) {
                        if (fields[j].contains(String.format("%s-%s", SET, REGION)) && fields[j].contains("UE")
                                && fields[j].contains("https://") && !(fields[j].contains("OP"))) {
                            String[] splitURL = fields[j].split("scale-to-width-down/");
                            String url = splitURL[0] + splitURL[1].substring(3);
                            System.out.println(url);
                            return url;
                        }
                    }
                }
            }
        }
        System.out.println("Error: Could not find image url.");
        return "";
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
