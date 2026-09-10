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

import java.io.Closeable;
import java.io.IOException;

/**
 * Make sure that code stays Java 8 compatible!
 */
public interface MemoryInputIF extends Closeable {
  /**
   * Reads a single byte at the absolute position.
   */
  byte readByte(long position) throws IOException;

  /**
   * Reads up to 'length' bytes starting at absolute position into the destination array.
   */
  void readBytes(long position, byte[] dest, int offset, int length) throws IOException;

  /**
   * Reads up to 'length' bytes starting at current position into the destination array.
   */
  int read(byte[] dest, int offset, int length) throws IOException;

  /**
   * Reads up to 'dest.length' bytes starting at current position.
   */
  int read(byte[] dest) throws IOException;

  /**
   * Reads one byte starting at current position and returns it as unsigned value (and therefore int).
   */
  int read() throws IOException;

  void seek(long position) throws IOException;

  long getFilePointer() throws IOException;

  /**
   * Returns total length of the binary input in bytes.
   */
  long length();

  MemoryInputIF copy() throws IOException;

  @Override
  default void close() throws IOException {
    // Default no-op for inputs that don't hold unmanaged resources
  }
}
