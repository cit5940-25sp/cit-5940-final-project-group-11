import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest {

    @Test
    public void testSayHello() {
        assertEquals("Hello, World!", HelloWorld.sayHello());
    }

    @Test
    public void testNothing() {
        assertEquals(3, 4);
    }

    @Test
    public void testSomething() {
        assertEquals(9.5, 4);
    }
}

private void printString(int column, int row, String text) {
    for (int i = 0; i < text.length(); i++) {
        screen.setCharacter(column + i, row,
                new TextCharacter(text.charAt(i),
                        TextColor.ANSI.WHITE,
                        TextColor.ANSI.BLACK));
    }
}