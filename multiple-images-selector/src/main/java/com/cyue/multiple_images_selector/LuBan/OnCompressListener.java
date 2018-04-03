package com.cyue.multiple_images_selector.LuBan;

import java.io.File;

public interface OnCompressListener {

  /**
   * Fired when the compression is started, override to handle in your own code
   */
  void onStart();

  /**
   * Fired when a compression returns successfully, override to handle in your own code
   */
  void onSuccess(File file, String url);

  /**
   * Fired when a compression fails to complete, override to handle in your own code
   */
  void onError(Throwable e, String url);
}
