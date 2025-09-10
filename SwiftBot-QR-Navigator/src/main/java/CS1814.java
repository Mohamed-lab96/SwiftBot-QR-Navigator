// Importing the SwiftBot library to control the robot
import swiftbot.*; // import

// Importing library to handle images
import java.awt.image.BufferedImage; // import

// Importing library to write to files
import java.io.*; // import

// Importing library for using ArrayList
import java.util.*; // import

// Main class for the program
public class CS1814 { // class

    // Making a SwiftBot object so we can use it
    static SwiftBotAPI bot; // variable declaration

    // This list will store the journey steps
    static List<String> journeyLog = new ArrayList<>(); // list declaration

    // This is where the program starts
    public static void main(String[] args) throws InterruptedException { // main method
        try { // try block
            // Try to connect to the SwiftBot
            bot = new SwiftBotAPI(); // create SwiftBot instance
        } catch (Exception e) { // catch block
            // If it doesn't work, show a message
            System.out.println("Could not connect to SwiftBot."); 
            return; // return statement to end program
        }

        // Tell the user to press Button A to begin
        System.out.println("Waiting for Button A to be pressed to start..."); 

        // When Button A is pressed, run the main program
        bot.enableButton(Button.A, () -> { // enable button with lambda
            System.out.println("Button A pressed. Starting program..."); 
            runMainProgram(); // call method
        });

        // Keep checking for button press
        while (true) { // infinite while loop
            Thread.sleep(100); // pause for 100ms
        }
    } // end main method

    // This controls whether the loop continues
    static boolean continueRunning = true; // boolean variable

    // This runs the main robot actions
    public static void runMainProgram() { // method
        try { // try block
            while (continueRunning) { // while loop (runs until false)
                // Try to scan a QR code
                System.out.println("Scanning for QR code (10 seconds)..."); 
                String qrCode = scanQRCode(10); // call method and get result

                // If no QR code found
                if (qrCode.isEmpty()) { // if statement
                    System.out.println("No QR code detected."); 
                    continue; // skip to next loop iteration
                }

                // Split the QR code like "23-red" into parts
                String[] parts = qrCode.split("-"); // split string

                if (parts.length != 2) { // if statement (check format)
                    // If it's not in the right format
                    System.out.println("Invalid QR format. Expected: number-colour."); 
                    continue; // skip to next iteration
                }

                // Get the number and colour
                int number = Integer.parseInt(parts[0].trim()); // parse integer
                String colour = parts[1].trim().toLowerCase(); // parse colour string

                // Convert number to binary, octal, and hex
                String binary = decimalToBinary(number); // call method
                String octal = decimalToOctal(number); // call method
                String hex = decimalToHex(number); // call method

                // Show the conversions
                System.out.println("Scanned Decimal Number: " + number); 
                System.out.println("Scanned Colour: " + colour); 
                System.out.println("Binary: " + binary); 
                System.out.println("Octal: " + octal);
                System.out.println("Hexadecimal: " + hex); 

                // Make a log entry to save later
                String logEntry = "Input: " + number + "-" + colour + // string concatenation
                        "\nBinary: " + binary +
                        "\nOctal: " + octal +
                        "\nHexadecimal: " + hex + "\n";

                // Add the entry to the list
                journeyLog.add(logEntry); // add to list

                // Use octal number for speed
                int speed = Integer.parseInt(octal); // parse octal to int
                if (speed < 30) speed += 25; // if statement (make sure speed not too low)
                if (speed > 100) speed = 100; // if statement (limit speed)

                // Check if hex contains any letters A-F
                boolean hexHasLetters = hex.matches(".*[A-F].*"); // regex check

                // Blink the lights once or twice
                blinkUnderlights(colour); // call method
                if (hexHasLetters) blinkUnderlights(colour); // if statement (call again)

                // Move the bot using the binary values
                moveUsingBinary(binary, speed); // call method

                // Blink the lights again
                blinkUnderlights(colour); // call method
                if (hexHasLetters) blinkUnderlights(colour); // if statement (call again)

                // Ask user if they want to scan again or stop
                System.out.println("Press 'Y' to scan another QR code or 'X' to exit."); 

                // Flag to wait for button press
                final boolean[] waiting = {true}; // boolean array flag

                // Turn off buttons first
                bot.disableButton(Button.Y); // disable button
                bot.disableButton(Button.X); // disable button

                // Turn on Y button to scan again
                bot.enableButton(Button.Y, () -> { // enable button with lambda
                    System.out.println("Button Y pressed. Scanning another QR..."); 
                    waiting[0] = false; // stop waiting
                });

                // Turn on X button to stop the program
                bot.enableButton(Button.X, () -> { // enable button with lambda
                    System.out.println("Button X pressed. Exiting program..."); 
                    continueRunning = false; // set flag to false
                    waiting[0] = false; // stop waiting
                });

                // Wait for user to press a button
                while (waiting[0]) { // while loop
                    Thread.sleep(100); // sleep 100ms
                }
            } // end while loop

            // After loop ends, save the log to a file
            System.out.println("Program finished."); // print
            saveJourneyLog(); // call method

        } catch (Exception e) { // catch block
            // If something goes wrong, show the error
            System.out.println("Something went wrong during the program."); 
            e.printStackTrace(); // print error
        }
    } // end method

