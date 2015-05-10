# HotasCtrl

A small command line tool to setup lighting and display on the Saitek/MadCatz
X52/X52pro.

It is written in Java, so a JRE is required, at least version 7. The JAR-file
is an executable JAR bundled with all dependencies. It should work on Windows
and Linux also, but is currently only tested on OS X.

The tool will print a usage description when no args or unknown args are
given.

Arguments consists of a keyword denoting a subsystem and one or more
parameters. Any number of subsystems can appear on the command line, even the
same subsystems can appear multiple times. The setup will be executed in the
order of the appearance. Keywords and parameters are case-insensitive.

## Valid Arguments:

- light:
    - parameters: <type> <brightness level>
    - where type is on of: mfd, led, all
    - and brightness level is either a numerical between 0 and 127
      
      or a named value: off, dark, half, full, on (same as "full")

- led:
    - parameters: <LED name> <color>
    - where LED name is on of: A, B, D, E, I, T1, T2, T3, POV, FIRE, THROTTLE, ALL
    - and color is on of: red, amber, green, off, on (same as "green")
    
      _**NOTE:** useful values for FIRE and THROTTLE are only "on" and "off"._

- line1,line2,line3:
    - parameters: "<some text>"
    
      _**NOTE:** the display can only show up to 16 characters per line,
        the text will be clipped after the 16th character.
        Automatic scrolling is not supported._

- text:
    - parameters: "<some text\nwith multiple\nlines>"
    
      This allows to set multiple lines of text with in one argument.
      The lines should be separated by the character sequence "\n".


## Examples:

- Initialize light and LEDs to the defaults as set by the Windows driver:

> java -jar HotasCtrl.jar light all on led all on

- A more sophisticated setup, maybe more suited for a specific application:

> java -jar HotasCtrl.jar light all half led all amber led I red led POV green led T1 green led T2 amber led T3 red text "Elite: Dangerous\n    Welcome\n Cmdr  Jameson"


## Thanks, Acknowledgments:

The included SaitekX52pro class was created and posted by user "atcurtis" 
in the "Mac Elite: Dangerous" forum. It helped me to get started, because he 
already sorted the low-level stuff of the Saitek SDK out and the code pointed
me to the USB4Java library.

It will may be replaced with something that fits better into the architecture,
but that has currently no high priority for me.
