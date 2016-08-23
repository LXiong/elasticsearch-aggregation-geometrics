package geometrics.search.aggregations.metrics.geometrics.sum;

import java.io.IOException;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.search.aggregations.AggregationStreams;
import org.elasticsearch.search.aggregations.InternalAggregation;

public class SumGeoDistanceStream implements AggregationStreams.Stream {

	public InternalAggregation readResult(StreamInput in) throws IOException {
		InternalSumGeoDistance result = new InternalSumGeoDistance();
		result.readFrom(in);
		return result;
	}

}
