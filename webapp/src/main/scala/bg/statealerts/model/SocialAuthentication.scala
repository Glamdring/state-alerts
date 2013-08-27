package bg.statealerts.model

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
case class SocialAuthentication {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "INT(11) UNSIGNED")
    var id: Long = _

    @ManyToOne
    var user: User = _
    @Column
    var providerId: String = _
    @Column
    var providerUserId: String = _
    @Column
    var token: String = _
    @Column
    var refreshToken: String = _
    @Column
    var secret: String = _
    @Column(nullable=false)
    var expirationTime: Long = _
    @Column
    var imageUrl: String = _
}
