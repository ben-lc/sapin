package com.github.ben_lc.sapin.etl.service

import com.github.ben_lc.sapin.model.Location
import com.github.ben_lc.sapin.repository.LocationRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.geotools.data.DataStoreFinder
import org.locationtech.jts.geom.MultiPolygon
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
class GeopackageService @Autowired constructor(val locationRepo: LocationRepository) {
  suspend fun loadLocation(gpkgFolder: File, vararg gpkgProps: GpkgProps) = coroutineScope {
    val params =
        mapOf("dbtype" to "geopkg", "database" to gpkgFolder.absolutePath, "read_only" to true)
    val datastore = DataStoreFinder.getDataStore(params)

    gpkgProps.forEach { props ->
      val featureIterator = datastore.getFeatureSource(props.tableName).features.features()

      val locations = flow {
        while (featureIterator.hasNext()) {
          val feature = featureIterator.next()
          val location =
              Location(
                  level = props.level,
                  name = feature.getAttribute(props.nameColumn) as String,
                  isoId =
                      if (props.isoIdColumn.isNullOrEmpty()) null
                      else feature.getAttribute(props.isoIdColumn) as String,
                  geom = (feature.defaultGeometry as MultiPolygon).toText())
          emit(location)
        }
      }
      launch { locationRepo.saveAll(locations) }
    }
  }

  data class GpkgProps(
      val tableName: String,
      val isoIdColumn: String?,
      val nameColumn: String,
      val level: Location.Level
  )
}
