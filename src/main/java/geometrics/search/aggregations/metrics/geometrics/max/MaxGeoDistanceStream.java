package geometrics.search.aggregations.metrics.geometrics.max;

import java.io.IOException;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.search.aggregations.AggregationStreams;
import org.elasticsearch.search.aggregations.InternalAggregation;

public class MaxGeoDistanceStream implements AggregationStreams.Stream {

	public InternalAggregation readResult(StreamInput in) throws IOException {
		InternalMaxGeoDistance result = new InternalMaxGeoDistance();
		result.readFrom(in);
		return result;
	}

}
