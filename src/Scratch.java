
public class Scratch {

    static void combine(int input[], int n, int r, int buffer_i, int buffer[], int input_i) {
        if (buffer_i == r) {

            return;
        }

        // When no more elements are there to put in data[]
        if (input_i >= n)
            return;

        // current is included
        buffer[buffer_i] = input[input_i];

        // put next at next location
        combine(input, n, r, buffer_i + 1, buffer, input_i + 1);

        // current is excluded, replace it with next (Note that i+1 is passed, but index is not changed)
        combine(input, n, r, buffer_i, buffer, input_i + 1);
    }

    /*Driver function to check for above function*/
    public static void main(String[] args) {
        int input[] = {1, 2};
        int n = input.length;
        int r = 1;
        combine(input, n, r, 0, new int[r], 0);
    }
}