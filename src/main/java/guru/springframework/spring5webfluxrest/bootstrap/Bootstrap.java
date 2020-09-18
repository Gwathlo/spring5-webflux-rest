package guru.springframework.spring5webfluxrest.bootstrap;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

	private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;
	
	public Bootstrap(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
		super();
		this.categoryRepository = categoryRepository;
		this.vendorRepository = vendorRepository;
	}



	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("LOADING DATA");
		loadCategories();
		loadVendors();
		
		log.info("LOADING DATA END");
		
	}



	private void loadVendors() {
		vendorRepository.save(Vendor.builder().firstName("Michael").lastName("Parker").build()).block();

		vendorRepository.save(Vendor.builder().firstName("John").lastName("Smith").build()).block();
		
		vendorRepository.save(Vendor.builder().firstName("Michael").lastName("Weston").build()).block();
		
		log.info("Loaded vendors: " + vendorRepository.count().block());
		
	}



	private void loadCategories() {
		categoryRepository.save(Category.builder().description("Fruits").build()).block();
		
		categoryRepository.save(Category.builder().description("Packages").build()).block();
		
		categoryRepository.save(Category.builder().description("Nuts").build()).block();
		
		log.info("Loaded categories: " + categoryRepository.count().block());
		
	}

}
