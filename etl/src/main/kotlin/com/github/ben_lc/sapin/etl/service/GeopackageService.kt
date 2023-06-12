package com.github.ben_lc.sapin.etl.service

import com.github.ben_lc.sapin.etl.model.LocationEtl
import com.github.ben_lc.sapin.etl.repository.LocationEtlRepository
import com.github.ben_lc.sapin.etl.util.getAttribute
import com.github.ben_lc.sapin.etl.util.getAttributeOrNullForValue
import java.io.File
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.geotools.data.DataStoreFinder
import org.locationtech.jts.geom.MultiPolygon
import org.springframework.stereotype.Component

@Component
class GeopackageService(val locationRepo: LocationEtlRepository) {
  suspend fun loadLocation(gpkgFolder: File, vararg gpkgProps: GpkgProps) = coroutineScope {
    val params =
        mapOf("dbtype" to "geopkg", "database" to gpkgFolder.absolutePath, "read_only" to true)

    launch {
      val datastore = DataStoreFinder.getDataStore(params)

      gpkgProps.forEach { props ->
        val featureIterator = datastore.getFeatureSource(props.tableName).features.features()

        val locations = flow {
          while (featureIterator.hasNext()) {
            val feature = featureIterator.next()
            val location =
                LocationEtl(
                    level = props.level,
                    name = feature.getAttribute<String>(props.nameColumn)!!,
                    isoId = feature.getAttributeOrNullForValue<String>(props.isoIdColumn, "NA"),
                    levelLocalName = feature.getAttribute<String>(props.levelLocalName),
                    levelLocalNameEn = feature.getAttribute<String>(props.levelLocalNameEn),
                    geom = (feature.defaultGeometry as MultiPolygon).toText(),
                    srcId = feature.getAttribute<String>(props.srcId)!!,
                    srcParentId = feature.getAttribute<String>(props.srcParentId))
            emit(location)
          }
        }
        locationRepo.saveAll(locations)
        featureIterator.close()
      }
      datastore.dispose()
    }
  }

  data class GpkgProps(
      val tableName: String,
      val isoIdColumn: String? = null,
      val nameColumn: String,
      val level: Int,
      val levelLocalName: String? = null,
      val levelLocalNameEn: String? = null,
      val srcId: String,
      val srcParentId: String? = null
  )
}