    // Try to read a QR code for a set number of seconds
    public static String scanQRCode(int seconds) throws InterruptedException { // method
        long endTime = System.currentTimeMillis() + seconds * 1000; // calculate end time
        while (System.currentTimeMillis() < endTime) { // while loop
            BufferedImage image = bot.getQRImage(); // take photo
            String result = bot.decodeQRImage(image); // decode QR
            if (!result.isEmpty()) { // if statement
                return result; // return string
            }
            Thread.sleep(500); // wait 500ms
        }
        return ""; // return empty string if nothing found
    } // end method

    // turn on the underlights with a chosen colour
    public static void setUnderlights(String colour) { 
        int[] rgb; // array for colour

        // Choose the right colour
        if (colour.equals("red")) { // if
            rgb = new int[]{255, 0, 0}; // red RGB
        } else if (colour.equals("green")) { // else if
            rgb = new int[]{0, 255, 0}; // green RGB
        } else if (colour.equals("blue")) { // else if
            rgb = new int[]{0, 0, 255}; // blue RGB
        } else if (colour.equals("white")) { // else if
            rgb = new int[]{255, 255, 255}; // white RGB
        } else { // else
            // Use white if colour is not known
            System.out.println("Unknown colour. Defaulting to white."); 
            rgb = new int[]{255, 255, 255}; // default white
        }

        // turn on lights
        bot.fillUnderlights(rgb); // set underlights
    } // end method

    // flash the lights once
    public static void blinkUnderlights(String colour) throws InterruptedException { // method
        setUnderlights(colour); // call method (lights on)
        Thread.sleep(500); // wait
        bot.disableUnderlights(); // lights off
        Thread.sleep(500); // wait
    } // end method

    // move the robot using each bit of the binary number
    public static void moveUsingBinary(String binary, int speed) throws InterruptedException { // method
        System.out.println("Starting movement based on binary..."); // print
        for (int i = binary.length() - 1; i >= 0; i--) { // for loop (iterate bits)
            char bit = binary.charAt(i); // get bit
            if (bit == '1') { // if bit 1
                System.out.println("Bit 1 - Moving forward"); 
                bot.move(speed, speed, 2000); // move forward
            } else if (bit == '0') { // else if bit 0
                System.out.println("Bit 0 - Turning 90 degrees"); 
                bot.move(speed, -speed, 600); // turn
            }
            Thread.sleep(200); // wait
        }
    } // end method

    // Change a decimal number to binary
    public static String decimalToBinary(int number) { // method
        if (number == 0) return "0"; // if 0, return "0"
        String binary = ""; // create string
        while (number > 0) { // while loop
            binary = (number % 2) + binary; // build binary string
            number = number / 2; // divide by 2
        }
        return binary; // return result
    } // end method

    // Change a decimal number to octal
    public static String decimalToOctal(int number) { // method
        if (number == 0) return "0"; // if 0
        String octal = ""; // string
        while (number > 0) { // while loop
            octal = (number % 8) + octal; // build octal string
            number = number / 8; // divide by 8
        }
        return octal; // return result
    } // end method

    // Change a decimal number to hex
    public static String decimalToHex(int number) { // method
        if (number == 0) return "0"; // if 0
        String hex = ""; // string
        char[] hexChars = "0123456789ABCDEF".toCharArray(); // hex digits
        while (number > 0) { // while loop
            hex = hexChars[number % 16] + hex; // build hex string
            number = number / 16; // divide by 16
        }
        return hex; // return result
    } // end method

    // Save everything in the journey log to a file
    public static void saveJourneyLog() { // method
        String fileName = "swiftbot_journeys.txt"; // filename
        try { // try block
            FileWriter writer = new FileWriter(fileName); // create writer
            for (String entry : journeyLog) { // for loop
                writer.write(entry + "Saved in data/home/pi"); // write entry
            }
            writer.close(); // close file
            System.out.println("Journey log saved to: " + fileName); 
            System.out.println("You can find it in the same folder as your program."); 
        } catch (IOException e) { // catch block
            // If file can’t be saved
            System.out.println("Failed to save journey log."); 
        }
    } 
}
