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
package com.palantir.isofilereader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Implementations for methods not supported in Java 8.
 */
public class Java8Support {

  public static boolean String_isBlank(final String s) {
    return s.trim().isEmpty();
  }

  public static int Arrays_compare(final byte[] array1, final byte[] array2) {
    if (array1 == null || array2 == null) {
      throw new NullPointerException("array to compare must not be null");
    }
    // only partial support, but sufficient for us
    return Arrays.equals(array1, array2) ? 0 : 1;
  }

  public static String Files_readString(final Path path) throws IOException {
    // read as UTF-8 string
    return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
  }

  public static byte[] InputStream_readAllBytes(final InputStream is) throws IOException {
    return load(is);
  }

  public static int InputStream_readNBytes(final InputStream is, final byte[] b, int off, int len) throws IOException {
    int overall = 0;
    while (len > 0) {
      int count = is.read(b, off, len);
      if (count == -1) {
        return overall;
      }
      overall += count;
      off += count;
      len -= count;
    }
    return overall;
  }

  public static long InputStream_transferTo(InputStream source, OutputStream target) throws IOException {
    byte[] buf = new byte[8192];
    long copied = 0;
    int length;
    while ((length = source.read(buf)) != -1) {
        target.write(buf, 0, length);
        copied += length;
    }
    return copied;
  }

  private static byte[] load(final InputStream is) throws IOException {
    return load(is, 8192);
  }

  private static byte[] load(final InputStream is, int bufferSize) throws IOException {
    byte[] tmpData = new byte[bufferSize];
    int offs = 0;
    int addOn = bufferSize * 2;

    try {
      do {
        final int readLen = is.read(tmpData, offs, tmpData.length - offs);
        if (readLen == -1) {
          break;
        }
        offs += readLen;
        if (offs == tmpData.length) {
          final byte[] newres = new byte[tmpData.length + addOn];
          if (addOn < 1048576) {
            addOn = addOn * 2;
          }
          System.arraycopy(tmpData, 0, newres, 0, tmpData.length);
          tmpData = newres;
        }
      } while (true);
    } finally {
      is.close();
    }

    final byte[] data = new byte[offs];
    System.arraycopy(tmpData, 0, data, 0, offs);
    return data;
  }

}
