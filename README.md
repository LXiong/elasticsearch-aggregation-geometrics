# elasticsearch-aggregation-geometrics

This plugin adds single-value metrics aggregation on geo-point fields, incluing `avg`/`sum`/`min`/`max` values of distance for each bucket. Since they're single-value metrics, they can be used to sort a parent terms aggregation. 

## Installing Geo-Metrics
### Cloning the source

```
git clone https://github.com/zaobao/elasticsearch-aggregation-geometrics.git
```

### Configuring the compatibility

Find the `pom.xml` in the root directory, edit this line to match your Elasticsearch version:
```
<elasticsearch.version>2.3.4</elasticsearch.version>
```
Additionally, if your java version is not 1.8, you need to edit the file `plugin-descriptor.properties` and change the parameter `java.version`.

### Packaging

```
mvn package
```
It will create a zip file in the `releases` directory.

### Installing to your elasticsearch

Extract the zip file to Elasticsearch's `plugin` path directly, or use the 'bin/plugin' command to install it.

## Wroking with Geo-Metrics

### Preparing data

PUT pokemon_go
```
{
  "mappings": {
    "pokemon": {
      "properties": {
        "name": {
          "type": "string"
        },
        "location": {
          "type": "geo_point"
        }
      }
    }
  }
}
```
Given these documents:
```
1: { "name": "Pikachu", "location": {"lat": 41.12, "lon": -71.34} }
2: { "name": "Bulbasaur", "location": {"lat": 42, "lon": -72} }
3: { "name": "Bulbasaur", "location": {"lat": 40, "lon": -74} }
4: { "name": "Charmander", "location": {"lat": 40, "lon": -70} }
5: { "name": "Charmander", "location": {"lat": 45.2, "lon": -71.14} }
6: { "name": "Squirtle", "location": {"lat": 41.22, "lon": -81.34} }
7: { "name": "Squirtle", "location": {"lat": 90, "lon": 90} }
```

### Basics

#### avg-geo-distance

Caculate average distance for each kind of pokemon.

Request:
```
{
  "size": 10,
  "query": {
    "term": {
      "name": "charmander"
    }
  },
  "aggs": {
    "avg-distance": {
      "avg-geo-distance": {
        "distance_type": "plane",
        "origin": {
          "lat": 40.5,
          "lon": -70
        },
        "field": "location"
      }
    }
  }
}
```
Response:
```
{
    "took": 12,
    "timed_out": false,
    "_shards":
    {
        "total": 5,
        "successful": 5,
        "failed": 0
    },
    "hits":
    {
        "total": 2,
        "max_score": 1.4054651,
        "hits":
        [
            {
                "_index": "pokemon_go",
                "_type": "pokemon",
                "_id": "4",
                "_score": 1.4054651,
                "_source":
                {
                    "name": "Charmander",
                    "location":
                    {
                        "lat": 40,
                        "lon": -70
                    }
                }
            },
            {
                "_index": "pokemon_go",
                "_type": "pokemon",
                "_id": "5",
                "_score": 1,
                "_source":
                {
                    "name": "Charmander",
                    "location":
                    {
                        "lat": 45.2,
                        "lon": -71.14
                    }
                }
            }
        ]
    },
    "aggregations":
    {
        "avg-distance":
        {
            "value": 297015.9600738707
        }
    }
}
```

#### sum-geo-distance

Similar to avg-geo-distance

#### min-geo-distance

Similar to avg-geo-distance

#### max-geo-distance

Similar to avg-geo-distance

### Ordering a terms aggregation

Find nearest pokemons group by pokemon's name and order them from near to far.

Request:
```
{
  "size": 0,
  "aggs": {
    "nearestPMs": {
      "terms": {
        "field": "name",
        "size": 10000,
        "order": [
          {
            "distance": "asc"
          }
        ]
      },
      "aggs": {
        "distance": {
          "min-geo-distance": {
            "distance_type" : "plane",
            "unit" : "km",
            "origin": {
              "lat": 40.5,
              "lon": -70
            },
            "field": "location"
          }
        }
      }
    }
  }
}
```
Response:
```
{
    "took": 4,
    "timed_out": false,
    "_shards":
    {
        "total": 5,
        "successful": 5,
        "failed": 0
    },
    "hits":
    {
        "total": 7,
        "max_score": 0,
        "hits":
        [
        ]
    },
    "aggregations":
    {
        "nearestPMs":
        {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets":
            [
                {
                    "key": "charmander",
                    "doc_count": 2,
                    "distance":
                    {
                        "value": 55.659749543611014
                    }
                },
                {
                    "key": "pikachu",
                    "doc_count": 1,
                    "distance":
                    {
                        "value": 164.3612676449634
                    }
                },
                {
                    "key": "bulbasaur",
                    "doc_count": 1,
                    "distance":
                    {
                        "value": 278.2987281028672
                    }
                },
                {
                    "key": "squirtle",
                    "doc_count": 2,
                    "distance":
                    {
                        "value": 1264.90491541276
                    }
                }
            ]
        }
    }
}
```

## License
MPL
