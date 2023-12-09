package $package;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.benjaminbinford.utils.IO;

/**
 * Unit test for simple App.
 */
class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTrue()
    {
        final var input = IO.getResource("${package.replace('.', '/')}/testinput.txt");

        final var app = new App(input);
    }
}
