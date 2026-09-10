/*
 * (c) hubersn Software
 * www.hubersn.com
 */

/*
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org/>
*/
package com.hubersn.memory;

import java.io.IOException;
import java.util.Objects;

/**
 * Minimal implementation of MemoryInputIF for in-memory sources.
 */
public class ByteArrayMemoryInput implements MemoryInputIF {

  private final byte[] data;
  private final int offset;
  private final int size;

  private int currentPosition = 0;

  public ByteArrayMemoryInput(final byte[] data) {
    this(data, 0, data.length);
  }

  public ByteArrayMemoryInput(final byte[] data, final int offset, final int length) {
    this.data = Objects.requireNonNull(data, "data cannot be null");
    if (offset < 0 || length < 0 || offset + length > data.length) {
      throw new IndexOutOfBoundsException("Invalid offset or length for byte array");
    }
    this.offset = offset;
    this.size = length;
  }

  public void seek(final long position) {
    this.currentPosition = (int)position;
  }

  @Override
  public byte readByte(final long position) throws IOException {
    checkBounds(position, 1);
    return this.data[this.offset + (int) position];
  }

  @Override
  public void readBytes(final long position, final byte[] dest, final int destOffset, final int length) throws IOException {
    checkBounds(position, length);
    System.arraycopy(this.data, this.offset + (int) position, dest, destOffset, length);
  }

  @Override
  public int read(final byte[] dest, final int destOffset, final int length) throws IOException {
    checkBounds(this.currentPosition, length);
    System.arraycopy(this.data, this.offset + this.currentPosition, dest, destOffset, length);
    this.currentPosition += length;
    return length;
  }

  @Override
  public int read(final byte[] dest) throws IOException {
    return read(dest, 0, dest.length);
  }

  @Override
  public long length() {
    return this.size;
  }

  private void checkBounds(long position, long length) throws IOException {
    if (position < 0 || length < 0 || position + length > this.size) {
      throw new IOException("position/length out of bounds for this byte[] array at position " + position);
    }
  }

  @Override
  public MemoryInputIF copy() {
    return this;
  }

  @Override
  public int read() throws IOException {
    byte signedValue = this.data[this.currentPosition];
    this.currentPosition++;
    if (signedValue < 0) {
      return 256 + signedValue;
    }
    return signedValue;
  }

  @Override
  public long getFilePointer() throws IOException {
    return this.currentPosition;
  }
}
