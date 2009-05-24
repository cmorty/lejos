#!/usr/bin/env python
#
# Take the flash_routine.bin file, and embed it as an array of bytes
# in a flash_routine.h, ready for packaging with the C firmware
# flasher.
#
# If a file name is provided on the commandline, load that file as the
# firmware flashing routine instead.
#

from sys import argv

if len(argv) >= 2:
    fname = argv[1]
else:
    fname = 'flash.bin'
    
if len(argv) >= 3:
    ofname = argv[2]
else:
    ofname = '../pccomms/lejos/pc/comm/FlashWrite.java'


fwbin = file(fname)

# Build the char representation in memory

def char_by_char(f):
    while True:
        d = f.read(1)
        if d == '':
            raise StopIteration
        yield d

data = []
java_data = []
for c in char_by_char(fwbin):
    data.append("0x%s" % c.encode('hex'))
    java_data.append("(byte)0x%s" % c.encode('hex'))

for i in range(0, len(data), 12):
    data[i] = "\n" + data[i]

for i in range(0, len(data), 8):
    java_data[i] = "\n" + java_data[i]

data_str = ', '.join(data)
len_data = "0x%X" % len(data)

# Now create the java version
data_str = ', '.join(java_data)
out = file(ofname, 'w')
out.write('package lejos.pc.comm;\n');
out.write('/**\n * Machine-generated file. Do not modify.\n */\n')
out.write('interface FlashWrite {\n  static final byte[] CODE = {')
out.write(data_str)
out.write('\n  };\n}\n')
out.close()
