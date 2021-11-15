package org.springframework.samples.petclinic.service;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
// Ensure that if the mysql profile is active we connect to the real database:
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DataBeforeServiceTest {
	List<Pet> petsList;

	@Autowired
	protected OwnerRepository owners;
	@Autowired
	protected PetRepository pets;
	@Autowired
	protected VisitRepository visits;

	@Transactional
	@BeforeEach
	void setup(){System.out.println("Setting up");

		petsList = new ArrayList<Pet>();

		Owner owner=new Owner();
		owner.setFirstName("Test");
		owner.setLastName("TestNachname");
		owner.setAddress("4, Evans Street");
		owner.setCity("Wollongong");
		owner.setTelephone("4444444444");
		this.owners.save(owner);

		for(int i=9;i>=0;i--){
			Pet pet=new Pet();
			pet.setName("name"+i);
			PetType type=new PetType();
			type.setName(pet.getName()+i);
			pet.setType(type);
			owner.addPet(pet);
			petsList.add(pet);
			this.pets.save(pet);
		}
	}

	@Transactional
	@Test
	void listAllOrderedPets(){
		List<PetType> petTypes=pets.findPetTypes();
		assertThat(petTypes.get(5).getName()).contains("name00");
		assertThat(petTypes.get(6).getName()).contains("name11");
		assertThat(petTypes.get(7).getName()).contains("name22");
	}

	@Transactional
	@Test
	void checkForVisits() {

		for(int j=0;j<3;j++){
			Visit v = new Visit();
			v.setPetId(petsList.get(j).getId());
			v.setDescription(String.format("Visit number: %d for Pet: %s",v.getId(),petsList.get(j).getName()));
			petsList.get(j).addVisit(v);
			this.visits.save(v);
			this.pets.save(petsList.get(j));
			System.out.println(petsList.get(j));
		}

		assertThat(petsList.get(0).getVisits().size()).isGreaterThan(0);
		assertThat(petsList.get(1).getVisits().size()).isLessThan(2).isGreaterThan(0);
		assertThat(petsList.get(2).getVisits().size()).isGreaterThanOrEqualTo(1);
	}
}
