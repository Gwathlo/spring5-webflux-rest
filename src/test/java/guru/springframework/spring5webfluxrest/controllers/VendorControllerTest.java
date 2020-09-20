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

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public class VendorControllerTest {

	WebTestClient webTestClient;
	@Mock
	VendorRepository vendorRepository;

	VendorController vendorController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		vendorController = new VendorController(vendorRepository);
		webTestClient = WebTestClient.bindToController(vendorController).build();
	}

	@Test
	public void testListVendors() {
		given(vendorRepository.findAll())
		.willReturn(Flux.just(Vendor.builder().firstName("John").lastName("Smith").build(), 
				Vendor.builder().firstName("Michael").lastName("Weston").build()));



		webTestClient.get()
			.uri("/api/v1/vendors/")
			.exchange()
			.expectBodyList(Vendor.class)
			.hasSize(2);
	}

	@Test
	public void testGetById() {
		given(vendorRepository.findById("someid"))
		.willReturn(Mono.just(Vendor.builder().firstName("Michael").lastName("Weston").build()));

		webTestClient.get()
		.uri("/api/v1/vendors/someid")
		.exchange()
		.expectBody(Vendor.class);
	}
	
	@Test
	public void testCreateVendor() {
		given(vendorRepository.saveAll(any(Publisher.class)))
			.willReturn(Flux.just(Vendor.builder().firstName("Michael").lastName("Knight").build()));

		Mono<Vendor> venToSaveMono = Mono.just(Vendor.builder().firstName("Michael").lastName("Weston").build());

		webTestClient.post()
			.uri("/api/v1/vendors")
			.body(venToSaveMono, Vendor.class)
			.exchange()
			.expectStatus()
			.isCreated();
	}
	
	@Test
	public void testUpdate() {
		given(vendorRepository.save(any(Vendor.class)))
			.willReturn(Mono.just(Vendor.builder().firstName("Michael").lastName("Knight").build()));
		
		Mono<Vendor> venToUpdateMono = Mono.just(Vendor.builder().firstName("Michael").lastName("Weston").build());
		
		webTestClient.put()
			.uri("/api/v1/vendors/asdf")
			.body(venToUpdateMono, Vendor.class)
			.exchange()
			.expectStatus()
			.isOk();
	}

	@Test
	public void testPatchWithchanges() {
		given(vendorRepository.findById(anyString()))
			.willReturn(Mono.just(Vendor.builder().firstName("Michael").build()));
		
		given(vendorRepository.save(any(Vendor.class)))
			.willReturn(Mono.just(Vendor.builder().firstName("Michael").lastName("Knight").build()));
		
		Mono<Vendor> venToUpdateMono = Mono.just(Vendor.builder().firstName("Michael").lastName("Weston").build());
		
		webTestClient.patch()
			.uri("/api/v1/vendors/asdf")
			.body(venToUpdateMono, Vendor.class)
			.exchange()
			.expectStatus()
			.isOk();
		
		verify(vendorRepository).save(any());
	}
	
	@Test
	public void testPatchWithoutchanges() {
		given(vendorRepository.findById(anyString()))
			.willReturn(Mono.just(Vendor.builder().build()));
		
		given(vendorRepository.save(any(Vendor.class)))
			.willReturn(Mono.just(Vendor.builder().build()));
		
		Mono<Vendor> venToUpdateMono = Mono.just(Vendor.builder().build());
		
		webTestClient.patch()
			.uri("/api/v1/vendors/asdf")
			.body(venToUpdateMono, Vendor.class)
			.exchange()
			.expectStatus()
			.isOk();
		
		verify(vendorRepository, never()).save(any());
	}

}
