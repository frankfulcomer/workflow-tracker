package com.example.tracker;

import com.example.tracker.model.Owner;
import com.example.tracker.model.Status;
import com.example.tracker.model.WorkItem;
import com.example.tracker.repository.OwnerRepository;
import com.example.tracker.repository.WorkItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final WorkItemRepository workItemRepository;
    private final OwnerRepository ownerRepository;

    public DataSeeder(WorkItemRepository workItemRepository, OwnerRepository ownerRepository) {
        this.workItemRepository = workItemRepository;
        this.ownerRepository = ownerRepository;
    }

    @Override
    public void run(String... args) {
        if (ownerRepository.count() == 0) {
            ownerRepository.save(newOwner("FirstName1 LastName1"));
            ownerRepository.save(newOwner("FirstName2 LastName2"));
            ownerRepository.save(newOwner("FirstName3 LastName3"));
        }

        if (workItemRepository.count() > 0) {
            return;
        }

        Owner owner1 = ownerRepository.findAll().get(0);
        Owner owner2 = ownerRepository.findAll().get(1);
        Owner owner3 = ownerRepository.findAll().get(2);

        // All seed items start in NEW with no status history, so any non-NEW state
        // exercised while testing comes from a real transition (and thus has a real,
        // verifiable history trail) rather than an arbitrary pre-seeded state.
        seed("Set up staging environment", "Provision and configure the staging box for QA sign-off.", owner1);
        seed("Investigate login timeout bug", "Users are getting logged out after ~2 minutes of inactivity.", owner2);
        seed("Write regression suite for checkout", "Cover happy path plus 3 known edge cases.", owner3);
        seed("Verify data migration script", "Confirm row counts and checksums match between old and new schema.", null);
    }

    private Owner newOwner(String name) {
        Owner owner = new Owner();
        owner.setName(name);
        return owner;
    }

    private void seed(String title, String description, Owner owner) {
        WorkItem item = new WorkItem();
        item.setTitle(title);
        item.setDescription(description);
        item.setOwner(owner);
        item.setStatus(Status.NEW);
        workItemRepository.save(item);
    }
}
