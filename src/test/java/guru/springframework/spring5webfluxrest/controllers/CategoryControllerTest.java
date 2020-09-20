package guru.springframework.spring5webfluxrest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public class CategoryControllerTest {

	WebTestClient webTestClient;
	@Mock
	CategoryRepository categoryRepository;

	CategoryController categoryController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		categoryController = new CategoryController(categoryRepository);
		webTestClient = WebTestClient.bindToController(categoryController).build();
	}

	@Test
	public void testListCategories() {
		given(categoryRepository.findAll())
			.willReturn(Flux.just(Category.builder().description("Cat1").build(), Category.builder().description("Cat2").build()));

		webTestClient.get()
			.uri("/api/v1/categories/")
			.exchange()
			.expectBodyList(Category.class)
			.hasSize(2);
	}

	@Test
	public void testGetById() {
		given(categoryRepository.findById("someid"))
			.willReturn(Mono.just(Category.builder().description("Cat").build()));

		webTestClient.get()
			.uri("/api/v1/categories/someid")
			.exchange()
			.expectBody(Category.class);
	}
	
	@Test
	public void testCreateCategory() {
		given(categoryRepository.saveAll(any(Publisher.class)))
			.willReturn(Flux.just(Category.builder().description("descrp").build()));

		Mono<Category> catToSaveMono = Mono.just(Category.builder().description("Some Cat").build());

		webTestClient.post()
			.uri("/api/v1/categories")
			.body(catToSaveMono, Category.class)
			.exchange()
			.expectStatus()
			.isCreated();
	}
	
	@Test
	public void testUpdate() {
		given(categoryRepository.save(any(Category.class)))
			.willReturn(Mono.just(Category.builder().description("descrp").build()));
		
		Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("Some Cat").build());
		
		webTestClient.put()
			.uri("/api/v1/categories/asdf")
			.body(catToUpdateMono, Category.class)
			.exchange()
			.expectStatus()
			.isOk();
		
		
	}
	
	@Test
	public void testPatchWithchanges() {
		given(categoryRepository.findById(anyString()))
			.willReturn(Mono.just(Category.builder().build()));
		
		given(categoryRepository.save(any(Category.class)))
			.willReturn(Mono.just(Category.builder().description("descrp").build()));
		
		Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("Some Cat").build());
		
		webTestClient.patch()
			.uri("/api/v1/categories/asdf")
			.body(catToUpdateMono, Category.class)
			.exchange()
			.expectStatus()
			.isOk();
		
		verify(categoryRepository).save(any());
	} 

	@Test
	public void testPatchWithoutchanges() {
		given(categoryRepository.findById(anyString()))
			.willReturn(Mono.just(Category.builder().build()));
		
		given(categoryRepository.save(any(Category.class)))
			.willReturn(Mono.just(Category.builder().build()));
		
		Mono<Category> catToUpdateMono = Mono.just(Category.builder().build());
		
		webTestClient.patch()
			.uri("/api/v1/categories/asdf")
			.body(catToUpdateMono, Category.class)
			.exchange()
			.expectStatus()
			.isOk();
		
		verify(categoryRepository, never()).save(any());
	}
}

