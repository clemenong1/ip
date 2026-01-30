package bob.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link Parser}.
 */
@DisplayName("Parser")
class ParserTest {

    /* ---------- parseIndex ---------- */ 

    @Test
    @DisplayName("parseIndex: valid 1-based input returns 0-based index")
    void parseIndex_validOneBased_returnsZeroBased() {
        assertEquals(0, Parser.parseIndex("mark 1", "mark "));
        assertEquals(1, Parser.parseIndex("mark 2", "mark "));
        assertEquals(9, Parser.parseIndex("mark 10", "mark "));
        assertEquals(0, Parser.parseIndex("delete 1", "delete "));
    }

    @Test
    @DisplayName("parseIndex: input with extra spaces after number")
    void parseIndex_withSpaces_returnsCorrectIndex() {
        assertEquals(0, Parser.parseIndex("mark 1   ", "mark "));
        assertEquals(2, Parser.parseIndex("mark  3 ", "mark "));
    }

    @Test
    @DisplayName("parseIndex: non-numeric returns -1")
    void parseIndex_nonNumeric_returnsMinusOne() {
        assertEquals(-1, Parser.parseIndex("mark abc", "mark "));
        assertEquals(-1, Parser.parseIndex("mark 1a", "mark "));
        assertEquals(-1, Parser.parseIndex("mark ", "mark "));
    }

    @Test
    @DisplayName("parseIndex: empty after prefix returns -1")
    void parseIndex_emptyAfterPrefix_returnsMinusOne() {
        assertEquals(-1, Parser.parseIndex("mark ", "mark "));
        assertEquals(-1, Parser.parseIndex("delete ", "delete "));
    }

    @Test
    @DisplayName("parseIndex: negative number returns negative index (caller should validate)")
    void parseIndex_negativeNumber_returnsNegativeIndex() {
        assertEquals(-2, Parser.parseIndex("mark -1", "mark "));
    }

    /* ---------- parseDeadlineArgs ---------- */ 

    @Test
    @DisplayName("parseDeadlineArgs: valid command returns description and by")
    void parseDeadlineArgs_validCommand_returnsDescriptionAndBy() {
        String[] result = Parser.parseDeadlineArgs("deadline return book /by 2025-01-15 1800");
        assertArrayEquals(new String[]{"return book", "2025-01-15 1800"}, result);
    }

    @Test
    @DisplayName("parseDeadlineArgs: trims description and by")
    void parseDeadlineArgs_trimsWhitespace() {
        String[] result = Parser.parseDeadlineArgs("deadline  submit report  /by  2025-02-01 2359");
        assertArrayEquals(new String[]{"submit report", "2025-02-01 2359"}, result);
    }

    @Test
    @DisplayName("parseDeadlineArgs: missing /by throws IllegalArgumentException")
    void parseDeadlineArgs_missingBy_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                Parser.parseDeadlineArgs("deadline return book"));
        assertThrows(IllegalArgumentException.class, () ->
                Parser.parseDeadlineArgs("deadline return book /by"));
    }

    @Test
    @DisplayName("parseDeadlineArgs: empty description throws IllegalArgumentException")
    void parseDeadlineArgs_emptyDescription_throws() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                Parser.parseDeadlineArgs("deadline   /by 2025-01-15"));
        assertEquals("Add a description for your deadline task.", e.getMessage());
    }

    @Test
    @DisplayName("parseDeadlineArgs: empty by time throws IllegalArgumentException")
    void parseDeadlineArgs_emptyBy_throws() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                Parser.parseDeadlineArgs("deadline return book /by  "));
        assertEquals("The deadline time cannot be empty.", e.getMessage());
    }

    @Test
    @DisplayName("parseDeadlineArgs: description can contain spaces")
    void parseDeadlineArgs_descriptionWithSpaces() {
        String[] result = Parser.parseDeadlineArgs("deadline buy milk and eggs /by 2025-01-20 0900");
        assertArrayEquals(new String[]{"buy milk and eggs", "2025-01-20 0900"}, result);
    }

    // ---------- parseFindKeyword ----------

    @Test
    @DisplayName("parseFindKeyword: valid input returns keyword")
    void parseFindKeyword_validInput_returnsKeyword() {
        assertEquals("book", Parser.parseFindKeyword("find book"));
        assertEquals("return book", Parser.parseFindKeyword("find return book"));
    }

    @Test
    @DisplayName("parseFindKeyword: trims whitespace")
    void parseFindKeyword_trimsWhitespace() {
        assertEquals("book", Parser.parseFindKeyword("find  book  "));
    }

    // ---------- parseOnDate (bonus: third method from same class) ----------

    @Test
    @DisplayName("parseOnDate: valid yyyy-MM-dd returns LocalDate")
    void parseOnDate_validFormat_returnsLocalDate() {
        LocalDate d = Parser.parseOnDate("on 2025-01-15");
        assertEquals(LocalDate.of(2025, 1, 15), d);
    }

    @Test
    @DisplayName("parseOnDate: invalid date throws DateTimeParseException")
    void parseOnDate_invalidFormat_throws() {
        assertThrows(DateTimeParseException.class, () ->
                Parser.parseOnDate("on 15-01-2025"));
        assertThrows(DateTimeParseException.class, () ->
                Parser.parseOnDate("on not-a-date"));
    }
}
