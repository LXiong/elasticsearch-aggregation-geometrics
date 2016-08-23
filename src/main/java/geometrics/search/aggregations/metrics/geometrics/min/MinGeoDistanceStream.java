package geometrics.search.aggregations.metrics.geometrics.min;

import java.io.IOException;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.search.aggregations.AggregationStreams;
import org.elasticsearch.search.aggregations.InternalAggregation;

public class MinGeoDistanceStream implements AggregationStreams.Stream {

	public InternalAggregation readResult(StreamInput in) throws IOException {
		InternalMinGeoDistance result = new InternalMinGeoDistance();
		result.readFrom(in);
		return result;
	}

}
