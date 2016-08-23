package geometrics;

import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.aggregations.AggregationStreams;

import geometrics.search.aggregations.metrics.geometrics.avg.AvgGeoDistanceParser;
import geometrics.search.aggregations.metrics.geometrics.avg.AvgGeoDistanceStream;
import geometrics.search.aggregations.metrics.geometrics.avg.InternalAvgGeoDistance;
import geometrics.search.aggregations.metrics.geometrics.max.InternalMaxGeoDistance;
import geometrics.search.aggregations.metrics.geometrics.max.MaxGeoDistanceParser;
import geometrics.search.aggregations.metrics.geometrics.max.MaxGeoDistanceStream;
import geometrics.search.aggregations.metrics.geometrics.min.InternalMinGeoDistance;
import geometrics.search.aggregations.metrics.geometrics.min.MinGeoDistanceParser;
import geometrics.search.aggregations.metrics.geometrics.min.MinGeoDistanceStream;
import geometrics.search.aggregations.metrics.geometrics.sum.InternalSumGeoDistance;
import geometrics.search.aggregations.metrics.geometrics.sum.SumGeoDistanceParser;
import geometrics.search.aggregations.metrics.geometrics.sum.SumGeoDistanceStream;


public class GeoMetircPlugin extends Plugin {
	
	public static final String NAME = "geo-metric";

	@Override
	public String description() {
		return "Elasticsearch Geo-Metric Plugin";
	}

	@Override
	public String name() {
		return NAME;
	}
	
	public void onModule(SearchModule module) {
		module.registerAggregatorParser(AvgGeoDistanceParser.class);
		AggregationStreams.registerStream(new AvgGeoDistanceStream(), InternalAvgGeoDistance.TYPE.stream());
		module.registerAggregatorParser(SumGeoDistanceParser.class);
		AggregationStreams.registerStream(new SumGeoDistanceStream(), InternalSumGeoDistance.TYPE.stream());
		module.registerAggregatorParser(MinGeoDistanceParser.class);
		AggregationStreams.registerStream(new MinGeoDistanceStream(), InternalMinGeoDistance.TYPE.stream());
		module.registerAggregatorParser(MaxGeoDistanceParser.class);
		AggregationStreams.registerStream(new MaxGeoDistanceStream(), InternalMaxGeoDistance.TYPE.stream());
	}

}
