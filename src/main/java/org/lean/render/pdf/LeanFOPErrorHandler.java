package org.lean.render.pdf;

import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

public class LeanFOPErrorHandler implements ErrorHandler {

  protected static SimpleLog logger;

  protected final Log getLogger() {
    if (logger == null) {
      logger = new SimpleLog("FOP/Transcoder");
      logger.setLevel(SimpleLog.LOG_LEVEL_INFO);
    }
    return logger;
  }

  /** {@inheritDoc} */
  public void error(TranscoderException te) throws TranscoderException {
    getLogger().error(te.getMessage());
  }

  /** {@inheritDoc} */
  public void fatalError(TranscoderException te) throws TranscoderException {
    throw te;
  }

  /** {@inheritDoc} */
  public void warning(TranscoderException te) throws TranscoderException {
    getLogger().warn(te.getMessage());
  }
}
