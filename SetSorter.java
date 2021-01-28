import java.util.Arrays;
import java.util.Scanner;

public class SetSorter {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("How many numbers are in the set.");
        int[] vals = new int[100];
        for (int i = 0; i < input.nextInt(); i++)
            vals[i] = input.nextInt();
        Arrays.sort(vals);
        for (int i = 0; i < vals.length; i++)
            System.out.println(vals[i]);

    }
}
