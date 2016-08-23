package geometrics.search.aggregations.metrics.geometrics.min;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.support.format.ValueFormatter;
import org.elasticsearch.search.aggregations.support.format.ValueFormatterStreams;

public class InternalMinGeoDistance extends InternalNumericMetricsAggregation.SingleValue implements Min {
	
	public final static Type TYPE = new Type("min-geo-distance");
	
	private double min;
	
	InternalMinGeoDistance() {} // for serialization

	public InternalMinGeoDistance(String name, double min, ValueFormatter formatter, List<PipelineAggregator> pipelineAggregators,
			Map<String, Object> metaData) {
		super(name, pipelineAggregators, metaData);
		this.min = min;
		this.valueFormatter = formatter;
	}
	
	public double value() {
		return getValue();
	}

	public double getValue() {
		return min;
	}

	@Override
	public Type type() {
		return TYPE;
	}

	@Override
	public InternalAggregation doReduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
		double min = Double.POSITIVE_INFINITY;
		for (InternalAggregation aggregation : aggregations) {
			min = Math.min(min, ((InternalMinGeoDistance) aggregation).min);
		}
		return new InternalMinGeoDistance(getName(), min, valueFormatter, pipelineAggregators(), getMetaData());
	}

	@Override
	public XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
		boolean hasValue = !Double.isInfinite(min);
		builder.field(CommonFields.VALUE, hasValue ? min : null);
		if (hasValue && !(valueFormatter instanceof ValueFormatter.Raw)) {
			builder.field(CommonFields.VALUE_AS_STRING, valueFormatter.format(min));
		}
		return builder;
	}

	@Override
	protected void doWriteTo(StreamOutput out) throws IOException {
		ValueFormatterStreams.writeOptional(valueFormatter, out);
		out.writeDouble(min);
	}

	@Override
	protected void doReadFrom(StreamInput in) throws IOException {
		valueFormatter = ValueFormatterStreams.readOptional(in);
		min = in.readDouble();
	}

}
