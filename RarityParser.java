import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

public class RarityParser {

    public static String SET = "MRD";
    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(new FileReader(SET + "Rar.txt"));
        s.useDelimiter("[;\\n]");
        Stream<String> tokens = s.tokens();
        ArrayList<String> data = new ArrayList<String>();
        tokens.forEachOrdered(data::add);
        s.close();
        for (int i=2; i<data.size(); i+=3) {
            System.out.println(data.get(i).trim());
        }
    }
}
