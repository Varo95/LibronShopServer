module LibronShopServer {
    requires java.sql;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires org.hibernate.commons.annotations;

    opens com.iesfranciscodelosrios.model to org.hibernate.orm.core, org.hibernate.commons.annotations;
    opens com.iesfranciscodelosrios.model.nmrelation to org.hibernate.commons.annotations, org.hibernate.orm.core;
    exports com.iesfranciscodelosrios;
}