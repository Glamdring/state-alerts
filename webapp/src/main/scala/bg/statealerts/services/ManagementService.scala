package bg.statealerts.services

import org.springframework.jmx.export.annotation.ManagedResource
import org.springframework.stereotype.Component
import javax.inject.Inject
import org.springframework.jmx.export.annotation.ManagedOperation
import bg.statealerts.scheduled.AlertJob

@ManagedResource
@Component
class ManagementService {

  @Inject
  var indexer: Indexer = _
  
  @Inject
  var alertJob: AlertJob = _
  
  @ManagedOperation
  def reindex() = {
    indexer.reindex
  }
  
  @ManagedOperation
  def sendAlerts() = {
    alertJob.send
  }
}