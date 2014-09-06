package bg.statealerts.scheduled

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.joda.time.format.DateTimeFormat
import org.springframework.beans.factory.annotation.Value
import javax.annotation.Resource
import javax.annotation.PostConstruct
import java.io.File
import org.springframework.scheduling.annotation.Scheduled
import java.util.regex.Pattern
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.io.InputStream
import org.apache.commons.io.IOUtils
import java.io.FileInputStream
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.io.BufferedOutputStream
import org.apache.commons.io.FileUtils
import java.io.OutputStream

@Component
class BackupJob {

    val log = LoggerFactory.getLogger(classOf[BackupJob])
    val ZIP_BUFFER = 2048
    val DATE_TIME_FORMAT = DateTimeFormat.forPattern("dd-MM-yyyy-HH-mm")

    @Value("${database.url}")
    var jdbcUrl: String = _

    @Value("${database.username}")
    var username: String = _

    @Value("${database.password}")
    var password: String = _

    @Value("${backup.dir}")
    var finalBackupDir: String = _

    @Value("${database.perform.backup}")
    var performBackup: Boolean = _

    var baseDir: String = _

    @PostConstruct
    def init() = {
         if (!finalBackupDir.endsWith("/")) {
             throw new IllegalArgumentException("backup.dir property must end with a slash");
         }

         baseDir = System.getProperty("java.io.tmpdir") + finalBackupDir;
         new File(baseDir).mkdirs();
    }

    @Scheduled(cron = "0 0 0 * 6 ?") //every Sunday midnight
    def run(): Unit = {
        if (performBackup) {
            parseAndPerformMySQLBackup(username, password, jdbcUrl);
        }
    }

    def parseAndPerformMySQLBackup(user: String, password: String, jdbcUrl: String) = {
        val port = "3306"

        val hostPattern = Pattern.compile("//((\\w)+)/")
        var m = hostPattern.matcher(jdbcUrl)
        var host: String = null
        if (m.find()) {
            host = m.group(1)
        }

        val dbPattern = Pattern.compile("/((\\w)+)\\?")
        m = dbPattern.matcher(jdbcUrl)
        var db: String = null;
        if (m.find()) {
            db = m.group(1)
        }

        log.debug(host + ":" + port + ":" + user + ":***:" + db)

        try {
            createBackup(host, port, user, password, db)
        } catch {
          case ex: Exception => log.error("Error during backup", ex)
        }

    }

    private def createBackup(host: String, port: String, user: String, password: String, db: String) = {
        val fileName = "backup-" + DATE_TIME_FORMAT.print(new DateTime())
        val baseFilePath = new File(baseDir + fileName).getAbsolutePath()
        val sqlFilePath = baseFilePath + ".sql"
        val passwordPart = if (StringUtils.isNotBlank(password)) " --password=" + password else ""
        val execString = "mysqldump --host=" + host + " --port=" + port + " --user=" + user + passwordPart + " --compact --complete-insert --extended-insert --single-transaction " + "--skip-comments --skip-triggers --default-character-set=utf8 " + db + " --result-file=" + sqlFilePath

        val process = Runtime.getRuntime().exec(execString)
        if (log.isDebugEnabled()) {
            log.debug("Output: " + IOUtils.toString(process.getInputStream()))
            log.debug("Error: " + IOUtils.toString(process.getErrorStream()))
        }
        if (process.waitFor() == 0) {
            zipBackup(baseFilePath)
        }

        val zipFile = new File(baseFilePath + ".zip")
        var is: InputStream = null
        var out: OutputStream = null;
        try {
          is = new BufferedInputStream(new FileInputStream(zipFile))
          out = new FileOutputStream(finalBackupDir + fileName + ".zip")
          IOUtils.copy(is, out)
        } finally {
          if (out != null) out.close()
          if (is != null) is.close()
        }

        // result = "SET FOREIGN_KEY_CHECKS = 0;\\n" + result
        // + "\\nSET FOREIGN_KEY_CHECKS = 1;";
    }

    private def zipBackup(baseFileName: String) = {
        val fos = new FileOutputStream(baseFileName + ".zip")
        val zos = new ZipOutputStream(new BufferedOutputStream(fos))

        val entryFile = new File(baseFileName + ".sql")
        val fi = new FileInputStream(entryFile)
        val origin = new BufferedInputStream(fi, ZIP_BUFFER)
        val entry = new ZipEntry("data.sql");
        zos.putNextEntry(entry);
        var count: Int = 0
        try {
        	IOUtils.copy(origin, zos)
        } finally {
        	origin.close();
        	zos.close();
        }

        entryFile.delete();
    }
}