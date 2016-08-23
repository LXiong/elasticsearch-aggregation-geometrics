package geometrics.search.aggregations.metrics.geometrics.max;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.support.format.ValueFormatter;
import org.elasticsearch.search.aggregations.support.format.ValueFormatterStreams;

public class InternalMaxGeoDistance extends InternalNumericMetricsAggregation.SingleValue implements Max {
	
	public final static Type TYPE = new Type("max-geo-distance");
	
	private double max;
	
	InternalMaxGeoDistance() {} // for serialization

	public InternalMaxGeoDistance(String name, double max, ValueFormatter formatter, List<PipelineAggregator> pipelineAggregators,
			Map<String, Object> metaData) {
		super(name, pipelineAggregators, metaData);
		this.max = max;
		this.valueFormatter = formatter;
	}
	
	public double value() {
		return getValue();
	}

	public double getValue() {
		return max;
	}

	@Override
	public Type type() {
		return TYPE;
	}

	@Override
	public InternalAggregation doReduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
		double max = Double.NEGATIVE_INFINITY;
		for (InternalAggregation aggregation : aggregations) {
			max = Math.max(max, ((InternalMaxGeoDistance) aggregation).max);
		}
		return new InternalMaxGeoDistance(getName(), max, valueFormatter, pipelineAggregators(), getMetaData());
	}

	@Override
	public XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
		boolean hasValue = !Double.isInfinite(max);
		builder.field(CommonFields.VALUE, hasValue ? max : null);
		if (hasValue && !(valueFormatter instanceof ValueFormatter.Raw)) {
			builder.field(CommonFields.VALUE_AS_STRING, valueFormatter.format(max));
		}
		return builder;
	}

	@Override
	protected void doWriteTo(StreamOutput out) throws IOException {
		ValueFormatterStreams.writeOptional(valueFormatter, out);
		out.writeDouble(max);
	}

	@Override
	protected void doReadFrom(StreamInput in) throws IOException {
		valueFormatter = ValueFormatterStreams.readOptional(in);
		max = in.readDouble();
	}

}
