package bg.statealerts.services

import org.springframework.jmx.export.annotation.ManagedResource
import org.springframework.stereotype.Component
import javax.inject.Inject
import org.springframework.jmx.export.annotation.ManagedOperation

@ManagedResource
@Component
class ManagementService {

  @Inject
  var indexer: Indexer = _
  
  @ManagedOperation
  def reindex() = {
    indexer.reindex
  }
}