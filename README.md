This project is a Java-based program for the SwiftBot robot. It allows the bot to scan QR codes formatted like number-colour (e.g., 13-red). The program then:

Extracts the number and colour from the QR code

Converts the number into binary, octal, and hexadecimal (without built-in Java conversion methods)

Uses the binary value to control movement patterns (forward and turning)

Uses the octal value to set movement speed

Uses the hexadecimal value to decide if the underlights should blink once or twice

Changes underlight colour based on the scanned QR code

Saves all scanned codes and conversions into a text file log

Allows user interaction with button controls:

A → Start program

Y → Scan another QR code

X → Exit program
