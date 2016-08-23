package geometrics.search.aggregations.metrics.geometrics.max;

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

public class MaxGeoDistanceAggregator extends NumericMetricsAggregator.SingleValue {

	final ValuesSource.Numeric valuesSource;
	final ValueFormatter formatter;

	DoubleArray maxs;

	public MaxGeoDistanceAggregator(
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
			maxs = bigArrays.newDoubleArray(1, true);
			maxs.fill(0, maxs.size(), Double.NEGATIVE_INFINITY);
		}
	}

	@Override
	public boolean needsScores() {
		return valuesSource != null && valuesSource.needsScores();
	}

	@Override
	public double metric(long owningBucketOrd) {
		if (valuesSource == null || owningBucketOrd >= maxs.size()) {
			return Double.NEGATIVE_INFINITY;
		}
		return maxs.get(owningBucketOrd);
	}

	@Override
	protected LeafBucketCollector getLeafCollector(LeafReaderContext ctx, LeafBucketCollector sub) throws IOException {
		if (valuesSource == null) {
			return LeafBucketCollector.NO_OP_COLLECTOR;
		}
		final BigArrays bigArrays = context.bigArrays();
		final SortedNumericDoubleValues allValues = valuesSource.doubleValues(ctx);
		final NumericDoubleValues values = MultiValueMode.MAX.select(allValues, Double.NEGATIVE_INFINITY);
		return new LeafBucketCollectorBase(sub, values) {
			@Override
			public void collect(int doc, long bucket) throws IOException {
				if (bucket >= maxs.size()) {
					long from = maxs.size();
					maxs = bigArrays.grow(maxs, bucket + 1);
					maxs.fill(from, maxs.size(), Double.NEGATIVE_INFINITY);
				}
				final double value = values.get(doc);
				double max = maxs.get(bucket);
				max = Math.max(max, value);
				maxs.set(bucket, max);
			}
		};
	}

	@Override
	public InternalAggregation buildAggregation(long bucket) throws IOException {
		if (valuesSource == null || bucket >= maxs.size()) {
			return buildEmptyAggregation();
		}
		return new InternalMaxGeoDistance(name, maxs.get(bucket), formatter, pipelineAggregators(), metaData());
	}

	@Override
	public InternalAggregation buildEmptyAggregation() {
		return new InternalMaxGeoDistance(name, Double.NEGATIVE_INFINITY, formatter, pipelineAggregators(), metaData());
	}

}
