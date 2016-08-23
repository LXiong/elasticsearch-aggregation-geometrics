package geometrics.search.aggregations.metrics.geometrics.avg;

import java.io.IOException;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.search.aggregations.AggregationStreams;
import org.elasticsearch.search.aggregations.InternalAggregation;

public class AvgGeoDistanceStream implements AggregationStreams.Stream {

	public InternalAggregation readResult(StreamInput in) throws IOException {
		InternalAvgGeoDistance result = new InternalAvgGeoDistance();
		result.readFrom(in);
		return result;
	}

}
