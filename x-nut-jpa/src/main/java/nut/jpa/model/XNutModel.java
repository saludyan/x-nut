package nut.jpa.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class XNutModel implements Serializable {

    @Id
    @GeneratedValue(generator = "xNutIdentifierGenerator")
    @GenericGenerator(name = "xNutIdentifierGenerator", strategy = "nut.jpa.identifier.XNutIdentifierGenerator")
    @Column(name = "id")
    private Long id;


    @Column(name = "status")
    private Integer status = 1;

    /**
     * [创建时间] — 。
     */
    @CreatedDate
    @Column(name = "create_time")
    private Date createTime;

    @LastModifiedDate
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * [记录创建者] —
     */
    @CreatedBy
    @Column(name = "creator")
    private String creator;

    /**
     * [记录更新者] —
     */
    @LastModifiedBy
    @Column(name = "updator")
    private String updator;

    /**
     * [版本号] —
     */
    @Version
    @Column(name = "version")
    private Integer version;


}
