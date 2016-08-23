package geometrics.search.aggregations.metrics.geometrics;

import java.io.IOException;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.search.SearchParseException;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.AggregatorFactory;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.support.GeoPointParser;
import org.elasticsearch.search.aggregations.support.ValuesSource;
import org.elasticsearch.search.aggregations.support.ValuesSourceConfig;
import org.elasticsearch.search.aggregations.support.ValuesSourceParser;
import org.elasticsearch.search.internal.SearchContext;

public abstract class GeoMetricsAggregatorParser implements Aggregator.Parser {

	private static final ParseField ORIGIN_FIELD = new ParseField("origin", "center", "point", "por");
	
	protected final InternalAggregation.Type aggType;

	protected GeoMetricsAggregatorParser(InternalAggregation.Type aggType) {
		this.aggType = aggType;
	}

	@Override
	public String type() {
		return aggType.name();
	}

	@Override
	public AggregatorFactory parse(String aggregationName, XContentParser parser, SearchContext context)
			throws IOException {
		ValuesSourceParser<ValuesSource.GeoPoint> vsParser = ValuesSourceParser.geoPoint(aggregationName, aggType, context).formattable(true)
				.build();
		GeoPointParser geoPointParser = new GeoPointParser(aggregationName, aggType, context, ORIGIN_FIELD);
		XContentParser.Token token;
		String currentFieldName = null;
		DistanceUnit unit = DistanceUnit.DEFAULT;
		GeoDistance distanceType = GeoDistance.DEFAULT;
		boolean hasField = false;
		while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
			if (token == XContentParser.Token.FIELD_NAME) {
				currentFieldName = parser.currentName();
			} else if (geoPointParser.token(currentFieldName, token, parser)) {
				continue;
			} else if (vsParser.token(currentFieldName, token, parser)) {
				hasField = true;
				continue;
			} else if (token == XContentParser.Token.VALUE_STRING) {
				if ("unit".equals(currentFieldName)) {
					unit = DistanceUnit.fromString(parser.text());
				} else if ("distance_type".equals(currentFieldName) || "distanceType".equals(currentFieldName)) {
					distanceType = GeoDistance.fromString(parser.text());
				} else {
					throw new SearchParseException(context, "Unknown key for a " + token + " in [" + aggregationName + "]: ["
							+ currentFieldName + "].", parser.getTokenLocation());
				}
			} else {
				throw new SearchParseException(context, "Unexpected token " + token + " in [" + aggregationName + "].",
						parser.getTokenLocation());
			}
		}
		
		GeoPoint origin = geoPointParser.geoPoint();
		if (origin == null) {
			throw new SearchParseException(context, "Missing [origin] in geo-distance aggregator [" + aggregationName + "]",
					parser.getTokenLocation());
		}
		
		if (!hasField) {
			throw new SearchParseException(context, "Missing [field] in geo-distance aggregator [" + aggregationName + "]",
					parser.getTokenLocation());
		}

		return createFactory(aggregationName, vsParser.config(), origin, unit, distanceType);
	}
	
	protected abstract AggregatorFactory createFactory(String aggregationName, ValuesSourceConfig<ValuesSource.GeoPoint> config, GeoPoint origin, DistanceUnit unit, GeoDistance distanceType);

}
