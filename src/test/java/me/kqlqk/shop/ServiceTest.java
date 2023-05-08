package me.kqlqk.shop;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("/application.properties")
@Sql(value = {"/AddData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public @interface ServiceTest {
}
