package geometrics.search.aggregations.metrics.geometrics.min;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.LeafReaderContext;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.common.util.DoubleArray;
import org.elasticsearch.index.fielddata.NumericDoubleValues;
import org.elasticsearch.index.fielddata.SortedNumericDoubleValues;
import org.elasticsearch.search.MultiValueMode;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.LeafBucketCollector;
import org.elasticsearch.search.aggregations.LeafBucketCollectorBase;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregator;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.support.AggregationContext;
import org.elasticsearch.search.aggregations.support.ValuesSource;
import org.elasticsearch.search.aggregations.support.format.ValueFormatter;

public class MinGeoDistanceAggregator extends NumericMetricsAggregator.SingleValue {

	final ValuesSource.Numeric valuesSource;
	final ValueFormatter formatter;

	DoubleArray mins;

	public MinGeoDistanceAggregator(
			String name,
			ValuesSource.Numeric valuesSource,
			ValueFormatter formatter,
			AggregationContext context,
			Aggregator parent, List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData) throws IOException {
		super(name, context, parent, pipelineAggregators, metaData);
		this.valuesSource = valuesSource;
		this.formatter = formatter;
		if (valuesSource != null) {
			final BigArrays bigArrays = context.bigArrays();
			mins = bigArrays.newDoubleArray(1, true);
			mins.fill(0, mins.size(), Double.POSITIVE_INFINITY);
		}
	}

	@Override
	public boolean needsScores() {
		return valuesSource != null && valuesSource.needsScores();
	}

	@Override
	public double metric(long owningBucketOrd) {
		if (valuesSource == null || owningBucketOrd >= mins.size()) {
			return Double.POSITIVE_INFINITY;
		}
		return mins.get(owningBucketOrd);
	}

	@Override
	protected LeafBucketCollector getLeafCollector(LeafReaderContext ctx, LeafBucketCollector sub) throws IOException {
		if (valuesSource == null) {
			return LeafBucketCollector.NO_OP_COLLECTOR;
		}
		final BigArrays bigArrays = context.bigArrays();
		final SortedNumericDoubleValues allValues = valuesSource.doubleValues(ctx);
		final NumericDoubleValues values = MultiValueMode.MIN.select(allValues, Double.POSITIVE_INFINITY);
		return new LeafBucketCollectorBase(sub, values) {
			@Override
			public void collect(int doc, long bucket) throws IOException {
				if (bucket >= mins.size()) {
					long from = mins.size();
					mins = bigArrays.grow(mins, bucket + 1);
					mins.fill(from, mins.size(), Double.POSITIVE_INFINITY);
				}
				final double value = values.get(doc);
				double min = mins.get(bucket);
				min = Math.min(min, value);
				mins.set(bucket, min);
			}
		};
	}

	@Override
	public InternalAggregation buildAggregation(long bucket) throws IOException {
		if (valuesSource == null || bucket >= mins.size()) {
			return buildEmptyAggregation();
		}
		return new InternalMinGeoDistance(name, mins.get(bucket), formatter, pipelineAggregators(), metaData());
	}

	@Override
	public InternalAggregation buildEmptyAggregation() {
		return new InternalMinGeoDistance(name, Double.POSITIVE_INFINITY, formatter, pipelineAggregators(), metaData());
	}

}
