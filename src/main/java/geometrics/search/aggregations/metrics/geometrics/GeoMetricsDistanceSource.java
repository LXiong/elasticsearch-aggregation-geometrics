package geometrics.search.aggregations.metrics.geometrics;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedNumericDocValues;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoDistance.FixedSourceDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.fielddata.MultiGeoPointValues;
import org.elasticsearch.index.fielddata.SortedBinaryDocValues;
import org.elasticsearch.index.fielddata.SortedNumericDoubleValues;
import org.elasticsearch.search.aggregations.support.ValuesSource;

public class GeoMetricsDistanceSource extends ValuesSource.Numeric {

	private final ValuesSource.GeoPoint source;
	private final GeoDistance distanceType;
	private final DistanceUnit unit;
	private final org.elasticsearch.common.geo.GeoPoint origin;

	public GeoMetricsDistanceSource(ValuesSource.GeoPoint source, GeoDistance distanceType, org.elasticsearch.common.geo.GeoPoint origin, DistanceUnit unit) {
		this.source = source;
		// even if the geo points are unique, there's no guarantee the distances are
		this.distanceType = distanceType;
		this.unit = unit;
		this.origin = origin;
	}

	@Override
	public boolean isFloatingPoint() {
		return true;
	}

	@Override
	public SortedNumericDocValues longValues(LeafReaderContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedNumericDoubleValues doubleValues(LeafReaderContext ctx) {
		final MultiGeoPointValues geoValues = source.geoPointValues(ctx);
		final FixedSourceDistance distance = distanceType.fixedSourceDistance(origin.getLat(), origin.getLon(), unit);
		return GeoDistance.distanceValues(geoValues, distance);
	}

	@Override
	public SortedBinaryDocValues bytesValues(LeafReaderContext ctx) {
		throw new UnsupportedOperationException();
	}

}
