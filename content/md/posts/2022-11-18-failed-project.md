{:title "Failed Project: Home Power Monitoring"
 :layout :post}

I recently got an electric car and wanted something more interactive
to look at given my electricity usage. I know my power meter transmits
usage data for PG&E to use and wanted to attempt to capture that data
myself, rendering it for my usage. I didn't succeed, and I'm kind of
stuck on what to try next, but this is where I left things.

I think my SmartMeter is [this](https://fccid.io/OWS-NIC514) model
or at least uses that module for transmitting data to PG&E.

I bought a NUC Linux system and a couple of software-defined radio
dongles. The first SDR I bought didn't have the frequency range that
my SmartMeter supports, so I bought another one with a wider frequency
range. It turns out the second one I bought doesn't go quite high
enough to capture the upper-frequency range that it supposedly uses. The
SmartMeter seems to use frequency ranges 902.3–926.9 MHz and
2.4058–2.4809 GHz, and the second SDR I bought goes up to somewhere around
2.1 GHz.

I did some digging around for other attempts to decode SmartMeters, and
got some good clues, but eventually found out that the model I have doesn't
support anything that has already been figured out. There were some guesses
that this SmartMeter uses frequency-shift keying for encoding data, which as
I understand it, modulates the signal between two close frequencies to encode
bits. I could try using [GNU Radio](https://gnuradio.org/) to decode the signal
and see if anything interesting came out.

To interject some complaints: getting a usable environment for running tests
was the biggest pain in the ass by far. What I'd like to do is have the NUC
communicate with the SDR, and possibly run GNU Radio on the NUC, while
developing code and running GUIs on my Mac laptop. Here is an incomplete list
of things I completely failed to accomplish with this setup:

1. Get file sharing working between my laptop and the NUC, so I can edit code
on my laptop and have it somehow appear on the NUC. Samba, FTP, and NFS all failed
for me, and I'm questioning whether I can use Linux at all anymore.
2. Get screen sharing work so I can VNC into the NUC. I went so far as to get a
fake HDMI dongle so Ubuntu believes it has a display.

In the end, I ran X11 over an SSH connection and had a tool rsync files over
as they were edited. Like a caveman.

To get started, I wanted to see what frequencies were candidates to look closer
given that the 20+ MHz range in the device's lower range seemed like a big one
to hunt around in. I tried using [gr-scan](https://github.com/briand/gr-scan),
but it only builds against much older versions of GNU Radio; I
[forked](https://github.com/csm/gr-scan) it to bring it up-to-date. After
scanning a bit, I got a hit at around 912.6 MHz.

I did a bunch of noodling around in GNU Radio after that and came up with
a [GNU Radio config](https://github.com/csm/getyourfreqon/blob/main/grc/fsk-test2.grc)
that can try decoding FSK signals. I got some interesting data, but I'm not
sure if the interesting signals were there, or were just an artifact from
using clock recovery on noise. What I kept seeing *looked* like prelude data
(I don't know what the actual terminology is): repeating bit sequences that
came before data, marking it as a signal. I couldn't make any sense of
what came after those bits, though.

That's where I [left things](https://github.com/csm/getyourfreqon), and I might
pick it back up and try doing more, especially if I can get VNC or file sharing
working. I might just need to somehow find documentation on how this SmartMeter
transmits data since I haven't reverse-engineered something like this before,
and am not sure how to go about it.