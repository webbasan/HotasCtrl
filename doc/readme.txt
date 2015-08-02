What's this?
============

A command line tool to setup lighting and display on the
Saitek/MadCatz X52/X52pro.

It is written in Java, so a JRE is required, at least version 7. The JAR-file
is an executable JAR bundled with all dependencies. It may work on Linux also,
but is currently only tested on OS X.

It is known not to work on Windows because of additional requirements of the
USB drivers on Windows. This still has to be investigated further.

The tool will print a usage description when no args or unknown args are
given.

Arguments consists of a keyword denoting a subsystem and one or more
parameters. Any number of subsystems can appear on the command line, even the
same subsystems can appear multiple times. The setup will be executed in the
order of the appearance. Keywords and parameters are case-insensitive.

Valid subsystem names are:

light:
    parameters: <type> <brightness level>
    where type is one of: mfd, led, all
    and brightness level is either a numerical between 0 and 127
        or a named value: off, dark, half, full, on (same as "full")

led:
    parameters: <LED name> <color>
    where LED name is one of: A, B, D, E, I, T1, T2, T3, POV, FIRE, THROTTLE, ALL
    and color is one of: red, amber, green, off, on (same as "green")
    NOTE: useful values for FIRE and THROTTLE are only "on" and "off".

line1,line2,line3:
    parameters: "<some text>"
    NOTE: the display can only show up to 16 characters per line,
        the text will be clipped after the 16th character.
        Automatic scrolling is not supported.

text:
    parameters: "<some text\nwith multiple\nlines>"
    This allows to set multiple lines of text with in one argument.
    The lines should be separated by the character sequence "\n".

clock:
    parameters: <clock type>
    where clock is one of: local_24h, local_12h, utc_24h, utc_12h, gmt_24h, gmt_12h, zulu_24h, zulu_12h
    The utc, gmt and zulu variants are convenient synonyms for the same clock types.
    If the clock is activated, the application will automatically go into "daemon" mode.

daemon:
    Activates the "daemon" mode: The application won't exit after the initial
    setup and keeps running. It will regularly check the connected state of
    the device and will initialize it again when it is re-connected.


Examples:
- Initialize light and LEDs to the defaults as set by the Windows driver:
    > java -jar HotasCtrl.jar light all on led all on
- A more sophisticated setup, maybe more suited for a specific application:
     > java -jar HotasCtrl.jar light all half led all amber led I red led POV green led T1 green led T2 amber led T3 red text "Elite: Dangerous\n    Welcome\n Cmdr  Jameson"
