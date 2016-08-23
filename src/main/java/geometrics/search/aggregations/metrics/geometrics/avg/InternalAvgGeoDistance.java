package geometrics.search.aggregations.metrics.geometrics.avg;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.support.format.ValueFormatter;
import org.elasticsearch.search.aggregations.support.format.ValueFormatterStreams;

public class InternalAvgGeoDistance extends InternalNumericMetricsAggregation.SingleValue implements Avg {
	
	public final static Type TYPE = new Type("avg-geo-distance");
	
	private double sum;
	private long count;
	
	InternalAvgGeoDistance() {} // for serialization

	public InternalAvgGeoDistance(String name, double sum, long count, ValueFormatter formatter, List<PipelineAggregator> pipelineAggregators,
			Map<String, Object> metaData) {
		super(name, pipelineAggregators, metaData);
		this.sum = sum;
		this.count = count;
		this.valueFormatter = formatter;
	}
	
	public double value() {
		return getValue();
	}

	public double getValue() {
		return sum / count;
	}

	@Override
	public Type type() {
		return TYPE;
	}

	@Override
	public InternalAggregation doReduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
		long count = 0;
		double sum = 0;
		for (InternalAggregation aggregation : aggregations) {
			count += ((InternalAvgGeoDistance) aggregation).count;
			sum += ((InternalAvgGeoDistance) aggregation).sum;
		}
		return new InternalAvgGeoDistance(getName(), sum, count, valueFormatter, pipelineAggregators(), getMetaData());
	}

	@Override
	public XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
		builder.field(CommonFields.VALUE, count != 0 ? getValue() : null);
		if (count != 0 && !(valueFormatter instanceof ValueFormatter.Raw)) {
			builder.field(CommonFields.VALUE_AS_STRING, valueFormatter.format(getValue()));
		}
		return builder;
	}

	@Override
	protected void doWriteTo(StreamOutput out) throws IOException {
		ValueFormatterStreams.writeOptional(valueFormatter, out);
		out.writeDouble(sum);
		out.writeVLong(count);
	}

	@Override
	protected void doReadFrom(StreamInput in) throws IOException {
		valueFormatter = ValueFormatterStreams.readOptional(in);
		sum = in.readDouble();
		count = in.readVLong();
	}

}
