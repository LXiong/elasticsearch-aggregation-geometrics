package geometrics.search.aggregations.metrics.geometrics.avg;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.LeafReaderContext;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.common.util.DoubleArray;
import org.elasticsearch.common.util.LongArray;
import org.elasticsearch.index.fielddata.SortedNumericDoubleValues;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.LeafBucketCollector;
import org.elasticsearch.search.aggregations.LeafBucketCollectorBase;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregator;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.support.AggregationContext;
import org.elasticsearch.search.aggregations.support.ValuesSource;
import org.elasticsearch.search.aggregations.support.format.ValueFormatter;

public class AvgGeoDistanceAggregator extends NumericMetricsAggregator.SingleValue {

	final ValuesSource.Numeric valuesSource;

	LongArray counts;
	DoubleArray sums;
	ValueFormatter formatter;

	public AvgGeoDistanceAggregator(
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
			counts = bigArrays.newLongArray(1, true);
			sums = bigArrays.newDoubleArray(1, true);
		}
	}

	@Override
	public boolean needsScores() {
		return valuesSource != null && valuesSource.needsScores();
	}

	@Override
	public double metric(long owningBucketOrd) {
		if (valuesSource == null || owningBucketOrd >= sums.size()) {
			return Double.NaN;
		}
		return sums.get(owningBucketOrd) / counts.get(owningBucketOrd);
	}

	@Override
	protected LeafBucketCollector getLeafCollector(LeafReaderContext ctx, LeafBucketCollector sub) throws IOException {
		if (valuesSource == null) {
			return LeafBucketCollector.NO_OP_COLLECTOR;
		}
		final BigArrays bigArrays = context.bigArrays();
		final SortedNumericDoubleValues values = valuesSource.doubleValues(ctx);
		return new LeafBucketCollectorBase(sub, values) {
			@Override
			public void collect(int doc, long bucket) throws IOException {
				counts = bigArrays.grow(counts, bucket + 1);
				sums = bigArrays.grow(sums, bucket + 1);

				values.setDocument(doc);
				final int valueCount = values.count();
				counts.increment(bucket, valueCount);
				double sum = 0;
				for (int i = 0; i < valueCount; i++) {
					sum += values.valueAt(i);
				}
				sums.increment(bucket, sum);
			}
		};
	}

	@Override
	public InternalAggregation buildAggregation(long bucket) throws IOException {
		if (valuesSource == null || bucket >= sums.size()) {
			return buildEmptyAggregation();
		}
		return new InternalAvgGeoDistance(name, sums.get(bucket), counts.get(bucket), formatter, pipelineAggregators(), metaData());
	}

	@Override
	public InternalAggregation buildEmptyAggregation() {
		return new InternalAvgGeoDistance(name, 0.0, 0l, formatter, pipelineAggregators(), metaData());
	}

}
