#!/bin/bash
# This file is based on the nxos toolchain script. It will download and build
# the arm toolchain used to build the leJOS firmware.
# Note: The particular version of the gcc and the associated libs has been
# selected to give the best size/performance of leJOS. Newer versions of
# the tools seem to produce larger and slower code.
SCRIPTDIR=$(dirname -- "$0") 
ROOT=$(pwd)
SRCDIR=$ROOT/src
BUILDDIR=$ROOT/build
PREFIX=$ROOT/install
MAKEOPTS=-j2

GCC_VERSION=4.3.2
GCC_URL=http://www.mirrorservice.org/sites/sourceware.org/pub/gcc/releases/gcc-$GCC_VERSION/gcc-$GCC_VERSION.tar.bz2
GCC_DIR=gcc-$GCC_VERSION

BINUTILS_VERSION=2.18.50
BINUTILS_URL=http://www.mirrorservice.org/sites/sourceware.org/pub/binutils/snapshots/binutils-$BINUTILS_VERSION.tar.bz2
BINUTILS_DIR=binutils-$BINUTILS_VERSION

NEWLIB_VERSION=1.16.0
NEWLIB_URL=ftp://sources.redhat.com/pub/newlib/newlib-$NEWLIB_VERSION.tar.gz
NEWLIB_DIR=newlib-$NEWLIB_VERSION

echo "I will build an arm-elf cross-compiler:

  Prefix: $PREFIX
  Sources: $SRCDIR
  Build files: $BUILDDIR

Press Enter to proceed or ^C now if you do NOT want to do this."
read IGNORE

#
# Helper functions.
#
die()
{
  if [ -n "$1" ]; then
    printf "%s\n" "$1"
  fi
  exit 1;
}

ensure_source()
{
    URL=$1
    FILE=$(basename -- "$1")

    if [ ! -e "$FILE" ]; then
        wget -O "$FILE.tmp" "$URL" && mv "$FILE.tmp" "$FILE" \
          || die "failed to fetch $URL"
    fi
}

unpack_source()
{
	echo -n "Extracing $1 ... " 
    tar -C "$SRCDIR" -xf "$1" || die "Failed to extract $1"
	echo "Done" 
}

# Create all the directories we need.
mkdir -p $SRCDIR $BUILDDIR $PREFIX || die

# First grab all the source files...
ensure_source $GCC_URL
ensure_source $BINUTILS_URL
ensure_source $NEWLIB_URL

# ... And unpack the sources.
unpack_source $(basename -- "$GCC_URL")
unpack_source $(basename -- "$BINUTILS_URL")
unpack_source $(basename -- "$NEWLIB_URL")

#
# Stage 0: Patching gcc
#
cat "$SCRIPTDIR/gcc01.patch" | patch -p0 -d "$SRCDIR/$GCC_DIR" || die
cat "$SCRIPTDIR/gcc02.patch" | patch -p0 -d "$SRCDIR/$GCC_DIR" || die

#
# Stage 1: Build binutils
#
(
mkdir -p "$BUILDDIR/$BINUTILS_DIR" && \
cd "$BUILDDIR/$BINUTILS_DIR" || die

$SRCDIR/$BINUTILS_DIR/configure --target=arm-elf --prefix="$PREFIX" \
    --enable-interwork --enable-multilib --with-float=soft \
    --disable-werror \
    && make $MAKEOPTS all && make install
) || die "Compiling/Installing binutils failed"

# Set the PATH to include the newly built binutils
export PATH=$PREFIX/bin:$PATH

#
# Stage 2: Patch the GCC multilib rules, then build the gcc compiler only
#
(
MULTILIB_CONFIG=$SRCDIR/$GCC_DIR/gcc/config/arm/t-arm-elf

echo "

MULTILIB_OPTIONS += mno-thumb-interwork/mthumb-interwork
MULTILIB_DIRNAMES += normal interwork

" >> "$MULTILIB_CONFIG" || die "Patching $MULTILIB_CONFIG failed"

mkdir -p "$BUILDDIR/$GCC_DIR" && \
cd "$BUILDDIR/$GCC_DIR" || die

"$SRCDIR/$GCC_DIR/configure" --target=arm-elf --prefix=$PREFIX \
    --enable-interwork --enable-multilib --with-float=soft \
    --enable-languages="c" --with-newlib \
    --with-headers=$SRCDIR/$NEWLIB_DIR/newlib/libc/include \
    && make $MAKEOPTS all-gcc && make install-gcc
) || die "Compiling/Installing gcc failed"

#
# Stage 3: Build and install newlib
#
(
mkdir -p $BUILDDIR/$NEWLIB_DIR && \
cd $BUILDDIR/$NEWLIB_DIR || die

$SRCDIR/$NEWLIB_DIR/configure --target=arm-elf --prefix=$PREFIX \
    --enable-interwork --enable-multilib --with-float=soft \
    && make $MAKEOPTS all && make install
) || die "Compiling/Installing newlib failed"

#
# Stage 4: Build and install the rest of GCC.
#
(
cd $BUILDDIR/$GCC_DIR || die

make $MAKEOPTS all && make install
) || die "Compiling/Installing gcc failed"

echo "
Build complete! Add $PREFIX/bin to your PATH to make arm-elf-gcc and friends
accessible directly.
"
