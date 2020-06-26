package org.lean.core.log;

import org.lean.core.exception.LeanException;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.MetricsRegistry;
import org.apache.hop.core.metrics.IMetricsSnapshot;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public class LeanMetricsUtil {

  public static final String PRESENTATION_START_LAYOUT = "PRESENTATION_START_LAYOUT";
  public static final String PRESENTATION_FINISH_LAYOUT = "PRESENTATION_FINISH_LAYOUT";

  public static final String PRESENTATION_START_RENDER = "PRESENTATION_START_RENDER";
  public static final String PRESENTATION_FINISH_RENDER = "PRESENTATION_FINISH_RENDER";

  public static final String IMAGE_START_RENDER = "IMAGE_START_RENDER";
  public static final String IMAGE_FINISH_RENDER = "IMAGE_FINISH_RENDER";

  /**
   * Return the time difference in miliseconds between the end and the start
   * @param log
   * @param startMetricsCode
   * @param finishMetricsCode
   * @throws LeanException in case the logChannel, start or end code couldn't be found in the registry
   * @return
   */
  public static long getLastDuration( ILogChannel log, String startMetricsCode, String finishMetricsCode ) throws LeanException {

    String logChannelId = log.getLogChannelId();

    MetricsRegistry registry = MetricsRegistry.getInstance();

    final AtomicLong startTime = new AtomicLong( -1L );
    final AtomicLong endTime = new AtomicLong( -1L );

    Queue<IMetricsSnapshot> snapshotList = registry.getSnapshotList( logChannelId );
    if (snapshotList==null) {
      throw new LeanException( "Unable to find metrics snapshot list for log channel ID "+logChannelId );
    }

    long duration = snapshotList.stream().filter( snapshot -> snapshot.getKey().equals( startMetricsCode ) || snapshot.getKey().equals( finishMetricsCode ) )
      .mapToLong( snapshot->{ return snapshot.getDate().getTime(); } )
      .reduce( 0, (a, b) -> Math.abs(a-b) );

    // log.logError("Alternative duration : "+duration);

      snapshotList.stream().forEach( snapshot -> {
        if (snapshot.getKey().equals(startMetricsCode)) {
          startTime.set( snapshot.getDate().getTime() );
        }
        if (snapshot.getKey().equals(finishMetricsCode)) {
          endTime.set( snapshot.getDate().getTime() );
        }
      } );

    if (startTime.get()<0) {
      throw new LeanException( "Unable to find start metrics for code '"+startMetricsCode+"' in log channel with ID "+logChannelId );
    }
    if (endTime.get()<0) {
      throw new LeanException( "Unable to find end metrics for code '"+finishMetricsCode+"' in log channel with ID "+logChannelId );
    }
    return Math.abs( endTime.get() - startTime.get());
  }

}
