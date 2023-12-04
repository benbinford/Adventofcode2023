package $package;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        final var input = IO.getResource("${package.replace('.', '/')}/input.txt");

    }
}
