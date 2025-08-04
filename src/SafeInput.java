import java.util.Map;
import java.util.Scanner;

public class SafeInput {


    // --------------------------------------------------------------------------------
    // Prevent instantiation
    // --------------------------------------------------------------------------------
    private SafeInput() {
        // By throwing here, even reflection-based instantiation fails fast.
        throw new AssertionError("SafeInput is a utility class and cannot be instantiated");
    }

    // --------------------------------------------------------------------------------
    // Public static helper methods
    // --------------------------------------------------------------------------------

    /**
     * Copyright © 2019-present, University of Cincinnati, Ohio. All rights reserved.
     * Prompts the user until they type a non-empty line
     *
     * @param pipe a Scanner opened to read from System.in
     * @param prompt prompt for the user
     * @return a String response that is not zero length
     */
    public static String getNonZeroLenString(Scanner pipe, String prompt)
    {
        String retString = ""; // Set this to length zero. Loop runs until it isn't
        do
        {
            System.out.print("\n" +prompt + ": "); // show prompt add space
            retString = pipe.nextLine();
        }while(retString.isEmpty());

        return retString;

    }

    /**
     * Prompts the user until they type a valid integer.
     *
     * @param pipe   a Scanner opened to read from System.in
     * @param prompt prompt text to show the user
     * @return       a valid integer read from the user
     */
    public static int getInt(Scanner pipe, String prompt) {
        String userInput;
        while (true) {
            System.out.print("\n" + prompt + ": ");
            userInput = pipe.nextLine().trim();

            try {
                return Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                // If it's all digits (with optional leading -), then parseInt failed due to range:
                if (userInput.matches("-?\\d+")) {
                    System.out.println(userInput
                            + " is outside the supported range of ["
                            + Integer.MIN_VALUE + " … " + Integer.MAX_VALUE + "].");
                } else {
                    // Otherwise, it really isn’t an integer literal:
                    System.out.println(userInput + " is not a valid integer.");
                }
            }
        }
    }

    /**
     * Prompts until the user types a syntactically valid double
     * that also isn’t infinite.
     *
     * @param pipe   a Scanner on System.in
     * @param prompt the text to show the user
     * @return       the parsed double
     */
    public static double getDouble(Scanner pipe, String prompt) {
        String userInput;

        while (true) {
            System.out.print("\n" + prompt + ": ");
            userInput = pipe.nextLine().trim();

            try {
                double result = Double.parseDouble(userInput);
                if (Double.isInfinite(result)) {
                    System.out.println(userInput
                            + " is outside the supported range of a Java double.");
                } else {
                    return result;
                }
            } catch (NumberFormatException e) {
                System.out.println(userInput + " is not a valid double.");
            }
        }
    }

    /**
     * Prompts the user until they enter an integer within the inclusive range [low–high].
     * Appends the range to the supplied prompt and uses SafeInput.getInt() to validate input.
     *
     * @param pipe   a Scanner opened to read from System.in
     * @param prompt the base message to display; the range “[low–high]” will be appended
     * @param low    the minimum acceptable integer value (inclusive)
     * @param high   the maximum acceptable integer value (inclusive)
     * @return       a valid integer between low and high, inclusive
     */
    public static int getRangedInt(Scanner pipe, String prompt, int low, int high) {

        // Guard against impossible requests
        if (low > high) {
            throw new IllegalArgumentException(
                    "getRangedInt: invalid bounds — low (" + low + ") must be <= high (" + high + ")");
        }

        int userRangedInt;
        String rangedPrompt = prompt + " [" + low + " to " + high + "]";

        do {
            userRangedInt = SafeInput.getInt(pipe, rangedPrompt);
            if (userRangedInt < low) {
                System.out.println(
                        userRangedInt + " is below the minimum allowed value of " + low + ". Please try again.");
            }
            else if (userRangedInt > high) {
                System.out.println(
                        userRangedInt + " is above the maximum allowed value of " + high + ". Please try again.");
            }
        } while (userRangedInt < low || userRangedInt > high);

        return userRangedInt;
    }

