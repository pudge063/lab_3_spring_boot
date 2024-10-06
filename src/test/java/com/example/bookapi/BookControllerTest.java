package com.example.bookapi;

import com.example.bookapi.model.Book;
import com.example.bookapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    @BeforeEach
    public void setUp() {
        bookRepository.deleteAll();

        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook = bookRepository.save(testBook);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateBookAsUser() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test title\", \"author\": \"Test author\"}"))
                .andExpect(status().isForbidden()); // Ожидаем 403 Forbidden
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateBookAsAdmin() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Admin book\", \"author\": \"Admin author\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Admin book"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateBookAsUser() throws Exception {
        mockMvc.perform(put("/api/books/" + testBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Title\", \"author\": \"Updated Author\"}"))
                .andExpect(status().isForbidden()); // Ожидаем 403 Forbidden
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateBookAsAdmin() throws Exception {
        mockMvc.perform(put("/api/books/" + testBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Admin Updated Title\", \"author\": \"Admin Updated Author\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Admin Updated Title"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteBookAsUser() throws Exception {
        mockMvc.perform(delete("/api/books/" + testBook.getId()))
                .andExpect(status().isForbidden()); // Ожидаем 403 Forbidden
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteBookAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/books/" + testBook.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetBooksAsUser() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetBooksAsAdmin() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    public void testGetBooksWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized()); // Ожидаем 401 Unauthorized
    }
}
