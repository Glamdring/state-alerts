package bg.statealerts.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;

@Entity
case class User extends Serializable {
    val serialVersionUID: Long = -3364753990290712657L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "INT(11) UNSIGNED")
    var id: Long = _

    @Column
    var username: String = _

    @Column(unique=true)
    @Email
    var email: String = _

    @Column
    var names: String = _

    @Column(nullable=false)
    var loginAutomatically: Boolean = _

    @Column(nullable=false)
    var receiveEmails: Boolean = _
    
    @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    var registrationTime: DateTime = _

    @Type(`type` = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    var lastLoginTime: DateTime = _

    @Column
    var loginToken: String = _
    @Column
    var loginSeries: String = _
}
