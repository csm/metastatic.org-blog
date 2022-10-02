Power meter hacking.

* Got small linux PC, SDR USB dongle.
* Installed rtl_tcp and rtlamr..
* rtlamr doesn't support my meter https://github.com/bemasher/rtlamr/discussions/195
* Started down recessim rabbithole
    * https://wiki.recessim.com/view/Gr-smart_meters_Setup_Guide
    * Stuck at loading fhss_utils_cf_estimate -- crashes gnu radio
      * Found fix: https://github.com/sandialabs/gr-fhss_utils/issues/8#issuecomment-1248440830
* OK so the OWS-NIC514 (what looks like the transmitter attached to my power meter) likely emits data encoded with FSK or GFSK. Let's find the frequency and try looking for interesting data.
* Got gr-scan utility back from the dead, eventually.
  * https://github.com/csm/gr-scan
  * Scanning seemed to get a signal hit at around 921.6 MHz (within OWS-NIC514's frequency range).
  * Quadrature Demod seemed to get something interesting at that frequency! Long strings of repeated characters, e.g.:

```
000057a0  52 37 32 95 4d 79 48 20  4a 6f 92 ec c9 a0 c8 e9  |R72.MyH Jo......|
000057b0  09 a5 83 70 4e fe cd 98  9d 92 ff 1e 40 5f 6b 7b  |...pN.......@_k{|
000057c0  c6 0c b2 63 06 48 b9 f6  b5 ad 6b 5a d6 b5 ad 6b  |...c.H....kZ...k|
000057d0  5a d6 b5 ad 6b 5a d6 b5  ad 6b 5a d6 b5 ad 6b 5a  |Z...kZ...kZ...kZ|
000057e0  d6 b5 ad 6b 5a d6 b5 ad  6b 5a d6 b5 ad 6b 5a d6  |...kZ...kZ...kZ.|
000057f0  b5 ad 6b 5a d6 b5 ad 6b  5a d6 b5 ad 6b 5a d6 b5  |..kZ...kZ...kZ..|
...
00005a30  ad 6b 5a d6 b5 ad 6b 5a  d6 b5 ad 6b 5a d6 b5 ad  |.kZ...kZ...kZ...|
00005a40  6b 5a d6 b5 ad 6b 5a d6  b5 ad 6b 5a d6 b5 ad 6b  |kZ...kZ...kZ...k|
00005a50  5a d6 b5 ad 6b 5a d6 b5  ad 6b 5a d6 b5 ad 6b 5a  |Z...kZ...kZ...kZ|
00005a60  d6 b5 ad 6b 5a d6 b5 ad  6b 5a d6 b5 ad 6b 5a d6  |...kZ...kZ...kZ.|
00005a70  b5 ad 6b 5a d6 b5 ad 6b  5a d6 b5 ad 6b 5a d6 7f  |..kZ...kZ...kZ..|
00005a80  c9 df 38 d3 b1 ec 7c d7  70 eb 5b ba 74 ab 7b a3  |..8...|.p.[.t.{.|
00005a90  2d cd f2 ec d7 a7 5a 4f  1f a6 63 69 fc 61 a5 93  |-.....ZO..ci.a..|
```

  * Repeated characters seem to all be similar, just offset in bits somehow. We might be on to something, we just need to be able to decode this.