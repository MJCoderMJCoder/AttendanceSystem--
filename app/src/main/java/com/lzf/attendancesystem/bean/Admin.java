package com.lzf.attendancesystem.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * 使用注释来定义模式和考勤实体类
 * 该@Entity注解打开Java类到数据库支持的实体。这也将指示greenDAO生成必要的代码（例如DAO）。@Entity 注解标记了一个Java类作为greenDAO一个presistable实体。
 * <p>
 * 数据库端的表和列名称是从实体和属性名称派生的。而不是Java中使用的驼峰案例样式，默认数据库名称是大写的，使用下划线来分隔单词。
 * 例如，名为creationDate的属性 将成为数据库列 CREATION_DATE。
 */
@Entity(
        // 如果您有多个模式，您可以告诉greenDAO一个实体属于哪个模式(选择任意字符串作为名称)。
        //        schema = "myschema",

        // 标记使实体“活动”:活动实体具有更新、删除和刷新方法。
        //        active = true,

        // 指定数据库中表的名称。默认情况下，名称基于实体类名称。
        //        nameInDb = "AWESOME_USERS",

        // 在这里定义跨越多个列的索引。
        //        indexes = {
        //                @Index(value = "name DESC", unique = true)
        //        },

        //标记DAO是否应该创建数据库表(默认为true)。如果您有多个实体映射到一个表，或者创建表是在greenDAO之外完成的，则将此设置为false。
        //        createInDb = false,

        // 是否应该生成all properties构造函数。总是需要一个无args构造函数。
        generateConstructors = true,

        // 如果缺少属性的getter和setter，是否应该生成它们。
        generateGettersSetters = true

        //Note that multiple schemas are currently not supported when using the Gradle plugin.（https://github.com/greenrobot/greenDAO/issues/356）
        // For the time being, continue to use your generator project.（http://greenrobot.org/greendao/documentation/generator/）
)
public class Admin implements Serializable {

    /**
     * 这个@Id注释选择long / Long属性作为实体ID。 在数据库方面，它是主键。 参数autoincrement是一个标志，使ID值不断增加（不重用旧值）。
     * 目前，实体必须用 long或 Long属性作为其主键。这是Android和SQLite的推荐做法。要解决这个问题，可以将key属性定义为一个附加属性，但是要为它创建一个惟一的索引
     */
    @Id(autoincrement = true)
    /**
     * 在属性上使用@Index为相应的数据库列创建数据库索引。 使用以下参数进行自定义：
     * name：如果你不喜欢greenDAO为索引生成的默认名称，你可以在这里指定你的名字。
     * unique：向索引添加UNIQUE约束，强制所有值都是唯一的。
     * 这个@Unique注释向数据库列添加唯一约束。注意，SQLite还隐式地为它创建了一个索引。
     * 注意:要添加跨越多个属性的索引，请参阅@Entity注释的文档。（http://greenrobot.org/greendao/documentation/modelling-entities/#The_Entity_Annotation）
     */
    @Index(unique = true)
    /**
     * 这个@Property注释允许您定义属性映射到的非默认列名称。  @Property(nameInDb = "USERNAME")
     * 如果不存在，greenDAO将以SQL-ish方式使用字段名称（大写，下划线而不是camel情况，例如customName将成为CUSTOM_NAME）。 注意：您当前只能使用内联常量来指定列名。
     */
    @Property(nameInDb = "ADMIN_ID")
    private long adminId;
    /**
     * 这个@NotNull注释使该属性成为数据库端的“NOT NULL”列。
     * 通常使用@NotNull标记基本类型（long，int，short，byte）是有意义的，同时使用包装类（Long，Integer，Short，Byte）具有可空值。
     */
    @NotNull
    private String adminAccount;
    /**
     * 这个@NotNull注释使该属性成为数据库端的“NOT NULL”列。
     * 通常使用@NotNull标记基本类型（long，int，short，byte）是有意义的，同时使用包装类（Long，Integer，Short，Byte）具有可空值。
     */
    @NotNull
    private String adminPassword;
    /**
     * 这个@NotNull注释使该属性成为数据库端的“NOT NULL”列。
     * 通常使用@NotNull标记基本类型（long，int，short，byte）是有意义的，同时使用包装类（Long，Integer，Short，Byte）具有可空值。
     */
    @NotNull
    private String adminName;
    /**
     * 这个@NotNull注释使该属性成为数据库端的“NOT NULL”列。
     * 通常使用@NotNull标记基本类型（long，int，short，byte）是有意义的，同时使用包装类（Long，Integer，Short，Byte）具有可空值。
     */
    @NotNull
    private String adminGender;
    /**
     * 这个@NotNull注释使该属性成为数据库端的“NOT NULL”列。
     * 通常使用@NotNull标记基本类型（long，int，short，byte）是有意义的，同时使用包装类（Long，Integer，Short，Byte）具有可空值。
     */
    @NotNull
    private String adminEmail;
    /**
     * 这个@NotNull注释使该属性成为数据库端的“NOT NULL”列。
     * 通常使用@NotNull标记基本类型（long，int，short，byte）是有意义的，同时使用包装类（Long，Integer，Short，Byte）具有可空值。
     */
    @NotNull
    private String adminPhone;
    /**
     * 这个@Transient注释标记要从持久性中排除的属性。 将它们用于临时状态等。或者，您也可以使用Java中的transient关键字。
     */
    @Transient
    private static final long serialVersionUID = -8997430981657272709L;

    @Generated(hash = 1139986266)
    public Admin(long adminId, @NotNull String adminAccount, @NotNull String adminPassword, @NotNull String adminName, @NotNull String adminGender,
                 @NotNull String adminEmail, @NotNull String adminPhone) {
        this.adminId = adminId;
        this.adminAccount = adminAccount;
        this.adminPassword = adminPassword;
        this.adminName = adminName;
        this.adminGender = adminGender;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
    }

    @Generated(hash = 1708792177)
    public Admin() {
    }

    public long getAdminId() {
        return this.adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public String getAdminAccount() {
        return this.adminAccount;
    }

    public void setAdminAccount(String adminAccount) {
        this.adminAccount = adminAccount;
    }

    public String getAdminPassword() {
        return this.adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getAdminName() {
        return this.adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminGender() {
        return this.adminGender;
    }

    public void setAdminGender(String adminGender) {
        this.adminGender = adminGender;
    }

    public String getAdminEmail() {
        return this.adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminPhone() {
        return this.adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }
}