    /**
     * Prompts the user until they enter a double within the inclusive range [low–high].
     * Appends the range to the supplied prompt and uses SafeInput.getInt() to validate input.
     *
     * @param pipe   a Scanner opened to read from System.in
     * @param prompt the base message to display; the range “[low–high]” will be appended
     * @param low    the minimum acceptable double value (inclusive)
     * @param high   the maximum acceptable double value (inclusive)
     * @return       a valid double between low and high, inclusive
     */
    public static double getRangedDouble(Scanner pipe, String prompt, double low, double high) {

        // Guard against impossible requests
        if (low > high) {
            throw new IllegalArgumentException(
                    "getRangedDouble: invalid bounds — low (" + low + ") must be <= high (" + high + ")");
        }

        double userRangedDouble;
        String rangedPrompt = prompt + " [" + low + " to " + high + "]";

        do {
            userRangedDouble = SafeInput.getDouble(pipe, rangedPrompt);
            if (userRangedDouble < low) {
                System.out.println(
                        userRangedDouble + " is below the minimum allowed value of " + low + ". Please try again.");
            }
            else if (userRangedDouble > high) {
                System.out.println(
                        userRangedDouble + " is above the maximum allowed value of " + high + ". Please try again.");
            }
        } while (userRangedDouble < low || userRangedDouble > high);

        return userRangedDouble;
    }

    /**
     * Prompts the user with a yes/no question and returns their confirmation as a boolean.
     * Repeats until the user enters one of the valid responses (case-insensitive): Y, YES, N, or NO.
     *
     * @param pipe   a Scanner opened to read from System.in
     * @param prompt the message to display (e.g. "Continue? (Y/N)")
     * @return       true if the user answers Y or YES; false if N or NO
     */
    public static boolean getYNConfirm(Scanner pipe, String prompt) {
        final Map<String,Boolean> VALID_ANSWERS = Map.of(
                "Y", true,
                "YES", true,
                "N", false,
                "NO", false
        );

        while (true) {
            System.out.print(prompt + " (Y/N):");
            // Take the user's answer, trim off extra whitespace, and make it uppercase.
            String userInput = pipe.nextLine()
                    .trim();

            // don't throw an error if the user just hits enter.
            // if blank, warn and re-prompt
            if (userInput.isEmpty()) {
                System.out.println("Input cannot be empty. Please answer Y/N.");
                continue;
            }

            // Use our map of valid choices to determine if the user gave a valid answer
            Boolean userChoice = VALID_ANSWERS.get(userInput.toUpperCase());

            if (userChoice != null) { // the user gave a valid choice
                return userChoice;
            } else {
                System.out.println( userInput + " is not a valid response.  Please answer Y/N.");
            }
        }
    }

    /**
     * Prompts the user until they enter a string that matches the given regular expression.
     *
     * @param pipe   a Scanner opened to read from System.in
     * @param prompt the message to display to the user (e.g. "SSN: ")
     * @param regEx  the regex pattern the input must match
     * @return       the first user input that matches the pattern
     */
    public static String getRegExString(Scanner pipe, String prompt, String regEx) {
        while (true) {
            System.out.print(prompt + " ");
            String userInput = pipe.nextLine();
            if (userInput.matches(regEx)) {
                return userInput;
            } else {
                System.out.println(
                        "Sorry. \"" + userInput + "\" does not match the correct pattern. "
                                + "The pattern we need to match is \"" + regEx + "\""
                );
            }
        }
    }

    /**
     * Prints a decorative header box around the given message, centered
     * within a fixed width of stars.
     *
     * @param msg the message to display in the header
     */
    public static void prettyHeader(String msg) {
        final int SCREEN_WIDTH = 60;
        final int SIDE_STARS = 3;                 // number of stars on each side of the message line
        String horizontalBorder = "*".repeat(SCREEN_WIDTH);
        String sideBorder = "*".repeat(SIDE_STARS);
        String middleLine;

        // Top border
        System.out.println(horizontalBorder);

        // Calculate padding
        int messageLength   = msg.length();
        int availableSpace  = SCREEN_WIDTH - (SIDE_STARS * 2) - messageLength;
        int leftPadding     = availableSpace / 2 + (availableSpace % 2);  // extra space on left if odd
        int rightPadding    = availableSpace - leftPadding;

        // Middle line
        middleLine = sideBorder
                + " ".repeat(leftPadding)
                + msg
                + " ".repeat(rightPadding)
                + sideBorder;
        System.out.println(middleLine);

        // Bottom border
        System.out.println(horizontalBorder);
    }
}