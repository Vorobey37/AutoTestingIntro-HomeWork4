package org.example.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourierInfoEntityTest extends AbstractTest{

    @Order(1)
    @Test
    void testGetCount() throws SQLException {

        String sql = "select * from courier_info";
        Statement stmt = getConnection().createStatement();
        int count = 0;

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()){
            count++;
        }

        Assertions.assertEquals(4, count);
    }

    @Order(2)
    @Test
    void tesGetCountORM(){

        final Query query = getSession().createSQLQuery("select * from courier_info")
                .addEntity(CourierInfoEntity.class);

        Assertions.assertEquals(4, query.getResultList().size());

    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"1, John", "2, Kate", "3, Bob", "4, Michael"})
    void testName(short courierId, String firstName) throws SQLException {

        String sql = "select * from courier_info where courier_id=" + courierId;
        Statement statement = getConnection().createStatement();
        String nameResult = "";

        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
            nameResult = rs.getString(2);
        }

        Assertions.assertEquals(firstName, nameResult);
    }

    @Order(4)
    @Test
    void testAddValue(){

        CourierInfoEntity entity = new CourierInfoEntity();
        entity.setCourierId((short) 5);
        entity.setFirstName("Barclay");
        entity.setLastName("De Toll");
        entity.setPhoneNumber("+7 921 921 2121");
        entity.setDeliveryType("ship");

        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("select * from courier_info where courier_id=" + 5)
                .addEntity(CourierInfoEntity.class);
        CourierInfoEntity courierInfoEntity = (CourierInfoEntity) query.getSingleResult();

        Assertions.assertNotNull(courierInfoEntity);
    }

    @Order(5)
    @Test
    void testDeleteValue(){

        final Query query = getSession()
                .createSQLQuery("select * from courier_info where courier_id=" + 5)
                .addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierInfoEntityBeforeDelete =
                query.uniqueResultOptional();

        Assumptions.assumeTrue(courierInfoEntityBeforeDelete.isPresent());

        Session session = getSession();
        session.beginTransaction();
        session.delete(courierInfoEntityBeforeDelete.get());
        session.getTransaction().commit();

        final Query queryAfter = getSession()
                .createSQLQuery("select * from courier_info where courier_id=" + 5)
                .addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierInfoEntityAfterDelete = queryAfter.uniqueResultOptional();

        Assertions.assertFalse(courierInfoEntityAfterDelete.isPresent());
    }
}
