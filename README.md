
# ucrpf1host

This program is a host program for a 3D printer called Panowin F1.
It can also be used on other 3D printers but beware that it might not
work perfectly.

Panowin F1 has some problems on its serial port circuit of its motherboard.
The error rate is very high and makes the g-code broken often. Also their
firmware is not the standard Marlin so it needs some special care when sending
out the G-Codes. Like if the error eats the newline character, then we need to
implement a timeout in host to resend the command when newline is missing.

This host program should be used under Linux. On Windows it is probably to use
pango software (the host and slicer) made by Panowin. It uses proprietary
binary P-code which deals better with the high transmission error rate.
