//package eltons.books;
//
//import eltons.books.DTOs.BookDTO;
//import eltons.books.controllers.BookController;
//import eltons.books.services.BookService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//@WebMvcTest(BookController.class)
//@AutoConfigureMockMvc
//class BookControllerTest {
//
//	@Test
//	void shouldReturnBooks() throws Exception {
//
//		List<BookDTO> books = List.of(
//				new BookDTO("Clean Code"),
//				new BookDTO("Effective Java")
//		);
//
//		when(bookService.searchBooks(any()))
//				.thenReturn(books);
//
//		mockMvc.perform(get("/books"))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$[0].title")
//						.value("Clean Code"))
//				.andExpect(jsonPath("$[1].title")
//						.value("Effective Java"));
//	}
//}