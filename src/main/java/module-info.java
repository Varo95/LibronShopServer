module LibronShopServer {
    requires java.desktop;
    requires org.slf4j;
    requires java.sql;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires org.hibernate.commons.annotations;

    opens com.iesfranciscodelosrios.model to org.hibernate.orm.core, org.hibernate.commons.annotations;
    exports com.iesfranciscodelosrios;
}