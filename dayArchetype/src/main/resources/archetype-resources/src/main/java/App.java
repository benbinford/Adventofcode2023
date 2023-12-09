package $package;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App 
{

    public App(String input) {
    }

    public static void main( String[] args )
    {
        final var input = IO.getResource("${package.replace('.', '/')}/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        
        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 100_000));
    }
}
