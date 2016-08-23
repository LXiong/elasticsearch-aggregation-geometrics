package geometrics.search.aggregations.metrics.geometrics;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregator;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.support.AggregationContext;
import org.elasticsearch.search.aggregations.support.ValuesSource;

public abstract class GeoMetricsAggregator extends NumericMetricsAggregator.SingleValue {
	
	final ValuesSource.GeoPoint valuesSource;
	final GeoPoint origin;
	final DistanceUnit unit;
	final GeoDistance distanceType;

	public GeoMetricsAggregator(
			String name,
			ValuesSource.GeoPoint valuesSource,
			GeoPoint origin,
			DistanceUnit unit,
			GeoDistance distanceType,
			AggregationContext context,
			Aggregator parent,
			List<PipelineAggregator> pipelineAggregators,
			Map<String, Object> metaData) throws IOException {
		super(name, context, parent, pipelineAggregators, metaData);
		this.valuesSource = valuesSource;
		this.origin = origin;
		this.unit = unit;
		this.distanceType = distanceType;
	}
	
	@Override
	public boolean needsScores() {
		return valuesSource != null && valuesSource.needsScores();
	}

}
