package geometrics.search.aggregations.metrics.geometrics.avg;

import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.aggregations.AggregatorFactory;
import org.elasticsearch.search.aggregations.support.ValuesSource.GeoPoint;

import geometrics.search.aggregations.metrics.geometrics.GeoMetricsAggregatorParser;

import org.elasticsearch.search.aggregations.support.ValuesSourceConfig;

public class AvgGeoDistanceParser extends GeoMetricsAggregatorParser {


	public AvgGeoDistanceParser() {
		super(InternalAvgGeoDistance.TYPE);
	}
	
	@Override
	protected AggregatorFactory createFactory(String aggregationName, ValuesSourceConfig<GeoPoint> config,
			org.elasticsearch.common.geo.GeoPoint origin, DistanceUnit unit, GeoDistance distanceType) {
		return new AvgGeoDistanceAggregatorFactory(aggregationName, type(), config, origin, unit, distanceType);
	}

}
