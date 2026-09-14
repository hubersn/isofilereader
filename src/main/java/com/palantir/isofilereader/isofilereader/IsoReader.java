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
package com.palantir.isofilereader.isofilereader;

import java.io.File;
import java.io.IOException;

import com.hubersn.memory.ByteArrayRandomAccessData;
import com.hubersn.memory.FileRandomAccessData;
import com.hubersn.memory.RandomAccessDataIF;
import com.palantir.isofilereader.isofilereader.iso.types.AbstractVolumeDescriptor;

/**
 * Extension of IsoFileReader to allow more fine-grained and performant control of the read process - focused on ISO9660, ignoring UDF for the moment.
 */
public class IsoReader extends IsoFileReader {

  public enum Format {
    ISO9660, RockRidge, Joliet;
  }

  public IsoReader(final File isoFile) throws IOException {
    this(new FileRandomAccessData(isoFile));
  }

  public IsoReader(final byte[] isoData) {
    this(new ByteArrayRandomAccessData(isoData));
  }

  public IsoReader(final RandomAccessDataIF isoData) {
    super();
    setIsoSourceData(isoData);
  }

  /**
   * Set the volume descriptor to use - volume descriptors are examined and set according to preferred format given, fallback
   * to primary volume descriptor if no match is found.
   * 
   * @param preferredFormat preferred format to work with.
   * @throws IOException if no volume descriptor is found, e.g. if provided data is not ISO9660.
   */
  public void init(final Format preferredFormat) throws IOException {
    // check volume descriptors without going through full "findOptimalSettings" workings
    final AbstractVolumeDescriptor[] volumeDescriptors = getTraditionalIsoReader().getVolumeDescriptors();
    if (volumeDescriptors == null || volumeDescriptors.length == 0) {
      throw new IOException("No volume descriptors found - no ISO9660 input data?");
    }
    // this is very simple-minded and basically only works as intended for CDBurn/CDVDBurn-mastered ISOs
    for (int i = 0; i < volumeDescriptors.length; i++) {
      AbstractVolumeDescriptor volumeDescriptor = volumeDescriptors[i];
      if (volumeDescriptor.getVolumeDescriptorType() == 1 && preferredFormat == Format.ISO9660) {
        getTraditionalIsoReader().setTableOfContentsInUse(i);
        return;
      }
      if (volumeDescriptor.getVolumeDescriptorType() == 2 && preferredFormat == Format.Joliet) {
        // TODO check raw data for typical UCS-2 16bit characters
        getTraditionalIsoReader().setTableOfContentsInUse(i);
        return;
      }
      if (preferredFormat == Format.RockRidge) {
        // TODO check if NM attributes are found in primary volume descriptor
        getTraditionalIsoReader().setTableOfContentsInUse(i);
        return;
      }
    }
    getTraditionalIsoReader().setTableOfContentsInUse(0);
  }
}
