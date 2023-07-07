package com.github.ben_lc.sapin.etl.service

import com.github.ben_lc.sapin.etl.LocationGeopackageProperties
import com.github.ben_lc.sapin.etl.NaturalAreaGeopackageProperties
import com.github.ben_lc.sapin.etl.model.LocationEtl
import com.github.ben_lc.sapin.etl.model.NaturalAreaEtl
import com.github.ben_lc.sapin.etl.repository.LocationEtlRepository
import com.github.ben_lc.sapin.etl.repository.NaturalAreaEtlRepository
import com.github.ben_lc.sapin.etl.util.getAttribute
import com.github.ben_lc.sapin.etl.util.getAttributeOrNullForValue
import java.io.File
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.geotools.data.DataStoreFinder
import org.geotools.filter.text.cql2.CQL
import org.locationtech.jts.geom.Geometry
import org.opengis.filter.Filter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GeopackageService(
    val locationRepo: LocationEtlRepository,
    val natureAreaEtlRepo: NaturalAreaEtlRepository
) {

  val logger: Logger = LoggerFactory.getLogger(GeopackageService::class.java)

  private fun getDataStoreParams(gpkgFolder: File) =
      mapOf("dbtype" to "geopkg", "database" to gpkgFolder.absolutePath, "read_only" to true)
  suspend fun loadLocation(gpkgFolder: File, gpkgProps: List<LocationGeopackageProperties>) =
      coroutineScope {
        launch {
          val datastore = DataStoreFinder.getDataStore(getDataStoreParams(gpkgFolder))

          gpkgProps.forEach { props ->
            logger.info("Start loading features from: ${props.tableName}")
            val featureIterator = datastore.getFeatureSource(props.tableName).features.features()

            val locations = flow {
              while (featureIterator.hasNext()) {
                val feature = featureIterator.next()
                val geom = (feature.defaultGeometry as Geometry)
                val location =
                    LocationEtl(
                        level = props.level,
                        name = feature.getAttribute<String>(props.nameColumnName)!!,
                        isoId =
                            feature.getAttributeOrNullForValue<String>(props.isoIdColumnName, "NA"),
                        levelName = feature.getAttribute<String>(props.levelNameColumnName),
                        levelNameEn = feature.getAttribute<String>(props.levelNameEnColumnName),
                        geom = geom.toText(),
                        srid = geom.srid,
                        srcId = feature.getAttribute<String>(props.srcIdColumnName)!!,
                        srcParentId = feature.getAttribute<String>(props.srcParentIdColumnName))
                emit(location)
              }
            }
            locationRepo.saveAll(locations)
            logger.info("Complete")
            featureIterator.close()
          }
          datastore.dispose()
        }
      }

  suspend fun loadNatureArea(gpkgFolder: File, gpkgProps: List<NaturalAreaGeopackageProperties>) =
      coroutineScope {
        launch {
          val datastore = DataStoreFinder.getDataStore(getDataStoreParams(gpkgFolder))

          gpkgProps.forEach { props ->
            logger.info("Start loading features from: ${props.tableName}")
            val filter =
                if (props.filter.isNullOrEmpty()) Filter.INCLUDE else CQL.toFilter(props.filter)
            val featureIterator =
                datastore.getFeatureSource(props.tableName).getFeatures(filter).features()

            val natureAreas = flow {
              while (featureIterator.hasNext()) {
                val feature = featureIterator.next()
                val geom = (feature.defaultGeometry as Geometry)
                val natureArea =
                    NaturalAreaEtl(
                        name = feature.getAttribute<String>(props.nameColumnName)!!,
                        srcId = feature.getAttribute<String>(props.srcIdColumnName)!!,
                        typeCode = props.typeCode,
                        description = feature.getAttribute<String>(props.descriptionColumnName),
                        geom = geom.toText(),
                        srid = geom.srid)
                emit(natureArea)
              }
            }
            natureAreaEtlRepo.saveAll(natureAreas)
            logger.info("Complete")
            featureIterator.close()
          }
          datastore.dispose()
        }
      }
}
