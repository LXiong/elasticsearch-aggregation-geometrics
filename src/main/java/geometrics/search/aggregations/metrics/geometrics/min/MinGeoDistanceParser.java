package geometrics.search.aggregations.metrics.geometrics.min;

import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.aggregations.AggregatorFactory;
import org.elasticsearch.search.aggregations.support.ValuesSource.GeoPoint;

import geometrics.search.aggregations.metrics.geometrics.GeoMetricsAggregatorParser;

import org.elasticsearch.search.aggregations.support.ValuesSourceConfig;

public class MinGeoDistanceParser extends GeoMetricsAggregatorParser {


	public MinGeoDistanceParser() {
		super(InternalMinGeoDistance.TYPE);
	}
	
	@Override
	protected AggregatorFactory createFactory(String aggregationName, ValuesSourceConfig<GeoPoint> config,
			org.elasticsearch.common.geo.GeoPoint origin, DistanceUnit unit, GeoDistance distanceType) {
		return new MinGeoDistanceAggregatorFactory(aggregationName, type(), config, origin, unit, distanceType);
	}

}
