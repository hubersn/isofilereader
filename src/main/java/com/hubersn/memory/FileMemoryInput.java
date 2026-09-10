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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Minimal implementation of MemoryInputIF for File source.
 */
public class FileMemoryInput implements MemoryInputIF {

  private final File file;
  private final RandomAccessFile raf;
  private final long size;

  public FileMemoryInput(final File file) throws IOException {
    this.file = file;
    if (!file.exists()) {
      throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
    }
    this.raf = new RandomAccessFile(file, "r");
    this.size = this.raf.length();
  }

  @Override
  public byte readByte(final long position) throws IOException {
    seek(position);
    byte[] data = new byte[1];
    this.raf.read(data, 0, 1);
    return data[0];
  }

  @Override
  public void readBytes(final long position, final byte[] dest, final int destOffset, final int length) throws IOException {
    seek(position);
    this.raf.read(dest, destOffset, length);
  }

  @Override
  public long length() {
    return this.size;
  }

  public void seek(final long position) throws IOException {
    this.raf.seek(position);
  }

  @Override
  public void close() throws IOException {
    if (this.raf != null && this.raf.getChannel().isOpen()) {
      this.raf.close();
    }
  }

  @Override
  public int read(byte[] dest,
                   int offset,
                   int length)
      throws IOException {
    return this.raf.read(dest, offset, length);
  }

  @Override
  public int read(byte[] dest)
      throws IOException {
    return read(dest, 0, dest.length);
  }

  @Override
  public MemoryInputIF copy() throws IOException {
    return new FileMemoryInput(this.file);
  }

  @Override
  public int read() throws IOException {
    return this.raf.read();
  }

  @Override
  public long getFilePointer() throws IOException {
    return this.raf.getFilePointer();
  }
}
