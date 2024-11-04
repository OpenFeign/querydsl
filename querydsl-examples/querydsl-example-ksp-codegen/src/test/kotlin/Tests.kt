import com.querydsl.example.ksp.Cat
import com.querydsl.example.ksp.Person
import com.querydsl.example.ksp.QCat
import com.querydsl.example.ksp.QPerson
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManagerFactory
import org.hibernate.cfg.AvailableSettings
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.hibernate.cfg.Configuration
import kotlin.test.Test

class Tests {
    @Test
    fun `select entity` () {
        val emf = initialize()

        run {
            val em = emf.createEntityManager()
            em.transaction.begin()
            em.persist(Person(424, "John Smith"))
            em.transaction.commit()
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QPerson.person
            val person = queryFactory
                .selectFrom(q)
                .where(q.name.eq("John Smith"))
                .fetchOne()
            if (person == null) {
                fail<Any>("No person was returned")
            } else {
                assertThat(person.id).isEqualTo(424)
            }
            em.close()
        }
    }

    @Test
    fun `filter entities`() {
        val emf = initialize()

        run {
            val em = emf.createEntityManager()
            em.transaction.begin()
            em.persist(Cat(100, "Neville Furbottom", true))
            em.persist(Cat(101, "Edgar Allan Paw", true))
            em.persist(Cat(102, "Admiral Meowington", false))
            em.transaction.commit()
            em.close()
        }

        run {
            val em = emf.createEntityManager()
            val queryFactory = JPAQueryFactory(em)
            val q = QCat.cat
            val animals = queryFactory
                .selectFrom(q)
                .where(q.isTailUpright.isTrue)
                .fetch()
                .map { it.name }
            assertThat(animals.size).isEqualTo(2)
            assertThat(animals).contains("Neville Furbottom")
            assertThat(animals).contains("Edgar Allan Paw")
            em.close()
        }
    }

    private fun initialize(): EntityManagerFactory {
        val configuration = Configuration()
            .setProperty(AvailableSettings.JAKARTA_JDBC_DRIVER, org.h2.Driver::class.qualifiedName!!)
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:h2:mem:my-database;")
            .setProperty(AvailableSettings.HBM2DDL_AUTO, "create-drop")
            .setProperty(AvailableSettings.SHOW_SQL, "true")
            .addAnnotatedClass(Person::class.java)
            .addAnnotatedClass(Cat::class.java)

        return configuration
            .buildSessionFactory()
            .unwrap(EntityManagerFactory::class.java)
    }
}
