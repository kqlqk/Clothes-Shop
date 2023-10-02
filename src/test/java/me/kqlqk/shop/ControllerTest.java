package me.kqlqk.shop;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(value = {"/AddData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public @interface ControllerTest {
}
