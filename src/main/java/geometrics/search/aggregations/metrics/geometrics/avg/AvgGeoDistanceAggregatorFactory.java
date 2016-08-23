package geometrics.search.aggregations.metrics.geometrics.avg;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.support.AggregationContext;
import org.elasticsearch.search.aggregations.support.ValuesSource;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregatorFactory;
import org.elasticsearch.search.aggregations.support.ValuesSourceConfig;

import geometrics.search.aggregations.metrics.geometrics.GeoMetricsDistanceSource;

public class AvgGeoDistanceAggregatorFactory extends ValuesSourceAggregatorFactory.LeafOnly<ValuesSource.GeoPoint> {

	private final GeoPoint origin;
	private final DistanceUnit unit;
	private final GeoDistance distanceType;
	
	protected AvgGeoDistanceAggregatorFactory(String name, String type,
			ValuesSourceConfig<ValuesSource.GeoPoint> valuesSourceConfig,
			GeoPoint origin, DistanceUnit unit, GeoDistance distanceType) {
		super(name, type, valuesSourceConfig);
		this.origin = origin;
		this.unit = unit;
		this.distanceType = distanceType;
	}

	@Override
	protected Aggregator createUnmapped(AggregationContext aggregationContext, Aggregator parent,
			List<PipelineAggregator> pipelineAggregators,
			Map<String, Object> metaData) throws IOException {
		return new AvgGeoDistanceAggregator(name, null, config.formatter(), aggregationContext, parent, pipelineAggregators, metaData);
	}

	@Override
	protected Aggregator doCreateInternal(ValuesSource.GeoPoint valuesSource, AggregationContext aggregationContext, Aggregator parent,
			boolean collectsFromSingleBucket, List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData)
			throws IOException {
		GeoMetricsDistanceSource distanceSource = new GeoMetricsDistanceSource(valuesSource, distanceType, origin, unit);
		return new AvgGeoDistanceAggregator(name, distanceSource, config.formatter(), aggregationContext, parent, pipelineAggregators, metaData);
	}

}
